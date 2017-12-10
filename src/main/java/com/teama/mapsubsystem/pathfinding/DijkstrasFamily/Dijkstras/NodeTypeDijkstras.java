package com.teama.mapsubsystem.pathfinding.DijkstrasFamily.Dijkstras;

import com.teama.mapsubsystem.data.MapNode;
import com.teama.mapsubsystem.data.NodeType;
import com.teama.mapsubsystem.pathfinding.Path;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.PriorityQueue;

public class NodeTypeDijkstras extends Dijkstras {
    NodeType endtype;


    public Path generatePath(MapNode start, NodeType endNodeType) {
        this.start=start;
        endtype = endNodeType;
        checkedPoints= new HashMap<>();
        queue=new PriorityQueue<>();
        if(disableNodes==null) disableNodes= new HashMap<String, MapNode>();

        KnownPointD checking ; // create a temp variable to keep track of which node are we on.

        //Generate Path
        for(checking = new KnownPointD(start,null,0);
            !checking.getNode().getNodeType().toString().equals(end.getNodeType().toString());   // reached the proper node type
            checking=queue.poll() // move forward one step
                )
        {
            putNodesIntoQueue(checking); // put adjacent node into queue.
            checkedPoints.put(checking.getNode().getId(),checking);
            if(queue.peek()==null) {
                throw new java.lang.RuntimeException("Cannot generate a route from the given start and end.");
            }
            // @TODO double check if this is good enough for errors.
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
