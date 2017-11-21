package com.teama.mapsubsystem.data;

import com.teama.drawing.MapDisplay;

public interface MapEdge {

    MapNode getStart();

    void setStart(MapNode start);

    MapNode getEnd();

    void setEnd(MapNode end);

    boolean isOnFloor(String floor);

    boolean doesNotCrossFloors();

    double getWeight();

    String getStartID();

    String getEndID();

    void setWeight(double weight);

    String getId();

    String toCSV();

    String toSQLVals();

    void displayOnScreen(MapDisplay display);
}
