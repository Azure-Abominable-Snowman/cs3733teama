package com.teama.mapsubsystem.pathfinding.TextualDirection;

import java.util.ArrayList;

/**
 * Represents a group of directions, needs to find the right images and the like for the directions in question
 */
public class TextDirections {
    private ArrayList<Direction> directions;

    public TextDirections(ArrayList<Direction> list) {
        directions=list;
    }

    public ArrayList<Direction> getDirections() {
        return directions;
    }
}
