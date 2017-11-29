package com.teama.mapsubsystem;

import com.teama.mapsubsystem.data.Location;
import com.teama.mapsubsystem.data.MapNode;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.Map;

public class NearestNode {
    public MapNode findNearestNode (Location hitLocation)
    {
        double minValue=10000000.0,nextValue;
        MapNode nearestNode = null;
        Map<String,MapNode> nodes =MapSubsystem.getInstance().getVisibleFloorNodes(hitLocation.getLevel());
        for (MapNode node : nodes.values()) {
            nextValue = calDistance(hitLocation,node);
            if ( minValue > nextValue )
            {
                minValue= nextValue;
                nearestNode = node;
            }
        }
        return nearestNode;
    }

    private double calDistance(Location point1 , MapNode node2)
    {
        double dx = point1.getxCoord() - node2.getCoordinate().getxCoord() ;
        double dy = point1.getyCoord() - node2.getCoordinate().getyCoord() ;
        return Math.sqrt(dx*dx+dy*dy);
    }

}
