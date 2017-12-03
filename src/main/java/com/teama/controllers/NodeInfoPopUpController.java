package com.teama.controllers;

import com.teama.mapsubsystem.data.MapNode;
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

    /**
     * Sets the node to display, must be done before
     * the pop up is shown or else nothing of value will be on the screen
     *
     * @param node
     */
    public void setInfo(MapNode node) {
        this.node = node;
        nodeName.setText(node.getShortDescription());
        nodeText.setText(node.getLongDescription());
    }

    @FXML
    void getDirections(ActionEvent event) {
        // Gets directions from the kiosk node to this point
        System.out.println("GET DIRECTIONS");
    }

}
