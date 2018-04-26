package create;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

public class EphesoftAPI {

	private static applicationProperties prop = new applicationProperties();
	final static Logger logger = Logger.getLogger(EphesoftAPI.class);

	public void copyBatchClass(String NewBatchClassName, String RequestingBatchClassCopy){
		logger.info("Starting Copy batch class " + NewBatchClassName + " requesting Batch Class copy " + RequestingBatchClassCopy);
		//create instance of XML file
		String pathtoUNC = prop.getPropertyValue("pathToWatchDirectory") + "\\" + NewBatchClassName;
		String FilePatTOXMLParms = createBatchClassXML(NewBatchClassName,NewBatchClassName,pathtoUNC,RequestingBatchClassCopy);


		HttpClient client = new HttpClient(); 
		String ServerName = prop.getPropertyValue("serverName");
		String port = prop.getPropertyValue("serverPort");
		String userName = prop.getPropertyValue("userName");
		String password = prop.getPropertyValue("password");

		Credentials defaultcreds = new UsernamePasswordCredentials(userName, password);
		client.getState().setCredentials(new AuthScope(ServerName, Integer.parseInt(port)), defaultcreds);
		client.getParams().setAuthenticationPreemptive(true);

		String url = "http://" + ServerName + ":" + port +"/dcma/rest/batchClass/copyBatchClass";
		PostMethod mPost = new PostMethod(url);
		// Adding Input XML file for processing

		File file = new File(FilePatTOXMLParms); 
		Part[] parts = new Part[1]; 
		try { 
			parts[0] = new FilePart(file.getName(), file); 
			MultipartRequestEntity entity = new MultipartRequestEntity(parts, mPost.getParams());
			mPost.setRequestEntity(entity); 
			int statusCode = client.executeMethod(mPost);

			if (statusCode == 200) { 
				logger.info("Batch class coppy seccessfuly");
				String responseBody = mPost.getResponseBodyAsString();
				// Generating result as responseBody.
				logger.info(statusCode + "---- Response body ----" + responseBody);
			} 
			else if (statusCode == 403) { 
				logger.error("Invalid username/password."); } 
			else { logger.error(mPost.getResponseBodyAsString());
			}
		} 
		catch (FileNotFoundException e) { 
			logger.error("File not found for processing Copy Batch class API.");
		} 
		catch (HttpException e) { 
			e.printStackTrace();
			logger.error(e.getMessage());
		}
		catch (IOException e) { e.printStackTrace();
		} 
		finally { 
			if (mPost != null) { 
				mPost.releaseConnection();
				logger.info("Rest API Connection Released");
			}
		}
		//Delete temp XML FILE 
		deletexmlFile(FilePatTOXMLParms);
	}

