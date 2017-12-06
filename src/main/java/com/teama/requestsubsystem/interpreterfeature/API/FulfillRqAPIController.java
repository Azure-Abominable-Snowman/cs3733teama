package com.teama.requestsubsystem.interpreterfeature.API;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import com.teama.requestsubsystem.Request;
import com.teama.requestsubsystem.RequestStatus;
import com.teama.requestsubsystem.interpreterfeature.InterpreterRequest;
import com.teama.requestsubsystem.interpreterfeature.InterpreterSubsystem;
import com.teama.requestsubsystem.interpreterfeature.TranslationType;
import javafx.beans.property.BooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableView;

import java.util.function.Predicate;

/**
 * Created by aliss on 12/5/2017.
 */
public class FulfillRqAPIController {
    @FXML
    private JFXTextField requestLocation, requiredLanguage, assignedStaff, serviceDuration, familySize;
    @FXML
    private JFXTextArea additionalInfo;
    @FXML
    JFXComboBox<TranslationType> translationType;
    @FXML
    private JFXButton cancelBtn, submitBtn;

    private BooleanProperty isFulfilling;

    private RequestTableData selectedRequest;
    private TableView<RequestTableData> requestTable;


    FulfillRqAPIController(RequestTableData selected, BooleanProperty isFulfilling, TableView<RequestTableData> table) {
        selectedRequest = selected;
        this.requestTable = table;
        this.isFulfilling = isFulfilling;
    }

    public void initialize() {
        translationType.getItems().clear();
        translationType.getItems().addAll(TranslationType.values());
        requestLocation.setText(selectedRequest.getLocation());
        requiredLanguage.setText(selectedRequest.getRequiredLanguage().toString());
        assignedStaff.setText(Integer.toString(selectedRequest.getStaffID()));
        additionalInfo.setText(selectedRequest.getNote());
    }


    @FXML
    void onSubmit(ActionEvent e) {
        //TODO make sure all fields filled in
        if (!serviceDuration.getText().equals("") && !familySize.getText().equals("") && translationType.getSelectionModel().getSelectedItem() != null) {
            Request r = InterpreterSubsystem.getInstance().getGenericRequest(selectedRequest.getRequestID());
            double duration = Double.parseDouble(serviceDuration.getText());
            int famSize = Integer.parseInt(familySize.getText());
            TranslationType translation = translationType.getSelectionModel().getSelectedItem();
            System.out.println(r.getRequestID());
            InterpreterRequest toFulFill = new InterpreterRequest(r, selectedRequest.getRequiredLanguage(), famSize, duration, translation);
            if (InterpreterSubsystem.getInstance().fulfillRequest(toFulFill)) {
                isFulfilling.setValue(false);
                requestTable.getItems().removeIf(new Predicate<RequestTableData>() {
                    @Override
                    public boolean test(RequestTableData requestTableData) {
                        return requestTableData.getRequestID() == selectedRequest.getRequestID();
                    }
                });
                selectedRequest.setStatus(RequestStatus.CLOSED);
                requestTable.getItems().add(selectedRequest);
            }
            else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Fulfillment Error");
                alert.setHeaderText("Failed to Fulfill Request");
                alert.setContentText("An error occured while attempting to fulfill this request.");
                alert.showAndWait();
            }
        }
        else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Fulfillment Error");
            alert.setHeaderText("Failed to Fulfill Request");
            alert.setContentText("Make sure all fields are filled.");
            alert.showAndWait();
        }

    }


    @FXML
    void onCancel(ActionEvent e) {
        isFulfilling.setValue(false);
    }

}
