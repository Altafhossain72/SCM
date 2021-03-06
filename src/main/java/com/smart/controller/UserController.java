package com.smart.controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.smart.dao.ContactRepository;
import com.smart.dao.UserRepository;
import com.smart.entities.Contact;
import com.smart.entities.User;
import com.smart.helper.Message;
import com.smart.jasperreports.SimpleReportExporter;
import com.smart.jasperreports.SimpleReportFiller;

@Controller
@RequestMapping("/user")
public class UserController {
	@Autowired
	private BCryptPasswordEncoder PasswordEncoder;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private ContactRepository contactRepository;
	
	@Autowired
	SimpleReportFiller simpleReportFiller;
	
	@Autowired
	private ServletContext servletContext;

	@ModelAttribute
	public void sendCommonData(Model m, Principal principal) {
		String userName = principal.getName();
		User user = this.userRepository.getUserByUsername(userName);
		int Rid = user.getId();
		m.addAttribute("user", user);
		m.addAttribute("Rid", Rid);
	}

	@RequestMapping("/index")
	public String userDashboard(Model m) {
		m.addAttribute("title", "User-Dashboard");
		return "normal/user_dashboard";
	}

	@GetMapping("/add-contact")
	public String addContactForm(Model m) {
		m.addAttribute("title", "Add Contacts");
		m.addAttribute("contact", new Contact());
		return "normal/add_contact_form";
	}

	@PostMapping("/process-contact")
	public String saveContact(@Valid @ModelAttribute("contact") Contact contact, BindingResult result,
			@RequestParam("profileImage") MultipartFile file, Model m, Principal p, HttpSession session) {
		try {

			if (result.hasErrors()) {
				System.out.println("something went wrong");
				return "normal/add_contact_form";
			}
			if(file.isEmpty()) {
				System.out.println("file is empty");
				contact.setImage("contact.png");
				m.addAttribute("contact", new Contact());
				String userName = p.getName();
				User user = this.userRepository.getUserByUsername(userName);
				user.getContacts().add(contact);
				contact.setUser(user);
				this.userRepository.save(user);
				session.setAttribute("message", new Message("Contact saved successfully !!", "alert-success"));
			}
			else {
			m.addAttribute("contact", new Contact());
			String userName = p.getName();
			User user = this.userRepository.getUserByUsername(userName);
			contact.setImage(file.getOriginalFilename());
			user.getContacts().add(contact);
			contact.setUser(user);
			File savePath = new ClassPathResource("static/image").getFile();
			Path path = Paths.get(savePath.getAbsolutePath() + File.separator + file.getOriginalFilename());
			Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
			this.userRepository.save(user);
			session.setAttribute("message", new Message("Contact saved successfully !!", "alert-success"));
			}

		} catch (Exception e) { 
			e.printStackTrace();
			m.addAttribute("contact", contact);
			session.setAttribute("message", new Message("Something went wrong !! " + e.getMessage(), "alert-danger"));
		}
		return "normal/add_contact_form";
	}
	//show contact
	//contact per page 5[n]
	//current page - page
	@GetMapping("/show-contacts/{page}")
	public String showContacts(@PathVariable("page") Integer page,Model m,Principal p) {
		m.addAttribute("title", "Show User Contacts");
		String userName = p.getName();
		User user = this.userRepository.getUserByUsername(userName);
		Pageable pageable = PageRequest.of(page, 5);
		Page<Contact> contacts = this.contactRepository.getContactsByUserId(user.getId(), pageable);
		m.addAttribute("contacts", contacts);
		m.addAttribute("currentPage", page);
		m.addAttribute("totalPages", contacts.getTotalPages());
		return "normal/show_contacts";
	}
	
