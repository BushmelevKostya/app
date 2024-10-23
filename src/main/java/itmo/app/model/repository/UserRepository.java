package itmo.app.model.repository;

import itmo.app.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
	Optional<User> findByEmail(String email);
	long countByIsAdminTrue();
	
	boolean existsByEmail(String email);
}
