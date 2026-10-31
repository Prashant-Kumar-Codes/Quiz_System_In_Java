package ui;

import dao.StudentDAO;
import model.Question;
import model.QuizResult;
import model.Student;
import service.QuestionApiService;
import service.QuizService;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextArea;
import javax.swing.SwingWorker;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

public class QuizPage extends JPanel {
	private final Student student;
	private final QuestionApiService questionApiService;
	private final StudentDAO studentDAO;
	private final Consumer<QuizResult> onComplete;
	private final Runnable onHome;
	private final QuizService quizService = new QuizService();
	private final JLabel progressLabel = new JLabel("Loading questions...");
	private final JLabel statusLabel = new JLabel(" ");
	private final JTextArea questionText = new JTextArea();
	private final JRadioButton[] optionButtons = new JRadioButton[4];
	private final ButtonGroup optionGroup = new ButtonGroup();
	private final int[] selectedAnswers = new int[10];
	private final JButton previousButton = new JButton("Previous");
	private final JButton nextButton = new JButton("Next");
	private final JButton submitButton = new JButton("Submit quiz");
	private final JButton retryButton = new JButton("Retry loading questions");
	private List<Question> questions = List.of();
	private int currentQuestion;

	public QuizPage(Student student, QuestionApiService questionApiService, StudentDAO studentDAO,
					Consumer<QuizResult> onComplete, Runnable onHome) {
		super(new BorderLayout(18, 18));
		this.student = student;
		this.questionApiService = questionApiService;
		this.studentDAO = studentDAO;
		this.onComplete = onComplete;
		this.onHome = onHome;
		Arrays.fill(selectedAnswers, -1);
		setBorder(BorderFactory.createEmptyBorder(26, 30, 20, 30));

		JPanel header = new JPanel(new BorderLayout());
		JLabel studentLabel = new JLabel("Student: " + student.getName() + "  ·  " + student.getRollNumber());
		header.add(studentLabel, BorderLayout.WEST);
		header.add(progressLabel, BorderLayout.EAST);
		add(header, BorderLayout.NORTH);

		questionText.setLineWrap(true);
		questionText.setWrapStyleWord(true);
		questionText.setEditable(false);
		questionText.setOpaque(false);
		questionText.setFont(questionText.getFont().deriveFont(java.awt.Font.BOLD, 20f));
		questionText.setBorder(BorderFactory.createEmptyBorder(10, 2, 18, 2));
		JPanel questionArea = new JPanel(new BorderLayout(0, 12));
		questionArea.add(questionText, BorderLayout.NORTH);
		JPanel options = new JPanel(new GridLayout(4, 1, 0, 10));
		for (int index = 0; index < optionButtons.length; index++) {
			final int optionIndex = index;
			optionButtons[index] = new JRadioButton();
			optionButtons[index].setOpaque(false);
			optionButtons[index].addActionListener(event -> selectedAnswers[currentQuestion] = optionIndex);
			optionGroup.add(optionButtons[index]);
			options.add(optionButtons[index]);
		}
		questionArea.add(options, BorderLayout.CENTER);
		add(questionArea, BorderLayout.CENTER);

		previousButton.addActionListener(event -> showQuestion(currentQuestion - 1));
		nextButton.addActionListener(event -> showQuestion(currentQuestion + 1));
		submitButton.addActionListener(event -> submitQuiz());
		retryButton.addActionListener(event -> loadQuestions());
		retryButton.setVisible(false);
		JButton homeButton = new JButton("Back to home");
		homeButton.addActionListener(event -> onHome.run());
		JPanel navigation = new JPanel();
		navigation.add(homeButton);
		navigation.add(previousButton);
		navigation.add(nextButton);
		navigation.add(submitButton);
		navigation.add(retryButton);
		JPanel footer = new JPanel(new BorderLayout());
		footer.add(statusLabel, BorderLayout.NORTH);
		footer.add(navigation, BorderLayout.SOUTH);
		add(footer, BorderLayout.SOUTH);
		setQuestionControlsEnabled(false);
	}

