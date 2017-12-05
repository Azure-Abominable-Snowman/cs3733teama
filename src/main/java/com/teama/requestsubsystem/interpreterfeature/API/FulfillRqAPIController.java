package com.teama.requestsubsystem.interpreterfeature.API;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import javafx.fxml.FXML;

/**
 * Created by aliss on 12/5/2017.
 */
public class FulfillRqAPIController {
    @FXML
    private JFXTextField requestLocation, requiredLanguage, assignedStaff, additionalInfo, serviceDuration, familySize, translationType;
    @FXML
    private JFXButton cancelBtn, submitBtn;

    private RequestTableData selectedRequest;

    FulfillRqAPIController(RequestTableData selected) {
        selectedRequest = selected;
    }

    
}
