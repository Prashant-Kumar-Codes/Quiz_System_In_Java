package ui;

import model.QuizResult;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;

public class ResultPage extends JPanel {
	public ResultPage(QuizResult result, Runnable returnHome) {
		super(new BorderLayout(16, 16));
		setBorder(BorderFactory.createEmptyBorder(50, 40, 40, 40));

		JLabel title = new JLabel("Quiz complete", SwingConstants.CENTER);
		title.setFont(title.getFont().deriveFont(Font.BOLD, 28f));
		add(title, BorderLayout.NORTH);

		JPanel details = new JPanel(new GridLayout(0, 1, 0, 12));
		JLabel student = new JLabel("Student: " + result.getStudentName(), SwingConstants.CENTER);
		JLabel score = new JLabel("Score: " + result.getScore() + " / " + result.getTotalQuestions(),
				SwingConstants.CENTER);
		score.setFont(score.getFont().deriveFont(Font.BOLD, 24f));
		JLabel percentage = new JLabel(String.format("Percentage: %.1f%%", result.getPercentage()),
				SwingConstants.CENTER);
		percentage.setFont(percentage.getFont().deriveFont(18f));
		details.add(student);
		details.add(score);
		details.add(percentage);
		add(details, BorderLayout.CENTER);

		JButton homeButton = new JButton("Return to results");
		homeButton.addActionListener(event -> returnHome.run());
		JPanel actions = new JPanel();
		actions.add(homeButton);
		add(actions, BorderLayout.SOUTH);
	}
}
