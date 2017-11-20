package com.teama.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class FulfillReqController implements Controller {
    @Override
    public String getFXMLFileName() {
        return "FulfillRequest.fxml";
    }

    @FXML
    Button back;

    @FXML
    private void backClick(ActionEvent event){
        SceneEngine.closePopOut();
    }
}
