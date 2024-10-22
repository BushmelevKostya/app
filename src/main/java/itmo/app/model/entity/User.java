package itmo.app.model.entity;

import jakarta.persistence.*;

@Table(name = "movie_users")
@Entity
public class User {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private String email;
	private String password;
	private boolean isAdminLogin;
	private boolean isAdmin;
	private boolean isApprovedAdmin;
	
	public User() {
	}
	
	public User(String email, String password) {
		this.email = email;
		this.password = password;
		this.isApprovedAdmin = false;
		this.isAdmin = false;
	}
	
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	public String getEmail() {
		return email;
	}
	
	public void setEmail(String email) {
		this.email = email;
	}
	
	public String getPassword() {
		return password;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}
	
	public boolean isAdmin() {
		return isAdmin;
	}
	
	public void setAdmin(boolean admin) {
		isAdmin = admin;
	}
	
	public boolean isApprovedAdmin() {
		return isApprovedAdmin;
	}
	
	public void setApprovedAdmin(boolean approvedAdmin) {
		isApprovedAdmin = approvedAdmin;
	}
}