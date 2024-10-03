package itmo.app.model;

public class Movie {
	private Integer id; //Поле не может быть null, Значение поля должно быть больше 0, Значение этого поля должно быть уникальным, Значение этого поля должно генерироваться автоматически
	private String name; //Поле не может быть null, Строка не может быть пустой
	private Coordinates coordinates; //Поле не может быть null
	private java.time.LocalDateTime creationDate; //Поле не может быть null, Значение этого поля должно генерироваться автоматически
	private long oscarsCount; //Значение поля должно быть больше 0
	private int budget; //Значение поля должно быть больше 0
	private Float totalBoxOffice; //Поле может быть null, Значение поля должно быть больше 0
	private MpaaRating mpaaRating; //Поле не может быть null
	private Person director; //Поле может быть null
	private Person screenwriter;
	private Person operator; //Поле может быть null
	private long length; //Значение поля должно быть больше 0
	private Integer goldenPalmCount; //Значение поля должно быть больше 0, Поле может быть null
	private long usaBoxOffice; //Значение поля должно быть больше 0
	private String tagline; //Поле не может быть null
	private MovieGenre genre; //Поле может быть null
}
