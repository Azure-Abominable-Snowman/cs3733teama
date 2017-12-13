package com.teama.controllers_refactor;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import com.teama.requestsubsystem.interpreterfeature.InterpreterRequest;
import com.teama.requestsubsystem.interpreterfeature.InterpreterSubsystem;
import com.teama.requestsubsystem.interpreterfeature.TranslationType;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;


/**
 * Created by jakepardue on 12/5/17.
 */
public class FInterpreterRequestController {
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
    private JFXTextField groupSize;

    @FXML
    private JFXButton btnSubmit;

    @FXML
    private JFXComboBox<TranslationType> cmbxTransType = new JFXComboBox<>();

    @FXML
    private JFXTextArea txtDescription;

    private InterpreterRequest reqToFulfill;

    Image check = new Image("/check.png");

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
    void onSubmitClick(ActionEvent e) {
        try {
            if (groupSize.getText().equals("") || txtDescription.getText().equals("") || txtTimeToFulfill.getText().equals("") || cmbxTransType.getValue() == null) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error!");
                alert.setHeaderText("Error with Submitting Your Request.");
                alert.setContentText("At least one of the fields is empty.  Please fill in the empty field or fields please.");
                alert.showAndWait();
                throw new NullPointerException("Please check ur fields.");

            }
            String timeToFulfillVal = txtTimeToFulfill.getText();
            String descriptionVal = txtDescription.getText();
            int serveTime = Integer.parseInt(txtTimeToFulfill.getText());
            TranslationType type = cmbxTransType.getValue();
            System.out.println(timeToFulfillVal + " " + descriptionVal);
            reqToFulfill.setServiceTime(Integer.parseInt(timeToFulfillVal));
            System.out.println(type.toString());
            InterpreterRequest request = new InterpreterRequest(reqToFulfill.getInfo(), reqToFulfill.getRequiredLanguage(), Integer.parseInt(timeToFulfillVal), serveTime, type);
            //InterpreterSubsystem.getInstance().updateRequest(new InterpreterRequest(reqToFulfill.getInfo(), reqToFulfill.getRequiredLanguage(),Integer.parseInt(timeToFulfillVal),serveTime, type));
            System.out.println("The code is updated");
            InterpreterSubsystem.getInstance().fulfillRequest(request);
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

     @FXML
    public void closeScreen(){
        Stage toClose = (Stage) staffID.getScene().getWindow();
        toClose.close();
     }
}
