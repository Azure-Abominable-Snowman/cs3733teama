package com.teama.mapsubsystem.data;

import com.teama.drawing.MapDisplay;

import java.util.ArrayList;

public abstract class MapNode {
    Location coordinate;
    String id, shortDescription, longDescription, teamAssignment;
    Enum<NodeType> nodeType;
    ArrayList<MapEdge> edges;
    private DrawNode animationStyle;

    public MapNode(String id, Location coordinate, Enum<NodeType> nodeType, String longDescription,
                       String shortDescription, String teamAssignment) {
        this(id, coordinate, nodeType, longDescription, shortDescription, teamAssignment, new ArrayList<>());
    }

    public MapNode(String id, Location coordinate, Enum<NodeType> nodeType, String longDescription,
                       String shortDescription, String teamAssignment, ArrayList<MapEdge> edges) {
        this.coordinate = coordinate;
        this.id = id;
        this.nodeType = nodeType;
        this.edges = edges;
        this.shortDescription = shortDescription;
        this.longDescription = longDescription;
        this.teamAssignment = teamAssignment;
        animationStyle = new DrawNodeInstantly();
    }

    public abstract String getId();
    public abstract Enum<NodeType> getNodeType();
    public abstract ArrayList<MapEdge> getEdges();
    public abstract void setEdges(ArrayList<MapEdge> edges);
    public abstract void addEdge(MapEdgeData edge);
    public abstract String getShortDescription();
    public abstract void setShortDescription(String shortDescription);
    public abstract String getLongDescription();
    public abstract void setLongDescription(String longDescription);
    public abstract String getTeamAssignment();
    public abstract String toCSV();
    public abstract String toSQLVals();
    public abstract Location getCoordinate();

    public void setAnimationStyle(DrawNode animationStyle) {
        this.animationStyle = animationStyle;
    }

    public void displayOnScreen(MapDisplay display) {
        animationStyle.displayOnScreen(display);
    }
}
