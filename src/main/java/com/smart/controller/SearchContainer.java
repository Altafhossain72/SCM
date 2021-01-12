package com.smart.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.smart.dao.ContactRepository;
import com.smart.dao.UserRepository;
import com.smart.entities.Contact;
import com.smart.entities.User;

@RestController
public class SearchContainer {
	@Autowired
	private UserRepository UserRepository;
	@Autowired
	private ContactRepository contactRepository;
	
	@GetMapping("/search/{query}")
	public ResponseEntity<?> search(@PathVariable("query") String query, Principal p){
		String userName = p.getName();
		User user = this.UserRepository.getUserByUsername(userName);
		List<Contact> contacts = this.contactRepository.findByNameContainingAndUser(query, user);
		return ResponseEntity.ok(contacts);
	}
	
	
	 @RequestMapping(value = "/user/autocomplete")
	    @ResponseBody
	    public List<String> autoName(@RequestParam(value = "term", required = false, defaultValue = "")String term, Principal p){
		 String userName = p.getName();
		 User user = this.UserRepository.getUserByUsername(userName);
		 List<String> email = this.contactRepository.getEmail(term, user);
		 System.out.println(email);
	        return email;
	    }

	 @RequestMapping(value = "/user/autocomplete2")
	    @ResponseBody
	    public List<String> autoNum(@RequestParam(value = "term", required = false, defaultValue = "")String term, Principal p){
		 String userName = p.getName();
		 User user = this.UserRepository.getUserByUsername(userName);
		 List<String> phone = this.contactRepository.getPhone(term, user);
		 System.out.println(phone);
	        return phone;
	    }
	
}
