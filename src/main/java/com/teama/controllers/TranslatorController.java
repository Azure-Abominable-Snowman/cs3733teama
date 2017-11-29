package com.teama.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.fxml.Initializable;
import javafx.event.ActionEvent;

import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;

import java.util.ListResourceBundle;

import javafx.application.Application;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class TranslatorController implements Initializable {
/*
    private boolean isEnglish = true;
    private boolean isSpanish = true;
    private boolean isFrench = true;
*/
    @FXML
    private ResourceBundle bundle;
    private Locale locale;


    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    @FXML
    private Button setEnglish;

    @FXML
    private Button setFrench;

    @FXML
    private Button setSpanish;

    @FXML
    private Label showTranslated;

    @FXML
    void btnEN(ActionEvent event) {
        /*if (isEnglish) {
            isEnglish = false;
            loadLang("en");
        } else if (!isEnglish) {
            isEnglish = true;
        }*/
        loadLang("en");
    }

    @FXML
    void btnFR(ActionEvent event) {
        /*if (isFrench) {
            isEnglish = false;
            loadLang("fr");
        } else if (!isFrench) {
            isFrench = true;
        }*/
        loadLang("fr");
    }

    @FXML
    void btnSP(ActionEvent event) {
        /*if (isSpanish) {
            isEnglish = false;
            loadLang("es");
        } else if (!isSpanish) {
            isSpanish = true;
        }*/
        loadLang("es");
    }


    private void loadLang(String lang) {
        locale = new Locale(lang);
        bundle = ResourceBundle.getBundle("lang", locale);
        showTranslated.setText(bundle.getString("greeting"));
    }
}