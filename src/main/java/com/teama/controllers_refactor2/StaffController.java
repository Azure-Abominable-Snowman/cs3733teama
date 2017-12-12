package com.teama.controllers_refactor2;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.Pane;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;

import java.io.IOException;

public class StaffController extends StaffToolController{

    @FXML
    private Pane parentPane;

    @FXML
    private AnchorPane interpPane;

    @FXML
    private AnchorPane SpritPane;

    @FXML
    private AnchorPane ElevatorPane;

    @FXML
    void interchange(ActionEvent event) {
        loadPane(new InterpreterModController());

    }

    private void loadPane(InterpreterModController controller) {
        try {
            interpPane.getChildren().clear();
            FXMLLoader mapLoader = new FXMLLoader(getClass().getResource(controller.getFXMLPath()));
            mapLoader.setController(controller);
            mapLoader.load();
            interpPane.getChildren().add(controller.getParentPane());
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    @Override
    public Pane getParentPane() {
        return parentPane;
    }

    @Override
    public String getFXMLPath() {
        return "/MainScreenDrawers/StaffEditor.fxml";
    }
}
