package com.teama.controllers;

import com.jfoenix.controls.JFXComboBox;
import com.teama.requestsubsystem.spiritualcarefeature.Religion;
import com.teama.requestsubsystem.spiritualcarefeature.SpiritualService;
import javafx.fxml.FXML;

/**
 * Created by jakepardue on 12/12/17.
 */
public class SpiritualCareController {

    @FXML
    private JFXComboBox<Religion> cmbRel;

    @FXML
    private JFXComboBox<SpiritualService> cmbService;

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


}
