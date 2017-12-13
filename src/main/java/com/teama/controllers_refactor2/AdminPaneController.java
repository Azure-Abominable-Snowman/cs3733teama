package com.teama.controllers_refactor2;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXTabPane;
import com.teama.controllers_refactor2.HamburgerController;
import com.teama.requestsubsystem.GenericRequest;
import com.teama.requestsubsystem.interpreterfeature.InterpreterRequest;
import com.teama.translator.Translator;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.logging.Logger;

import com.teama.requestsubsystem.requestCard;
import com.teama.login.LoginSubsystem;
import com.teama.requestsubsystem.interpreterfeature.InterpreterSubsystem;
import javafx.fxml.FXMLLoader;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;

import javax.swing.*;
import javax.swing.text.html.ImageView;

public class AdminPaneController extends HamburgerController{
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
    private AnchorPane mainPane;
    @FXML private AnchorPane apnToLoad;
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
        loadPane(new RequestsController());
    }

    @FXML
    void onMapClick(ActionEvent event) {
        loadPane(new MapEditorController());
    }

    @FXML
    void onRequestClick(ActionEvent event) {
       loadPane(new RequestsController());
    }

    @FXML
    void onSettingsClick(ActionEvent event) {
        loadPane(new SettingsPopOut());
    }

    @FXML
    void onStaffClick(ActionEvent event) {
        StaffController rc = new StaffController();
        loadPane(rc);
    }

    @FXML
    void mouseclick(ActionEvent event) {

    }

    private void loadPane(StaffToolController controller) {
        try {
            apnToLoad.getChildren().clear();
            FXMLLoader mapLoader = new FXMLLoader(getClass().getResource(controller.getFXMLPath()));
            mapLoader.setResources(Translator.getInstance().getNewBundle());
            mapLoader.setController(controller);
            mapLoader.load();
            apnToLoad.getChildren().add(controller.getParentPane());
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    @FXML private void onBackClick(ActionEvent e){
        this.closing.set(true);
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
    public void onOpen(){}
    public void onClose(){}
    public Pane getParentPane(){
        System.out.println(mainPane);
        return mainPane;}

    @Override
    public String getFXMLPath() {
        return "/MainScreenDrawers/AdminPane.fxml";
    }
}
