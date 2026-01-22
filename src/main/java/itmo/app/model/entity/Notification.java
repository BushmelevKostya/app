package itmo.app.model.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "notifications", indexes = {
	@Index(name = "idx_notification_email", columnList = "user_email"),
	@Index(name = "idx_notification_approved", columnList = "is_approved")
})
public class Notification extends Auditable {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(name = "user_email", nullable = false, length = 255)
	private String userEmail;
	
	@Column(name = "is_approved", nullable = false)
	private boolean isApproved = false;
	
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	public String getUserEmail() {
		return userEmail;
	}
	
	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}
	
	public boolean isApproved() {
		return isApproved;
	}
	
	public void setApproved(boolean approved) {
		isApproved = approved;
	}
}
