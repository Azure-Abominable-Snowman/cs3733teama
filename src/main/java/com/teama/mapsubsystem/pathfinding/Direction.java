package com.teama.mapsubsystem.pathfinding;

import com.teama.mapsubsystem.data.Location;
import com.teama.mapsubsystem.data.MapNode;

import java.util.ArrayList;


public class Direction {
    private double lengthOfPath;
    private Location start, end;
    private String description;
    private TurnType turn;
    private double timeInSec;
    private final double meterPerSec = 1.4 ; // from wikipedia
    private ArrayList<MapNode> highLightNodes ;

    public Direction(double length, Location start, Location end, String description , TurnType turn) {

        this.lengthOfPath = length;
        this.start = start;
        this.end = end;
        this.description = description;
        this.turn = turn;
        timeInSec = lengthOfPath / meterPerSec ;
        highLightNodes = new ArrayList<>();
    }

    public Direction(double length, Location start, Location end, String description , TurnType turn,ArrayList<MapNode> highLightNodes) {
        this(length,start,end,description,turn);
        this.highLightNodes = highLightNodes;
    }

    public double getLengthOfPath() {
        return lengthOfPath;
    }

    public Location getStart() {
        return start;
    }

    public Location getEnd() {
        return end;
    }

    public String getDescription() {
        return description;
    }

    public TurnType getTurn() {
        return turn;
    }

    public double getTimeInSec() {
        return timeInSec;
    }

    public ArrayList<MapNode> getHighLightNodes() { return highLightNodes; }
}
