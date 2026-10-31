# Quiz Examination System

A beginner-friendly Java Swing desktop app for student registration, 10-question General Knowledge quizzes, automatic scoring, and saved result history.

## Requirements

- Java 17 or newer
- Maven 3.9 or newer
- MySQL 8 or newer
- Internet access to retrieve questions from Open Trivia DB

## Configure MySQL

1. Start MySQL and create the database and tables by running `database/schema.sql`:

	```powershell
	mysql -u root -p < database/schema.sql
	```

2. Copy `config.properties.example` to `config.properties` and set your MySQL username and password. `config.properties` is ignored by Git.

The application creates the tables on startup if the database already exists. The schema script creates the database itself.

You can also configure the connection with `QUIZ_DB_URL`, `QUIZ_DB_USER`, and `QUIZ_DB_PASSWORD` environment variables.

## API Configuration

The API endpoint is configured by `api.url` in `config.properties` or the `QUIZ_API_URL` environment variable. The `api.key` / `QUIZ_API_KEY` setting is included as a placeholder for providers that require authentication. **Open Trivia DB does not require an API key**, so the default endpoint works with the placeholder unchanged.

The app requests 10 multiple-choice questions from the General Knowledge category, randomizes each question's answer order, and reports a clear error with a retry option if the service is unavailable.

## Build and Run

From the project directory:

```powershell
.\mvnw.cmd clean package
.\mvnw.cmd exec:java
```

## Project Layout

```text
database/schema.sql       MySQL schema
src/Main.java             Swing application entry point
src/config/               Local configuration
src/dao/                  JDBC data access
src/database/             Database connection and initialization
src/model/                Student, question, and result models
src/service/              API retrieval and quiz scoring
src/ui/                   Swing screens
```