	//showing details of user contacts
	@GetMapping("/{cid}/contact")
	public String showContactDetails(@PathVariable("cid") Integer cid,Model m,Principal p) {
		Optional<Contact> optional = this.contactRepository.findById(cid);
		Contact contact = optional.get();
		String userName = p.getName();
		User user = this.userRepository.getUserByUsername(userName);
		if(user.getId() == contact.getUser().getId()) {
		m.addAttribute("contact", contact);
		}
		m.addAttribute("title", "Contact Details");
		return "normal/contact_details";
	}
	
	@GetMapping("/delete/{cId}")
	public String deleteContact(@PathVariable("cId") Integer cId, Principal p) {
		
		try {
		
		Contact contact = this.contactRepository.findById(cId).get();
		String userName = p.getName();
		User user = this.userRepository.getUserByUsername(userName);
		if(user.getId()== contact.getUser().getId()) {
			/*
			 * contact.setUser(null); this.contactRepository.delete(contact);
			 */
			
			//delete old image file
			File file2 = new ClassPathResource("static/image").getFile();
			new File(file2, contact.getImage()).delete();
			
			user.getContacts().remove(contact);
			this.userRepository.save(user);
		}
		}catch (Exception e) {
			e.printStackTrace();
		}
		return "redirect:/user/show-contacts/0";
	}
	//handler to open update form
	@PostMapping("/update-contact/{cId}")
	public String updateContact(@PathVariable("cId") Integer cId,Model m) {
		Contact contact = this.contactRepository.findById(cId).get();
		m.addAttribute("contact", contact);
		m.addAttribute("title", "SCM-UpdateContact");
		return "normal/update-form";
	}
	//handler to update data of contact form
	@PostMapping("/update-process")
	public String updateFormData(@Valid @ModelAttribute("contact") Contact contact, BindingResult result,
			@RequestParam("profileImage") MultipartFile file, Model m, Principal p, HttpSession session) {
		try {

			if (result.hasErrors()) {
				System.out.println("something went wrong");
				return "normal/update-form";
			}
			Contact oldContact = this.contactRepository.findById(contact.getcId()).get();
			String userName = p.getName();
			User user = this.userRepository.getUserByUsername(userName);
			
			if(!file.isEmpty()) {
				contact.setUser(user);
				contact.setImage(file.getOriginalFilename());
				//delete old image file
				File file2 = new ClassPathResource("static/image").getFile();
				new File(file2, oldContact.getImage()).delete();
				//place new file
				File savePath = new ClassPathResource("static/image").getFile();
				Path path = Paths.get(savePath.getAbsolutePath() + File.separator + file.getOriginalFilename());
				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
				
				this.contactRepository.save(contact);
				session.setAttribute("message", new Message("Contact is updated successfully !! ", "alert-success"));
			}
			else {
				contact.setImage(oldContact.getImage());
				contact.setUser(user);
				this.contactRepository.save(contact);
				session.setAttribute("message", new Message("Contact is updated successfully !! ", "alert-success"));
			}

		} catch (Exception e) { session.setAttribute("message", new Message("Something went wrong !! " + e.getMessage(), "alert-danger"));
			e.printStackTrace();
			m.addAttribute("contact", contact);
			
		}
		return "redirect:/user/"+contact.getcId()+"/contact";
	}
	
	@GetMapping("/profile")
	public String profile(Model m, Principal p) {
		m.addAttribute("user", this.userRepository.getUserByUsername(p.getName()));
		m.addAttribute("title", "User-Profile");
		return "normal/profile";
	}
	
	@GetMapping("/update-user")
	public String updateUser(Model m ,Principal p) {
		m.addAttribute("user", this.userRepository.getUserByUsername(p.getName()));
		m.addAttribute("title", "Update-UserProfile");
		return "normal/update_user";
	}
	
