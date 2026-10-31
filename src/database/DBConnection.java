package database;

import config.AppConfig;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public final class DBConnection {
	private DBConnection() {
	}

	public static Connection getConnection() throws SQLException {
		String url = AppConfig.get("database.url", "QUIZ_DB_URL",
				"jdbc:mysql://localhost:3306/quiz_system?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true");
		String user = AppConfig.get("database.user", "QUIZ_DB_USER", "root");
		String password = AppConfig.get("database.password", "QUIZ_DB_PASSWORD", "");
		return DriverManager.getConnection(url, user, password);
	}

	public static void initializeSchema() throws SQLException {
		try (Connection connection = getConnection(); Statement statement = connection.createStatement()) {
			statement.executeUpdate("""
					CREATE TABLE IF NOT EXISTS students (
						id INT NOT NULL AUTO_INCREMENT,
						name VARCHAR(100) NOT NULL,
						roll_number VARCHAR(30) NOT NULL,
						course VARCHAR(100) NOT NULL,
						PRIMARY KEY (id),
						UNIQUE KEY uk_students_roll_number (roll_number)
					)
					""");
			statement.executeUpdate("""
					CREATE TABLE IF NOT EXISTS quiz_attempts (
						id INT NOT NULL AUTO_INCREMENT,
						student_id INT NOT NULL,
						score INT NOT NULL,
						total_questions INT NOT NULL,
						attempt_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
						PRIMARY KEY (id),
						KEY idx_quiz_attempts_student_id (student_id),
						CONSTRAINT fk_quiz_attempts_student
							FOREIGN KEY (student_id) REFERENCES students (id)
							ON DELETE CASCADE
					)
					""");
		}
	}
}
