package com.teama.mapsubsystem.pathfinding;

import com.teama.mapsubsystem.data.Floor;
import com.teama.mapsubsystem.data.Location;
import com.teama.mapsubsystem.data.NodeType;

import java.util.ArrayList;

/**
 * Represents a group of directions, needs to find the right images and the like for the directions in question
 */
public class TextDirections {
    private ArrayList<Direction> directions;
    private ArrayList<Location> floorList ;

    public TextDirections(ArrayList<Direction> list) {
        directions=list;
    }

    public ArrayList<Direction> getDirections() {
        return directions;
    }

    public ArrayList<Location> getFloorDirections()
    {
        if(floorList!=null) return floorList;
        // if the list is never made
        floorList = new ArrayList<>();
        floorList.add(directions.get(0).getStart()); // add in the starting floor.
        for(int i=1;i<directions.size();++i)
        {
            Direction thisdir = directions.get(i);
            String thisturn = thisdir.getTurn().toString();
            if(thisturn.equals(TurnType.ELEVATOR.toString())
                    || thisturn.equals(TurnType.STAIR.toString()))
            {
                floorList.add(thisdir.getEnd()); // add the floor of the end once.
            }
        }
        return floorList;
    }
}
