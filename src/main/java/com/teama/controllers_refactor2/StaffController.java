package com.teama.controllers_refactor2;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Tab;
import javafx.scene.layout.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.layout.Pane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;

import java.io.IOException;

public class StaffController extends StaffToolController{

    @FXML
    private Pane parentPane;

    @FXML
    private VBox intVBox;

    @FXML
    private VBox spirVBox;

    @FXML
    private VBox eleVBox;

    @FXML
    void interchange(ActionEvent event) {
    }

    public void initialize(){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/MaintananceStaffPopOut.fxml"));
            loader.load();
            IntStaffController myController = loader.getController();
            intVBox.getChildren().clear();
            intVBox.getChildren().add(myController.getVbxTest());
            System.out.println(myController.getVbxTest().getChildren().size());
            System.out.println(intVBox);
            FXMLLoader loader2 =new FXMLLoader(getClass().getResource("/MainScreenDrawers/testVbox.fxml"));
            loader2.load();
            IntStaffController myController2 = loader2.getController();
            spirVBox.getChildren().clear();
            spirVBox.getChildren().addAll(myController2.getVbxTest());

        }
        catch(IOException error){
            error.printStackTrace();
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
