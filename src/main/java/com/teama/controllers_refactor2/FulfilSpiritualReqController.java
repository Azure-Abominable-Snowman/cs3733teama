package com.teama.controllers_refactor2;

/**
 * Created by jakepardue on 12/13/17.
 */
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import com.teama.requestsubsystem.spiritualcarefeature.SpiritualCareRequest;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;


public class FulfilSpiritualReqController {

    @FXML
    private GridPane location;

    @FXML
    private JFXTextField staffName;

    @FXML
    private JFXTextField Religion;

    @FXML
    private JFXTextField txtLocation;

    @FXML
    private JFXTextField staffID;

    @FXML
    private JFXTextField txtTimeToFulfill;

    @FXML
    private JFXTextArea txtDescription;

    @FXML
    private JFXTextField groupSize;

    @FXML
    private JFXButton cancelButton;

    @FXML
    private JFXButton submitButton;

    private SpiritualCareRequest reqToFulfill;

    Image check = new Image("/check.png");

    final private BooleanProperty submitted = new SimpleBooleanProperty();

    public void initialize(){
        submitted.set(false);
    }
    public void setReqToFulfill(SpiritualCareRequest request){
        /*
        this.reqToFulfill=request;
        staffName.setText(SpiritualSubsystem.getInstance().getStaff(reqToFulfill.getStaffID()).getFirstName() + " "+ InterpreterSubsystem.getInstance().getStaff(reqToFulfill.getStaffID()).getLastName());
        language.setText(String.valueOf(reqToFulfill.getRequiredLanguage()));
        staffID.setText(Integer.toString(reqToFulfill.getStaffID()));
        txtLocation.setText(reqToFulfill.getLocation().toString());
        */
    }

    @FXML
    void onSubmitClick(ActionEvent event) {

    }


}
