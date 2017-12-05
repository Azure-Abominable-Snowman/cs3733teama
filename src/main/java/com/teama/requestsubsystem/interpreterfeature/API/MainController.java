package com.teama.requestsubsystem.interpreterfeature.API;

import com.teama.mapsubsystem.MapSubsystem;
import com.teama.mapsubsystem.data.MapNode;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Tab;
import javafx.scene.layout.Pane;

import java.io.IOException;

public class MainController {
    @FXML
    private Tab staffTab;

    @FXML
    private Tab requestTab;

    private String cssPath;
    private MapNode node;

    public MainController(String cssPath, String nodeId) {
        this.cssPath = cssPath;
        node = MapSubsystem.getInstance().getNode(nodeId);
    }

    public void initialize() throws IOException {
        // Set the staff tab and the request tab to their respective loaded FXML files
        FXMLLoader loader = new FXMLLoader();
        StaffAPIController staffController = new StaffAPIController();
        loader.setLocation(getClass().getResource("/StaffPopOutAPI.fxml"));
        try {
            Pane staffComponent;
            staffComponent = loader.load();
            staffComponent.getStylesheets().clear();
            staffComponent.getStylesheets().add(cssPath);
            staffTab.setContent(staffComponent);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
