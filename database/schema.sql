CREATE DATABASE IF NOT EXISTS quiz_system
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE quiz_system;

CREATE TABLE IF NOT EXISTS students (
    id INT NOT NULL AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    roll_number VARCHAR(30) NOT NULL,
    course VARCHAR(100) NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_students_roll_number (roll_number)
);

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
);