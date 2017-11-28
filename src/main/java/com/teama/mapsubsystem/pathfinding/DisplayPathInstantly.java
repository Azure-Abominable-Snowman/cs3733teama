package com.teama.mapsubsystem.pathfinding;

import com.teama.drawing.MapDisplay;
import com.teama.mapsubsystem.data.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

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
        // Clear all previous annotations from the screen
        display.clearText();
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
                //System.out.println(curEdge.getStartID()+" "+curEdge.getEndID()+" NO SWAP");
                lastEnd = curEdge.getEnd();
            } else {
                // Needs to be swapped
                curEdge = new MapEdgeData(curEdge.getId(), curEdge.getEnd(), curEdge.getStart());
                //System.out.println(curEdge.getStartID()+" "+curEdge.getEndID()+" SWAP");
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

            // See if the floor changes on this, if it does then draw an annotation on the node on the currently displayed floor
            // With the name of the next floor to go to
            Floor endFloor = curEdge.getEnd().getCoordinate().getLevel();
            Floor startFloor = curEdge.getStart().getCoordinate().getLevel();
            String annoText = "";
            if(!startFloor.equals(endFloor) && (startFloor.equals(floor) || endFloor.equals(floor))) {
                MapNode chFloorNode = curEdge.getStart();
                // We now know that the floor is being changed, but we need to follow the path until we arrive at the correct floor
                // Iterate through the the nodes in the path until we arrive on the next floor or the path ends.
                if(startFloor.equals(floor)) {
                    // For start floor -> different floor
                    for(int j = i+1; j < getConnectors().size(); j++) {
                        // Check if the start and end nodes are on the same floor
                        // If they are then this is the destination floor
                        MapEdge checkEdge = getConnectors().get(j);
                        if(checkEdge.getStart().getCoordinate().getLevel().equals(checkEdge.getEnd().getCoordinate().getLevel())) {
                            annoText = checkEdge.getStart().getCoordinate().getLevel().toString();
                            break;
                        }
                    }
                } else {
                    // For other floor -> start floor
                    // For loop must be reversed
                    for(int j = i-1; j < getConnectors().size(); j--) {
                        MapEdge checkEdge = getConnectors().get(j);
                        if(checkEdge.getStart().getCoordinate().getLevel().equals(checkEdge.getEnd().getCoordinate().getLevel())) {
                            annoText = checkEdge.getStart().getCoordinate().getLevel().toString();
                            break;
                        }
                    }
                }
                //System.out.println("DRAW "+annoText+" AS AN ANNOTATION WITH ID "+chFloorNode.getId());
                display.drawText(chFloorNode.getId(), "To "+annoText, chFloorNode.getCoordinate(), Font.font("Courier", FontWeight.BOLD, 16), false);
            }

        }
        // Draw the end special
        if(lastDrawn != null) {
            new DrawNodeInstantly(lastDrawn).displayOnScreen(display, 9, Color.RED);
        }
    }

    public void deleteFromScreen(MapDisplay display, Floor floor) {
        // Delete all the edges from the screen
        for(MapEdge e : getConnectors()) {
            display.deleteLine(e.getId());
        }
        // Redisplay all of the visible nodes on the current floor
        for(MapNode n : getNodes()) {
            if (n.getCoordinate().getLevel().equals(floor) && !n.getNodeType().equals(NodeType.HALL)) {
                new DrawNodeInstantly(n).displayOnScreen(display);
            }
            // Delete all possible annotations
            //display.deleteText(n.getId());
        }
    }

    public void deleteFromScreen(MapDisplay display) {
        deleteFromScreen(display, display.getCurrentFloor());
    }
}
