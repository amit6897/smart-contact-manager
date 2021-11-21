package com.smart.controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.smart.entities.Contact;
import com.smart.entities.User;
import com.smart.helper.Message;
import com.smart.repository.ContactRepository;
import com.smart.repository.UserRepository;

@Controller
@RequestMapping("/user")
public class UserController {
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private ContactRepository contactRepository;
	
	// method for adding common data to response
	@ModelAttribute
	public void addCommonData(Model m, Principal principal) {
		String userName = principal.getName();
		System.out.println("USERNAME "+userName);
		
		// get the user using userName(Email)
		User user = userRepository.getUserByUserName(userName);
		System.out.println("USER "+user);
		
		m.addAttribute("user", user);
	}
	
	// to access this user DashBoard we will fire up this URL -> localhost:8080/user/index
	
	// dashboard home
	@RequestMapping("/index")
	public String dashboard(Model model, Principal principal) {
		model.addAttribute("title", "User Dashboard");
		return "normal/user_dashboard";	
	}
	
	// open add form handler
	@GetMapping("/add-contact")
	public String openAddContactForm(Model model) {
		model.addAttribute("title", "Add Contact");
		model.addAttribute("contact", new Contact());
		return "normal/add_contact_form";
	}
	
	// processing add contact form
	@PostMapping("/process-contact")
	public String processContact(
			@ModelAttribute Contact contact, 
			@RequestParam("profileImage") MultipartFile file, 
			Principal principal, HttpSession session) {
		try {
			String name = principal.getName();
			User user = this.userRepository.getUserByUserName(name);
			
			// processing and uploading file...
			if(file.isEmpty()) 
			{
				// if file is empty then try our message
				System.out.println("File is empty");
				
			} else {
				// file the file to folder and update the name to contact
				contact.setImage(file.getOriginalFilename());
				
				File saveFile = new ClassPathResource("static/img").getFile();
				
				Path path = Paths.get(saveFile.getAbsolutePath()+File.separator+file.getOriginalFilename());
				
				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
				
				System.out.println("Image is uploaded");
			}
			
			user.getContacts().add(contact);	// user.getContacts jb krenge to list aa jayegi .add kr denge to us list m contact add ho jayega
			
			contact.setUser(user);	// contact ko user denge
			
			this.userRepository.save(user);		
			System.out.println("DATA"+contact);		
			System.out.println("Added to database");
			
			// message success .....
			session.setAttribute("message", new Message("Your contact has been added !!! Add more..", "sucess"));
		} catch(Exception e) {
			System.out.println("ERROR"+e.getMessage());
			e.printStackTrace();
			
			// message error
			session.setAttribute("message", new Message("Something went wrong !! Try again..", "danger"));
		}
		return "normal/add_contact_form";
	}
	
	// show contacts handler
	@GetMapping("/show-contacts")
	public String showContacts(Model m) {		// jb commented tarika use krenge tb model k sath Principle lga denge
		m.addAttribute("title", "Show User Contacts");
		// contact ki list ko behjni h
		/*
		String userName = principal.getName();
		User user = this.userRepository.getUserByUserName(userName);
		user.getContacts();
		*/
		
		return "normal/show_contacts";
	}
}
