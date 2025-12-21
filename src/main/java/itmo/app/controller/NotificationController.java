package itmo.app.controller;

import itmo.app.model.entity.Notification;
import itmo.app.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {
	
	@Autowired
	private NotificationService notificationService;
	
	@GetMapping("/pending")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<List<Notification>> getPendingRequests() {
		List<Notification> pendingRequests = notificationService.getPendingNotifications();
		return ResponseEntity.ok(pendingRequests);
	}
	
	@PostMapping("/approve/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<Void> approveRequest(@PathVariable Long id) {
		notificationService.approveAdminRequest(id);
		return ResponseEntity.ok().build();
	}
	
	@PostMapping("/reject/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<Void> rejectRequest(@PathVariable Long id) {
		notificationService.rejectAdminRequest(id);
		return ResponseEntity.ok().build();
	}
}
