package itmo.app.service;

import itmo.app.exception.BusinessException;
import itmo.app.exception.ResourceNotFoundException;
import itmo.app.model.entity.Notification;
import itmo.app.model.entity.User;
import itmo.app.model.repository.NotificationRepository;
import itmo.app.model.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class NotificationService {
	
	@Autowired
	private NotificationRepository notificationRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private UserService userService;
	
	@Transactional(readOnly = true)
	@PreAuthorize("hasRole('ADMIN')")
	public List<Notification> getPendingNotifications() {
		return notificationRepository.findByIsApprovedFalse();
	}
	
	@Transactional(readOnly = true)
	@PreAuthorize("hasRole('ADMIN')")
	public long getPendingNotificationsCount() {
		return notificationRepository.findByIsApprovedFalse().size();
	}
	
	@Transactional
	@PreAuthorize("hasRole('ADMIN')")
	public void approveAdminRequest(Long notificationId) {
		User currentUser = userService.getCurrentUser();
		
		if (!currentUser.isApprovedAdmin()) {
			throw new AccessDeniedException("Only approved admin can approve requests");
		}
		
		Notification notification = notificationRepository.findById(notificationId)
				.orElseThrow(() -> new ResourceNotFoundException("Notification", "id", notificationId));
		
		User user = userRepository.findByEmail(notification.getUserEmail())
				.orElseThrow(() -> new ResourceNotFoundException("User", "email", notification.getUserEmail()));
		
		user.setApprovedAdmin(true);
		userRepository.save(user);
		
		// Update notification
		notification.setApproved(true);
		notificationRepository.save(notification);
	}
	
	@Transactional
	@PreAuthorize("hasRole('ADMIN')")
	public void rejectAdminRequest(Long notificationId) {
		User currentUser = userService.getCurrentUser();
		
		if (!currentUser.isApprovedAdmin()) {
			throw new AccessDeniedException("Only approved admin can reject requests");
		}
		
		Notification notification = notificationRepository.findById(notificationId)
				.orElseThrow(() -> new ResourceNotFoundException("Notification", "id", notificationId));
		
		User user = userRepository.findByEmail(notification.getUserEmail())
				.orElseThrow(() -> new ResourceNotFoundException("User", "email", notification.getUserEmail()));
		
		// Remove admin status
		user.setAdmin(false);
		user.setApprovedAdmin(false);
		userRepository.save(user);
		
		// Delete notification
		notificationRepository.delete(notification);
	}
}
