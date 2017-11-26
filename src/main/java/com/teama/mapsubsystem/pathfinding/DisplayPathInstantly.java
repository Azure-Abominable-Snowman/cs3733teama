package com.teama.mapsubsystem.pathfinding;

import com.teama.drawing.MapDisplay;
import com.teama.mapsubsystem.data.*;
import javafx.scene.paint.Color;

public class DisplayPathInstantly extends DisplayPath {
    public DisplayPathInstantly(Path path) {
        super(path);
    }

    @Override
    public void displayOnScreen(MapDisplay display) {
        /*for(MapNode n : getNodes()) {
            new DrawNodeInstantly(n).displayOnScreen(display);
        }*/
        for(MapEdge e : getConnectors()) {
            new DrawEdgeInstantly(e).displayOnScreen(display);
        }
    }

    @Override
    public void displayOnScreen(MapDisplay display, Floor floor) {
        // boolean to highlight the start of a path on the floor special
        boolean drawnFirst = false;
        // Last node drawn
        MapNode lastDrawn = null;

        // Turn them the right way and then display the edges
        // Node where the path starts
        MapNode startNode = getNodes().get(0);
        System.out.println("START NODE: "+startNode.getShortDescription());
        // Node where the first part of the path ends
        MapNode lastEnd = null;
        // Start from the second edge and turn all of the connectors the right way around and then draw them
        for(int i = 0; i < getConnectors().size(); i++) {
            MapEdge curEdge = getConnectors().get(i);
            if(lastEnd == null) {
                lastEnd = curEdge.getEnd();
                // The first one always gets swapped, so make it initially backward
                if(!lastEnd.getId().equals(startNode.getId())) {
                    // Flip so the start node is first
                    lastEnd = getConnectors().get(0).getStart();
                }
            }
            if(curEdge.getStartID().compareTo(lastEnd.getId()) == 0) {
                // Doesn't need to be swapped
                System.out.println(curEdge.getStartID()+" "+curEdge.getEndID()+" NO SWAP");
                lastEnd = curEdge.getEnd();
            } else {
                // Needs to be swapped
                curEdge = new MapEdgeData(curEdge.getId(), curEdge.getEnd(), curEdge.getStart());
                System.out.println(curEdge.getStartID()+" "+curEdge.getEndID()+" SWAP");
                lastEnd = curEdge.getEnd();
            }
            if(curEdge.getStart().getCoordinate().getLevel().equals(floor) && curEdge.getEnd().getCoordinate().getLevel().equals(floor)) {
                new DrawEdgeInstantly(curEdge).displayOnScreen(display);
                if(!drawnFirst) {
                    new DrawNodeInstantly(curEdge.getStart()).displayOnScreen(display, 9, Color.RED);
                    drawnFirst = true;
                }
                lastDrawn = lastEnd;
            }
        }
        // Draw the end special
        if(lastDrawn != null) {
            new DrawNodeInstantly(lastDrawn).displayOnScreen(display, 9, Color.RED);
        }

        // TODO: Highlight switches between floors clearly
    }

    public void deleteFromScreen(MapDisplay display) {
        // Delete all the edges from the screen
        for(MapEdge e : getConnectors()) {
            display.deleteLine(e.getId());
        }
        // Redisplay all the visible nodes in the path on the screen
        for(MapNode n : getNodes()) {
            if(n.getNodeType() != NodeType.HALL) {
                new DrawNodeInstantly(n).displayOnScreen(display);
            }
        }
    }
}
