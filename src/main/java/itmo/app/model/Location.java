package itmo.app.model;

import jakarta.persistence.*;

@Entity
@Table(name = "locations")
public class Location {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	
	@Column(name = "LocationX", nullable = false) // Поле не может быть null
	private Double x;
	
	@Column(name = "LocationY", nullable = false) // Поле не может быть null
	private Double y;
	
	private float z;
	
	@Column(name = "LocationName", nullable = false) // Поле не может быть null
	private String name; // Поле не может быть null
	
	public long getId() {
		return id;
	}
	
	public void setId(long id) {
		this.id = id;
	}
	
	public Double getX() {
		return x;
	}
	
	public void setX(Double x) {
		this.x = x;
	}
	
	public Double getY() {
		return y;
	}
	
	public void setY(Double y) {
		this.y = y;
	}
	
	public float getZ() {
		return z;
	}
	
	public void setZ(float z) {
		this.z = z;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
}
