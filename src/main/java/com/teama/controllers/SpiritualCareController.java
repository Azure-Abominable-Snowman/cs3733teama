package com.teama.controllers;

import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXDatePicker;
import com.teama.requestsubsystem.spiritualcarefeature.Religion;
import com.teama.requestsubsystem.spiritualcarefeature.SpiritualService;
import javafx.fxml.FXML;

import java.time.LocalDate;

/**
 * Created by jakepardue on 12/12/17.
 */
public class SpiritualCareController {

    @FXML
    private JFXComboBox<Religion> cmbRel;

    @FXML
    private JFXComboBox<SpiritualService> cmbService;

    @FXML
    private JFXDatePicker datePicker;

    public void initialize(){
        cmbRel.getItems().clear();
        cmbRel.getItems().addAll(Religion.values());

        cmbService.getItems().clear();
        cmbService.getItems().addAll(SpiritualService.values());
    }

    public Religion getReligion(){
        return cmbRel.getSelectionModel().getSelectedItem();
    }

    public SpiritualService getSpiritualService(){
        return cmbService.getSelectionModel().getSelectedItem();
    }

    public LocalDate getDate(){
        return datePicker.getValue();
    }

    public void clearRequest(){
        cmbRel.getSelectionModel().clearSelection();
        cmbService.getSelectionModel().clearSelection();
        datePicker.getEditor().clear();
    }


}
