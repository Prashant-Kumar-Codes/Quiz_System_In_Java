package model;

import java.time.LocalDateTime;

public class QuizResult {
	private final int id;
	private final String studentName;
	private final String rollNumber;
	private final int score;
	private final int totalQuestions;
	private final LocalDateTime attemptDate;

	public QuizResult(int id, String studentName, String rollNumber, int score,
					  int totalQuestions, LocalDateTime attemptDate) {
		this.id = id;
		this.studentName = studentName;
		this.rollNumber = rollNumber;
		this.score = score;
		this.totalQuestions = totalQuestions;
		this.attemptDate = attemptDate;
	}

	public int getId() {
		return id;
	}

	public String getStudentName() {
		return studentName;
	}

	public String getRollNumber() {
		return rollNumber;
	}

	public int getScore() {
		return score;
	}

	public int getTotalQuestions() {
		return totalQuestions;
	}

	public LocalDateTime getAttemptDate() {
		return attemptDate;
	}

	public double getPercentage() {
		return totalQuestions == 0 ? 0 : score * 100.0 / totalQuestions;
	}
}
