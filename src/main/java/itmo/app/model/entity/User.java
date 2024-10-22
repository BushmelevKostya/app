package itmo.app.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Table(name = "movie_users")
@Entity
public class User {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
//	@OneToMany(mappedBy = "creator")
//	@JsonIgnore
//	private Set<Movie> createdMovies = new HashSet<>();
//
//	@OneToMany(mappedBy = "user")
//	private Set<MovieChange> changes = new HashSet<>();
	
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
	
//	public Set<Movie> getCreatedMovies() {
//		return createdMovies;
//	}
//
//	public void setCreatedMovies(Set<Movie> createdMovies) {
//		this.createdMovies = createdMovies;
//	}
//
//	public Set<MovieChange> getChanges() {
//		return changes;
//	}
//
//	public void setChanges(Set<MovieChange> changes) {
//		this.changes = changes;
//	}
	
	public boolean isAdminLogin() {
		return isAdminLogin;
	}
	
	public void setAdminLogin(boolean adminLogin) {
		isAdminLogin = adminLogin;
	}
}