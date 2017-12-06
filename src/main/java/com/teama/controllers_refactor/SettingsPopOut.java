package com.teama.controllers_refactor;

import com.teama.mapsubsystem.ExportFormat;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.fxml.FXML;
import com.teama.mapsubsystem.MapSubsystem;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class SettingsPopOut extends PopOutController {
    private int xOffset, yOffset;
    private ReadOnlyDoubleProperty xProperty, yProperty;
    MapSubsystem mapSubsystem = MapSubsystem.getInstance();



    @FXML
    private void exportCSV(ActionEvent event){
        mapSubsystem.export(ExportFormat.CSV, "1", "2");
    }
    
    public void initialize() {
        alignPane(xProperty, xOffset, yProperty, yOffset);
    }

    @Override
    public void onOpen(ReadOnlyDoubleProperty xProperty, int xOffset, ReadOnlyDoubleProperty yProperty, int yOffset) {
        this.xOffset = xOffset;
        this.yOffset = yOffset;
        this.xProperty = xProperty;
        this.yProperty = yProperty;
    }

    @Override
    public void onClose() {
        System.out.println("CLOSE SETTING");
    }

    @Override
    public String getFXMLPath(){return "/SettingsPopOut.fxml";}

}
