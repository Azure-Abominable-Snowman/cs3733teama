package com.teama.controllers;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import com.teama.drawing.MapDisplay;
import com.teama.mapsubsystem.MapSubsystem;
import com.teama.mapsubsystem.data.MapNode;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;

public class PathfindingController {

    private MapDisplay map;
    private JFXTextField searchBar;
    private JFXButton searchButton;
    private MapSubsystem mapSubsystem;
    private AnchorPane mapAreaPane;

    public PathfindingController(MapSubsystem mapSubsystem, MapDisplay map, AnchorPane mapAreaPane, JFXTextField searchBar, JFXButton searchButton) {
        this.map = map;
        this.searchBar = searchBar;
        this.searchButton = searchButton;
        this.mapAreaPane = mapAreaPane;
        this.mapSubsystem = mapSubsystem;

        // Display all the nodes for the given floor
        for(MapNode n : mapSubsystem.getFloorNodes(map.getCurrentFloor()).values()) {
            map.drawPoint(n.getId(), n.getCoordinate(), 4, Color.BLACK, false);
        }

        // Make each node clickable to reveal a detailed menu
        EventHandler<MouseEvent> clickedOnMapHandler = (event) -> {
            System.out.println("CLICKED ON MAP SCREEN AT "+event.getX()+" "+event.getY());
            if(event.isSecondaryButtonDown()) { // check for a node and if there is one display the node info

            }
        };

        mapAreaPane.onMouseClickedProperty().set(clickedOnMapHandler);
    }
}