	@PostMapping("/save-changes")
	public String saveChanges(@Valid @ModelAttribute("user") User user,BindingResult result,@RequestParam("newProfile") MultipartFile file,@RequestParam(value = "agreement", defaultValue = "false") boolean agreement,Model m, HttpSession session, Principal p) {
		try {
			if(!agreement) {
				System.out.println("You have not agreed terms and condition");
				throw new Exception("You have not agreed terms and condition");

			}
			if(result.hasErrors()) {
				m.addAttribute("user", user);
				return "normal/update_user";
			}
			User oldUser = this.userRepository.getUserByUsername(p.getName());
			if(!file.isEmpty()) {
				user.setImageUrl(file.getOriginalFilename());
				//delete old image file
				File file2 = new ClassPathResource("static/image").getFile();
				new File(file2, oldUser.getImageUrl()).delete();
				//place new file
				File savePath = new ClassPathResource("static/image").getFile();
				Path path = Paths.get(savePath.getAbsolutePath() + File.separator + file.getOriginalFilename());
				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
				this.userRepository.save(user);
				session.setAttribute("message", new Message("Successfully changed !! ", "alert-success"));
			}else {
				user.setImageUrl(oldUser.getImageUrl());
				this.userRepository.save(user);
				session.setAttribute("message", new Message("Successfully changed !! ", "alert-success"));
			}
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			m.addAttribute("user", user);
			System.out.println("Something went wrong!!");
			session.setAttribute("message", new Message("Something went wrong !! "+e.getMessage(), "alert-danger"));
				
		}
		return "redirect:/user/update-user";
	}
	
	@PostMapping("/change-password")
	public String changePassword(@RequestParam("oldPass") String oldPass, @RequestParam("newPass") String newPass, Principal p, HttpSession session) {
//		System.out.println("Old: "+oldPass);
//		System.out.println("New: "+newPass);
		User user = this.userRepository.getUserByUsername(p.getName());
		if(this.PasswordEncoder.matches(oldPass, user.getPassword())) {
//			change the password
			user.setPassword(this.PasswordEncoder.encode(newPass));
			this.userRepository.save(user);
			session.setAttribute("message", new Message("password has been succesfully changed..", "alert-success"));
		}
		else {
//			error
			session.setAttribute("message", new Message("password does not match with your old-password", "alert-danger"));
			return "redirect:/user/update-user";
		}
		return "redirect:/user/index";
	}
	
	
	
	@GetMapping("/sendmail")
	public String sendemail(Model m) {
		m.addAttribute("title", "User-Dashboard");
		return "normal/email";
	
	}
	
	
	@GetMapping("/sendsms")
	public String sendsms(Model m) {
		m.addAttribute("title", "User-Dashboard");
		return "normal/sms";
	
	}
	
	
	@GetMapping("/sendChat")
	public String sendchat(Model m) {
		m.addAttribute("title", "User-Dashboard");
		return "normal/chat";
	
	}
	
	
	
	//report controller
	
	@GetMapping("/gpdf/{guserId}")
	public String pdfWithParameter(@PathVariable int guserId, HttpServletResponse response,Principal p) {
		response.setContentType("application/pdf");
		
		try {
			
			SimpleReportExporter simpleExporter = new SimpleReportExporter();

			simpleReportFiller.setReportFileName("contactsReport.jrxml");
			simpleReportFiller.compileReport();

			Map<String, Object> parameters = new HashMap<>();
			parameters.put("guserId", guserId);
			simpleReportFiller.setParameters(parameters);
			simpleReportFiller.fillReport();
			simpleExporter.setJasperPrint(simpleReportFiller.getJasperPrint());

			simpleExporter.exportToPdf("contactsReport.pdf", "altaf");

			File file = new File("src/main/resources/reports/contactsReport.pdf");
			response.setHeader("Content-Type", servletContext.getMimeType(file.getName()));
			response.setHeader("Content-Length", String.valueOf(file.length()));
			response.setHeader("Content-Disposition", "inline; filename=\"contactsReport.pdf\"");
			Files.copy(file.toPath(), response.getOutputStream());
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	
	
	
	
}
