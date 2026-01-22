package itmo.app.service;

import itmo.app.dto.request.LoginRequest;
import itmo.app.dto.request.RegisterRequest;
import itmo.app.dto.response.AuthResponse;
import itmo.app.exception.BusinessException;
import itmo.app.exception.UnauthorizedException;
import itmo.app.model.entity.Notification;
import itmo.app.model.entity.User;
import itmo.app.model.repository.NotificationRepository;
import itmo.app.model.repository.UserRepository;
import itmo.app.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthenticationService {
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private NotificationRepository notificationRepository;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Autowired
	private AuthenticationManager authenticationManager;
	
	@Autowired
	private JwtTokenProvider jwtTokenProvider;
	
	@Transactional
	public AuthResponse register(RegisterRequest request) {
		// Check if email already exists
		if (userRepository.existsByEmail(request.getEmail())) {
			throw new BusinessException("Email already in use");
		}
		
		User user = new User();
		user.setEmail(request.getEmail());
		user.setPassword(passwordEncoder.encode(request.getPassword()));
		
		long adminCount = userRepository.countByIsAdminTrue();
		
		if (request.isAdminRequest() && adminCount > 0) {
			// Admin request - needs approval
			user.setAdmin(true);
			user.setApprovedAdmin(false);
			userRepository.save(user);
			
			// Create notification for existing admin
			Notification notification = new Notification();
			notification.setApproved(false);
			notification.setUserEmail(user.getEmail());
			notificationRepository.save(notification);
			
			throw new BusinessException("Administration request sent. Waiting for approval");
		} else if (request.isAdminRequest() && adminCount == 0) {
			// First admin - auto approve
			user.setAdmin(true);
			user.setApprovedAdmin(true);
		} else {
			// Regular user
			user.setAdmin(false);
			user.setApprovedAdmin(false);
		}
		
		userRepository.save(user);
		
		// Generate JWT token
		Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
		);
		
		String jwt = jwtTokenProvider.generateToken(authentication);
		
		return new AuthResponse(jwt, user.getEmail(), user.isAdmin(), user.isApprovedAdmin());
	}
	
	@Transactional(readOnly = true)
	public AuthResponse login(LoginRequest request, boolean isAdminLogin) {
		try {
			Authentication authentication = authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
			);
			
			User user = (User) authentication.getPrincipal();
			
			// Validate admin login requirements
			if (isAdminLogin) {
				if (!user.isAdmin()) {
					throw new UnauthorizedException("You need to register as an admin first");
				}
				if (!user.isApprovedAdmin()) {
					throw new UnauthorizedException("Your administration request has not yet been approved");
				}
			}
			
			String jwt = jwtTokenProvider.generateToken(authentication);
			
			return new AuthResponse(jwt, user.getEmail(), user.isAdmin(), user.isApprovedAdmin());
		} catch (AuthenticationException e) {
			throw new UnauthorizedException("Invalid email or password");
		}
	}
}
