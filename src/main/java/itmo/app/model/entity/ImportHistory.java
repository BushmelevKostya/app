package itmo.app.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

@Entity
public class ImportHistory {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@NotNull
	@Enumerated(EnumType.STRING)
	private ImportStatus status;
	
	@NotNull
	private String username;
	
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
