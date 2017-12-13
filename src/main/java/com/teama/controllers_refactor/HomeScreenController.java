package com.teama.controllers_refactor;

import com.jfoenix.controls.JFXButton;
import com.teama.translator.Translator;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

public class HomeScreenController {

    @FXML
    private JFXButton english;

    @FXML
    private JFXButton Spanish;

    @FXML
    private JFXButton french;

    @FXML
    private JFXButton portuguese;

    @FXML
    void onEnglishClick(ActionEvent event) {
        Translator.getInstance().setLang("en");
        Translator.getInstance().getText("about");
        Translator.getInstance().getText("help");

    }

    @FXML
    void onFrenchClick(ActionEvent event) {
        Translator.getInstance().setLang("fr");
        Translator.getInstance().getText("about");
        Translator.getInstance().getText("help");

    }

    @FXML
    void onPortugueseClick(ActionEvent event) {
        Translator.getInstance().setLang("pt");
        Translator.getInstance().getText("about");
        Translator.getInstance().getText("help");

    }

    @FXML
    void onSpanishClick(ActionEvent event) {
        Translator.getInstance().setLang("es");
        Translator.getInstance().getText("about");
        Translator.getInstance().getText("help");

    }

}
