package itmo.app.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "persons")
public class Person {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	
	@NotNull
	@Size(min = 1)
	@Column(name = "PersonName", nullable = false)
	private String name;
	
	@NotNull
	@Column(name = "PersonEyeColor", nullable = false)
	private Color eyeColor;
	
	@NotNull
	@Column(name = "PersonHairColor", nullable = false)
	private Color hairColor;
	
	@ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
	@JoinColumn(name = "location_id")
	private Location location;
	
	@NotNull
	@Min(1)
	@Column
	private float height;
	
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private Country nationality;
	
	public long getId() {
		return id;
	}
	
	public void setId(long id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public Color getEyeColor() {
		return eyeColor;
	}
	
	public void setEyeColor(Color eyeColor) {
		this.eyeColor = eyeColor;
	}
	
	public Color getHairColor() {
		return hairColor;
	}
	
	public void setHairColor(Color hairColor) {
		this.hairColor = hairColor;
	}
	
	public Location getLocation() {
		return location;
	}
	
	public void setLocation(Location location) {
		this.location = location;
	}
	
	public float getHeight() {
		return height;
	}
	
	public void setHeight(float height) {
		this.height = height;
	}
	
	public Country getNationality() {
		return nationality;
	}
	
	public void setNationality(Country nationality) {
		this.nationality = nationality;
	}
}
