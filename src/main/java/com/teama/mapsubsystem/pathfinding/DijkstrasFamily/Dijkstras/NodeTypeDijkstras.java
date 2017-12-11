package com.teama.mapsubsystem.pathfinding.DijkstrasFamily.Dijkstras;

import com.teama.mapsubsystem.data.MapNode;
import com.teama.mapsubsystem.data.NodeType;
import com.teama.mapsubsystem.pathfinding.Path;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.PriorityQueue;

public class NodeTypeDijkstras extends Dijkstras {
    NodeType endtype;


    /**
     * The main function that would find the shortest path to the closest node that is the certain node type.
     * @param start the start node
     * @param endNodeType the node type that this function will try to find and return once it found one. which will be the closest one.
     * @return the path to the closest node that is this type.
     */
    public Path generatePath(MapNode start, NodeType endNodeType) {
        this.start=start;
        endtype = endNodeType;
        checkedPoints= new HashMap<>();
        queue=new PriorityQueue<>();
        if(disableNodes==null) disableNodes= new HashMap<String, MapNode>();

        KnownPointD checking ; // create a temp variable to keep track of which node are we on.

        //Generate Path
        for(checking = new KnownPointD(start,null,0);
            !checking.getNode().getNodeType().toString().equals(endtype.toString());   // reached the proper node type TODO, this could be a lambda.
            checking=queue.poll() // move forward one step
                )
        {
            putNodesIntoQueue(checking); // put adjacent node into queue.
            checkedPoints.put(checking.getNode().getId(),checking);
            if(queue.peek()==null) {
                throw new java.lang.RuntimeException("Cannot generate a route from the given start and end.");
            }
        }
        // Done generating, output the path
        // make it into the format of outputting.
        return formatOutput(collectPath(checking));
    }


    public Path generatePath(MapNode start, NodeType endNodeType, ArrayList<MapNode> disableNodes){
        this.disableNodes=grabDisableNodes(disableNodes);
        return generatePath(start, endNodeType);
    }
}
