package itmo.app.controller;

import itmo.app.model.entity.User;
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
	private UserRepository userRepository;
	
	@PostMapping("/register")
	public ResponseEntity<String> registerUser(@RequestBody User user, @RequestParam boolean isAdminRequest) {
		long adminCount = userRepository.countByIsAdminTrue();
		
		if (isAdminRequest && adminCount > 0) {
			user.setAdmin(true);
			user.setApprovedAdmin(false);
			userRepository.save(user);
			return ResponseEntity.accepted().body("{\"message\":\"������ �� ����������������� ���������\"}");
		} else if (isAdminRequest && adminCount == 0) {
			user.setAdmin(true);
			user.setApprovedAdmin(true);
			userRepository.save(user);
			return ResponseEntity.ok("{\"message\":\"������ ������������� ������� ���������������\"}");
		} else {
			user.setAdmin(false);
			user.setApprovedAdmin(false);
			userRepository.save(user);
			return ResponseEntity.ok("{\"message\":\"������������ ������� ���������������\"}");
		}
	}
	
	@PostMapping("/login")
	public ResponseEntity<String> loginUser(@RequestBody User loginUser) {
		Optional<User> userOptional = userRepository.findByEmail(loginUser.getEmail());
		
		if (userOptional.isPresent()) {
			User user = userOptional.get();
			if (user.getPassword().equals(loginUser.getPassword())) {
				if (user.isAdmin() && !user.isApprovedAdmin()) {
					return ResponseEntity.accepted().body("{\"message\":\"��� ������ �� ����������������� ��� �� �������\"}");
				} else if (user.isAdmin() && user.isApprovedAdmin()) {
					return ResponseEntity.ok("{\"message\":\"������������� ������� �����\"}");
				} else {
					return ResponseEntity.ok("{\"message\":\"������������ ������� �����\"}");
				}
			} else {
				return ResponseEntity.accepted().body("{\"message\":\"�������� ������\"}");
			}
		} else {
			return ResponseEntity.accepted().body("{\"message\":\"������������ �� ������\"}");
		}
	}
}
