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
            System.out.println("UPDATED PATH ON CHANGED START NODE");
            genPath(settings.getPathOriginNodeProp().getValue(), settings.getPathEndNodeProp().getValue());
        });

        settings.getPathEndNodeProp().addListener((a) -> {
            System.out.println("UPDATED PATH ON CHANGED END NODE");
            genPath(settings.getPathOriginNodeProp().getValue(), settings.getPathEndNodeProp().getValue());
        });
    }

    public void genPath(MapNode dest) {
        MapNode newOrigin = ProgramSettings.getInstance().getPathOriginNodeProp().getValue();
        if(newOrigin != null) {
            genPath(newOrigin, dest);
        } else {
            genPath(mapSubsystem.getKioskNode(), dest);
        }
    }

    private boolean listen = true;

    public void genPath(MapNode origin, MapNode dest) {
        if(listen) {
            if(origin == null || dest == null) {
                return;
            }
            // Generate the path and put it on the screen
            MapNode newOrigin = mapSubsystem.getNode(origin.getId());
            MapNode newEnd = mapSubsystem.getNode(dest.getId());
            Path path;
            if (newOrigin != null && newEnd != null) {
                if (newOrigin.getId().equals(newEnd.getId())) {
                    System.out.println("Path start and end are the same");
                    // Remove the current path if there is one
                    drawingSubsystem.unDrawPath(curPathID);
                    curPathID = -1;
                    return;
                }
                path = mapSubsystem.getPathGenerator().generatePath(newOrigin, newEnd);
            } else {
                System.out.println("Path cannot be generated");
                return;
            }

            if (curPathID != -1) {
                drawingSubsystem.unDrawPath(curPathID);
            }

            curPathID = drawingSubsystem.drawPath(path);

            // Update the origin, end node and path without regenerating the path that was just created
            listen = false;
            ProgramSettings.getInstance().setCurrentDisplayedPathProp(path);
            ProgramSettings.getInstance().setPathOriginNodeProp(origin);
            ProgramSettings.getInstance().setPathEndNodeProp(dest);
            listen = true;

            // Open the directions pop out
            mainSidebarMap.get(PopOutType.DIRECTIONS).handle(null);
        }
    }
}