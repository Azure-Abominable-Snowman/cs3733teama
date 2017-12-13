package com.teama.controllers;

import com.teama.ProgramSettings;
import com.teama.controllers_refactor.PopOutType;
import com.teama.mapdrawingsubsystem.MapDrawingSubsystem;
import com.teama.mapsubsystem.MapSubsystem;
import com.teama.mapsubsystem.data.MapNode;
import com.teama.mapsubsystem.data.NodeType;
import com.teama.mapsubsystem.pathfinding.DijkstrasFamily.Dijkstras.NodeTypeDijkstras;
import com.teama.mapsubsystem.pathfinding.DijkstrasFamily.reverseAStar.ReverseAstar;

import com.teama.mapsubsystem.pathfinding.Path;
import com.teama.mapsubsystem.pathfinding.PathAlgorithm;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;

import java.util.Map;

public class PathfindingController {


    private String curPathID = null;
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
                    curPathID = null;
                    return;
                }
                // path = new NodeTypeDijkstras().generatePath(newOrigin, NodeType.EXIT); // TODO the line to test NodeTypeDijkstras.
                 // path = new NodeTypeDijkstras().generatePath(newOrigin, NodeType.EXIT); // TODO the line to test NodeTypeDijkstras.
                path = mapSubsystem.getPathGenerator().generatePath(newOrigin, newEnd); // TODO hijack this line to test
            } else {
                System.out.println("Path cannot be generated");
                return;
            }

            if (curPathID != null) {
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


    /**
     * a copy of genPath from above. this is only used when emergency button is clicked.
     */
    public void genExitPath(){
        for(;!listen;); // wait until listen is back to true.

        NodeTypeDijkstras finder = new NodeTypeDijkstras();

        // these are copied from genPath in PathfindingController
        MapNode origin = ProgramSettings.getInstance().getPathOriginNodeProp().getValue();
        if(origin == null) origin = mapSubsystem.getKioskNode();
        else origin=mapSubsystem.getNode(origin.getId());
        // start is already at the exit.
        if(origin == null) {
            System.out.println("In EmergencyClick, Can't find Origin");
        }

        if(origin.getNodeType().toString().equals(NodeType.EXIT.toString()))
        {
            System.out.printf("Start node is already set as an exit at %s",origin.getId());
            drawingSubsystem.unDrawPath(curPathID);
            curPathID = null;
            return;
        }
        Path path = finder.generatePath(origin,NodeType.EXIT);

        // these are copy from the function above.
        if (curPathID != null) {
            drawingSubsystem.unDrawPath(curPathID);
        }

        curPathID = drawingSubsystem.drawPath(path);

        // Update the origin, end node and path without regenerating the path that was just created
        listen = false;
        ProgramSettings.getInstance().setCurrentDisplayedPathProp(path);
        ProgramSettings.getInstance().setPathOriginNodeProp(origin);
        ProgramSettings.getInstance().setPathEndNodeProp(path.getEndNode());
        listen = true;

        // additional new line, center the middle node of path when drawn.

        drawingSubsystem.setViewportCenter(path.getNodes().get((path.getNodes().size()-1)/2).getCoordinate());

        // Open the directions pop out
        mainSidebarMap.get(PopOutType.DIRECTIONS).handle(null);

    }
}