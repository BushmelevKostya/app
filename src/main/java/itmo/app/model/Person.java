package itmo.app.model;

public class Person {
	private String name; //���� �� ����� ���� null, ������ �� ����� ���� ������
	private Color eyeColor; //���� �� ����� ���� null
	private Color hairColor; //���� �� ����� ���� null
	private Location location; //���� ����� ���� null
	private float height; //�������� ���� ������ ���� ������ 0
	private Country nationality; //���� �� ����� ���� null
	
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
