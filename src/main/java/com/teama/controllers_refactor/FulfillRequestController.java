package com.teama.controllers_refactor;

import com.jfoenix.controls.JFXButton;
import com.teama.requestsubsystem.RequestStatus;
import com.teama.requestsubsystem.interpreterfeature.InterpreterRequest;
import com.teama.requestsubsystem.interpreterfeature.InterpreterSubsystem;
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
    private JFXTextField sizeOfGroup;

    @FXML
    private JFXTextField txtTimeToFulfill;
    @FXML
    private JFXButton btnSubmit;

    @FXML
    private JFXTextArea txtDescription;

    private InterpreterRequest reqToFulfill;

    final private BooleanProperty submitted = new SimpleBooleanProperty();

    public void initialize(){

     }
    public void setReqToFulfill(InterpreterRequest request){
        this.reqToFulfill=request;
        staffName.setText("iugwueiod");
        language.setText(String.valueOf(reqToFulfill.getRequiredLanguage()));
        staffID.setText(Integer.toString(reqToFulfill.getStaffID()));
        txtLocation.setText(reqToFulfill.getLocation().toString());
        sizeOfGroup.setText(Integer.toString(reqToFulfill.getFamilySize()));


    }
    public BooleanProperty getSubmitted(){
        return submitted;
     }

    @FXML
    void onSubmitClick(ActionEvent e){
        try{
            String timeToFulfillVal = txtTimeToFulfill.getText();
            String descriptionVal = txtDescription.getText();
            System.out.println(timeToFulfillVal+" "+descriptionVal);
            reqToFulfill.setServiceTime(Integer.parseInt(timeToFulfillVal));
            InterpreterSubsystem.getInstance().fulfillRequest(reqToFulfill);
            submitted.setValue(true);
            Stage thisStage = (Stage) txtDescription.getScene().getWindow();
            thisStage.close();
        }
        catch(Exception exception){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error!");
            alert.setHeaderText("Please fill in field");
            alert.setContentText("Invalid or missing date in fields");
            alert.showAndWait();
        }
     }
}
