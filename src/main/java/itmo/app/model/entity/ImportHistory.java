package itmo.app.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "import_history", indexes = {
	@Index(name = "idx_import_username", columnList = "username"),
	@Index(name = "idx_import_status", columnList = "status")
})
public class ImportHistory extends Auditable {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 50)
	private ImportStatus status;
	
	@NotNull
	@Column(nullable = false, length = 255)
	private String username;
	
	@Column(nullable = false)
	private int countObjects;
	
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	public @NotNull ImportStatus getStatus() {
		return status;
	}
	
	public void setStatus(@NotNull ImportStatus status) {
		this.status = status;
	}
	
	public @NotNull String getUsername() {
		return username;
	}
	
	public void setUsername(@NotNull String username) {
		this.username = username;
	}
	
	public int getCountObjects() {
		return countObjects;
	}
	
	public void setCountObjects(int countObjects) {
		this.countObjects = countObjects;
	}
}
