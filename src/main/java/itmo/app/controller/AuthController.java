package itmo.app.controller;

import itmo.app.dto.request.LoginRequest;
import itmo.app.dto.request.RegisterRequest;
import itmo.app.dto.response.AuthResponse;
import itmo.app.service.AuthenticationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
	
	@Autowired
	private AuthenticationService authenticationService;
	
	@PostMapping("/register")
	public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
		AuthResponse response = authenticationService.register(request);
		return ResponseEntity.ok(response);
	}
	
	@PostMapping("/login")
	public ResponseEntity<AuthResponse> login(
			@Valid @RequestBody LoginRequest request,
			@RequestParam(defaultValue = "false") boolean isAdminLogin) {
		AuthResponse response = authenticationService.login(request, isAdminLogin);
		return ResponseEntity.ok(response);
	}
}
