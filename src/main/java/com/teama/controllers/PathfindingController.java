package com.teama.controllers;

import com.jfoenix.controls.JFXButton;
import com.teama.drawing.MapDisplay;
import com.teama.mapsubsystem.MapSubsystem;
import com.teama.mapsubsystem.data.DrawNodeInstantly;
import com.teama.mapsubsystem.data.Floor;
import com.teama.mapsubsystem.data.Location;
import com.teama.mapsubsystem.data.MapNode;
import com.teama.mapsubsystem.pathfinding.DisplayPath;
import com.teama.mapsubsystem.pathfinding.DisplayPathInstantly;
import com.teama.mapsubsystem.pathfinding.Path;
import javafx.beans.Observable;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

import java.util.Set;

public class PathfindingController {

    private MapDisplay map;
    private MapSubsystem mapSubsystem;
    private AnchorPane mapAreaPane;
    private ScrollPane mapScrollPane;
    private Canvas mapCanvas;
    private VBox floorButtonBox;

    private final String illuminatedFloorButtonClass = "illuminatedfloorbutton";
    private final String regularFloorButtonClass = "floorbutton";
    private final String pressedFloorButtonClass = "pressedfloorbutton";


    public PathfindingController(MapSubsystem mapSubsystem, MapDisplay map, AnchorPane mapAreaPane, VBox floorButtonBox) {
        this.map = map;
        this.mapAreaPane = mapAreaPane;
        this.mapSubsystem = mapSubsystem;
        this.mapScrollPane = map.getUnderlyingScrollPane();
        this.mapCanvas = map.getUnderlyingCanvas();
        this.floorButtonBox = floorButtonBox;

        // Stuff for the node pop up window

        switchFloor(Floor.GROUND);

        /*for(MapNode n : mapSubsystem.getFloorNodes(map.getCurrentFloor()).values()) {
            // Display all edges (DEBUG)
            for(MapEdge e : n.getEdges()) {
                new DrawEdgeInstantly(e).displayOnScreen(map);
            }
        }*/

        // Populate the floor button box
        for(Floor floor : Floor.values()) {
            JFXButton curFloorButton = new JFXButton();
            curFloorButton.setText(floor.toString());
            curFloorButton.getStylesheets().add("css/MainScreenStyle.css");
            curFloorButton.getStyleClass().add(regularFloorButtonClass);
            curFloorButton.setId(floor.toString());
            curFloorButton.setPrefWidth(35);
            curFloorButton.pressedProperty().addListener((Observable obs) -> {
                switchFloor(floor);
            });

            floorButtonBox.getChildren().add(curFloorButton);
        }
    }

    /**
     * Called when floors are switched
     * @param floor
     */
    private void switchFloor(Floor floor) {
        Floor prevFloor = map.getCurrentFloor();

        // Wipe the whole map
        map.clear();
        // Changes the floor then updates the nodes
        map.setCurrentFloor(floor);
        // Display all the nodes for the given floor
        for(MapNode n : mapSubsystem.getVisibleFloorNodes(map.getCurrentFloor()).values()) {
            new DrawNodeInstantly(n).displayOnScreen(map);
        }

        // Display the path on the floor if needed
        if(curPath != null) {
            curPath.displayOnScreen(map, map.getCurrentFloor());
        }
    }
    private DisplayPath curPath;

    /**
     * Generates a path using a mouse event (x and y coordinates)
     * @param mouseEvent
     */
    public void genPathWithClicks(MouseEvent mouseEvent) {
        Location clickedLoc = new Location((int)mouseEvent.getX(), (int)mouseEvent.getY(), map.getCurrentFloor(), "Unknown");
        String curPointId = map.pointAt(clickedLoc);

        System.out.println("PATH CLICK");

        if(curPointId != null) {
            MapNode curNode = mapSubsystem.getNode(curPointId);
            System.out.println("PATH NODE SPECIFIED");
            genPath(curNode);
        }
    }

    /**
     * Generates a path using the destination node specified
     * @param dest
     */
    public void genPath(MapNode dest) {
        Path path = mapSubsystem.getPathGenerator().generatePath(mapSubsystem.getKioskNode(), dest);
        if(curPath != null) {
            curPath.deleteFromScreen(map);
            // unlight floors traveled on the button box
            for(Node button : floorButtonBox.getChildren()) {
                if(button.getStyleClass().contains(illuminatedFloorButtonClass)) {
                    button.getStyleClass().remove(illuminatedFloorButtonClass);
                }
            }
        }
        DisplayPath dpi = new DisplayPathInstantly(path);
        dpi.displayOnScreen(map, map.getCurrentFloor());
        Set<Floor> floorsTraveled = path.getFloorsCrossedExceptTrans();
        System.out.println(floorsTraveled);

        // Light up floors traveled on the button box
        for(Node button : floorButtonBox.getChildren()) {
            if(floorsTraveled.contains(Floor.getFloor(button.getId()))) {
                button.getStyleClass().add(illuminatedFloorButtonClass);
            }
        }

        curPath = dpi;
    }
}