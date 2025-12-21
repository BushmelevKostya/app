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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {
	
	@Mock
	private UserRepository userRepository;
	
	@Mock
	private NotificationRepository notificationRepository;
	
	@Mock
	private PasswordEncoder passwordEncoder;
	
	@Mock
	private AuthenticationManager authenticationManager;
	
	@Mock
	private JwtTokenProvider jwtTokenProvider;
	
	@Mock
	private Authentication authentication;
	
	@InjectMocks
	private AuthenticationService authenticationService;
	
	private RegisterRequest registerRequest;
	private LoginRequest loginRequest;
	private User testUser;
	
	@BeforeEach
	void setUp() {
		registerRequest = new RegisterRequest();
		registerRequest.setEmail("test@example.com");
		registerRequest.setPassword("password123");
		registerRequest.setAdminRequest(false);
		
		loginRequest = new LoginRequest();
		loginRequest.setEmail("test@example.com");
		loginRequest.setPassword("password123");
		
		testUser = new User();
		testUser.setId(1L);
		testUser.setEmail("test@example.com");
		testUser.setPassword("encodedPassword");
		testUser.setAdmin(false);
		testUser.setApprovedAdmin(false);
	}
	
	@Test
	void register_ShouldCreateRegularUser_WhenNotAdminRequest() {
		// Arrange
		when(userRepository.existsByEmail(anyString())).thenReturn(false);
		when(userRepository.countByIsAdminTrue()).thenReturn(0L);
		when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
		when(userRepository.save(any(User.class))).thenReturn(testUser);
		when(authenticationManager.authenticate(any())).thenReturn(authentication);
		when(jwtTokenProvider.generateToken(any())).thenReturn("jwt-token");
		
		// Act
		AuthResponse response = authenticationService.register(registerRequest);
		
		// Assert
		assertNotNull(response);
		assertEquals("jwt-token", response.getToken());
		assertEquals("test@example.com", response.getEmail());
		assertFalse(response.isAdmin());
		verify(userRepository, times(1)).save(any(User.class));
		verify(notificationRepository, never()).save(any());
	}
	
	@Test
	void register_ShouldThrowException_WhenEmailAlreadyExists() {
		// Arrange
		when(userRepository.existsByEmail("test@example.com")).thenReturn(true);
		
		// Act & Assert
		BusinessException exception = assertThrows(BusinessException.class, 
				() -> authenticationService.register(registerRequest));
		
		assertEquals("Email already in use", exception.getMessage());
		verify(userRepository, never()).save(any());
	}
	
	@Test
	void register_ShouldAutoApproveFirstAdmin() {
		// Arrange
		registerRequest.setAdminRequest(true);
		when(userRepository.existsByEmail(anyString())).thenReturn(false);
		when(userRepository.countByIsAdminTrue()).thenReturn(0L);
		when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
		
		User adminUser = new User();
		adminUser.setEmail("test@example.com");
		adminUser.setAdmin(true);
		adminUser.setApprovedAdmin(true);
		
		when(userRepository.save(any(User.class))).thenReturn(adminUser);
		when(authenticationManager.authenticate(any())).thenReturn(authentication);
		when(jwtTokenProvider.generateToken(any())).thenReturn("jwt-token");
		
		// Act
		AuthResponse response = authenticationService.register(registerRequest);
		
		// Assert
		assertNotNull(response);
		assertTrue(response.isAdmin());
		assertTrue(response.isApprovedAdmin());
		verify(notificationRepository, never()).save(any());
	}
	
	@Test
	void register_ShouldRequireApproval_WhenNotFirstAdmin() {
		// Arrange
		registerRequest.setAdminRequest(true);
		when(userRepository.existsByEmail(anyString())).thenReturn(false);
		when(userRepository.countByIsAdminTrue()).thenReturn(1L);
		when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
		when(userRepository.save(any(User.class))).thenReturn(testUser);
		
		// Act & Assert
		BusinessException exception = assertThrows(BusinessException.class, 
				() -> authenticationService.register(registerRequest));
		
		assertEquals("Administration request sent. Waiting for approval", exception.getMessage());
		verify(userRepository, times(1)).save(any(User.class));
		verify(notificationRepository, times(1)).save(any(Notification.class));
	}
	
	@Test
	void login_ShouldReturnToken_WhenCredentialsValid() {
		// Arrange
		when(authenticationManager.authenticate(any())).thenReturn(authentication);
		when(authentication.getPrincipal()).thenReturn(testUser);
		when(jwtTokenProvider.generateToken(any())).thenReturn("jwt-token");
		
		// Act
		AuthResponse response = authenticationService.login(loginRequest, false);
		
		// Assert
		assertNotNull(response);
		assertEquals("jwt-token", response.getToken());
		assertEquals("test@example.com", response.getEmail());
		verify(authenticationManager, times(1)).authenticate(any());
	}
	
	@Test
	void login_ShouldThrowException_WhenCredentialsInvalid() {
		// Arrange
		when(authenticationManager.authenticate(any()))
				.thenThrow(new BadCredentialsException("Invalid credentials"));
		
		// Act & Assert
		assertThrows(UnauthorizedException.class, 
				() -> authenticationService.login(loginRequest, false));
	}
	
	@Test
	void login_ShouldThrowException_WhenAdminLoginButNotAdmin() {
		// Arrange
		when(authenticationManager.authenticate(any())).thenReturn(authentication);
		when(authentication.getPrincipal()).thenReturn(testUser);
		
		// Act & Assert
		UnauthorizedException exception = assertThrows(UnauthorizedException.class, 
				() -> authenticationService.login(loginRequest, true));
		
		assertEquals("You need to register as an admin first", exception.getMessage());
	}
	
	@Test
	void login_ShouldThrowException_WhenAdminNotApproved() {
		// Arrange
		testUser.setAdmin(true);
		testUser.setApprovedAdmin(false);
		
		when(authenticationManager.authenticate(any())).thenReturn(authentication);
		when(authentication.getPrincipal()).thenReturn(testUser);
		
		// Act & Assert
		UnauthorizedException exception = assertThrows(UnauthorizedException.class, 
				() -> authenticationService.login(loginRequest, true));
		
		assertEquals("Your administration request has not yet been approved", exception.getMessage());
	}
	
	@Test
	void login_ShouldSucceed_WhenApprovedAdminLogin() {
		// Arrange
		testUser.setAdmin(true);
		testUser.setApprovedAdmin(true);
		
		when(authenticationManager.authenticate(any())).thenReturn(authentication);
		when(authentication.getPrincipal()).thenReturn(testUser);
		when(jwtTokenProvider.generateToken(any())).thenReturn("jwt-token");
		
		// Act
		AuthResponse response = authenticationService.login(loginRequest, true);
		
		// Assert
		assertNotNull(response);
		assertTrue(response.isAdmin());
		assertTrue(response.isApprovedAdmin());
	}
}