	public void loadQuestions() {
		progressLabel.setText("Loading questions...");
		statusLabel.setText("Connecting to Open Trivia DB.");
		retryButton.setVisible(false);
		setQuestionControlsEnabled(false);
		new SwingWorker<List<Question>, Void>() {
			@Override
			protected List<Question> doInBackground() throws Exception {
				return questionApiService.fetchQuestions();
			}

			@Override
			protected void done() {
				try {
					questions = get();
					currentQuestion = 0;
					setQuestionControlsEnabled(true);
					statusLabel.setText(" ");
					showQuestion(0);
				} catch (InterruptedException exception) {
					Thread.currentThread().interrupt();
					showLoadError(exception);
				} catch (ExecutionException exception) {
					showLoadError(exception.getCause());
				}
			}
		}.execute();
	}

	private void showLoadError(Throwable error) {
		progressLabel.setText("Questions unavailable");
		statusLabel.setText("Could not load questions: " + error.getMessage());
		retryButton.setVisible(true);
		revalidate();
		repaint();
	}

	private void showQuestion(int questionIndex) {
		if (questionIndex < 0 || questionIndex >= questions.size()) {
			return;
		}
		currentQuestion = questionIndex;
		Question question = questions.get(currentQuestion);
		questionText.setText(question.getText());
		for (int index = 0; index < optionButtons.length; index++) {
			optionButtons[index].setText(question.getOptions().get(index));
		}
		optionGroup.clearSelection();
		if (selectedAnswers[currentQuestion] >= 0) {
			optionButtons[selectedAnswers[currentQuestion]].setSelected(true);
		}
		progressLabel.setText("Question " + (currentQuestion + 1) + " of " + questions.size());
		previousButton.setEnabled(currentQuestion > 0);
		nextButton.setEnabled(currentQuestion < questions.size() - 1);
	}

	private void submitQuiz() {
		int unanswered = (int) Arrays.stream(selectedAnswers).filter(answer -> answer < 0).count();
		if (unanswered > 0) {
			int choice = JOptionPane.showConfirmDialog(this,
					unanswered + " question(s) are unanswered. Submit anyway?", "Unanswered questions",
					JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
			if (choice != JOptionPane.YES_OPTION) {
				return;
			}
		}
		List<Integer> answers = new ArrayList<>(selectedAnswers.length);
		for (int answer : selectedAnswers) {
			answers.add(answer);
		}
		int score = quizService.calculateScore(questions, answers);
		setQuestionControlsEnabled(false);
		statusLabel.setText("Saving your result...");
		new SwingWorker<QuizResult, Void>() {
			@Override
			protected QuizResult doInBackground() throws Exception {
				return studentDAO.saveResult(student, score, questions.size());
			}

			@Override
			protected void done() {
				try {
					onComplete.accept(get());
				} catch (InterruptedException exception) {
					Thread.currentThread().interrupt();
					showSaveError(exception);
				} catch (ExecutionException exception) {
					showSaveError(exception.getCause());
				}
			}
		}.execute();
	}

	private void showSaveError(Throwable error) {
		setQuestionControlsEnabled(true);
		statusLabel.setText("Could not save result: " + error.getMessage());
		JOptionPane.showMessageDialog(this, "Your quiz score could not be saved.\n\n" + error.getMessage(),
				"Save failed", JOptionPane.ERROR_MESSAGE);
	}

	private void setQuestionControlsEnabled(boolean enabled) {
		for (JRadioButton optionButton : optionButtons) {
			if (optionButton != null) {
				optionButton.setEnabled(enabled);
			}
		}
		previousButton.setEnabled(enabled && currentQuestion > 0);
		nextButton.setEnabled(enabled && currentQuestion < questions.size() - 1);
		submitButton.setEnabled(enabled);
	}
}
