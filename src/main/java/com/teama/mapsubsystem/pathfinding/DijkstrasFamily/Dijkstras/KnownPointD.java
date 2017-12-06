package com.teama.mapsubsystem.pathfinding.DijkstrasFamily.Dijkstras;

import com.teama.mapsubsystem.data.MapNode;
import com.teama.mapsubsystem.data.MapEdge;
import com.teama.mapsubsystem.pathfinding.DijkstrasFamily.KnownPoint;

import java.util.ArrayList;

public class KnownPointD extends KnownPoint {

    protected KnownPointD lastNode;

    //constructor
    public KnownPointD(MapNode node, KnownPointD lastNode, int pastCost) {
        this.node = node;
        this.lastNode = lastNode;
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


    /**
     * Implemented to allow PriorityQueue to sort this.
     * Compare based on the CompleteCost
     * @param o KnownPoint to compare to itself
     * @return 0 if equal. 1 if completeCost in this one is bigger, -1 if this one is smaller.
     */
    @Override
    public int compareTo(Object o) {
        KnownPointD node = (KnownPointD) o;
        if (this.pastCost > node.pastCost) return 1;
        else if (this.pastCost== node.pastCost) return 0;
        else if (this.pastCost< node.pastCost) return -1;
        return 0;
    }

    //////////// getter and setter
    public KnownPointD getLastNode() {
        return lastNode;
    }

    public void setLastNode(KnownPointD lastNode) {
        this.lastNode = lastNode;
    }
}
