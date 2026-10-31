package service;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import config.AppConfig;
import model.Question;
import org.apache.commons.text.StringEscapeUtils;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class QuestionApiService {
    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    public List<Question> fetchQuestions() throws IOException, InterruptedException {
        String url = AppConfig.get("api.url", "QUIZ_API_URL",
                "https://opentdb.com/api.php?amount=10&category=9&type=multiple");
        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofSeconds(20))
                .header("Accept", "application/json")
                .header("User-Agent", "QuizExaminationSystem/1.0")
                .GET();
        String apiKey = AppConfig.get("api.key", "QUIZ_API_KEY", "");
        if (!apiKey.isBlank() && !apiKey.equals("YOUR_API_KEY_HERE")) {
            requestBuilder.header("X-API-Key", apiKey);
        }

        HttpResponse<String> response = httpClient.send(requestBuilder.build(), HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() < 200 || response.statusCode() >= 300) {
            throw new IOException("Question API returned HTTP " + response.statusCode() + ".");
        }

        JsonObject payload;
        try {
            payload = JsonParser.parseString(response.body()).getAsJsonObject();
        } catch (RuntimeException exception) {
            throw new IOException("Question API returned an invalid response.", exception);
        }
        int responseCode = payload.has("response_code") ? payload.get("response_code").getAsInt() : -1;
        if (responseCode != 0) {
            throw new IOException("Question API could not provide questions (response code " + responseCode + ").");
        }

        JsonArray results = payload.getAsJsonArray("results");
        List<Question> questions = new ArrayList<>();
        for (JsonElement element : results) {
            JsonObject item = element.getAsJsonObject();
            String correctAnswer = decode(item.get("correct_answer").getAsString());
            List<String> options = new ArrayList<>();
            options.add(correctAnswer);
            for (JsonElement incorrect : item.getAsJsonArray("incorrect_answers")) {
                options.add(decode(incorrect.getAsString()));
            }
            if (options.size() != 4) {
                continue;
            }
            Collections.shuffle(options);
            questions.add(new Question(decode(item.get("question").getAsString()), options, correctAnswer));
        }
        if (questions.size() != 10) {
            throw new IOException("The question API returned " + questions.size() + " usable questions; 10 are required.");
        }
        return questions;
    }

    private String decode(String value) {
        return StringEscapeUtils.unescapeHtml4(value);
    }
}