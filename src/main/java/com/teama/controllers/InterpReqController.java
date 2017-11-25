package com.teama.controllers;

import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;

import com.teama.requestsubsystem.interpreterfeature.Language;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;

public class InterpReqController {
    @FXML
    AnchorPane parentPane;
    @FXML
    JFXTextField txtFamilySize;
    @FXML
    JFXComboBox<Language> cmbLang;
    public InterpReqController(){

    }
    public AnchorPane getParentPane() {
        return parentPane;
    }
}
