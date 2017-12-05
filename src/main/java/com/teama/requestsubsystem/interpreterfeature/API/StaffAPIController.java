package com.teama.requestsubsystem.interpreterfeature.API;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import com.teama.messages.ContactInfo;
import com.teama.messages.Provider;
import com.teama.requestsubsystem.GenericStaff;
import com.teama.requestsubsystem.interpreterfeature.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class StaffAPIController {

    @FXML
    private JFXCheckBox English;

    @FXML
    private JFXCheckBox German;

    @FXML
    private JFXCheckBox French;

    @FXML
    private JFXCheckBox Spanish;

    @FXML
    private JFXCheckBox Russian;

    @FXML
    private JFXCheckBox Cantonese;

    @FXML
    private JFXCheckBox Luxembourgish;

    @FXML
    private JFXCheckBox Moldovan;

    @FXML
    private JFXCheckBox Ukranian;

    @FXML
    private JFXCheckBox ASL;

    @FXML
    private JFXTextField FirstName;

    @FXML
    private JFXTextField LastName;

    @FXML
    private JFXTextField PhoneNo;

    @FXML
    private JFXComboBox<Provider> Providers;

    @FXML
    private JFXTextField Email;

    @FXML
    private JFXComboBox<CertificationType> Certifications;

    @FXML
    private JFXButton Cancel;

    @FXML
    private JFXButton btnSubmit;

    @FXML
    private JFXButton btnDelete;

    @FXML
    private TableView<InterpreterTableAdapter> InterpInfoTable;

    @FXML
    private TableColumn<InterpreterTableAdapter,String> firstCol, lastCol, langCol;

    @FXML
    private TableColumn<InterpreterTableAdapter,String> certCol, phoneCol, emailCol;

    private ArrayList<JFXCheckBox> languageCheckBox = new ArrayList<>();

    @FXML
    public void initialize() {
        // Add all of the checkboxes to an array
        languageCheckBox.add(English);
        languageCheckBox.add(Spanish);
        languageCheckBox.add(French);
        languageCheckBox.add(German);
        languageCheckBox.add(Russian);
        languageCheckBox.add(Cantonese);
        languageCheckBox.add(Luxembourgish);
        languageCheckBox.add(Moldovan);
        languageCheckBox.add(Ukranian);
        languageCheckBox.add(ASL);

        // Populate certification table
        Certifications.getItems().addAll(CertificationType.values());

        // Add phone providers
        Providers.getItems().addAll(Provider.values());
    }

    @FXML
    void cancelInterpreter(ActionEvent event) {

    }

    @FXML
    void deleteInterpeter(ActionEvent event) {

    }

    @FXML
    void submitInterpreter(ActionEvent event) {
        String firstName = FirstName.getText();
        String lastName = LastName.getText();
        if(firstName.trim().length() == 0 || lastName.trim().length() == 0) {
            // No first or last name
            return;
        }
        // Generate the information for an interpreter from the fields in the form
        ContactInfo contactInfo = new ContactInfo();
        String phoneNumber = PhoneNo.getText();
        String emailAddr = Email.getText();
        if(!"".equals(phoneNumber)) {
            contactInfo.setPhoneNumber(phoneNumber);
        }
        Provider prov = Providers.getSelectionModel().getSelectedItem();
        if(prov != null) {
            contactInfo.setProvider(prov);
        }
        if(!"".equals(emailAddr)) {
            contactInfo.setEmailAddress(emailAddr);
        }
        GenericStaff genericStaff = new GenericStaff(firstName, lastName, contactInfo);
        // Get language set
        Set<Language> languageSet = new HashSet<>();
        for(JFXCheckBox checkBox : languageCheckBox) {
            if(checkBox.isPressed()) {
                languageSet.add(Language.valueOf(checkBox.getText()));
            }
        }
        // Get certification
        CertificationType cert = Certifications.getSelectionModel().getSelectedItem();
        if(cert == null) {
            return;
        }
        InterpreterStaff newStaffMember = new InterpreterStaff(genericStaff, languageSet, cert);

        InterpreterSubsystem.getInstance().addStaff(newStaffMember);
    }
}
