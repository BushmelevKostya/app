package itmo.app.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "locations")
public class Location {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	
	@NotNull
	@Column(name = "LocationX", nullable = false)
	private Double x;
	
	@NotNull
	@Column(name = "LocationY", nullable = false)
	private Double y;
	
	@NotNull
	private float z;
	
	@Column(name = "LocationName", nullable = false)
	private String name;
	
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
