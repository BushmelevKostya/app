package itmo.app.model;

public class Person {
	private String name; //Поле не может быть null, Строка не может быть пустой
	private Color eyeColor; //Поле не может быть null
	private Color hairColor; //Поле не может быть null
	private Location location; //Поле может быть null
	private Double height; //Поле не может быть null, Значение поля должно быть больше 0
	private Country nationality; //Поле может быть null
}
