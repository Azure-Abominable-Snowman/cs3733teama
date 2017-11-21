package com.teama.controllers;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import com.teama.drawing.MapDisplay;
import com.teama.mapsubsystem.MapSubsystem;
import com.teama.mapsubsystem.data.Location;
import com.teama.mapsubsystem.data.MapNode;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;

import java.io.IOException;

public class PathfindingController {

    private MapDisplay map;
    private JFXTextField searchBar;
    private JFXButton searchButton;
    private MapSubsystem mapSubsystem;
    private AnchorPane mapAreaPane;
    private ScrollPane mapScrollPane;
    private Canvas mapCanvas;

    public PathfindingController(MapSubsystem mapSubsystem, MapDisplay map, AnchorPane mapAreaPane, JFXTextField searchBar, JFXButton searchButton) {
        this.map = map;
        this.searchBar = searchBar;
        this.searchButton = searchButton;
        this.mapAreaPane = mapAreaPane;
        this.mapSubsystem = mapSubsystem;
        this.mapScrollPane = map.getUnderlyingScrollPane();
        this.mapCanvas = map.getUnderlyingCanvas();

        // Display all the nodes for the given floor
        for(MapNode n : mapSubsystem.getVisibleFloorNodes(map.getCurrentFloor()).values()) {
            map.drawPoint(n.getId(), n.getCoordinate(), 4, Color.BLACK, false);
        }

        // Make each node clickable to reveal a detailed menu
        EventHandler<MouseEvent> clickedOnMapHandler = (MouseEvent event) -> {
            //System.out.println("CLICKED ON MAP SCREEN AT "+event.getX()+" "+event.getY());

            if(nodeInfo != null && mapAreaPane.getChildren().contains(nodeInfo)) {
                mapAreaPane.getChildren().remove(nodeInfo);
                nodeInfo = null;
            }

            if(event.isControlDown()) { // check for a node and if there is one display the node info
                generateNodePopUp(event);
            }
        };

        map.getUnderlyingCanvas().onMouseClickedProperty().set(clickedOnMapHandler);

        // When the map is resized the pop up must be taken off the screen
        // TODO: Make the pop up move instead
        ChangeListener<Number> removePopUpWhenResized = (ObservableValue<? extends Number> obsVal, Number oldVal, Number newVal) -> {
            if(nodeInfo != null && mapAreaPane.getChildren().contains(nodeInfo)) {
                mapAreaPane.getChildren().remove(nodeInfo);
                nodeInfo = null;
            }
        };

        mapAreaPane.heightProperty().addListener(removePopUpWhenResized);
        mapAreaPane.widthProperty().addListener(removePopUpWhenResized);


        EventHandler<MouseEvent> panningMap = (MouseEvent event) -> {
            if(nodeInfo != null && mapAreaPane.getChildren().contains(nodeInfo)) {
                mapAreaPane.getChildren().remove(nodeInfo);
                nodeInfo = null;
            }
        };

        mapScrollPane.onDragDetectedProperty().set(panningMap);
    }


    private Parent nodeInfo;

    /**
     * Generates a node pop up if able from the given mouse event
     * @param event
     */
    private void generateNodePopUp(MouseEvent event) {
        System.out.println("CLICK ON NODE BUTTON");

        // Get the id of the node clicked on (if any)
        String id = map.pointAt(new Location((int)event.getX(), (int)event.getY(), map.getCurrentFloor(), "Unknown"));

        System.out.println("CLICKED ON "+id);

        if(id != null) {
            MapNode clickedNode = mapSubsystem.getNode(id);

            // Load the screen in and display it on the cursor
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/NodeInfoPopUp.fxml"));
            try {
                nodeInfo = loader.load();
            } catch (IOException e) {
                e.printStackTrace();
            }
            NodeInfoPopUpController ni = loader.getController();
            ni.setNode(clickedNode);

            // Create pane to load nodeInfo root node into
            nodeInfo.toFront(); // bring to front of screen
            mapAreaPane.getChildren().add(nodeInfo);

            // Display the pane next to the mouse cursor
            double windowW = 140; // TODO: Pull these directly from the parent node itself.
            double windowH = 225;

            double newX = event.getSceneX()-windowW/2;
            double newY = event.getSceneY()-windowH;

            if(newX <= mapScrollPane.getWidth()-nodeInfo.getBoundsInParent().getWidth() && newX >= 0) {
                nodeInfo.setTranslateX(newX);
            } else if (newX > 0) {
                nodeInfo.setTranslateX(mapScrollPane.getWidth()-nodeInfo.getBoundsInParent().getWidth()*1.2);
            }

            if(newY <= mapScrollPane.getHeight() && newY >= 0) {
                System.out.println("MOVE Y");
                nodeInfo.setTranslateY(newY);
            }
        }
    }
}