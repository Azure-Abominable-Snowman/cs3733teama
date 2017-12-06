package com.teama.controllers_refactor;

import com.teama.mapsubsystem.MapSubsystem;
import com.teama.mapsubsystem.ExportFormat;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;

public class SettingsPopOut extends PopOutController {
    private int xOffset, yOffset;
    private ReadOnlyDoubleProperty xProperty, yProperty;
    MapSubsystem mapSubsystem = MapSubsystem.getInstance();



    @FXML
    private void exportCSV(ActionEvent event){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Export To CSV");
        Stage s = new Stage();
        File selectedFile = fileChooser.showSaveDialog(s);
        mapSubsystem.export(ExportFormat.CSV, selectedFile.getAbsolutePath()+"_nodes.csv", selectedFile.getAbsolutePath()+"_edges.csv");
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
