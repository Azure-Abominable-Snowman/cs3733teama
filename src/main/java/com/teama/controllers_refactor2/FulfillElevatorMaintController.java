package com.teama.controllers_refactor2;

/**
 * Created by jakepardue on 12/13/17.
 */
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import com.teama.requestsubsystem.elevatorfeature.ElevatorRequest;
import com.teama.requestsubsystem.elevatorfeature.ElevatorSubsystem;
import com.teama.requestsubsystem.elevatorfeature.MaintenanceType;
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

public class FulfillElevatorMaintController {



    @FXML
    private JFXTextField staffName;

    @FXML
    private JFXTextField MaintType;

    @FXML
    private JFXTextField txtLocation;

    @FXML
    private JFXTextField staffID;

    @FXML
    private JFXTextField txtTimeToFulfill;

    @FXML
    private JFXTextArea txtDescription;

    @FXML
    private JFXButton cancelButton;

    @FXML
    private JFXButton submitButton;

    Image check = new Image("/check.png");

    private ElevatorRequest reqToFulfill;

    final private BooleanProperty submitted = new SimpleBooleanProperty();

    public void initialize(){
        submitted.set(false);
    }

    public void setReqToFulfill(ElevatorRequest request){
        this.reqToFulfill=request;
        System.out.println(request);
        System.out.println(reqToFulfill);
        staffName.setText(ElevatorSubsystem.getInstance().getStaff(reqToFulfill.getStaffID()).getFirstName() + " "+ ElevatorSubsystem.getInstance().getStaff(reqToFulfill.getStaffID()).getLastName());
        MaintType.setText(String.valueOf(reqToFulfill.getMaintenanceType()));
        staffID.setText(Integer.toString(reqToFulfill.getStaffID()));
        txtLocation.setText(reqToFulfill.getLocation().toString());

    }

    public BooleanProperty getSubmitted(){
        return submitted;
    }

    @FXML
    void onSubmitClick(ActionEvent event) {
        try {
            if (txtTimeToFulfill.getText().equals("") || txtDescription.getText().equals("")){
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error!");
                alert.setHeaderText("Error with Submitting Your Request.");
                alert.setContentText("At least one of the fields is empty.  Please fill in the empty field or fields please.");
                alert.showAndWait();
                throw new NullPointerException("Please check ur fields bitch");

            }
            String timeToFulfillVal = txtTimeToFulfill.getText();
            String descriptionVal = txtDescription.getText();
            int serveTime = Integer.parseInt(txtTimeToFulfill.getText());
            System.out.println(timeToFulfillVal + " " + descriptionVal);
            reqToFulfill.setServiceTime(Integer.parseInt(timeToFulfillVal));
            ElevatorRequest request = new ElevatorRequest(reqToFulfill.getInfo(), reqToFulfill.getpLevel(), reqToFulfill.getMaintenanceType(), timeToFulfillVal);

            ElevatorSubsystem.getInstance().fulfillRequest(request);
            submitted.setValue(true);
            Stage thisStage = (Stage) txtDescription.getScene().getWindow();
            thisStage.close();
            Notifications notifications = Notifications.create()
                    .title("Success!")
                    .text("Your completed elevator request has been completed.")
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



