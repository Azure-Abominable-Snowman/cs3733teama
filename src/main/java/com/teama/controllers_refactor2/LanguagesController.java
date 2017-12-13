package com.teama.controllers_refactor2;

import com.jfoenix.controls.JFXButton;
import java.net.URL;
import java.util.ResourceBundle;

import com.teama.translator.Translator;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

public class LanguagesController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private JFXButton englishBtn;

    @FXML
    private JFXButton germanBtn;

    @FXML
    private JFXButton spanishBtn;

    @FXML
    private JFXButton frenchBtn;

    @FXML
    private JFXButton chineseBtn;

    @FXML
    private JFXButton portugueseBtn;

    @FXML
    private JFXButton russianBtn;

    @FXML
    void onChineseBtnclicked(ActionEvent event) {
        Translator.getInstance().setLang("zh-Hans");

    }

    @FXML
    void onEngBtnClicked(ActionEvent event) {
        Translator.getInstance().setLang("en");

    }

    @FXML
    void onFrenchBtnClicked(ActionEvent event) {
        Translator.getInstance().setLang("fr");

    }

    @FXML
    void onGermanBtnClicked(ActionEvent event) {
        Translator.getInstance().setLang("de");

    }

    @FXML
    void onPortugueseBtnClicked(ActionEvent event) {
        Translator.getInstance().setLang("pt");

    }

    @FXML
    void onRussianBtnClicked(ActionEvent event) {
        Translator.getInstance().setLang("ru");

    }

    @FXML
    void onSpanishBtnClicked(ActionEvent event) {
        Translator.getInstance().setLang("es");

    }

    @FXML
    void initialize() {


    }
}
