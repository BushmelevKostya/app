package itmo.app.controller;

import itmo.app.model.entity.Notification;
import itmo.app.model.entity.User;
import itmo.app.model.repository.NotificationRepository;
import itmo.app.model.repository.UserRepository;
import org.hibernate.type.descriptor.sql.internal.NamedNativeOrdinalEnumDdlTypeImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "http://localhost:4200")
public class UserController {
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private NotificationRepository notificationRepository;
	
	@PostMapping("/register")
	public ResponseEntity<String> registerUser(@RequestBody User user, @RequestParam boolean isAdminRequest) {
		long adminCount = userRepository.countByIsAdminTrue();
		
		if (isAdminRequest && adminCount > 0) {
			user.setAdmin(true);
			user.setApprovedAdmin(false);
			userRepository.save(user);
			
			Notification notification = new Notification();
			notification.setApproved(false);
			notification.setUserEmail(user.getEmail());
			notificationRepository.save(notification);
			
			return ResponseEntity.accepted().body("{\"message\":\"Administration request sent\"}");
		} else if (isAdminRequest && adminCount == 0) {
			user.setAdmin(true);
			user.setApprovedAdmin(true);
			userRepository.save(user);
			return ResponseEntity.ok("{\"message\":\"\n" +
					"The first administrator has been successfully registered\"}");
		} else {
			user.setAdmin(false);
			user.setApprovedAdmin(false);
			userRepository.save(user);
			return ResponseEntity.ok("{\"message\":\"User successfully registered\"}");
		}
	}
	
	@PostMapping("/login")
	public ResponseEntity<String> loginUser(@RequestBody User loginUser) {
		Optional<User> userOptional = userRepository.findByEmail(loginUser.getEmail());
		
		if (userOptional.isPresent()) {
			User user = userOptional.get();
			if (user.getPassword().equals(loginUser.getPassword())) {
				System.out.println(user.isAdmin());
				System.out.println(user.isApprovedAdmin());
				if (user.isAdmin() && !user.isApprovedAdmin()) {
					return ResponseEntity.accepted().body("{\"message\":\"Your administration request has not yet been approved\"}");
				} else if (user.isAdmin() && user.isApprovedAdmin()) {
					return ResponseEntity.ok("{\"message\":\"The administrator has successfully logged in\"}");
				} else {
					return ResponseEntity.ok("{\"message\":\"The user has successfully logged in\"}");
				}
			} else {
				return ResponseEntity.accepted().body("{\"message\":\"Wrong password\"}");
			}
		} else {
			return ResponseEntity.accepted().body("{\"message\":\"User not found\"}");
		}
	}
}
