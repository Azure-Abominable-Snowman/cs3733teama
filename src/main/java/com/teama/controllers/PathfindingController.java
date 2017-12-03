package com.teama.controllers;

import com.teama.mapdrawingsubsystem.MapDisplay;
import com.teama.mapdrawingsubsystem.MapDrawingSubsystem;
import com.teama.mapsubsystem.MapSubsystem;
import com.teama.mapsubsystem.data.Floor;
import com.teama.mapsubsystem.data.Location;
import com.teama.mapsubsystem.data.MapNode;
import com.teama.mapsubsystem.pathfinding.DisplayPath;
import com.teama.mapsubsystem.pathfinding.DisplayPathInstantly;
import com.teama.mapsubsystem.pathfinding.Path;
import com.teama.mapsubsystem.pathfinding.TextualDirection.TextDirections;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.util.Set;

public class PathfindingController {

    private MapDisplay map;
    private MapSubsystem mapSubsystem;
    private VBox floorButtonBox;
    private DisplayPath curPath;
    private MapDrawingSubsystem mapDrawingSubsystem = MapDrawingSubsystem.getInstance();

    public PathfindingController(MapSubsystem mapSubsystem, MapDisplay map, AnchorPane mapAreaPane, VBox floorButtonBox, Text floorDisplay) {
        this.map = map;
        this.mapSubsystem = mapSubsystem;
        this.floorButtonBox = floorButtonBox;
    }

    /**
     * Generates a path using a mouse event (x and y coordinates)
     * @param mouseEvent
     */
    public void genPathWithClicks(MouseEvent mouseEvent) {
        Location clickedLoc = new Location(mouseEvent, mapDrawingSubsystem.getCurrentFloor());
        MapNode curNode = mapDrawingSubsystem.nodeAt(clickedLoc);
        System.out.println("PATH CLICK");
        if(curNode != null) {
            System.out.println("PATH NODE SPECIFIED");
            genPath(curNode);
        }
    }

    /**
     * Generates a path using the destination node specified
     * @param dest
     */
    public TextDirections genPath(MapNode dest) {
        return genPath(mapSubsystem.getOriginNode(), dest);
    }

    public TextDirections genPath(MapNode origin, MapNode dest) {
        Path path = mapSubsystem.getPathGenerator().generatePath(mapSubsystem.getNode(origin.getId()), mapSubsystem.getNode(dest.getId()));
        if(curPath != null) {
            curPath.deleteFromScreen(map);
        }
        DisplayPath dpi = new DisplayPathInstantly(path);
        dpi.displayOnScreen(map, map.getCurrentFloor());
        Set<Floor> floorsTraveled = path.getFloorsCrossedExceptTrans();
        System.out.println(floorsTraveled);

        curPath = dpi;

        return null;
    }
}