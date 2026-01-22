package itmo.app.model.repository;

import itmo.app.model.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
	List<Notification> findByIsApprovedFalse();
	
	List<Notification> findByIsApproved(boolean approved);
	
	List<Notification> findByUserEmailAndIsApproved(String userEmail, boolean approved);
}
