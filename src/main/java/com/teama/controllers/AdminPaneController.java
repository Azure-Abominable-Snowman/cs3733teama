package com.teama.controllers;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXTabPane;
import com.teama.requestsubsystem.GenericRequest;
import com.teama.requestsubsystem.interpreterfeature.InterpreterRequest;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import java.awt.event.MouseEvent;
import java.net.URL;
import java.sql.Connection;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.logging.Logger;

import com.teama.requestsubsystem.requestCard;
import com.teama.login.LoginSubsystem;
import com.teama.requestsubsystem.interpreterfeature.InterpreterSubsystem;

public class AdminPaneController{
    private final Logger log = Logger.getLogger(this.getClass().getPackage().getName());
    InterpreterSubsystem interpreterSubsystem = InterpreterSubsystem.getInstance();

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private JFXButton request;

    @FXML
    private JFXButton staff;

    @FXML
    private JFXButton tool;

    @FXML
    private JFXButton setting;

    @FXML
    private JFXTabPane tabepane;

    @FXML
    void Mapclick(ActionEvent event) {

    }

    @FXML
    void Requestclick(ActionEvent event) {

    }

    @FXML
    void Settingclick(ActionEvent event) {

    }

    @FXML
    void Staffclick(ActionEvent event) {

    }

    @FXML
    void mouseclick(MouseEvent event) {

    }

    @FXML
    private JFXListView<String> requestList;

    private int staffID;
    private Connection conn = null;
    private Statement stmt = null;
    ArrayList<InterpreterRequest> interpreterRequestList;
    ArrayList<GenericRequest> genericRequestList;

    public void initialize(){
        int staffID = LoginSubsystem.getInstance().getSystemUser().getStaffID();

        //TODO:get request in the database based on the currently logged in user
        interpreterRequestList = interpreterSubsystem.getInterpreterRequestsByStaff(staffID);

        for(InterpreterRequest IR: interpreterRequestList){
            requestList.getItems().add(IR.toString());
        }
        interpreterRequestList.clear();
    }

    public void updateStaffRequestList(){
        requestList.getItems().clear();
        interpreterRequestList = interpreterSubsystem.getInterpreterRequestsByStaff(staffID);

        for(InterpreterRequest IR: interpreterRequestList){
            requestList.getItems().add(IR.toString());
        }
        interpreterRequestList.clear();
    }

    public void getStaffRequest(){
        requestList.getItems().clear();
        //genericRequestList = interpreterSubsystem.getGenericRequest();
    }

}
