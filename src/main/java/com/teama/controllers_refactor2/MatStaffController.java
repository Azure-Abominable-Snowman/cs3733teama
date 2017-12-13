package com.teama.controllers_refactor2;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import com.teama.messages.ContactInfo;
import com.teama.messages.ContactInfoTypes;
import com.teama.messages.Provider;
import com.teama.requestsubsystem.GenericStaff;
import com.teama.requestsubsystem.elevatorfeature.ElevatorAdapter;
import com.teama.requestsubsystem.elevatorfeature.ElevatorStaff;
import com.teama.requestsubsystem.elevatorfeature.ElevatorSubsystem;
import com.teama.requestsubsystem.elevatorfeature.MaintenanceType;
import com.teama.requestsubsystem.interpreterfeature.InterpreterStaff;
import com.teama.requestsubsystem.interpreterfeature.InterpreterTableAdapter;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class MatStaffController {
    @FXML
    VBox vbxParentBox;
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
    private JFXButton Cancel;

    @FXML
    private JFXButton btnSubmit;

    @FXML
    private JFXButton btnDelete;

    @FXML
    private TableView<ElevatorAdapter> elevatorInfoTable;

    @FXML
    private TableColumn<ElevatorAdapter, String> firstCol;
    @FXML
    private JFXCheckBox cbxCode;

    @FXML
    private JFXCheckBox cbxElectrical;

    @FXML
    private JFXCheckBox cbxRepairs;

    @FXML
    private JFXCheckBox cbxTrapped;
    @FXML
    private TableColumn<ElevatorAdapter, String> lastCol;

    @FXML
    private TableColumn<ElevatorAdapter, String> phoneCol;

    @FXML
    private TableColumn<ElevatorAdapter, String> emailCol;
    @FXML
    private TableColumn<ElevatorAdapter, String> certCol;

    ElevatorStaff staffToInsert;
    BooleanProperty editing;
    private ArrayList<JFXCheckBox> matTypes;
    public void initialize(){
        editing = new SimpleBooleanProperty(false);
        for(Provider provider : Provider.values()){
            Providers.getItems().add(provider);
        }
        initElevatorColumns();
        editing.addListener((obs, before, editing) -> {
            if (editing) {
                btnSubmit.setText("Modify");
            } else {
                btnSubmit.setText("Add");
            }
        });
        matTypes=new ArrayList<>();
        matTypes.add(cbxCode); matTypes.add(cbxElectrical); matTypes.add(cbxRepairs);
        matTypes.add(cbxTrapped);
    }
    private ObservableList<ElevatorAdapter> tableVals;
    private void initElevatorColumns(){
        firstCol.setCellValueFactory(
                new PropertyValueFactory<>("firstName"));
        lastCol.setCellValueFactory(
                new PropertyValueFactory<>("lastName"));
        phoneCol.setCellValueFactory(
                new PropertyValueFactory<>("phone"));
        emailCol.setCellValueFactory(
                new PropertyValueFactory<>("email"));
        certCol.setCellValueFactory(
                new PropertyValueFactory<>("certifications")
        );
        tableVals =  FXCollections.observableArrayList();
        for(ElevatorStaff elevatorStaff: getElevatorStaff()){
            tableVals.add(new ElevatorAdapter(elevatorStaff));
        }
        elevatorInfoTable.setItems(tableVals);
        elevatorInfoTable.setRowFactory(tv -> {
            TableRow<ElevatorAdapter> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (! row.isEmpty()) {
                    ElevatorAdapter clickedRow = row.getItem();
                    initElevatorMod(clickedRow.getElevatorBase());
                    editing.setValue(true);
                    System.out.println(clickedRow.getEmail());
                }
            });
            return row;
        });
    }

    private void initElevatorMod(ElevatorStaff elevatorBase) {
        staffToInsert=elevatorBase;
        if(staffToInsert!=null){
            FirstName.setText(staffToInsert.getFirstName());
            LastName.setText(staffToInsert.getLastName());
            Email.setText(staffToInsert.getEmail());
            Providers.setValue(staffToInsert.getProvider());
            PhoneNo.setText(staffToInsert.getPhoneNumber());
        }
        for(JFXCheckBox box: matTypes){
            box.selectedProperty().set(false);
        }
        for(JFXCheckBox box : matTypes){
            for(MaintenanceType t: staffToInsert.getSpecialization()){
                if(MaintenanceType.valueOf(box.getId()).equals(t)){
                    box.selectedProperty().set(true);
                }
            }
        }
    }
    private void blankEditor(){
        FirstName.clear();
        LastName.clear();
        Email.clear();
        Providers.setValue(null);
        PhoneNo.clear();
        for(JFXCheckBox box : matTypes){
            box.selectedProperty().set(false);
        }
    }


    public VBox getVbxParentPane() {
        return vbxParentBox;
    }
    @FXML
    private void deleteStaff(ActionEvent e){
        if(staffToInsert!=null) {
            String firstName = staffToInsert.getFirstName();
            String lastName = staffToInsert.getLastName();
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Delete Serviceperson");
            alert.setHeaderText("Remove Interpreter from database");
            alert.setContentText("Are your sure you want to delete \n" + firstName + " "
                    + lastName + " from the database.");
            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == ButtonType.OK) {
                ElevatorSubsystem.getInstance().removeStaff(staffToInsert.getStaffID());
                blankEditor();
            } else {
                alert.close();
            }
            updateElevatorList();
            editing.setValue(false);
        }
    }
    @FXML
    private void submitStaff(ActionEvent e) {
        try {
            boolean filled = false;
            for (JFXCheckBox check : matTypes) {
                if (check.selectedProperty().get()) {
                    filled = true;
                }
            }
            if (!filled) {
                throw new NullPointerException();
            }
            Set<ContactInfoTypes> contactTypes = new HashSet<>();
            contactTypes.add(ContactInfoTypes.EMAIL);
            contactTypes.add(ContactInfoTypes.TEXT);
            contactTypes.add(ContactInfoTypes.PHONE);
            Provider provider = Providers.getValue();
            String phoneNumber = PhoneNo.getText();
            String email = Email.getText();
            ContactInfo contactInfo = new ContactInfo(contactTypes, phoneNumber, email, provider);
            GenericStaff staffInfo = new GenericStaff(FirstName.getText(), LastName.getText(), contactInfo);
            Set<MaintenanceType> maintenanceTypes = new HashSet<>();
            for (JFXCheckBox box : matTypes) {
                if (box.selectedProperty().get()) {
                    System.out.println(MaintenanceType.valueOf(box.getId()));
                    maintenanceTypes.add(MaintenanceType.valueOf(box.getId()));
                }
            }
            ElevatorSubsystem elevatorSubsystem = ElevatorSubsystem.getInstance();
            if (!editing.get()) {
                ElevatorStaff toAdd = new ElevatorStaff(staffInfo, maintenanceTypes);
                elevatorSubsystem.addStaff(toAdd);
                blankEditor();
            } else {
                ElevatorStaff toMod = elevatorInfoTable.getItems().get(elevatorInfoTable.getSelectionModel().getFocusedIndex()).getElevatorBase();
                toMod.setGenInfo(staffInfo);
                toMod.setSpecialization(maintenanceTypes);
                elevatorSubsystem.updateStaff(toMod);
            }
            updateElevatorList();
        }
        catch(NullPointerException pointer){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Invalid information");
            alert.setContentText("Invalid or empty field, please check information and \n submit again");
            Optional<ButtonType> result = alert.showAndWait();
        }
    }

    @FXML
    private void cancelStaff(ActionEvent e){
        blankEditor();
        staffToInsert=null;
        editing.setValue(false);
    }
    private void updateElevatorList(){
        initElevatorColumns();

    }
    private ArrayList<ElevatorStaff> getElevatorStaff(){
        return ElevatorSubsystem.getInstance().getAllStaff();
       /* Set<ContactInfoTypes> avail = new HashSet<>();
        avail.add(ContactInfoTypes.EMAIL);
        avail.add(ContactInfoTypes.TEXT);
        avail.add(ContactInfoTypes.PHONE);
        ContactInfo c = new ContactInfo(avail, "4444441134", "wwong2@wpi.edu", Provider.ATT);
        GenericStaff g = new GenericStaff("William", "Wong", c);
        Set<MaintenanceType> specializations = new HashSet<>();
        specializations.add(MaintenanceType.CODECHECK);
        specializations.add(MaintenanceType.PERSONTRAPPED);
        ElevatorStaff wilson = new ElevatorStaff(g, specializations);
        ArrayList<ElevatorStaff> staff = new ArrayList<>();
        staff.add(wilson);
        return staff;*/
    }
}
