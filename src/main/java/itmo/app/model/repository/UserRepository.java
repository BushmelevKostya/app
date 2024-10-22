package itmo.app.model.repository;

import itmo.app.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
	Optional<User> findByEmail(String email);
	long countByIsAdminTrue();
	
	boolean existsByEmail(String email);
}
