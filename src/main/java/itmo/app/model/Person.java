package itmo.app.model;

public class Person {
	private String name; //Поле не может быть null, Строка не может быть пустой
	private Color eyeColor; //Поле не может быть null
	private Color hairColor; //Поле не может быть null
	private Location location; //Поле может быть null
	private float height; //Значение поля должно быть больше 0
	private Country nationality; //Поле не может быть null
	
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
