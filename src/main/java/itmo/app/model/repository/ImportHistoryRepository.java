package itmo.app.model.repository;

import itmo.app.model.entity.ImportHistory;
import itmo.app.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ImportHistoryRepository extends JpaRepository<ImportHistory, Long> {
	Optional<List<ImportHistory>> findAllByUsername(String username);
}
