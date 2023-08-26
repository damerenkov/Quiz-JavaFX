package com.denismerenkov.quiz.controllers;

import com.denismerenkov.quiz.App;
import com.denismerenkov.quiz.repository.Repository;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.util.prefs.Preferences;

public class MainFormController {


    public CheckBox showCorrectAnswers;
    private Preferences prefs = Preferences.userRoot();

    /**
     * Объект Button с Label “From Internet”.
     * При нажатии кнопки должна запускаться Loading Form (см. пункт 2), реализующая загрузку викторины из интернета.
     * @param actionEvent
     */
    public void fromInternetButton(ActionEvent actionEvent) {
        try {
            if(this.showCorrectAnswers.isSelected()){
                this.prefs.put("showAnswer", "true");
            }
            App.closeWindow(actionEvent);
            App.openWindow("loadingForm.fxml", "Loading Form",
                    null);
        } catch (IOException e) {
            App.showAlert("Error", "Incorrect data", Alert.AlertType.ERROR);
        }
    }

    /**
     * Объект Button с Label “From File”.
     * При нажатии кнопки викторина должна будет загружаться из .json или .csv файла.
     * Для этого необходимо использовать объект класса FileChooser.ExtensionFilter.
     * Диалоговое окно для выбора папки с викториной должно запоминать ранее выбранный путь к папке,
     * а не открывать папку по умолчанию (для этого можно использовать класс Preferences).
     * После выбора файла должна запускаться форма, отвечающая за игру (Game Form).
     * @param actionEvent
     */
    public void fromFileButton(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extFilterJson = new FileChooser.ExtensionFilter("Json file *.json", "*.json");
        fileChooser.getExtensionFilters().add(extFilterJson);
        FileChooser.ExtensionFilter extFilterCSV = new FileChooser.ExtensionFilter("CSV file *.csv", "*.csv");
        fileChooser.getExtensionFilters().add(extFilterCSV);
        String s = this.prefs.get("save", "Not found");
        if(!s.equals("Not found")){
            fileChooser.setInitialDirectory(new File(s));
        }
        File file = fileChooser.showOpenDialog(null);
        if(file !=  null){
                try {
                    App.openWindow("gameForm.fxml", "Game Form", new Repository(file).getQuiz());
                } catch (IOException e) {
                    App.showAlert("Error", "Incorrect file type", Alert.AlertType.ERROR);
                }
            }

    }
}
