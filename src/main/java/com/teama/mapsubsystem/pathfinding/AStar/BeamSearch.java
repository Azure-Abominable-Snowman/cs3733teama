package com.teama.mapsubsystem.pathfinding.AStar;

import com.teama.mapsubsystem.data.MapEdge;
import com.teama.mapsubsystem.data.MapNode;

public class BeamSearch extends AStar {

    int maxsize;

    /**
     * Put all the nodes that are linked to checking into the queue while keep the queue within the max size.
     * @param checking is the node currently under examining.
     */
    @Override
    protected void putNodesIntoQueue (KnownPoint checking)
    {
        for(MapEdge e : checking.getEdge()) // putting the adjacentNodes into queue
        {
            MapNode nextNode= adjacentNode(e,checking.getNode());  // get the node to be calculated.

            if( !checkedPoints.containsKey(nextNode.getId())) {  // prevent from going to points already been at.
                int newPastCost = checking.getPastCost() + (int) e.getWeight();

                KnownPoint nextPoint = new KnownPoint(nextNode, checking, newPastCost,
                        newPastCost + calDistance(nextNode, end)); // Generate a new Point from checking point to add into queue.
                queue.add(nextPoint); // add into queue
            }
        }
    }

}
