package itmo.app.controller;

import itmo.app.controller.services.PasswordUtil;
import itmo.app.controller.services.UserContext;
import itmo.app.controller.services.UserValidationService;
import itmo.app.model.entity.Notification;
import itmo.app.model.entity.User;
import itmo.app.model.repository.NotificationRepository;
import itmo.app.model.repository.UserRepository;
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
	private UserContext userContext;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private NotificationRepository notificationRepository;
	
	@Autowired
	private UserValidationService userValidationService;
	
	@PostMapping("/register")
	public ResponseEntity<String> registerUser(@RequestBody User user, @RequestParam boolean isAdminRequest) {
		long adminCount = userRepository.countByIsAdminTrue();
		
		if (isAdminRequest && adminCount > 0) {
			user.setAdmin(true);
			user.setApprovedAdmin(false);
			
			String hashedPassword = PasswordUtil.hashPassword(user.getPassword());
			user.setPassword(hashedPassword);
			userRepository.save(user);
			
			Notification notification = new Notification();
			notification.setApproved(false);
			notification.setUserEmail(user.getEmail());
			notificationRepository.save(notification);
			
			return ResponseEntity.ok("{\"message\":\"Administration request sent\"}");
		} else if (isAdminRequest && adminCount == 0) {
			user.setAdmin(true);
			user.setApprovedAdmin(true);
			String hashedPassword = PasswordUtil.hashPassword(user.getPassword());
			user.setPassword(hashedPassword);
			userRepository.save(user);
			return ResponseEntity.ok("{\"message\":\"\n" +
					"The first administrator has been successfully registered\"}");
		} else {
			user.setAdmin(false);
			user.setApprovedAdmin(false);
			String hashedPassword = PasswordUtil.hashPassword(user.getPassword());
			user.setPassword(hashedPassword);
			userRepository.save(user);
			return ResponseEntity.ok("{\"message\":\"User successfully registered\"}");
		}
	}
	
	@PostMapping("/login/{isAdminLogin}")
	public ResponseEntity<String> loginUser(@RequestBody User loginUser, @PathVariable boolean isAdminLogin) {
		try {
			userValidationService.validateUserCredentials(loginUser);
		} catch (Exception e) {
			String message = e.getMessage();
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body("{\"message\":\"" + message + "\"}");
		}
		
		Optional<User> userOptional = userRepository.findByEmail(loginUser.getEmail());
		if (userOptional.isPresent()) {
			User user = userOptional.get();
			String hashedPassword = PasswordUtil.hashPassword(loginUser.getPassword());
			if (hashedPassword.equals(user.getPassword())) {
				if (user.isAdmin() && !user.isApprovedAdmin() && isAdminLogin) {
					return ResponseEntity.status(HttpStatus.FORBIDDEN)
							.body("{\"message\":\"Your administration request has not yet been approved\"}");
				} else if (user.isAdmin() && !user.isApprovedAdmin() && !isAdminLogin) {
					return ResponseEntity.ok("{\"message\":\"The user has successfully logged in\"}");
				} else if (user.isAdmin() && user.isApprovedAdmin()) {
					return ResponseEntity.ok("{\"message\":\"The administrator has successfully logged in\"}");
				} else if (!user.isAdmin() && isAdminLogin) {
					return ResponseEntity.status(HttpStatus.FORBIDDEN)
							.body("{\"message\":\"Firstly, you need to register as an admin\"}");
				} else {
					return ResponseEntity.ok("{\"message\":\"The user has successfully logged in\"}");
				}
			} else {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
						.body("{\"message\":\"Wrong password\"}");
			}
		} else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body("{\"message\":\"User not found\"}");
		}
	}

	
	@GetMapping("/check-email")
	public ResponseEntity<Boolean> checkEmail(@RequestParam String email) {
		boolean exists = userRepository.existsByEmail(email);
		return ResponseEntity.ok(!exists);
	}
}
