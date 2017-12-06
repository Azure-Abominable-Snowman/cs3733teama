package com.teama.controllers_refactor;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.teama.requestsubsystem.RequestStatus;
import com.teama.requestsubsystem.interpreterfeature.InterpreterRequest;
import com.teama.requestsubsystem.interpreterfeature.InterpreterSubsystem;
import com.teama.requestsubsystem.interpreterfeature.TranslationType;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;

import javafx.fxml.FXML;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import javafx.scene.control.Alert;
import javafx.stage.Stage;



/**
 * Created by jakepardue on 12/5/17.
 */
public class FulfillRequestController{


    @FXML
    private JFXTextField staffName;

    @FXML
    private JFXTextField language;

    @FXML
    private JFXTextField txtLocation;

    @FXML
    private JFXTextField staffID;

    @FXML
    private JFXTextField txtTimeToFulfill;

    @FXML
    private JFXButton btnSubmit;

    @FXML
    private JFXComboBox<TranslationType> cmbxTransType = new JFXComboBox<>();

    @FXML
    private JFXTextArea txtDescription;

    private InterpreterRequest reqToFulfill;

    final private BooleanProperty submitted = new SimpleBooleanProperty();

    public void initialize(){
        submitted.set(false);
        cmbxTransType.getItems().clear();
        cmbxTransType.getItems().addAll(TranslationType.ASL, TranslationType.VERBAL, TranslationType.WRITTEN);
     }
    public void setReqToFulfill(InterpreterRequest request){
        this.reqToFulfill=request;
        staffName.setText(InterpreterSubsystem.getInstance().getStaff(reqToFulfill.getStaffID()).getFirstName() + " "+ InterpreterSubsystem.getInstance().getStaff(reqToFulfill.getStaffID()).getLastName());
        language.setText(String.valueOf(reqToFulfill.getRequiredLanguage()));
        staffID.setText(Integer.toString(reqToFulfill.getStaffID()));
        txtLocation.setText(reqToFulfill.getLocation().toString());

    }

    public BooleanProperty getSubmitted(){
        return submitted;
     }

    @FXML
    void onSubmitClick(ActionEvent e){
        try{
            String timeToFulfillVal = txtTimeToFulfill.getText();
            String descriptionVal = txtDescription.getText();
            int serveTime = Integer.parseInt(txtTimeToFulfill.getText());
            TranslationType type = cmbxTransType.getValue();
            System.out.println(timeToFulfillVal+" "+descriptionVal);
            reqToFulfill.setServiceTime(Integer.parseInt(timeToFulfillVal));
            System.out.println(type.toString());
            InterpreterRequest request = new InterpreterRequest(reqToFulfill.getInfo(),reqToFulfill.getRequiredLanguage(),Integer.parseInt(timeToFulfillVal),serveTime, type);
            //InterpreterSubsystem.getInstance().updateRequest(new InterpreterRequest(reqToFulfill.getInfo(), reqToFulfill.getRequiredLanguage(),Integer.parseInt(timeToFulfillVal),serveTime, type));
            System.out.println("The code is updated");
            InterpreterSubsystem.getInstance().fulfillRequest(request);
            submitted.setValue(true);
            Stage thisStage = (Stage) txtDescription.getScene().getWindow();
            thisStage.close();
        }
        catch(NullPointerException exception){
            exception.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error!");
            alert.setHeaderText("Please fill in field");
            alert.setContentText("Invalid or missing date in fields");
            alert.showAndWait();
        }
     }
}
