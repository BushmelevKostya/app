package itmo.app.controller;

import itmo.app.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {
	
	@Autowired
	private UserService userService;
	
	@GetMapping("/check-email")
	public ResponseEntity<Boolean> checkEmail(@RequestParam String email) {
		boolean available = userService.isEmailAvailable(email);
		return ResponseEntity.ok(available);
	}
}
