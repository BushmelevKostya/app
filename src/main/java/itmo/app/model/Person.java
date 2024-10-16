package itmo.app.model;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "persons")
public class Person {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	
	@Column(name = "PersonName", nullable = false) // ���� �� ����� ���� null, ������ �� ����� ���� ������
	private String name;
	
	@Column(name = "PersonEyeColor", nullable = false) // ���� �� ����� ���� null
	private Color eyeColor;
	
	@Column(name = "PersonHairColor", nullable = false) // ���� �� ����� ���� null
	private Color hairColor;
	
	@OneToMany(mappedBy = "person", cascade = CascadeType.ALL)
	private List<Location> locations;
	
	@Column
	private float height; // �������� ���� ������ ���� ������ 0
	
	@Enumerated(EnumType.STRING)
	@Column(nullable = false) // ���� �� ����� ���� null
	private Country nationality; // ���� �� ����� ���� null
	
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
	
	public List<Location> getLocations() {
		return locations;
	}
	
	public void setLocations(List<Location> locations) {
		this.locations = locations;
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
