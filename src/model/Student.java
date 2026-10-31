package model;

public class Student {
	private final int id;
	private final String name;
	private final String rollNumber;
	private final String course;

	public Student(int id, String name, String rollNumber, String course) {
		this.id = id;
		this.name = name;
		this.rollNumber = rollNumber;
		this.course = course;
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getRollNumber() {
		return rollNumber;
	}

	public String getCourse() {
		return course;
	}
}
