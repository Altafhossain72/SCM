package com.smart.model;

public class EmailRequest {
   private String to;
   private String subject;
   private String message;
   private String attFile;
   
   
   
public EmailRequest() {
	super();
	// TODO Auto-generated constructor stub
}




public EmailRequest(String to, String subject, String message, String attFile) {
	super();
	this.to = to;
	this.subject = subject;
	this.message = message;
	this.attFile = attFile;
}




public String getTo() {
	return to;
}




public void setTo(String to) {
	this.to = to;
}




public String getSubject() {
	return subject;
}




public void setSubject(String subject) {
	this.subject = subject;
}




public String getMessage() {
	return message;
}




public void setMessage(String message) {
	this.message = message;
}




public String getAttFile() {
	return attFile;
}




public void setAttFile(String attFile) {
	this.attFile = attFile;
}




@Override
public String toString() {
	return "EmailRequest [to=" + to + ", subject=" + subject + ", message=" + message + ", attFile=" + attFile + "]";
}








	
}
