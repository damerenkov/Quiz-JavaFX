module com.denismerenkov.quiz {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.prefs;
    requires com.fasterxml.jackson.annotation;
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.databind;
    requires java.desktop;
    requires org.apache.commons.lang3;


    opens com.denismerenkov.quiz to javafx.fxml;
    exports com.denismerenkov.quiz;
    exports com.denismerenkov.quiz.controllers;
    exports com.denismerenkov.quiz.model;
    exports com.denismerenkov.quiz.repository;
    opens com.denismerenkov.quiz.controllers to javafx.fxml;
}