package itmo.app.service;

import itmo.app.exception.ResourceNotFoundException;
import itmo.app.model.entity.Notification;
import itmo.app.model.entity.User;
import itmo.app.model.repository.NotificationRepository;
import itmo.app.model.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {
	
	@Mock
	private NotificationRepository notificationRepository;
	
	@Mock
	private UserRepository userRepository;
	
	@Mock
	private UserService userService;
	
	@InjectMocks
	private NotificationService notificationService;
	
	private User adminUser;
	private User pendingUser;
	private Notification testNotification;
	
	@BeforeEach
	void setUp() {
		adminUser = new User();
		adminUser.setId(1L);
		adminUser.setEmail("admin@example.com");
		adminUser.setAdmin(true);
		adminUser.setApprovedAdmin(true);
		
		pendingUser = new User();
		pendingUser.setId(2L);
		pendingUser.setEmail("pending@example.com");
		pendingUser.setAdmin(true);
		pendingUser.setApprovedAdmin(false);
		
		testNotification = new Notification();
		testNotification.setId(1L);
		testNotification.setUserEmail("pending@example.com");
		testNotification.setApproved(false);
	}
	
	@Test
	void getPendingNotifications_ShouldReturnList() {
		// Arrange
		List<Notification> pending = Arrays.asList(testNotification);
		when(notificationRepository.findByIsApprovedFalse()).thenReturn(pending);
		
		// Act
		List<Notification> result = notificationService.getPendingNotifications();
		
		// Assert
		assertNotNull(result);
		assertEquals(1, result.size());
		verify(notificationRepository, times(1)).findByIsApprovedFalse();
	}
	
	@Test
	void getPendingNotificationsCount_ShouldReturnCount() {
		// Arrange
		List<Notification> pending = Arrays.asList(testNotification, new Notification());
		when(notificationRepository.findByIsApprovedFalse()).thenReturn(pending);
		
		// Act
		long count = notificationService.getPendingNotificationsCount();
		
		// Assert
		assertEquals(2L, count);
	}
	
	@Test
	void approveAdminRequest_ShouldApproveUser_WhenValidRequest() {
		// Arrange
		when(userService.getCurrentUser()).thenReturn(adminUser);
		when(notificationRepository.findById(1L)).thenReturn(Optional.of(testNotification));
		when(userRepository.findByEmail("pending@example.com")).thenReturn(Optional.of(pendingUser));
		
		// Act
		notificationService.approveAdminRequest(1L);
		
		// Assert
		assertTrue(pendingUser.isApprovedAdmin());
		verify(userRepository, times(1)).save(pendingUser);
		verify(notificationRepository, times(1)).save(testNotification);
		assertTrue(testNotification.isApproved());
	}
	
	@Test
	void approveAdminRequest_ShouldThrowException_WhenCurrentUserNotApprovedAdmin() {
		// Arrange
		User unapprovedAdmin = new User();
		unapprovedAdmin.setAdmin(true);
		unapprovedAdmin.setApprovedAdmin(false);
		
		when(userService.getCurrentUser()).thenReturn(unapprovedAdmin);
		
		// Act & Assert
		assertThrows(AccessDeniedException.class, 
				() -> notificationService.approveAdminRequest(1L));
		
		verify(notificationRepository, never()).save(any());
	}
	
	@Test
	void approveAdminRequest_ShouldThrowException_WhenNotificationNotFound() {
		// Arrange
		when(userService.getCurrentUser()).thenReturn(adminUser);
		when(notificationRepository.findById(anyLong())).thenReturn(Optional.empty());
		
		// Act & Assert
		assertThrows(ResourceNotFoundException.class, 
				() -> notificationService.approveAdminRequest(999L));
	}
	
	@Test
	void approveAdminRequest_ShouldThrowException_WhenUserNotFound() {
		// Arrange
		when(userService.getCurrentUser()).thenReturn(adminUser);
		when(notificationRepository.findById(1L)).thenReturn(Optional.of(testNotification));
		when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
		
		// Act & Assert
		assertThrows(ResourceNotFoundException.class, 
				() -> notificationService.approveAdminRequest(1L));
	}
	
	@Test
	void rejectAdminRequest_ShouldRejectUser_WhenValidRequest() {
		// Arrange
		when(userService.getCurrentUser()).thenReturn(adminUser);
		when(notificationRepository.findById(1L)).thenReturn(Optional.of(testNotification));
		when(userRepository.findByEmail("pending@example.com")).thenReturn(Optional.of(pendingUser));
		
		// Act
		notificationService.rejectAdminRequest(1L);
		
		// Assert
		assertFalse(pendingUser.isAdmin());
		assertFalse(pendingUser.isApprovedAdmin());
		verify(userRepository, times(1)).save(pendingUser);
		verify(notificationRepository, times(1)).delete(testNotification);
	}
	
	@Test
	void rejectAdminRequest_ShouldThrowException_WhenCurrentUserNotApprovedAdmin() {
		// Arrange
		User unapprovedAdmin = new User();
		unapprovedAdmin.setAdmin(true);
		unapprovedAdmin.setApprovedAdmin(false);
		
		when(userService.getCurrentUser()).thenReturn(unapprovedAdmin);
		
		// Act & Assert
		assertThrows(AccessDeniedException.class, 
				() -> notificationService.rejectAdminRequest(1L));
		
		verify(notificationRepository, never()).delete(any());
	}
}
