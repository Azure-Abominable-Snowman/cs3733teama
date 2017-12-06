package com.teama.mapsubsystem.pathfinding.TextualDirection;

import com.teama.mapsubsystem.data.Location;



public class Direction {
    private double lengthOfPath;
    private Location start, end;
    private String description;
    private TurnType turn;
    private double timeInSec;
    private final double meterPerSec = 1.4 ; // from wikipedia

    public Direction(double length, Location start, Location end, String description , TurnType turn) {

        this.lengthOfPath = length;
        this.start = start;
        this.end = end;
        this.description = description;
        this.turn = turn;
        timeInSec = lengthOfPath / meterPerSec ;
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
}
