package com.teama.controllers;

/**
 * Created by jakepardue on 11/28/17.
 */

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListView;
import com.teama.messages.EmailMessage;
import com.teama.messages.Message;
import com.teama.messages.SMSMessage;
import com.teama.requestsubsystem.interpreterfeature.InterpreterStaff;
import com.teama.requestsubsystem.interpreterfeature.InterpreterSubsystem;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class FulfillRequestController {

    private InterpreterStaff staffToFulfill;
    private String staffID = " ";
    private ArrayList<InterpreterStaff> possibleStaff;
    private Message message;

    @FXML
    private JFXListView<InterpreterStaff> listView;

    @FXML
    private JFXButton backButton;

    @FXML
    private JFXButton assignButton;

    @FXML
    private Label reqType;

    @FXML
    private Label loc;

    @FXML
    private Label note;

    @FXML
    private Label id;

    @FXML
    private Label priorityLevel;

    @FXML
    public void onSelect(MouseEvent e){
        System.out.println(listView.getSelectionModel().getSelectedItem());
        staffToFulfill = listView.getSelectionModel().getSelectedItem();
    }

    @FXML
    void fulfill(ActionEvent event) {
        System.out.println(staffToFulfill);
        SMSMessage message1 = new SMSMessage(staffToFulfill.getProvider(), staffToFulfill.getPhoneNumber());
        if(!message1.sendMessage(staffToFulfill.getContactInfo(),message)){
            EmailMessage message2 = new EmailMessage();
            message2.sendMessage(staffToFulfill.getContactInfo(),message);
        }
    }

    @FXML
    void onBack(ActionEvent event) {
        //close pop-up window

    }

    public void initialize() {
        //fill ListView with Interpreter staff
        ArrayList<InterpreterStaff> possibleStaff = InterpreterSubsystem.getInstance().findQualified(null);
        for (InterpreterStaff s : possibleStaff) {
            listView.getItems().add(s);
        }
    }




}
