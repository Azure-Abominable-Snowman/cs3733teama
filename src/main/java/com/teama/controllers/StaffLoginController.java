package com.teama.controllers;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXRadioButton;
import com.jfoenix.controls.JFXTextField;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

public class StaffLoginController implements Controller {
    private final BooleanProperty isLoggedIn = new SimpleBooleanProperty();

    @Override
    public String getFXMLFileName() {
        return "LogIn2.fxml";
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

///

    @FXML
    private JFXButton loginButton;
    @FXML
    private JFXTextField uname;
    @FXML
    private JFXPasswordField pword;
    @FXML
    private JFXRadioButton admin, staff;
    @FXML
    private VBox vbox;
    @FXML
    private GridPane gPane;


    @FXML
    private void onBackClick(ActionEvent event){

    }

    public void initialize() {

        setLoggedIn(false);
        ToggleGroup radioButtons = new ToggleGroup();

        //radioButtons.getProperties().addListener;
        //admin.getProperties().
        admin.setSelectedColor(Color.DARKBLUE);
        staff.setSelectedColor(Color.DARKBLUE);
        radioButtons.getToggles().setAll(admin, staff);

        //Group radioButtons = new Group();
        //radioButtons.getChildren().setAll(admin, staff);
        //radioButtons.getChildren().
    }


    private String adminID = "admin";
    private String adminPassword = "adminPW";
    private String inputUserID, inputUserPassword;


    public BooleanProperty getLoggedInProperty() {
        return isLoggedIn;
    }

    public final boolean isLoggedIn() {
        return isLoggedIn.get();
    }

    public final void setLoggedIn(Boolean update) {
        isLoggedIn.set(update);
    }


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
            setLoggedIn(true);
            //this.get
            //isLoggedin = true;
            //SceneEngine.setAdminStatus(true);
            //SceneEngine.closeLogin();
            //SceneEngine.display(MainScreenController_old.class, null);
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
