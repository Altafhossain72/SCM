package com.smart.controller;


import java.io.IOException;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.smart.dao.UserRepository;
import com.smart.helper.Message;
import com.smart.model.EmailRequest;
import com.smart.services.EmailService;


@Controller
public class EmailController {

	@Autowired
	private EmailService emailService;
	

	@PostMapping("/sendSuccesEmail")
	public String sendSuccesEmail(@ModelAttribute("emailRequest") EmailRequest emailRequest,@RequestParam("attchFile") MultipartFile file, HttpSession session) throws IOException{
		String absolutePath = this.emailService.convert(file).getAbsolutePath();
		
		System.out.println("attached file path "+absolutePath);
		boolean result = this.emailService.sendEmailWithAttach(emailRequest.getSubject(), emailRequest.getMessage(),absolutePath ,emailRequest.getTo());
		
		if(result) {
			session.setAttribute("message", new Message("Email sent Successfully  !! ", "alert-success"));
			
		}
		
		else {
			session.setAttribute("message", new Message("Email sent failed !! ", "alert-danger"));
		}
		return "redirect:/user/sendmail";
		
	}
	
	
	
	//sms sending
	@PostMapping("/sendSuccesSms")
	public String sendSuccesSms(HttpSession session){

			session.setAttribute("message", new Message("SMS sent Successfully  !! ", "alert-success"));
			
			return "redirect:/user/sendsms";
		
	}
	
	
	
}
