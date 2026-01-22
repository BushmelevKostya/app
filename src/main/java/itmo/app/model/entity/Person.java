package itmo.app.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "persons", indexes = {
	@Index(name = "idx_person_name", columnList = "PersonName")
})
public class Person {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@NotNull
	@Size(min = 1, max = 255)
	@Column(name = "PersonName", nullable = false, length = 255)
	private String name;
	
	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(name = "PersonEyeColor", nullable = false, length = 50)
	private Color eyeColor;
	
	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(name = "PersonHairColor", nullable = false, length = 50)
	private Color hairColor;
	
	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
	@JoinColumn(
		name = "location_id",
		foreignKey = @ForeignKey(name = "fk_person_location")
	)
	private Location location;
	
	@NotNull
	@Min(1)
	@Column(nullable = false)
	private float height;
	
	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 50)
	private Country nationality;
	
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
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
