package itmo.app.model.repository;

import itmo.app.model.entity.MinioFiles;
import itmo.app.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MinioFilesRepository extends JpaRepository<MinioFiles, Long> {
	void deleteByHistoryId(Long historyId);
	
	MinioFiles findByFileName(String fileName);
	
	List<MinioFiles> findByUploadedBy(User user);
}
