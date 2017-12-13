package com.teama.controllers_refactor2;

/**
 * Created by jakepardue on 12/13/17.
 */
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import com.teama.requestsubsystem.spiritualcarefeature.SpiritualCareRequest;
import com.teama.requestsubsystem.spiritualcarefeature.SpiritualCareSubsystem;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;

import java.time.LocalDate;


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

    @FXML
    private JFXDatePicker datePicker;

    Image check = new Image("/check.png");

    final private BooleanProperty submitted = new SimpleBooleanProperty();

    public void initialize(){
        submitted.set(false);
    }

    public void setReqToFulfill(SpiritualCareRequest request){
        this.reqToFulfill=request;
        staffName.setText(SpiritualCareSubsystem.getInstance().getStaff(reqToFulfill.getStaffID()).getFirstName() + " "+ SpiritualCareSubsystem.getInstance().getStaff(reqToFulfill.getStaffID()).getLastName());
        Religion.setText(String.valueOf(reqToFulfill.getReligion()));
        staffID.setText(Integer.toString(reqToFulfill.getStaffID()));
        txtLocation.setText(reqToFulfill.getLocation().toString());
    }

    public BooleanProperty getSubmitted(){
        return submitted;
    }

    @FXML
    void onSubmitClick(ActionEvent event) {
        try {
            if (txtTimeToFulfill.getText().equals("") || txtDescription.getText().equals("") || groupSize.getText().equals("") || datePicker.getValue() == null){
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error!");
                alert.setHeaderText("Error with Submitting Your Request.");
                alert.setContentText("At least one of the fields is empty.  Please fill in the empty field or fields please.");
                alert.showAndWait();

                throw new NullPointerException("Please check ur fields");

            }
            String timeToFulfillVal = txtTimeToFulfill.getText();
            String descriptionVal = txtDescription.getText();
            int serveTime = Integer.parseInt(txtTimeToFulfill.getText());
            LocalDate date = datePicker.getValue();
            System.out.println(timeToFulfillVal + " " + descriptionVal);
            reqToFulfill.setServiceTime(Integer.parseInt(timeToFulfillVal));
            SpiritualCareRequest request = new SpiritualCareRequest(reqToFulfill.getGenRequest(), reqToFulfill.getReligion(), reqToFulfill.getSpiritualService(),date, serveTime);
            //InterpreterSubsystem.getInstance().updateRequest(new InterpreterRequest(reqToFulfill.getInfo(), reqToFulfill.getRequiredLanguage(),Integer.parseInt(timeToFulfillVal),serveTime, type));
            System.out.println("The code is updated");
            SpiritualCareSubsystem.getInstance().fulfillRequest(request);
            submitted.setValue(true);
            Stage thisStage = (Stage) txtDescription.getScene().getWindow();
            thisStage.close();
            Notifications notifications = Notifications.create()
                    .title("Success!")
                    .text("Your completed interpreter request has been completed.")
                    .graphic(new ImageView(check))
                    .hideAfter(Duration.seconds(4))
                    .position(Pos.CENTER)
                    .onAction(new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent event) {
                            System.out.println("Hi Kent");
                        }
                    });
            notifications.show();
        } catch (NullPointerException exception) {
            exception.printStackTrace();
        }

    }


}
