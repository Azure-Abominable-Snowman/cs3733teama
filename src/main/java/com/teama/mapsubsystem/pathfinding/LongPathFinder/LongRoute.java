package com.teama.mapsubsystem.pathfinding.LongPathFinder;

import com.teama.mapsubsystem.data.MapNode;

import java.util.ArrayList;

public class LongRoute {
    private ArrayList<MapNode> route = new ArrayList<>();
    private double distance ;

    public LongRoute(MapNode endNode) {
        this.route.add(endNode);
        distance = 0;
    }

    /**
     * This function is to add the next node into the end of the list and accumulate the distance
     * @param newNode is the node needed to add
     */
    public void addNodeToBack (MapNode newNode,double weight)
    {
        route.add(newNode);
        distance+=weight;
    }

    //getters

    public double getDistance() {
        return distance;
    }

    public ArrayList<MapNode> getRoute() {
        return route;
    }
}
