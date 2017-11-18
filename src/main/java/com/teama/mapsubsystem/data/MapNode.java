package com.teama.mapsubsystem.data;

import com.teama.drawing.MapDisplay;

import java.util.ArrayList;

public interface MapNode {
    String getId();
    Enum<NodeType> getNodeType();
    ArrayList<MapEdge> getEdges();
    void setEdges(ArrayList<MapEdge> edges);
    void addEdge(MapEdge edge);
    String getShortDescription();
    void setShortDescription(String shortDescription);
    String getLongDescription();
    void setLongDescription(String longDescription);
    String getTeamAssignment();
    String toCSV();
    String toSQLVals();
    Location getCoordinate();

    void displayOnScreen(MapDisplay display);
}
