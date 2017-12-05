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
        loader.setLocation(getClass().getResource("/StaffPopOutAPI.fxml"));
        Pane staffComponent;
        staffComponent = loader.load();
        staffComponent.getStylesheets().clear();
        staffComponent.getStylesheets().add(cssPath);
        staffTab.setContent(staffComponent);

        FXMLLoader reqLoader = new FXMLLoader();
        RequestAPIController requestController = new RequestAPIController(node);
        reqLoader.setLocation(getClass().getResource("/RequestPopOutAPI.fxml"));
        reqLoader.setController(requestController);
        Pane requestComponent;
        requestComponent = reqLoader.load();
        requestComponent.getStylesheets().clear();
        requestComponent.getStylesheets().add(cssPath);
        requestTab.setContent(requestComponent);

    }
}
