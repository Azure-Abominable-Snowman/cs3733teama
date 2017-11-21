package com.teama.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;

public class StaffLoginController implements Controller {

    @Override
    public String getFXMLFileName() {
        return "StaffLogIn.fxml";
    }

    @FXML
    private AnchorPane loginPlane;

    @FXML
    private GridPane loginGrid;

    @FXML
    private Text AdminID;

    @FXML
    private Text passWord;

    @FXML
    private TextField IDfield;

    @FXML
    private TextField passwordField;

    @FXML
    private Button login;

    @FXML
    private Button reset;

    @FXML
    private Text admintext;

    @FXML
    private Label loginMessage;


    @FXML
    private void onBackClick(ActionEvent event){
        SceneEngine.display(MainScreenController_old.class, null);
        SceneEngine.closeLogin();
    }

    private String adminID = "admin";
    private String adminPassword = "adminPW";
    private String inputUserID, inputUserPassword;



    @FXML
    private void onLoginClick(ActionEvent event){
        inputUserID = IDfield.getText();
        inputUserPassword = passwordField.getText();
        identify();
    }

    @FXML
    private void onEnterPressed(KeyEvent ke){
        if (ke.getCode().equals(KeyCode.ENTER))
        {
            inputUserID = IDfield.getText();
            inputUserPassword = passwordField.getText();
            identify();
        }
    }



    private void identify(){
        if (inputUserID.equals(adminID) && inputUserPassword.equals(adminPassword)){
            System.out.println("succeed");
            SceneEngine.setAdminStatus(true);
            SceneEngine.closeLogin();
            SceneEngine.display(MainScreenController_old.class, null);
        } else{
            System.out.println("failed");
            loginMessage.setText("Incorrect user or pw.");
            IDfield.setText("");
            passwordField.setText("");
        }
    }
    @FXML
    private void onResetClick(ActionEvent event){
        IDfield.setText("");
        passwordField.setText("");
    }




}