	public void autoApproveWebservice(String token){
		logger.info("Starting PreApprove  " + token);
		//create instance of XML file
		HttpClient client = new HttpClient(); 
		String ServerName = prop.getPropertyValue("serverName");
		String port = prop.getPropertyValue("serverPort"); 

		
		String url = "http://" + ServerName + ":" + port +"/demoEphesoftOnDemand/NewAccountServlet?token="+ token +"&approved=true";
		GetMethod getMethod = new GetMethod(url);
		// Adding Input XML file for processing 
			int statusCode;
			try {
				statusCode = client.executeMethod(getMethod);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		       try {
		           statusCode = client.executeMethod(getMethod);
		           if (statusCode == 200) {
		               System.out.println("Web service executed successfully.");
		               String responseBody = getMethod.getResponseBodyAsString();
		               System.out.println(statusCode + " *** " + responseBody);
		           } else if (statusCode == 403) {
		               System.out.println("Invalid username/password.");
		           } else {
		               System.out.println(getMethod.getResponseBodyAsString());
		           }
		           } catch (HttpException e) {
		               e.printStackTrace();
		           } catch (IOException e) {
		               e.printStackTrace();
		           } finally {
		               if (getMethod != null) {
		               getMethod.releaseConnection();
		           }
		       }
		   }

	
	
	public static boolean uploadBatch(String filePath, String batchClassID, String batchName) {
		logger.info("***Starting Batch Upload*** File" + filePath + " For batch class " + batchClassID +" "+ batchName);
		HttpClient client = new HttpClient();
		String ServerName = prop.getPropertyValue("serverName");
		String port = prop.getPropertyValue("serverPort");
		String userName = prop.getPropertyValue("userName");
		String password = prop.getPropertyValue("password");

		Credentials defaultcreds = new UsernamePasswordCredentials(userName, password);
		client.getState().setCredentials(new AuthScope(ServerName, Integer.parseInt(port)), defaultcreds);
		client.getParams().setAuthenticationPreemptive(true);

		String url = "http://"+ ServerName +":" + port + "/dcma/rest/uploadBatch/"+ batchClassID +"/" + batchName;
		PostMethod mPost = new PostMethod(url); 
		// adding image file for processing
		File file1 = new File(filePath);
		Part[] parts = new Part[1];
		try {
			parts[0] = new FilePart(file1.getName(), file1);
			MultipartRequestEntity entity = new MultipartRequestEntity(parts, mPost.getParams());

			mPost.setRequestEntity(entity);
			int statusCode = client.executeMethod(mPost);
			String responseBody = mPost.getResponseBodyAsString();
			// Generating result as responseBody.
			System.out.println(statusCode + "***" + responseBody);
			if (statusCode == 200) {    	  
				logger.info("Web service executed successfully.");
				return true;
			} else if (statusCode == 403) {
				logger.error("Invalid username/password.");
				return false;
			} else {
				logger.error(mPost.getResponseBodyAsString());
			}
		} catch (FileNotFoundException e) {
			logger.error("File not found for processing.");
		} catch (HttpException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (mPost != null) {
				mPost.releaseConnection();            	  
			}
		}
		return false;
	}


	private static void deletexmlFile(String FilePatTOXMLParms){
		try{	    		
			File xmlfile = new File(FilePatTOXMLParms);
			xmlfile.setWritable(true);
			if(xmlfile.delete()){
				logger.info(xmlfile.getName() + " is deleted!");
			}else{
				logger.error("Delete operation is failed.");
			}    	   
		}catch(Exception e){	    		
			e.printStackTrace();   		
		}
	}

	public String createBatchClassXML(String batchClassName, String batchClassDescription, String batchUNCFolder, String copyBatchClassIdentifier) {
		logger.info("Starting Creating BatchCLass Copy XML");
		applicationProperties prop = new applicationProperties();
		String xmlFilePath = prop.getPropertyValue("pathToWatchDirectory")+ "\\"+ batchClassName +".xml";

		//File file = new File("/eden/src/com/EphesoftAPI/copyBatchClass.xml");
		//String absolutePath = file.getAbsolutePath();

		try
		{
			SAXBuilder sb = new SAXBuilder();
			//Document doc = sb.build("C:\\Users\\Chris Mac\\workspace\\eden\\src\\com\\EphesoftAPI\\copyBatchClass.xml");

			Document doc = sb.build(this.getClass().getResourceAsStream("copyBatchClass.xml"));

			Element root = doc.getRootElement();
			Element params = root.getChild("Params");
			List<Element> paramList = params.getChildren("Param");

			for (Element currentParm : paramList) {
				String pName = currentParm.getChildText("Name");
				Element pValue = currentParm.getChild("Value");

				if (pName.equalsIgnoreCase("batchClassName")){
					pValue.setText(batchClassName);
				}
				if (pName.equalsIgnoreCase("batchClassDescription")){
					pValue.setText(batchClassDescription);
				}
				if (pName.equalsIgnoreCase("batchUNCFolder")){
					pValue.setText(batchUNCFolder);
				}
				if (pName.equalsIgnoreCase("copyBatchClassIdentifier")){
					pValue.setText(copyBatchClassIdentifier);
				}
			}

			// new XMLOutputter().output(doc, System.out);
			XMLOutputter xmlOutput = new XMLOutputter();

			// display nice nice
			xmlOutput.setFormat(Format.getPrettyFormat());
			FileWriter FW = new FileWriter(xmlFilePath);
			xmlOutput.output(doc, FW);
			FW.close();
			logger.info("Copy xml created " + xmlFilePath);
			return xmlFilePath;

		}	
		catch (Exception x)
		{
			logger.error(x.getMessage());
			return null;
		}		
	}

}
