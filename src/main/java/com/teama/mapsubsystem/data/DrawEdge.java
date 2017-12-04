package com.teama.mapsubsystem.data;

import com.teama.mapdrawingsubsystem.MapDisplay;

public abstract class DrawEdge implements MapEdge {
    private MapEdge edge;

    public DrawEdge(MapEdge edge) {
        this.edge = edge;
    }

    @Override
    public MapNode getStart() {
        return edge.getStart();
    }

    @Override
    public void setStart(MapNode start) {
        edge.setStart(start);
    }

    @Override
    public MapNode getEnd() {
        return edge.getEnd();
    }

    @Override
    public void setEnd(MapNode end) {
        edge.setEnd(end);
    }

    @Override
    public boolean isOnFloor(String floor) {
        return edge.isOnFloor(floor);
    }

    @Override
    public boolean doesNotCrossFloors() {
        return edge.doesNotCrossFloors();
    }

    @Override
    public double getWeight() {
        return edge.getWeight();
    }

    @Override
    public String getStartID() {
        return edge.getStartID();
    }

    @Override
    public String getEndID() {
        return edge.getEndID();
    }

    @Override
    public void setWeight(double weight) {
        edge.setWeight(weight);
    }

    @Override
    public String getId() {
        return edge.getId();
    }

    @Override
    public String toCSV() {
        return edge.toCSV();
    }

    @Override
    public String toSQLVals() {
        return edge.toSQLVals();
    }

    public abstract void displayOnScreen(MapDisplay display);

    public abstract void removeFromScreen(MapDisplay display);
}
