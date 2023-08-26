package com.denismerenkov.quiz.controllers;

import com.denismerenkov.quiz.App;
import com.denismerenkov.quiz.model.Quiz;
import com.denismerenkov.quiz.model.Result;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.prefs.Preferences;

public class GameFormController implements ControllerData<Quiz> {
    @FXML
    public TabPane questionMenu;
    private Quiz quiz;
    private List<String> correctAnswers = new ArrayList<>();
    private Map<String, ToggleGroup> answers = new LinkedHashMap<>();


    @Override
    public void initData(Quiz data) {
        this.quiz = data;
        for (int i = 0; i < this.quiz.getResults().size(); i++) {
            Result result = this.quiz.getResults().get(i);
            Tab tab = new Tab("Q" + (i + 1));
            VBox radioButtons = createRadioButtons(result, "Q" + (i + 1));
            tab.setContent(radioButtons);
            tab.getContent().prefHeight(100);
            tab.getContent().prefWidth(200);
            this.questionMenu.getTabs().add(tab);
        }
        this.questionMenu.getTabs().add(resultTab());
    }


    private VBox createRadioButtons(Result result, String questionNumber) {
        VBox box = new VBox();
        String question = result.getQuestion();
        if(question.length() > 70){
            StringBuilder builder = new StringBuilder();
            AtomicInteger count = new AtomicInteger();
            String[] s = question.split(" ");
            Arrays.stream(s).forEach(s1 -> {
                if(count.get() > 65){
                    builder.append("\n");
                    count.set(0);
                }
                builder.append(s1);
                builder.append(" ");
                count.addAndGet(s1.length());
            });
            question = builder.toString();
        }
        box.getChildren().add(new Label(question));

        box.setSpacing(10);
        box.setPadding(new Insets(50, 20, 10, 30));

        RadioButton correct = new RadioButton();
        correct.setText(result.getCorrectAnswer());
        List <RadioButton> buttons = new ArrayList<>();
        ToggleGroup tg = new ToggleGroup();
        this.correctAnswers.add(result.getCorrectAnswer());
        buttons.add(correct);
        for (String incorrectAnswer : result.getIncorrectAnswers()) {
            RadioButton incorrect = new RadioButton();
            incorrect.setText(incorrectAnswer);
            buttons.add(incorrect);
        }
        Collections.shuffle(buttons);
        for (RadioButton button : buttons) {
            button.setToggleGroup(tg);
            box.getChildren().add(button);
        }
        this.answers.put(questionNumber, tg);
        return box;
    }


    private Tab resultTab() {
        Tab res = new Tab("Result");

        Button checkButton = new Button();
        checkButton.setText("Check");
        checkButton.setTranslateX(250);
        checkButton.setTranslateY(50);
        res.setContent(checkButton);
        checkButton.setOnAction(actionEvent -> {
            VBox box = new VBox();
            box.setSpacing(5);
            box.setPadding(new Insets(30, 10, 10, 10));
            box.getChildren().add(new Label("Statistics:"));
            int correctCount = 0;
            Preferences prefs = Preferences.userRoot();
            String s = prefs.get("showAnswer", "false");
            System.out.println(s);
            for (Map.Entry<String, ToggleGroup> entry : answers.entrySet()) {
                if (entry.getValue().getSelectedToggle() != null) {
                    RadioButton selectedToggle = (RadioButton) entry.getValue().getSelectedToggle();
                    if (correctAnswers.contains(selectedToggle.getText())) {
                        box.getChildren().add(new Label(entry.getKey() + ": +"));
                        correctCount++;
                    } else {
                        if (s.equals("true")) {
                            String key = entry.getKey();
                            String[] split = key.split("");
                            box.getChildren().add(new Label(entry.getKey() + ": -   "
                                    + correctAnswers.get(Integer.parseInt(split[1]) - 1)));
                        } else {
                            box.getChildren().add(new Label(entry.getKey() + ": -"));
                        }
                    }
                } else {
                    App.showAlert("Error", "Answer for all the questions", Alert.AlertType.ERROR);
                    return;
                }
            }
            box.getChildren().add(
                    new Label("Correct/Incorrect: " + correctCount + "/" + (answers.size() - correctCount)));
            box.getChildren().add(
                    new Label("Correct Answers Rate:" + (correctCount * 100) / answers.size() + "%"));
            res.setContent(box);
            prefs.put("showAnswer", "false");
        });
        return res;
    }
}
