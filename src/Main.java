import java.awt.CardLayout;
import java.sql.SQLException;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import dao.StudentDAO;
import database.DBConnection;
import service.QuestionApiService;
import ui.HomePage;
import ui.QuizPage;
import ui.RegistrationPage;
import ui.ResultPage;

public class Main {
	private static final String HOME = "home";
	private static final String REGISTRATION = "registration";
	private static final String QUIZ = "quiz";
	private static final String RESULT = "result";

	private final JFrame frame = new JFrame("Quiz Examination System");
	private final CardLayout cardLayout = new CardLayout();
	private final JPanel pages = new JPanel(cardLayout);
	private final StudentDAO studentDAO = new StudentDAO();
	private final QuestionApiService questionApiService = new QuestionApiService();
	private final HomePage homePage;
	private RegistrationPage registrationPage;
	private QuizPage quizPage;
	private ResultPage resultPage;

	private Main() {
		homePage = new HomePage(this::showRegistration, this::refreshResults);
		pages.add(homePage, HOME);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setContentPane(pages);
		frame.setSize(850, 600);
		frame.setMinimumSize(new java.awt.Dimension(700, 500));
		frame.setLocationRelativeTo(null);
		showHome();
	}

	private void showHome() {
		cardLayout.show(pages, HOME);
		refreshResults();
	}

	private void refreshResults() {
		try {
			homePage.setResults(studentDAO.getRecentResults(100));
			homePage.setStatus("Showing the latest quiz attempts.");
		} catch (SQLException exception) {
			homePage.setResults(java.util.List.of());
			homePage.setStatus("Database unavailable: " + exception.getMessage());
		}
	}

	private void showRegistration() {
		if (registrationPage != null) {
			pages.remove(registrationPage);
		}
		registrationPage = new RegistrationPage(this::registerStudent, this::showHome);
		pages.add(registrationPage, REGISTRATION);
		cardLayout.show(pages, REGISTRATION);
	}

	private void registerStudent(String name, String rollNumber, String course) {
		try {
			var student = studentDAO.registerStudent(name, rollNumber, course);
			showQuiz(student);
		} catch (SQLException exception) {
			JOptionPane.showMessageDialog(frame,
					"Could not register this student. Check the database connection and try again.\n\n"
							+ exception.getMessage(),
					"Registration failed", JOptionPane.ERROR_MESSAGE);
		}
	}

	private void showQuiz(model.Student student) {
		if (quizPage != null) {
			pages.remove(quizPage);
		}
		quizPage = new QuizPage(student, questionApiService, studentDAO,
				this::showResult, this::showHome);
		pages.add(quizPage, QUIZ);
		cardLayout.show(pages, QUIZ);
		quizPage.loadQuestions();
	}

	private void showResult(model.QuizResult result) {
		if (resultPage != null) {
			pages.remove(resultPage);
		}
		resultPage = new ResultPage(result, this::showHome);
		pages.add(resultPage, RESULT);
		cardLayout.show(pages, RESULT);
	}

	private void show() {
		frame.setVisible(true);
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> {
			try {
				DBConnection.initializeSchema();
			} catch (SQLException exception) {
				JOptionPane.showMessageDialog(null,
						"The app will open, but database features will not work until MySQL is configured.\n\n"
								+ exception.getMessage(),
						"Database connection failed", JOptionPane.WARNING_MESSAGE);
			}
			new Main().show();
		});
	}
}