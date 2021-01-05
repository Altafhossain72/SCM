package com.smart.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.smart.model.EmailRequest;
import com.smart.services.EmailService;


@RestController
public class EmailController {

	@Autowired
	private EmailService emailService;
	
	@PostMapping("/sendSuccesEmail")
	public ResponseEntity<?> sendSuccesEmail(@ModelAttribute("emailRequest") EmailRequest emailRequest){
		boolean result = this.emailService.sendEmailWithAttach(emailRequest.getSubject(), emailRequest.getMessage(),emailRequest.getAttFile() ,emailRequest.getTo());
		
		if(result) {
			return ResponseEntity.ok("Email sent successfully...");
		}
		
		else {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body( "Email sent failed");
		}
		
		
	}
}
