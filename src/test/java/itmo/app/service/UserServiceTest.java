package itmo.app.service;

import itmo.app.dto.response.PageResponse;
import itmo.app.exception.ResourceNotFoundException;
import itmo.app.model.entity.User;
import itmo.app.model.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
	
	@Mock
	private UserRepository userRepository;
	
	@Mock
	private SecurityContext securityContext;
	
	@Mock
	private Authentication authentication;
	
	@InjectMocks
	private UserService userService;
	
	private User testUser;
	
	@BeforeEach
	void setUp() {
		testUser = new User();
		testUser.setId(1L);
		testUser.setEmail("test@example.com");
		testUser.setPassword("encodedPassword");
		testUser.setAdmin(false);
		testUser.setApprovedAdmin(false);
	}
	
	@Test
	void getCurrentUser_ShouldReturnCurrentUser_WhenUserExists() {
		// Arrange
		when(securityContext.getAuthentication()).thenReturn(authentication);
		when(authentication.getName()).thenReturn("test@example.com");
		SecurityContextHolder.setContext(securityContext);
		when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
		
		// Act
		User result = userService.getCurrentUser();
		
		// Assert
		assertNotNull(result);
		assertEquals("test@example.com", result.getEmail());
		verify(userRepository, times(1)).findByEmail("test@example.com");
	}
	
	@Test
	void getCurrentUser_ShouldThrowException_WhenUserNotFound() {
		// Arrange
		when(securityContext.getAuthentication()).thenReturn(authentication);
		when(authentication.getName()).thenReturn("nonexistent@example.com");
		SecurityContextHolder.setContext(securityContext);
		when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
		
		// Act & Assert
		assertThrows(ResourceNotFoundException.class, () -> userService.getCurrentUser());
		verify(userRepository, times(1)).findByEmail("nonexistent@example.com");
	}
	
	@Test
	void getUserByEmail_ShouldReturnUser_WhenUserExists() {
		// Arrange
		when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
		
		// Act
		User result = userService.getUserByEmail("test@example.com");
		
		// Assert
		assertNotNull(result);
		assertEquals("test@example.com", result.getEmail());
		verify(userRepository, times(1)).findByEmail("test@example.com");
	}
	
	@Test
	void getUserByEmail_ShouldThrowException_WhenUserNotFound() {
		// Arrange
		when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
		
		// Act & Assert
		assertThrows(ResourceNotFoundException.class, () -> userService.getUserByEmail("nonexistent@example.com"));
	}
	
	@Test
	void getUserById_ShouldReturnUser_WhenUserExists() {
		// Arrange
		when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
		
		// Act
		User result = userService.getUserById(1L);
		
		// Assert
		assertNotNull(result);
		assertEquals(1L, result.getId());
		verify(userRepository, times(1)).findById(1L);
	}
	
	@Test
	void getUserById_ShouldThrowException_WhenUserNotFound() {
		// Arrange
		when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
		
		// Act & Assert
		assertThrows(ResourceNotFoundException.class, () -> userService.getUserById(999L));
	}
	
	@Test
	void getAllUsers_ShouldReturnPaginatedUsers() {
		// Arrange
		User user2 = new User();
		user2.setId(2L);
		user2.setEmail("test2@example.com");
		
		User user3 = new User();
		user3.setId(3L);
		user3.setEmail("test3@example.com");
		
		List<User> allUsers = Arrays.asList(testUser, user2, user3);
		when(userRepository.findAll()).thenReturn(allUsers);
		
		// Act
		PageResponse<User> result = userService.getAllUsers(0, 2);
		
		// Assert
		assertNotNull(result);
		assertEquals(2, result.getContent().size());
		assertEquals(0, result.getPage());
		assertEquals(2, result.getSize());
		assertEquals(3, result.getTotalElements());
		verify(userRepository, times(1)).findAll();
	}
	
	@Test
	void getAllUsers_ShouldHandleLastPage() {
		// Arrange
		List<User> allUsers = Arrays.asList(testUser);
		when(userRepository.findAll()).thenReturn(allUsers);
		
		// Act
		PageResponse<User> result = userService.getAllUsers(0, 2);
		
		// Assert
		assertNotNull(result);
		assertEquals(1, result.getContent().size());
		assertEquals(0, result.getPage());
	}
	
	@Test
	void existsByEmail_ShouldReturnTrue_WhenUserExists() {
		// Arrange
		when(userRepository.existsByEmail("test@example.com")).thenReturn(true);
		
		// Act
		boolean result = userService.existsByEmail("test@example.com");
		
		// Assert
		assertTrue(result);
		verify(userRepository, times(1)).existsByEmail("test@example.com");
	}
	
	@Test
	void existsByEmail_ShouldReturnFalse_WhenUserDoesNotExist() {
		// Arrange
		when(userRepository.existsByEmail(anyString())).thenReturn(false);
		
		// Act
		boolean result = userService.existsByEmail("nonexistent@example.com");
		
		// Assert
		assertFalse(result);
	}
	
	@Test
	void isEmailAvailable_ShouldReturnTrue_WhenEmailNotUsed() {
		// Arrange
		when(userRepository.existsByEmail("new@example.com")).thenReturn(false);
		
		// Act
		boolean result = userService.isEmailAvailable("new@example.com");
		
		// Assert
		assertTrue(result);
	}
	
	@Test
	void isEmailAvailable_ShouldReturnFalse_WhenEmailAlreadyUsed() {
		// Arrange
		when(userRepository.existsByEmail("test@example.com")).thenReturn(true);
		
		// Act
		boolean result = userService.isEmailAvailable("test@example.com");
		
		// Assert
		assertFalse(result);
	}
}
