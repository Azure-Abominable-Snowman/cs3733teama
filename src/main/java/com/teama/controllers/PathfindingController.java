package com.teama.controllers;

import com.teama.ProgramSettings;
import com.teama.controllers_refactor.PopOutType;
import com.teama.mapdrawingsubsystem.MapDrawingSubsystem;
import com.teama.mapsubsystem.MapSubsystem;
import com.teama.mapsubsystem.data.MapNode;
import com.teama.mapsubsystem.pathfinding.Path;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;

import java.util.Map;

public class PathfindingController {


    private long curPathID = -1;
    private MapDrawingSubsystem drawingSubsystem = MapDrawingSubsystem.getInstance();
    private MapSubsystem mapSubsystem = MapSubsystem.getInstance();
    private Map<PopOutType, EventHandler<MouseEvent>> mainSidebarMap;

    public PathfindingController(Map<PopOutType, EventHandler<MouseEvent>> mainSidebarMap) {
        this.mainSidebarMap = mainSidebarMap;

        ProgramSettings settings = ProgramSettings.getInstance();

        // Set the pathfinding controller to update its path automatically on a changed start node
        settings.getPathOriginNodeProp().addListener((a) -> {
            genPath(settings.getPathOriginNodeProp().getValue(), settings.getPathEndNodeProp().getValue());
        });
    }

    public void genPath(MapNode dest) {
        genPath(mapSubsystem.getOriginNode(), dest);
    }

    public void genPath(MapNode origin, MapNode dest) {
        // Generate the path and put it on the screen
        Path path = mapSubsystem.getPathGenerator().generatePath(mapSubsystem.getNode(origin.getId()), mapSubsystem.getNode(dest.getId()));

        if(curPathID != -1) {
            drawingSubsystem.unDrawPath(curPathID);
        }
        curPathID = drawingSubsystem.drawPath(path);

        // Put the current path into settings
        ProgramSettings.getInstance().setCurrentDisplayedPathProp(path);
        ProgramSettings.getInstance().setPathEndNodeProp(dest);

        // Open the directions pop out
        mainSidebarMap.get(PopOutType.DIRECTIONS).handle(null);
    }
}