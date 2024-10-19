package itmo.app.controller;

import itmo.app.model.entity.Notification;
import itmo.app.model.entity.User;
import itmo.app.model.repository.NotificationRepository;
import itmo.app.model.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/notifications")
@CrossOrigin(origins = "http://localhost:4200")
public class NotificationController {
	@Autowired
	private NotificationRepository notificationRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	@PostMapping("/request")
	public ResponseEntity<Notification> createRequest(@RequestBody Notification notification) {
		Notification newRequest = notificationRepository.save(notification);
		return new ResponseEntity<>(newRequest, HttpStatus.CREATED);
	}
	
	@GetMapping("/pending")
	public ResponseEntity<List<Notification>> getPendingRequests() {
		List<Notification> pendingRequests = notificationRepository.findByIsApprovedFalse();
		return new ResponseEntity<>(pendingRequests, HttpStatus.OK);
	}
	
	@PostMapping("/approve/{id}")
	public ResponseEntity<Void> approveRequest(@PathVariable Long id) {
		Optional<Notification> request = notificationRepository.findById(id);
		if (request.isPresent()) {
			Notification notification = request.get();
			notification.setApproved(true);
			notificationRepository.save(notification);
			System.out.println(notification.getUserEmail());
			Optional<User> isUser = userRepository.findByEmail(notification.getUserEmail());
			User user;
			if (isUser.isPresent()) {
				System.out.println(123);
				user = isUser.get();
				user.setApprovedAdmin(true);
				userRepository.save(user);
			}
			
			return new ResponseEntity<>(HttpStatus.OK);
		}
		return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	}
}
