package com.teama.controllers;

import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextArea;
import com.teama.requestsubsystem.Request;
import com.teama.requestsubsystem.RequestType;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class FeatureAccdnController implements Initializable {
    //this is for the Create a request tab
    @FXML
    JFXComboBox<String> cmbBuilding;
    @FXML
    JFXComboBox<String> cmbFloor;
    @FXML
    JFXComboBox<String> cmbLocation;
    @FXML
    JFXTextArea txtAdditionalInfo;
    @FXML
    JFXComboBox<String> cmbType;
    @FXML
    VBox createReqBox;
    private AnchorPane curReqPane;
    public void initialize(URL url, ResourceBundle rb){
        cmbType.setItems(FXCollections.observableArrayList(RequestType.INTR.getValue(), RequestType.SEC.getValue()));
        curReqPane=null;
    }

    @FXML
    public void onRequestSelected(){
        try {
            System.out.println("Firing");
            if(cmbType.getSelectionModel().getSelectedItem().equals(RequestType.INTR.getValue())){
                System.out.println("Firing");
                AnchorPane interpParent = FXMLLoader.load(getClass().getResource("/InterpreterReq.fxml"));
                if(curReqPane!=interpParent) {
                    createReqBox.getChildren().remove(curReqPane);
                    createReqBox.getChildren().add(interpParent);
                    curReqPane=interpParent;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
