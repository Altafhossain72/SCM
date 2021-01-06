package com.smart.services;

import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.springframework.stereotype.Service;

@Service
public class EmailService {


	
	
	public boolean sendEmail(String subject, String message, String to) {
		boolean f = false;
//	        rest of the code
		String from = "j2eeround44nclc@gmail.com";
//	        variable for gmail
		String host = "smtp.gmail.com";
//	        getting system properties
		Properties properties = System.getProperties();

//	        setting important information in properties
//	        host setup
		properties.put("mail.smtp.host", host);
		properties.put("mail.smtp.port", "465");
		properties.put("mail.smtp.ssl.enable", "true");
		properties.put("mail.smtp.auth", "true");

//	        Step 1: to get session object
		Session session = Session.getInstance(properties, new Authenticator() {
			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication("j2eeround44nclc@gmail.com", "babukhaiso");
			}
		});
		session.setDebug(true);
//	        Step 2 : compose the message
		MimeMessage mimeMessage = new MimeMessage(session);

		try {
			// from email id
			mimeMessage.setFrom(from);
//	        set reciepent id
			mimeMessage.setRecipient(Message.RecipientType.TO, new InternetAddress(to));
//	            setting subject
			mimeMessage.setSubject(subject);
//	            set text to message
//	            mimeMessage.setText(message);
			mimeMessage.setContent(message, "text/html");
			// mimeMessage.
//	            Step 3: sending message using Transport class
			Transport.send(mimeMessage);
			System.out.println("Sent successfully............");
			f = true;
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("something went wrong...........");
		}

		return f;
	}

	
	public boolean sendEmailWithAttach(String subject, String message,String filename, String to) {
		boolean f = false;
//	        rest of the code
		String from = "j2eeround44nclc@gmail.com";
//	        variable for gmail
		String host = "smtp.gmail.com";
//	        getting system properties
		Properties properties = System.getProperties();

//	        setting important information in properties
//	        host setup
		properties.put("mail.smtp.host", host);
		properties.put("mail.smtp.port", "465");
		properties.put("mail.smtp.ssl.enable", "true");
		properties.put("mail.smtp.auth", "true");

//	        Step 1: to get session object
		Session session = Session.getInstance(properties, new Authenticator() {
			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication("j2eeround44nclc@gmail.com", "babukhaiso");
			}
		});
		session.setDebug(true);
//	        Step 2 : compose the message
		MimeMessage mimeMessage = new MimeMessage(session);

		try {
			// from email id
			mimeMessage.setFrom(from);
//	        set reciepent id
			mimeMessage.setRecipient(Message.RecipientType.TO, new InternetAddress(to));
//	            setting subject
			mimeMessage.setSubject(subject);
//	            set text to message
//	            mimeMessage.setText(message);

			//coding for attachment start
			BodyPart messageBodyPart = new MimeBodyPart();
			messageBodyPart.setContent(message, "text/html");
//	        messageBodyPart.setText("Here's the file");
	        Multipart multipart = new MimeMultipart();
	        multipart.addBodyPart(messageBodyPart);
	        messageBodyPart = new MimeBodyPart();
	        filename= "E:/anchor1.png";
	        DataSource source = new FileDataSource(filename);
	        messageBodyPart.setDataHandler(new DataHandler(source));
	        messageBodyPart.setFileName(filename);
	        multipart.addBodyPart(messageBodyPart);
	        mimeMessage.setContent(multipart);
			
	    	//coding for attachment end
			
	
			
			
			// mimeMessage.
//	            Step 3: sending message using Transport class
			Transport.send(mimeMessage);
			System.out.println("Sent successfully............");
			f = true;
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("something went wrong...........");
		}

		return f;
	}

	
	
	
	

}
