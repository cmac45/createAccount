package create;
import java.util.Properties;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.apache.log4j.Logger;


public class email {

	private static applicationProperties prop = new applicationProperties();

	final static Logger logger = Logger.getLogger(email.class);
	
	public static void sendEmail (String to, String Subject, String body) {

		// Sender's email ID needs to be mentioned
		String from = prop.getPropertyValue("FromEmail");//change accordingly
		final String username = prop.getPropertyValue("EmailUserName");//change accordingly
		final String password = prop.getPropertyValue("EmailPassword");//change accordingly

		
		// Assuming you are sending email through relay.jangosmtp.net
		String host = prop.getPropertyValue("host");

		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", host);
		props.put("mail.smtp.port", "587");

		// Get the Session object.
		Session session = Session.getInstance(props,
				new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username, password);
			}
		});

		try {
			// Create a default MimeMessage object.
			Message message = new MimeMessage(session);

			// Set From: header field of the header.	        	         	         
			message.setFrom(new InternetAddress(from));

			// Set To: header field of the header.
			//for (int i=0; i < sendto.length ; i++) {
			message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
			//message.setRecipient(Message.RecipientType.TO , InternetAddress.parse("abc@abc.com,abc@def.com,ghi@abc.com"));
			//}
			// Set Subject: header field
			message.setSubject(Subject);


			// Now set the actual message
			String messageText = body;
			// Create the message part

			BodyPart messageBodyPart = new MimeBodyPart();

			// Create a multipar message
			Multipart multipart = new MimeMultipart();               

			//Messge of the body of email
			messageBodyPart.setContent(messageText, "text/html");
			multipart.addBodyPart(messageBodyPart);	        

			// Send the complete message parts
			message.setContent(multipart);

			// Send message
			Transport.send(message);

			logger.info("Sent email successfully....");

		} catch (MessagingException e) {
			throw new RuntimeException(e);
		}
	}

}
