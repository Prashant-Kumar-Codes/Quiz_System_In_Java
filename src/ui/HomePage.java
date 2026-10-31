package ui;

import model.QuizResult;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class HomePage extends JPanel {
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private final DefaultTableModel tableModel = new DefaultTableModel(
            new Object[]{"Date", "Student", "Roll number", "Score", "Percentage"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private final JLabel statusLabel = new JLabel(" ");

    public HomePage(Runnable startQuiz, Runnable refresh) {
        super(new BorderLayout(16, 16));
        setBorder(javax.swing.BorderFactory.createEmptyBorder(24, 28, 20, 28));

        JLabel title = new JLabel("Quiz Examination System", SwingConstants.LEFT);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 26f));
        JLabel subtitle = new JLabel("General Knowledge · 10 questions");
        subtitle.setFont(subtitle.getFont().deriveFont(14f));

        JPanel heading = new JPanel(new BorderLayout(0, 6));
        heading.add(title, BorderLayout.NORTH);
        heading.add(subtitle, BorderLayout.CENTER);
        add(heading, BorderLayout.NORTH);

        JTable resultsTable = new JTable(tableModel);
        resultsTable.setRowHeight(28);
        resultsTable.setFillsViewportHeight(true);
        resultsTable.getTableHeader().setReorderingAllowed(false);
        JScrollPane tableScrollPane = new JScrollPane(resultsTable);
        tableScrollPane.setPreferredSize(new Dimension(700, 350));
        add(tableScrollPane, BorderLayout.CENTER);

        JButton startButton = new JButton("Start quiz");
        startButton.addActionListener(event -> startQuiz.run());
        JButton refreshButton = new JButton("Refresh results");
        refreshButton.addActionListener(event -> refresh.run());
        JPanel actions = new JPanel(new BorderLayout(12, 0));
        actions.add(statusLabel, BorderLayout.CENTER);
        JPanel buttons = new JPanel();
        buttons.add(refreshButton);
        buttons.add(startButton);
        actions.add(buttons, BorderLayout.EAST);
        add(actions, BorderLayout.SOUTH);
    }

    public void setResults(List<QuizResult> results) {
        tableModel.setRowCount(0);
        for (QuizResult result : results) {
            tableModel.addRow(new Object[]{
                    result.getAttemptDate().format(DATE_FORMAT),
                    result.getStudentName(),
                    result.getRollNumber(),
                    result.getScore() + " / " + result.getTotalQuestions(),
                    String.format("%.1f%%", result.getPercentage())
            });
        }
    }

    public void setStatus(String status) {
        statusLabel.setText(status);
    }
}