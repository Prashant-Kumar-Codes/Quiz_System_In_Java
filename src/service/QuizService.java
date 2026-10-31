package service;

import model.Question;

import java.util.List;

public class QuizService {
    public int calculateScore(List<Question> questions, List<Integer> selectedOptions) {
        int score = 0;
        for (int questionIndex = 0; questionIndex < questions.size(); questionIndex++) {
            Integer selectedOption = questionIndex < selectedOptions.size()
                    ? selectedOptions.get(questionIndex) : null;
            int selectedIndex = selectedOption == null ? -1 : selectedOption;
            if (selectedIndex >= 0 && selectedIndex < questions.get(questionIndex).getOptions().size()
                    && questions.get(questionIndex).getOptions().get(selectedIndex)
                    .equals(questions.get(questionIndex).getCorrectAnswer())) {
                score++;
            }
        }
        return score;
    }
}