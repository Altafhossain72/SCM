package com.smart.controller;


import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.smart.dao.UserRepository;
import com.smart.helper.Message;
import com.smart.model.EmailRequest;
import com.smart.services.EmailService;


@Controller
public class EmailController {

	@Autowired
	private EmailService emailService;
	

	@PostMapping("/sendSuccesEmail")
	public String sendSuccesEmail(@ModelAttribute("emailRequest") EmailRequest emailRequest, HttpSession session){
		boolean result = this.emailService.sendEmailWithAttach(emailRequest.getSubject(), emailRequest.getMessage(),emailRequest.getAttFile() ,emailRequest.getTo());
		
		if(result) {
			session.setAttribute("message", new Message("Email sent Successfully  !! ", "alert-success"));
			
		}
		
		else {
			session.setAttribute("message", new Message("Email sent failed !! ", "alert-danger"));
		}
		return "redirect:/user/sendmail";
		
	}
}
