package com.teama.requestsubsystem.interpreterfeature.API;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import com.teama.messages.ContactInfo;
import com.teama.messages.Provider;
import com.teama.requestsubsystem.GenericStaff;
import com.teama.requestsubsystem.interpreterfeature.*;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Optional;
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
    private TableColumn<InterpreterTableAdapter, String> firstCol, lastCol, langCol;

    @FXML
    private TableColumn<InterpreterTableAdapter, String> certCol, phoneCol, emailCol;

    private ArrayList<JFXCheckBox> languageCheckBox = new ArrayList<>();

    private ObservableList<InterpreterTableAdapter> tableVals;

    private SimpleObjectProperty<InterpreterTableAdapter> currentRow = new SimpleObjectProperty<>();

    private BooleanProperty editing = new SimpleBooleanProperty();

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

        firstCol.setCellValueFactory(
                new PropertyValueFactory<>("firstName"));
        lastCol.setCellValueFactory(
                new PropertyValueFactory<>("lastName"));
        langCol.setCellValueFactory(
                new PropertyValueFactory<>("languages"));
        certCol.setCellValueFactory(
                new PropertyValueFactory<>("certification"));
        phoneCol.setCellValueFactory(
                new PropertyValueFactory<>("phone"));
        emailCol.setCellValueFactory(
                new PropertyValueFactory<>("email"));
        tableVals = FXCollections.observableArrayList();
        for (InterpreterStaff interp : InterpreterSubsystem.getInstance().getAllStaff()) {
            tableVals.add(new InterpreterTableAdapter(interp));
        }
        InterpInfoTable.setItems(tableVals);
        InterpInfoTable.setRowFactory(tv -> {
            TableRow<InterpreterTableAdapter> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (!row.isEmpty()) {
                    editing.set(true);
                    // Trigger editing mode by setting a property to true
                    currentRow.set(row.getItem());
                }
            });
            return row;
        });

        // Listens to when the current row changes and updates the fields in the form accordingly
        currentRow.addListener((a, before, after) -> {
            // Clears the form first
            clearForm();
            // Sets the form
            InterpreterStaff staffToInsert = after.getInterpreter();
            Set<Language> languages = staffToInsert.getLanguages();
            for (Language lang : languages) {
                for(JFXCheckBox box : languageCheckBox) {
                    if(Language.valueOf(box.getText()).equals(lang)) {
                        box.setSelected(true);
                        break;
                    }
                }
            }
            FirstName.setText(staffToInsert.getFirstName());
            LastName.setText(staffToInsert.getLastName());
            Providers.setValue(staffToInsert.getProvider());
            Certifications.setValue(staffToInsert.getCertification());
            PhoneNo.setText(staffToInsert.getPhoneNumber());
            Email.setText(staffToInsert.getEmail());
        });

        editing.addListener((obs, before, editing) -> {
            if (editing) {
                btnSubmit.setText("Modify");
            }
            else{
                btnSubmit.setText("Add");
            }
        });
    }

    private void clearForm() {
        for (JFXCheckBox checkBox : languageCheckBox) {
            checkBox.setSelected(false);
        }
        FirstName.clear();
        LastName.clear();
        PhoneNo.clear();
        Providers.getSelectionModel().clearSelection();
        Email.clear();
        Certifications.getSelectionModel().clearSelection();
        PhoneNo.clear();
        Providers.setValue(null);
        Certifications.setValue(null);
    }

    @FXML
    void cancelInterpreter(ActionEvent event) {
        // Clears the form
        clearForm();
        editing.setValue(false);
    }

    @FXML
    void deleteInterpeter(ActionEvent event) {
        InterpreterStaff staffToInsert = currentRow.get().getInterpreter();
        String firstName = staffToInsert.getFirstName();
        String lastName = staffToInsert.getLastName();
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Interpreter");
        alert.setHeaderText("Remove Interpreter from database");
        alert.setContentText("Are your sure you want to delete \n"+firstName +" "
                +lastName+" from the database.");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK){
            InterpreterSubsystem.getInstance().removeStaff(staffToInsert.getStaffID());
            clearForm();
        } else {
            alert.close();
        }
        updateInterpList();
    }

    @FXML
    void submitInterpreter(ActionEvent event) {
        String firstName = FirstName.getText();
        String lastName = LastName.getText();
        if (firstName.trim().length() == 0 || lastName.trim().length() == 0) {
            // No first or last name
            return;
        }
        // Generate the information for an interpreter from the fields in the form
        ContactInfo contactInfo = new ContactInfo();
        String phoneNumber = PhoneNo.getText();
        String emailAddr = Email.getText();
        if (!"".equals(phoneNumber)) {
            contactInfo.setPhoneNumber(phoneNumber);
        }
        Provider prov = Providers.getSelectionModel().getSelectedItem();
        if (prov != null) {
            contactInfo.setProvider(prov);
        }
        if (!"".equals(emailAddr)) {
            contactInfo.setEmailAddress(emailAddr);
        }
        GenericStaff genericStaff = new GenericStaff(firstName, lastName, contactInfo);
        // Get language set
        Set<Language> languageSet = new HashSet<>();
        for (JFXCheckBox checkBox : languageCheckBox) {
            if (checkBox.isSelected()) {
                languageSet.add(Language.valueOf(checkBox.getText()));
            }
        }
        // Get certification
        CertificationType cert = Certifications.getSelectionModel().getSelectedItem();
        if (cert == null) {
            return;
        }
        InterpreterStaff newStaffMember = new InterpreterStaff(genericStaff, languageSet, cert);

        if(btnSubmit.getText().equals("Modify")) {
            InterpreterStaff staff = InterpreterSubsystem.getInstance().getIntepreterStaff(currentRow.get().getInterpreter().getStaffID());
            InterpreterSubsystem.getInstance().updateStaff(new InterpreterStaff(staff.getStaffID(), newStaffMember));

        } else {
            InterpreterSubsystem.getInstance().addStaff(newStaffMember);
        }
        updateInterpList();
        clearForm();
    }

    public void updateInterpList(){
        System.out.println("updating list");
        tableVals =  FXCollections.observableArrayList();
        for(InterpreterStaff interp: InterpreterSubsystem.getInstance().getAllStaff()) {
            tableVals.add(new InterpreterTableAdapter(interp));
        }
        InterpInfoTable.setItems(tableVals);
    }
}
