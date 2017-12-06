package com.teama.controllers;

/**
 * Created by jakepardue on 11/29/17.
 */
import com.jfoenix.controls.JFXListView;
import com.teama.messages.ContactInfo;
import com.teama.messages.ContactInfoTypes;
import com.teama.messages.Provider;
import com.teama.requestsubsystem.GenericStaff;
import com.teama.requestsubsystem.interpreterfeature.*;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.input.MouseEvent;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class ViewStaffController {

    @FXML
    private JFXListView<InterpreterStaff> staffViewList;

    private InterpreterStaff staffToFulfill;
    private Language language;
    private BooleanProperty isComplete= new SimpleBooleanProperty();

/*
    public void setRequestViewList(Language Lang){
        //fill ListView with Interpreter staff
        ArrayList<InterpreterStaff> possibleStaff = InterpreterSubsystem.getInstance().findQualified(Lang);
        for (InterpreterStaff s : possibleStaff) {
            staffViewList.getItems().add(s);
        }
    }

    */

    @FXML
    void onSelect(MouseEvent event) {
        staffToFulfill = staffViewList.getSelectionModel().getSelectedItem();
        System.out.println(staffToFulfill);
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Assignment");
        alert.setHeaderText("Confirm Assignment");
        alert.setContentText("Assign "+staffToFulfill.getFirstName() + " "
        +staffToFulfill.getLastName()+"?");
        Optional<ButtonType> result = alert.showAndWait();
        if(result.get()==ButtonType.OK){
            isComplete.set(true);
        }


    }
    public InterpreterStaff getStaffToFulfill(){return staffToFulfill;}

    public void setLanguage(Language lang){
        this.language= lang;
        staffViewList.getItems().clear();
        try {
            ArrayList<InterpreterStaff> staff = InterpreterSubsystem.getInstance().findQualified(lang);
            for (InterpreterStaff interpreter : staff) {
                staffViewList.getItems().add(interpreter);
            }
        } catch(NullPointerException e) {
            System.out.println("somethings fucked");
            e.printStackTrace();
        }
    }

    public BooleanProperty getIsComplete(){return isComplete;}
    public void setIsComplete(boolean complete){
        this.isComplete.set(complete);
    }

    private ArrayList<InterpreterStaff> getStaff(){

        Set<ContactInfoTypes> avail = new HashSet<ContactInfoTypes>();
        avail.add(ContactInfoTypes.EMAIL);
        avail.add(ContactInfoTypes.TEXT);
        avail.add(ContactInfoTypes.PHONE);
        ContactInfo c = new ContactInfo(avail, "6034893939", "jjpardue@wpi.edu", Provider.VERIZON);

        Set<Language> langs = new HashSet<>();
        langs.add(Language.ASL);
        langs.add(Language.French);
        langs.add(Language.Moldovan);
        langs.add(Language.JAVA);

        Set<Language> langs2 = new HashSet<>();
        langs2.add(Language.Moldovan);
        langs2.add(Language.German);
        InterpreterInfo i2 = new InterpreterInfo(langs2, CertificationType.CCHI);


        GenericStaff g = new GenericStaff("William", "Wong", c);
        InterpreterStaff wilson = new InterpreterStaff(g, langs, CertificationType.CCHI);
        GenericStaff g2 = new GenericStaff("Joe", "J", c);
        InterpreterStaff joe = new InterpreterStaff(g2, langs2, CertificationType.CCHI);
        ArrayList<InterpreterStaff> staff = new ArrayList<>();
        staff.add(wilson);
        staff.add(joe);
        return staff;
    }
}
