package create;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Hashtable;
import java.util.Random;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import org.apache.log4j.Logger;



/**
 * @author Ephesoft
 * @version 1.0
 */
public class  NewUser{

	final static Logger logger = Logger.getLogger(NewUser.class);

	private static applicationProperties prop = new applicationProperties();
	//mysql driver
	//private static String SQL_DRIVER = "com.mysql.jdbc.Driver";
	//MS SQL Driver
	private static String SQL_DRIVER = "net.sourceforge.jtds.jdbc.Driver";
	private static String DB_NAME = prop.getPropertyValue("DataBaseName");
	private static String SQL_CONNECTION_STRING = "jdbc:jtds:sqlserver://localhost:1433;databaseName=" + DB_NAME;

	/**
	 * Basic scripting template that will traverse through a document list and its document level fields.
	 * Includes instantiated string variables containing the most relevant information from batch xml tags.  
	 * Users of this script should change the class name for the appropriate module/plugin, as well as the 
	 * this method's name. 
	 */



	public static void createNewUser(String UserName, String Password) {
		logger.info("****Starting to Creating User " + UserName);
		UserName.toLowerCase();
		
		//create ldap account
		try {
			Ldap.addRole(UserName, UserName);
			Ldap.addUser(UserName, Password);
			
		} catch (NamingException e1) {
			// TODO Auto-generated catch block
			logger.error(e1);
		}
		
		
		//Copy batch classes API 
		String[] batchclassesToCopy = prop.getPropertyValue("batchClasses").split(";");

		// Random Number to make batch class name unique
		Random rand = new Random();
		// nextInt is normally exclusive of the top value,	    
		String randomNum = Integer.toString(rand.nextInt((99999 - 10000) + 1) + 10000);		
		for (String batchClassID: batchclassesToCopy) { 

			String BatchClassName = prop.getPropertyValue(batchClassID) +"_"+ randomNum;

			//EphesoftAPI.importBatchClass(batchClassID, BatchClassName);
			EphesoftAPI EpheAPI = new EphesoftAPI();
			EpheAPI.copyBatchClass(BatchClassName, batchClassID);
			// set the batch class role
			//get the database batch class id
			try {
				String NewBatchClassID = getBatchClassFromBatchClassName(BatchClassName);
				//dbBatchClassID = getBatchClassID(NewBatchClassID);
				//Set the batch class Role;
				setBatchClassRole(NewBatchClassID, UserName);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				logger.error(e.getMessage());
			}
		}
		//Set the Batch Resource 
		try {
			setBatchClassResourceAccess(UserName);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			logger.error(e.getMessage());
		}		
	}


	public static void setBatchClassResourceAccess (String SetRole) throws SQLException 
	{
		Connection dbConnection = null;
		Statement statement = null;
		try
		{
			dbConnection = getDBConnection();
			statement = dbConnection.createStatement();
			String queryString = "INSERT INTO resource_authorizer(creation_date,last_modified,resource,user_role)"+  
					"VALUES(CURDATE(),CURDATE(),'BATCH_INSTANCE_SCREEN', '"+SetRole+"'),"+
					"(CURDATE(),CURDATE(),'BATCH_CLASS_SCREEN', '"+SetRole+"'),"+
					"(CURDATE(),CURDATE(),'FOLDER_MANAGEMENT_SCREEN', '"+SetRole+"'),"+
					"(CURDATE(),CURDATE(),'REPORTING_SCREEN', '"+SetRole+"'),"+
					"(CURDATE(),CURDATE(),'BATCH_LIST_SCREEN', '"+SetRole+"'),"+
					"(CURDATE(),CURDATE(),'REVIEW_VALIDATE_SCREEN', '"+SetRole+"'),"+
					"(CURDATE(),CURDATE(),'WEB_SCANNER_SCREEN', '"+SetRole+"'),"+
					"(CURDATE(),CURDATE(),'UPLOAD_BATCH_SCREEN', '"+SetRole+"');";
			logger.info(queryString);
			statement.executeUpdate(queryString);	
		}          	                	               	

		catch (SQLException e)
		{
			logger.error(e.getMessage());
		}
		finally
		{
			if (statement  != null)
			{
				statement.close();
			}
		}
	}


	public static String getBatchClassFromBatchClassName(String BatchClassName) throws SQLException {
		Statement stmt = null;
		Connection dbConnection = null;
		logger.info("getbatchClassFromBatchClassName");
		try {

			dbConnection = getDBConnection();

			String query = "SELECT id FROM batch_class WHERE batch_class_name = '" + BatchClassName + "';";

			String batchClass = "";
			//Run the Select statement
			stmt = dbConnection.createStatement();
			logger.info(query);

			ResultSet rs = stmt.executeQuery(query);
			while (rs.next()) {
				batchClass = rs.getString(1);
			}
			return batchClass;
		} catch (SQLException e ) {
			logger.error(e.getMessage());
			return null;
		} finally {
			if (stmt != null) { stmt.close(); }
		}
	}

	public static void setBatchClassRole (String BatchclassID, String SetRole) throws SQLException 
	{
		Connection dbConnection = null;
		Statement statement = null;
		logger.info("***SetBatchClassRole***");
		try
		{
			dbConnection = getDBConnection();
			statement = dbConnection.createStatement();

			String queryString = "INSERT INTO batch_class_groups(creation_date,group_name,batch_class_id)  VALUES(CURDATE(),'" + SetRole + "', '"+ BatchclassID +"');";

			logger.info(queryString);
			statement.executeUpdate(queryString);	
		}          	                	               	

		catch (SQLException e)
		{
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		finally
		{
			if (statement  != null)
			{
				statement.close();
			}
		}
	}

	private static Connection getDBConnection() {
		Connection dbConnection = null;
		try {
			Class.forName(SQL_DRIVER);
		} catch (ClassNotFoundException e) {
			logger.error(e.getMessage());
		}
		try {

			String SqlUser = prop.getPropertyValue("SqlUser");
			String SqlPassword = prop.getPropertyValue("SqlPassword");
			dbConnection = DriverManager.getConnection(SQL_CONNECTION_STRING, SqlUser, SqlPassword);
			return dbConnection;
		} catch (SQLException e) {
			logger.error(e.getMessage());
		}
		return dbConnection;
	}
}
