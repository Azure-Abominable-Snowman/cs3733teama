package com.teama.mapsubsystem.data;

import com.teama.drawing.MapDisplay;

import java.util.ArrayList;

public abstract class DrawNode implements MapNode {

    private MapNode mapNode;

    public DrawNode(MapNode mapNode) {
        this.mapNode = mapNode;
    }

    @Override
    public String getId() {
        return mapNode.getId();
    }

    @Override
    public Enum<NodeType> getNodeType() {
        return mapNode.getNodeType();
    }

    @Override
    public ArrayList<MapEdge> getEdges() {
        return mapNode.getEdges();
    }

    @Override
    public void setEdges(ArrayList<MapEdge> edges) {
        mapNode.setEdges(edges);
    }

    @Override
    public void addEdge(MapEdge edge) {
        mapNode.addEdge(edge);
    }

    @Override
    public String getShortDescription() {
        return mapNode.getShortDescription();
    }

    @Override
    public void setShortDescription(String shortDescription) {
        mapNode.setShortDescription(shortDescription);
    }

    @Override
    public String getLongDescription() {
        return mapNode.getLongDescription();
    }

    @Override
    public void setLongDescription(String longDescription) {
        mapNode.setLongDescription(longDescription);
    }

    @Override
    public String getTeamAssignment() {
        return mapNode.getTeamAssignment();
    }

    @Override
    public String toCSV() {
        return mapNode.toCSV();
    }

    @Override
    public String toSQLVals() {
        return mapNode.toSQLVals();
    }

    @Override
    public Location getCoordinate() {
        return mapNode.getCoordinate();
    }


    public abstract void displayOnScreen(MapDisplay display);

    public abstract void removeFromScreen(MapDisplay display);
}
