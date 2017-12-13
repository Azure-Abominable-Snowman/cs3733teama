package com.teama.controllers_refactor2;


import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import com.teama.controllers.Controller;
import com.teama.login.AccessType;
import com.teama.login.LoginSubsystem;
import com.teama.messages.ContactInfo;
import com.teama.messages.ContactInfoTypes;
import com.teama.messages.Provider;
import com.teama.requestsubsystem.GenericStaff;
import com.teama.requestsubsystem.interpreterfeature.*;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;


public class InterpreterModController extends StaffToolController implements Controller {
    boolean editing;
    private final BooleanProperty completed = new SimpleBooleanProperty();
    private InterpreterStaff staffToInsert;
   // private InterpreterSubsystem interpDB= InterpreterSubsystem.getInstance();
   @FXML
   private AnchorPane Pane;

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
    private JFXTextField Email;

    @FXML
    private JFXTextField PhoneNo;

    @FXML
    private JFXTextField LastName;

    @FXML
    private JFXComboBox<CertificationType> Certifications;

    @FXML
    private JFXComboBox<Provider> Providers;

    @FXML
    private JFXButton Cancel;

    @FXML
    private JFXButton Submit;

    @FXML
    private JFXButton btnDelete;

    @FXML
    private ArrayList<JFXCheckBox> languageBoxList = new ArrayList<>();
    @FXML
    private void deleteInterpeter(ActionEvent event){
        String firstName = staffToInsert.getFirstName();
        String lastName = staffToInsert.getLastName();
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Delete Interpreter");
        alert.setHeaderText("Remove Interpreter from database");
        alert.setContentText("Are your sure you want to delete \n"+firstName +" "
        +lastName+" from the database.");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK){
            //TODO actually delete the interpreter
            InterpreterSubsystem.getInstance().removeStaff(staffToInsert.getStaffID());
            editing=true;
            completed.set(true);
        } else {
            alert.close();
        }
    }
    @FXML
    private void cancelInterpreter(ActionEvent event) {
        editing = false;
        completed.set(true);
    }
    @FXML
    private void submitInterpreter(ActionEvent e){
        Set<ContactInfoTypes> contactTypes = new HashSet<ContactInfoTypes>();
        contactTypes.add(ContactInfoTypes.EMAIL);
        contactTypes.add(ContactInfoTypes.TEXT);
        contactTypes.add(ContactInfoTypes.PHONE);
        System.out.println(Providers.getValue());
        Provider provider = Providers.getValue();
        String phoneNumber = PhoneNo.getText();
        String email = Email.getText();
        ContactInfo contactInfo = new ContactInfo(contactTypes, phoneNumber, email, provider);
        GenericStaff staffInfo = new GenericStaff( FirstName.getText(), LastName.getText(), contactInfo);
        Set<Language> langs = new HashSet<>();
        for(JFXCheckBox box: languageBoxList){
            if(box.selectedProperty().get()){
                langs.add(Language.valueOf(box.getText()));
            }
        }
        CertificationType certification = Certifications.getSelectionModel().getSelectedItem();

        if(staffToInsert==null){
            InterpreterStaff interpreterStaff = new InterpreterStaff(staffInfo, langs, certification);
            InterpreterSubsystem.getInstance().addStaff(interpreterStaff);
        }
        else{
            InterpreterStaff interpreterStaff = new InterpreterStaff(staffInfo,langs, certification);
            InterpreterSubsystem.getInstance().updateStaff(interpreterStaff);
        }
        editing = true;
        //make sure to do any manipulations before completed is called
        completed.set(true);
        //TODO determine whether an interpreter is being added or modified and then do whatever's necessary
    }
    //when the edit/view is complete it will push out the update when completed
    public void initialize(){
        editing = false;
        completed.setValue(false);
        initComboBoxes();
        initForm();
        LoginSubsystem login = LoginSubsystem.getInstance();
        if(login.getSystemUser().getAccess()!= AccessType.ADMIN){
            disableEditor();
        }

    }
    private void initComboBoxes(){
        for(Provider provider : Provider.values()){
            Providers.getItems().add(provider);
        }
        for(CertificationType certification: CertificationType.values()){
            Certifications.getItems().add(certification);
        }
    }
    private void initForm(){
        if(languageBoxList.size()==0) {
            languageBoxList.add(English);
            languageBoxList.add(Spanish);
            languageBoxList.add(French);
            languageBoxList.add(German);
            languageBoxList.add(Russian);
            languageBoxList.add(Cantonese);
            languageBoxList.add(Luxembourgish);
            //languageBoxList.add(Moldovan);
            languageBoxList.add(Ukranian);
            languageBoxList.add(ASL);
        }
        if(staffToInsert!=null) {
            for (Language language : staffToInsert.getLanguages()) {
                for (JFXCheckBox box : languageBoxList) {
                    if (box.getText().equals(language.toString())){
                        box.selectedProperty().setValue(true);
                    }
                }
            }
            FirstName.setText(staffToInsert.getFirstName());
            LastName.setText(staffToInsert.getLastName());
        //    Providers.setValue(staffToInsert.getProvider());
            Certifications.setValue(staffToInsert.getCertification());
            PhoneNo.setText(staffToInsert.getPhoneNumber());
            Email.setText(staffToInsert.getEmail());
        }

    }
    private void disableEditor(){
        English.setDisable(true); Spanish.setDisable(true); French.setDisable(true);
        German.setDisable(true); Russian.setDisable(true); Cantonese.setDisable(true);
        Luxembourgish.setDisable(true); Moldovan.setDisable(true); Moldovan.setDisable(true);
        Ukranian.setDisable(true); ASL.setDisable(true);
        FirstName.setEditable(false); LastName.setEditable(false);
        PhoneNo.setEditable(false); Providers.setDisable(true);
       // Email.setEditable(false); Certifications.setDisable(true);
        Submit.setDisable(true); btnDelete.setVisible(false);
    }
    public void setInterpreter(InterpreterStaff interpreter){
        staffToInsert=interpreter;
        //System.out.println(interpreter.getFirstName());
        initForm();
       // FirstName.setText(interpreter.getFirstName());
    }
    public void setEditing(boolean editing){
        this.editing = editing;
    }
    public final BooleanProperty getCompleted(){
        return completed;
    }
    public final void setCompleted(boolean completed){this.completed.setValue(completed);}
    @Override
    public String getFXMLFileName() {
        return "MainScreenDrawers/InterpreterModScreen.fxml";
    }
    public boolean getEditing(){return editing;}

    @Override
    public Pane getParentPane() {
        return Pane;
    }

    @Override
    public String getFXMLPath() {
        return "/MainScreenDrawers/InterpreterModScreen";
    }
}

