package com.teama.controllers;

import com.teama.drawing.MapDisplay;
import com.teama.mapsubsystem.MapSubsystem;
import com.teama.mapsubsystem.data.MapNode;
import com.teama.mapsubsystem.pathfinding.TextualDirection.TextDirections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.text.Text;

public class NodeInfoPopUpController {
    @FXML
    private Text nodeName;

    @FXML
    private Text nodeText;

    // Node to display the information from
    private MapNode node;

    // In order to modify attributes displayed
    private MapDisplay map;

    private MapSubsystem mapSubsystem;

    private PathfindingController pathfinding;

    private MainScreenSidebarController sidebarController;

    /**
     * Sets the node to display, must be done before
     * the pop up is shown or else nothing of value will be on the screen
     *
     * @param node
     */
    public void setInfo(MapNode node, MapDisplay map, MapSubsystem mapSubsystem, PathfindingController pathfinding, MainScreenSidebarController sidebarController) {
        this.node = node;
        this.map = map;
        this.mapSubsystem = mapSubsystem;
        this.pathfinding = pathfinding;
        this.sidebarController = sidebarController;
        nodeName.setText(node.getShortDescription());
        nodeText.setText(node.getLongDescription());
    }

    @FXML
    void getDirections(ActionEvent event) {
        // Gets directions from the kiosk node to this point
        TextDirections dir = pathfinding.genPath(node);
        if(sidebarController != null) {
            sidebarController.setDirections(dir);
        }
    }

}
