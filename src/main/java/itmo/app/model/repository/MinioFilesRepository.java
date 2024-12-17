package itmo.app.model.repository;

import itmo.app.model.entity.MinioFiles;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MinioFilesRepository extends JpaRepository<MinioFiles, Long> {
}
