package com.teama.mapsubsystem.pathfinding.DijkstrasFamily;

import com.teama.mapsubsystem.data.MapEdge;
import com.teama.mapsubsystem.data.MapNode;

import java.util.ArrayList;

public abstract class KnownPoint implements  Comparable  {

    protected MapNode node;
    protected int pastCost;

    public MapNode getNode() {
        return node;
    }

    public void setNode(MapNode node) {
        this.node = node;
    }

    public int getPastCost() {
        return pastCost;
    }

    public void setPastCost(int pastCost) {
        this.pastCost = pastCost;
    }

    /**
     * a little helper function
     * @return return the edges contained with stored node.
     */
    public ArrayList<MapEdge> getEdge()
    {
        return node.getEdges();
    }


}
