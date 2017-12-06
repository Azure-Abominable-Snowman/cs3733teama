package com.teama.controllers;

import com.teama.mapdrawingsubsystem.PopUpDrawing;
import com.teama.mapsubsystem.data.MapNode;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;

public class NodeInfoPopUpController extends PopUpDrawing {
    @FXML
    private Text nodeName;

    @FXML
    private Text nodeText;


    // Node to display the information from
    private MapNode node;

    private PathfindingController pathfindingController;


    /**
     * Sets the node to display, must be done before
     * the pop up is shown or else nothing of value will be on the screen
     *
     * @param node
     */
    public void setInfo(MapNode node, PathfindingController pathController, MouseEvent e) {
        this.node = node;
        nodeName.setText(node.getShortDescription());
        nodeText.setText(node.getLongDescription());
        this.pathfindingController = pathController;

        generatePopUp(e.getSceneX(), e.getSceneY(), 0, -5);

    }

    @FXML
    void getDirections(ActionEvent event) {
        // Gets directions from the kiosk node to this point
        System.out.println("GET DIRECTIONS");
        pathfindingController.genPath(node);
    }

}
