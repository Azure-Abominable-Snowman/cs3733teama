package com.teama.controllers;

import com.jfoenix.controls.JFXComboBox;
import com.teama.requestsubsystem.PriorityLevel;
import com.teama.requestsubsystem.elevatorfeature.MaintenanceType;
import javafx.fxml.FXML;

/**
 * Created by jakepardue on 12/9/17.
 */
public class MaintenanceReqController {
    @FXML
    private JFXComboBox<MaintenanceType> maintenanceTypeBox;

    @FXML
    private JFXComboBox<PriorityLevel> priorityBox;

    public void initialize(){
        priorityBox.getItems().clear();
        priorityBox.getItems().addAll(PriorityLevel.LOW, PriorityLevel.MEDIUM, PriorityLevel.HIGH);

        maintenanceTypeBox.getItems().clear();
        maintenanceTypeBox.getItems().addAll( MaintenanceType.values());
    }

    public MaintenanceType getMaintenanceType(){
        return maintenanceTypeBox.getSelectionModel().getSelectedItem();
    }

    public PriorityLevel getPriority(){
        return priorityBox.getSelectionModel().getSelectedItem();
    }

    public void clearRequest(){
        maintenanceTypeBox.getSelectionModel().clearSelection();
        priorityBox.getSelectionModel().clearSelection();
    }





}
