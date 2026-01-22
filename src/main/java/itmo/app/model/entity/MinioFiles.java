package itmo.app.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "minio_files", indexes = {
	@Index(name = "idx_minio_history", columnList = "history_id")
})
public class MinioFiles extends Auditable {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(
		name = "history_id", 
		nullable = false,
		foreignKey = @ForeignKey(name = "fk_minio_history")
	)
	private ImportHistory importHistory;
	
	@NotNull
	@Column(nullable = false, length = 500)
	private String fileName;
	
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	public ImportHistory getImportHistory() {
		return importHistory;
	}
	
	public void setImportHistory(ImportHistory importHistory) {
		this.importHistory = importHistory;
	}
	
	public String getFileName() {
		return fileName;
	}
	
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
}
