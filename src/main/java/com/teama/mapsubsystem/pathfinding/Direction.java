package com.teama.mapsubsystem.pathfinding;

import com.teama.mapsubsystem.data.Location;

public class Direction {
    private double lengthOfPath;
    private Location start, end;
    private String description;

    public Direction(double length, Location start, Location end, String description) {
        this.lengthOfPath = length;
        this.start = start;
        this.end = end;
        this.description = description;
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
}
