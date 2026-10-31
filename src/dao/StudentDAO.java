package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import database.DBConnection;
import model.QuizResult;
import model.Student;

public class StudentDAO {
	public Student registerStudent(String name, String rollNumber, String course) throws SQLException {
		String insertSql = """
				INSERT INTO students (name, roll_number, course) VALUES (?, ?, ?)
				ON DUPLICATE KEY UPDATE id = LAST_INSERT_ID(id)
				""";
		String selectSql = "SELECT id, name, roll_number, course FROM students WHERE roll_number = ?";
		try (Connection connection = DBConnection.getConnection();
			 PreparedStatement insertStatement = connection.prepareStatement(insertSql)) {
			insertStatement.setString(1, name);
			insertStatement.setString(2, rollNumber);
			insertStatement.setString(3, course);
			insertStatement.executeUpdate();
			try (PreparedStatement selectStatement = connection.prepareStatement(selectSql)) {
				selectStatement.setString(1, rollNumber);
				try (ResultSet studentRow = selectStatement.executeQuery()) {
					if (!studentRow.next()) {
						throw new SQLException("Student could not be found after registration.");
					}
					return new Student(studentRow.getInt("id"), studentRow.getString("name"),
							studentRow.getString("roll_number"), studentRow.getString("course"));
				}
			}
		}
	}

	public QuizResult saveResult(Student student, int score, int totalQuestions) throws SQLException {
		String sql = "INSERT INTO quiz_attempts (student_id, score, total_questions) VALUES (?, ?, ?)";
		try (Connection connection = DBConnection.getConnection();
			 PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
			statement.setInt(1, student.getId());
			statement.setInt(2, score);
			statement.setInt(3, totalQuestions);
			statement.executeUpdate();
			try (ResultSet keys = statement.getGeneratedKeys()) {
				if (!keys.next()) {
					throw new SQLException("Result was saved but the database did not return an ID.");
				}
				return new QuizResult(keys.getInt(1), student.getName(), student.getRollNumber(),
						score, totalQuestions, LocalDateTime.now());
			}
		}
	}

	public List<QuizResult> getRecentResults(int limit) throws SQLException {
		String sql = """
				SELECT s.name, s.roll_number, qa.id, qa.score, qa.total_questions, qa.attempt_date
				FROM quiz_attempts qa
				JOIN students s ON s.id = qa.student_id
				ORDER BY qa.attempt_date DESC, qa.id DESC
				LIMIT ?
				""";
		List<QuizResult> results = new ArrayList<>();
		try (Connection connection = DBConnection.getConnection();
			 PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setInt(1, Math.max(1, limit));
			try (ResultSet rows = statement.executeQuery()) {
				while (rows.next()) {
					Timestamp timestamp = rows.getTimestamp("attempt_date");
					results.add(new QuizResult(rows.getInt("id"), rows.getString("name"),
							rows.getString("roll_number"), rows.getInt("score"),
							rows.getInt("total_questions"), timestamp.toLocalDateTime()));
				}
			}
		}
		return results;
	}
}
