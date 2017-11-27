package com.teama.controllers;

import com.jfoenix.controls.JFXButton;
import com.teama.drawing.MapDisplay;
import com.teama.mapsubsystem.MapSubsystem;
import com.teama.mapsubsystem.data.DrawEdgeInstantly;
import com.teama.mapsubsystem.data.DrawNodeInstantly;
import com.teama.mapsubsystem.data.MapEdge;
import com.teama.mapsubsystem.data.MapNode;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.text.Text;

public class NodeInfoPopUpController {
    @FXML
    private Text nodeName;

    @FXML
    private Text nodeText;

    @FXML
    private JFXButton selectNodeButton;

    @FXML
    private JFXButton deleteNodeButton;

    // Node to display the information from
    private MapNode node;

    // In order to modify attributes displayed
    private MapDisplay map;

    private MapSubsystem mapSubsystem;

    /**
     * Sets the node to display, must be done before
     * the pop up is shown or else nothing of value will be on the screen
     *
     * @param node
     */
    public void setInfo(MapNode node, MapDisplay map, MapSubsystem mapSubsystem) {
        this.node = node;
        this.map = map;
        this.mapSubsystem = mapSubsystem;
        nodeName.setText(node.getShortDescription());
        nodeText.setText(node.getLongDescription());
    }

    private boolean addEdgeMode = false;

    @FXML
    void selectNode(ActionEvent event) {
        // TODO: Make this the selected node in the MainScreenSidebarController

        // TODO: Swing out hamburger menu and display the part of it regarding the addition of edges
    }

    @FXML
    void deleteThisNode(ActionEvent event) {
        for (MapEdge e : node.getEdges()) {
            new DrawEdgeInstantly(e).removeFromScreen(map);
        }
        new DrawNodeInstantly(node).removeFromScreen(map);
        mapSubsystem.deleteNode(node.getId());
    }

}
