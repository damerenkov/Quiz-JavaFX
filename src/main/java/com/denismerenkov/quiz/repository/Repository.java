package com.denismerenkov.quiz.repository;

import com.denismerenkov.quiz.App;
import com.denismerenkov.quiz.model.Quiz;
import com.denismerenkov.quiz.model.Result;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.scene.control.Alert;
import org.apache.commons.lang3.StringEscapeUtils;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class Repository {
    private Quiz quiz = new Quiz();
    private ObjectMapper objectMapper = new ObjectMapper();

    public Repository(String urlName) throws IOException {
        URL url = new URL(urlName);
        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()))) {
            this.quiz = this.objectMapper.readValue(reader, new TypeReference<>() {
            });
            for (Result result : this.quiz.getResults()) {
                result.setQuestion(StringEscapeUtils.unescapeHtml4(result.getQuestion()));
                result.setCorrectAnswer(StringEscapeUtils.unescapeHtml4(result.getCorrectAnswer()));
                List<String> incorrect = new ArrayList<>();
                for (String incorrectAnswer : result.getIncorrectAnswers()) {
                    incorrectAnswer = StringEscapeUtils.unescapeHtml4(incorrectAnswer);
                    incorrect.add(incorrectAnswer);
                }
                result.setIncorrectAnswers(incorrect);
            }
        }
    }

    public Repository(File file) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            if (file.getName().endsWith(".json")) {
                this.quiz = this.objectMapper.readValue(reader, Quiz.class);
                decryptQuiz(this.quiz);
            } else if (file.getName().endsWith(".csv")) {
                ArrayList<Result> results = new ArrayList<>();
                while (reader.ready()) {
                    String s = reader.readLine();
                    String[] split = s.split(";");
                    List<String> list = new ArrayList<>();
                    list.add(split[5]);
                    list.add(split[6]);
                    list.add(split[7]);
                    Result result = new Result(split[0], split[1], split[2], split[3], split[4], list);
                    results.add(result);
                }
                this.quiz.setResults(results);
                System.out.println(this.quiz);
                decryptQuiz(this.quiz);
                System.out.println(this.quiz);
            } else {
                App.showAlert("Error", "Incorrect file type", Alert.AlertType.ERROR);
            }
        }
    }

    public Quiz getQuiz() {
        return quiz;
    }

    public void saveJson(File file) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            encryptQuiz(this.quiz);
            this.objectMapper.writeValue(writer, this.quiz);
        }
    }
    public void saveCSV(File file) throws IOException {
        encryptQuiz(this.quiz);
        Files.write(file.toPath(), this.quiz.quizFormatCSV());
    }

    private static void decryptQuiz(Quiz quiz) {
        for (Result result : quiz.getResults()) {
            String correctAnswer = result.getCorrectAnswer();
            result.setCorrectAnswer(decrypt(correctAnswer));
            List<String> incorrectAnswers = new ArrayList<>();
            for (String incorrectAnswer : result.getIncorrectAnswers()) {
                incorrectAnswers.add(decrypt(incorrectAnswer));
            }
            result.setIncorrectAnswers(incorrectAnswers);
        }
    }
    private static void encryptQuiz(Quiz quiz){
        for (Result result : quiz.getResults()) {
            String correctAnswer = result.getCorrectAnswer();
            result.setCorrectAnswer(encrypt(correctAnswer));
            List<String> incorrectAnswers = new ArrayList<>();
            for (String incorrectAnswer : result.getIncorrectAnswers()) {
                incorrectAnswers.add(encrypt(incorrectAnswer));
            }
            result.setIncorrectAnswers(incorrectAnswers);
        }
    }

    private static String decrypt(String text) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < text.length(); i++) {
            char c = caesarCipherCharDecrypt(text.charAt(i));
            builder.append(c);
        }
        return builder.toString();
    }
    private static String encrypt(String text){
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < text.length(); i++) {
            char c = caesarCipherCharEncrypt(text.charAt(i));
            builder.append(c);
        }
        return builder.toString();
    }

    public static char caesarCipherCharEncrypt(char c) {
        int res;
        if (c > 'W' && c <= 'Z' || c > 'w' && c <= 'z') {
            int diff = 'z' - 'c';
            res = c - diff;
        } else if (c > '6' && c <= '9'){
            int diff = '9' - '2';
            res = c - diff;
        } else {
            res = c + 3;
        }
        return (char) res;
    }
    public static char caesarCipherCharDecrypt(char c) {
        int res;
        if (c < 'D' && c >= 'A' || c < 'd' && c >= 'a') {
            int diff = 'c' - 'z';
            res = c - diff;
        } else if (c < '3' && c >= '0'){
            int diff = '2' - '9';
            res = c - diff;
        }else {
            res = c - 3;
        }
        return (char) res;
    }

}