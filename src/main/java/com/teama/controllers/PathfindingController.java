package com.teama.controllers;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import com.teama.drawing.MapDisplay;
import com.teama.mapsubsystem.MapSubsystem;
import com.teama.mapsubsystem.data.*;
import com.teama.mapsubsystem.pathfinding.*;
import javafx.beans.Observable;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class PathfindingController {

    private MapDisplay map;
    private JFXTextField searchBar;
    private JFXButton searchButton;
    private MapSubsystem mapSubsystem;
    private AnchorPane mapAreaPane;
    private ScrollPane mapScrollPane;
    private Canvas mapCanvas;

    public PathfindingController(MapSubsystem mapSubsystem, MapDisplay map, AnchorPane mapAreaPane, JFXTextField searchBar, JFXButton searchButton, VBox floorButtonBox) {
        this.map = map;
        this.searchBar = searchBar;
        this.searchButton = searchButton;
        this.mapAreaPane = mapAreaPane;
        this.mapSubsystem = mapSubsystem;
        this.mapScrollPane = map.getUnderlyingScrollPane();
        this.mapCanvas = map.getUnderlyingCanvas();

        // Stuff for the node pop up window

        switchFloor(Floor.GROUND);

        /*for(MapNode n : mapSubsystem.getFloorNodes(map.getCurrentFloor()).values()) {
            // Display all edges (DEBUG)
            for(MapEdge e : n.getEdges()) {
                new DrawEdgeInstantly(e).displayOnScreen(map);
            }
        }*/

        // Make each node clickable to reveal a detailed menu
        EventHandler<MouseEvent> clickedOnMapHandler = (MouseEvent event) -> {
            //System.out.println("CLICKED ON MAP SCREEN AT "+event.getX()+" "+event.getY());

            if(nodeInfo != null && mapAreaPane.getChildren().contains(nodeInfo)) {
                mapAreaPane.getChildren().remove(nodeInfo);
                nodeInfo = null;
            }

            if(event.isControlDown()) { // check for a node and if there is one display the node info
                generateNodePopUp(event);
            } else {
                genPathWithClicks(event);
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

        // Zoom in and out using plus and minus keys
        mapScrollPane.onKeyTypedProperty().set((KeyEvent event) -> {
            switch(event.getCharacter()) {
                case "=":
                    // zoom in
                    if(map.getZoom() < 3) { // TODO: make this not throw an exception when the image gets too big
                        map.setZoom(map.getZoom() + 0.1);
                    }
                    break;
                case "-":
                    // zoom out
                    if(map.getZoom() > 1.5) {
                        map.setZoom(map.getZoom() - 0.1);
                    }
                    break;
            }
        });

        // Populate the floor button box
        for(Floor floor : Floor.values()) {
            JFXButton curFloorButton = new JFXButton();
            curFloorButton.setText(floor.toString());
            curFloorButton.getStylesheets().add("css/MainScreenStyle.css");
            curFloorButton.getStyleClass().add("floorbutton");
            curFloorButton.setPrefWidth(35);
            curFloorButton.pressedProperty().addListener((Observable obs) -> {
                switchFloor(floor);
            });
            floorButtonBox.getChildren().add(curFloorButton);
        }

    }

    private void switchFloor(Floor floor) {
        // Wipe the whole map
        map.clear();
        // Changes the floor then updates the nodes
        map.setCurrentFloor(floor);
        // Display all the nodes for the given floor
        for(MapNode n : mapSubsystem.getVisibleFloorNodes(map.getCurrentFloor()).values()) {
            new DrawNodeInstantly(n).displayOnScreen(map);
        }
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
            ni.setInfo(clickedNode, map, mapSubsystem);

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

    private DisplayPath oldPath;

    private PathGenerator gen = new PathGenerator(new AStar());

    private void genPathWithClicks(MouseEvent mouseEvent) {
        Location clickedLoc = new Location((int)mouseEvent.getX(), (int)mouseEvent.getY(), map.getCurrentFloor(), "Unknown");
        String curPointId = map.pointAt(clickedLoc);

        System.out.println("PATH CLICK");

        if(curPointId != null) {
            MapNode curNode = mapSubsystem.getNode(curPointId);
            System.out.println("PATH NODE SPECIFIED");
            Path path = gen.generatePath(mapSubsystem.getKioskNode(), curNode);
            if(oldPath != null) {
                oldPath.deleteFromScreen(map);
            }
            DisplayPath dpi = new DisplayPathInstantly(path);
            dpi.displayOnScreen(map);
            oldPath = dpi;
        }
    }
}