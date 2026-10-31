package ui;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

public class RegistrationPage extends JPanel {
    private final JTextField nameField = new JTextField(24);
    private final JTextField rollNumberField = new JTextField(24);
    private final JTextField courseField = new JTextField(24);

    public RegistrationPage(StudentRegistrationHandler register, Runnable cancel) {
        super(new BorderLayout(12, 16));
        setBorder(BorderFactory.createEmptyBorder(36, 40, 30, 40));

        JLabel title = new JLabel("Student registration");
        title.setFont(title.getFont().deriveFont(java.awt.Font.BOLD, 24f));
        add(title, BorderLayout.NORTH);

        JPanel fields = new JPanel(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(10, 4, 10, 12);
        constraints.anchor = GridBagConstraints.LINE_START;
        constraints.gridx = 0;
        constraints.gridy = 0;
        fields.add(new JLabel("Full name"), constraints);
        constraints.gridx = 1;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        fields.add(nameField, constraints);
        constraints.gridx = 0;
        constraints.gridy++;
        constraints.fill = GridBagConstraints.NONE;
        fields.add(new JLabel("Roll number"), constraints);
        constraints.gridx = 1;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        fields.add(rollNumberField, constraints);
        constraints.gridx = 0;
        constraints.gridy++;
        constraints.fill = GridBagConstraints.NONE;
        fields.add(new JLabel("Course"), constraints);
        constraints.gridx = 1;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        fields.add(courseField, constraints);
        add(fields, BorderLayout.CENTER);

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(event -> cancel.run());
        JButton registerButton = new JButton("Register and continue");
        registerButton.addActionListener(event -> {
            String name = nameField.getText().trim();
            String rollNumber = rollNumberField.getText().trim();
            String course = courseField.getText().trim();
            if (name.isBlank() || rollNumber.isBlank() || course.isBlank()) {
                javax.swing.JOptionPane.showMessageDialog(this, "Complete all fields before continuing.",
                        "Missing information", javax.swing.JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (name.length() > 100 || rollNumber.length() > 30 || course.length() > 100) {
                javax.swing.JOptionPane.showMessageDialog(this,
                        "Name and course must be 100 characters or fewer; roll number must be 30 characters or fewer.",
                        "Input is too long", javax.swing.JOptionPane.WARNING_MESSAGE);
                return;
            }
            register.register(name, rollNumber, course);
        });
        JPanel actions = new JPanel();
        actions.add(cancelButton);
        actions.add(registerButton);
        add(actions, BorderLayout.SOUTH);
    }

    @FunctionalInterface
    public interface StudentRegistrationHandler {
        void register(String name, String rollNumber, String course);
    }
}