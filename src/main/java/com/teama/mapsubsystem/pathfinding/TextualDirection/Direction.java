package com.teama.mapsubsystem.pathfinding.TextualDirection;

import com.teama.mapsubsystem.data.Location;

import java.awt.*;
import javax.swing.*;


public class Direction {
    private double lengthOfPath;
    private Location start, end;
    private String description;
    private TurnType turn;


    public Direction(double length, Location start, Location end, String description , TurnType turn) {

        this.lengthOfPath = length;
        this.start = start;
        this.end = end;
        this.description = description;
      //  this.icon = icon;
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


}
