package com.denismerenkov.quiz.controllers;

import com.denismerenkov.quiz.App;
import com.denismerenkov.quiz.model.Quiz;
import com.denismerenkov.quiz.repository.Repository;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.prefs.Preferences;

public class LoadingFormController {
    public Preferences prefs;
    private Map<String, Integer> category = new LinkedHashMap<>();
    @FXML
    public ComboBox<String> categoryList;
    @FXML
    public ComboBox<String> difficultList;
    public TextField numberQuestions;

    public void initialize() {
        ArrayList<String> difficult = new ArrayList<>();
        difficult.add("Easy");
        difficult.add("Medium");
        difficult.add("Hard");
        this.difficultList.setItems(FXCollections.observableList(difficult));


        this.category.put("Animals", 27);
        this.category.put("Celebrities", 26);
        this.category.put("Geography", 22);
        this.category.put("Sports", 21);
        this.categoryList.setItems(FXCollections.observableList(this.category.keySet().stream().toList()));
    }

    public void startQuizButton(ActionEvent actionEvent) {
        String numberQuestions = this.numberQuestions.getText();
        String category = this.categoryList.getSelectionModel().getSelectedItem();
        String difficult = this.difficultList.getSelectionModel().getSelectedItem();
        if (numberQuestions != null && category != null && difficult != null) {
            try {
                int numberQuestionsInt = Integer.parseInt(numberQuestions);
                if (numberQuestionsInt > 10 || numberQuestionsInt < 1) {
                    App.showAlert("Error", "Select the number of questions from 1 to 10", Alert.AlertType.ERROR);
                    return;
                }
                String requestURL = "https://opentdb.com/api.php?amount=" + numberQuestions
                        + "&category=" + this.category.get(category)
                        + "&difficulty=" + difficult.toLowerCase() + "&type=multiple";

                try {
                    Quiz quiz = new Repository(requestURL).getQuiz();
                    App.openWindow("gameForm.fxml", "Game Form", quiz);
                    App.closeWindow(actionEvent);
                } catch (IOException e) {
                    App.showAlert("Error", "Incorrect data", Alert.AlertType.ERROR);
                }
            } catch (NumberFormatException e) {
                App.showAlert("Error", "Incorrect data type", Alert.AlertType.ERROR);
            }
        } else {
            App.showAlert("Error", "Select quiz configuration", Alert.AlertType.ERROR);
        }
    }

    public void saveQuizButton(ActionEvent actionEvent) {
        String numberQuestions = this.numberQuestions.getText();
        try {
            int numberQuestionsInt = Integer.parseInt(numberQuestions);
            if (numberQuestionsInt > 10 || numberQuestionsInt < 1) {
                App.showAlert("Error", "Select the number of questions from 1 to 10", Alert.AlertType.ERROR);
            }
            String category = this.categoryList.getSelectionModel().getSelectedItem();
            String difficult = this.difficultList.getSelectionModel().getSelectedItem();
            String requestURL = "https://opentdb.com/api.php?amount=" + numberQuestions
                    + "&category=" + this.category.get(category)
                    + "&difficulty=" + difficult.toLowerCase() + "&type=multiple";
            try {
                Repository repository = new Repository(requestURL);
                FileChooser fileChooser = new FileChooser();
                FileChooser.ExtensionFilter extFilterJson =
                        new FileChooser.ExtensionFilter("Json file *.json", "*.json");
                fileChooser.getExtensionFilters().add(extFilterJson);
                FileChooser.ExtensionFilter extFilterCSV =
                        new FileChooser.ExtensionFilter("CSV file *.csv", "*.csv");
                fileChooser.getExtensionFilters().add(extFilterCSV);
                this.prefs = Preferences.userRoot();
                File file = fileChooser.showSaveDialog(null);
                if (file != null) {
                    if (file.getName().endsWith(".json")) {
                        repository.saveJson(file);
                    } else if (file.getName().endsWith(".csv")) {
                        repository.saveCSV(file);
                    }
                    this.prefs.put("save", file.getParent());
                }
            } catch (IOException e) {
                App.showAlert("Error", "Incorrect data", Alert.AlertType.ERROR);
            }
        } catch (
                NumberFormatException e) {
            App.showAlert("Error", "Incorrect data type", Alert.AlertType.ERROR);
        }
    }

}
