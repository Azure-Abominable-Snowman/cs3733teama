package com.teama.controllers;

import com.teama.controllers_refactor.PopOutType;
import com.teama.mapdrawingsubsystem.MapDrawingSubsystem;
import com.teama.mapsubsystem.MapSubsystem;
import com.teama.mapsubsystem.data.MapNode;
import com.teama.mapsubsystem.pathfinding.DirectionsGenerator;
import com.teama.mapsubsystem.pathfinding.Path;
import com.teama.mapsubsystem.pathfinding.TextualDirection.Direction;
import com.teama.mapsubsystem.pathfinding.TextualDirection.TextDirections;
import com.teama.mapsubsystem.pathfinding.TextualDirection.TextualDirections;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;

import java.util.Map;

public class PathfindingController {


    private long curPathID = -1;
    private MapDrawingSubsystem drawingSubsystem = MapDrawingSubsystem.getInstance();
    private MapSubsystem mapSubsystem = MapSubsystem.getInstance();
    private Map<PopOutType, EventHandler<MouseEvent>> mainSidebarMap;
    private DirectionsGenerator directionsGenerator;

    public PathfindingController(Map<PopOutType, EventHandler<MouseEvent>> mainSidebarMap) {
        this.mainSidebarMap = mainSidebarMap;
    }

    public void genPath(MapNode dest) {
        genPath(mapSubsystem.getOriginNode(), dest);
    }

    public void genPath(MapNode origin, MapNode dest) {
        // Generate the path and put it on the screen
        Path path = mapSubsystem.getPathGenerator().generatePath(origin, dest);

        if(curPathID != -1) {
            drawingSubsystem.unDrawPath(curPathID);
        }
        curPathID = drawingSubsystem.drawPath(path);

        // Generate the textual directions and put them on the directions screen
        // Then show the directions screen
        directionsGenerator = new TextualDirections();
        TextDirections directions = directionsGenerator.generateDirections(path);

        for(Direction d : directions.getDirections()) {
            System.out.println(d.getDescription());
        }
    }
}