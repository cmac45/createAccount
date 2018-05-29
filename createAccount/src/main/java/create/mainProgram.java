package create;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.DriverManager;
public class mainProgram {
	private static applicationProperties prop = new applicationProperties();
	
	private static String SQL_DRIVER = "net.sourceforge.jtds.jdbc.Driver";
	private static String DB_NAME = prop.getPropertyValue("DataBaseName");
	private static String SQL_CONNECTION_STRING = "jdbc:jtds:sqlserver://localhost:1433;databaseName=" + DB_NAME;


	public static void main(String[] args) {
		// TODO Auto-generated method stub

		
		//Create User Group LDAP
		NewUser.createNewUser("mglaves-demo@box.com", "Ephe$0ft");
		
		
		//create.email.sendEmail("Chris.macwilliams@ephesoft.com", "Welcome to the Ephesoft Demo Server", "<p>Your User name is  </p> <p>Your password is</p>");
	
	}
	
	public static void testSql() throws SQLException {
		Statement stmt = null;
		Connection dbConnection = null;
		
		try {

			dbConnection = getDBConnection();

			String query = "SELECT id FROM batch_class WHERE batch_class_name = 'InvoiceProcessing_73904';";

			String batchClass = "";
			//Run the Select statement
			stmt = dbConnection.createStatement();
			System.out.println(query);

			ResultSet rs = stmt.executeQuery(query);
			while (rs.next()) {
				batchClass = rs.getString(1);
				System.out.println(batchClass);
			}
		
		} catch (SQLException e ) {
		
		} finally {
			if (stmt != null) { stmt.close(); }
		}
	}

	private static Connection getDBConnection() {
		Connection dbConnection = null;
		try {
			Class.forName(SQL_DRIVER);
		} catch (ClassNotFoundException e) {
			System.out.println(e.getMessage());
		}
		try {

			String SqlUser = prop.getPropertyValue("SqlUser");
			String SqlPassword = prop.getPropertyValue("SqlPassword");
			dbConnection = DriverManager.getConnection(SQL_CONNECTION_STRING, SqlUser, SqlPassword);
			return dbConnection;
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		return dbConnection;
	}
}

