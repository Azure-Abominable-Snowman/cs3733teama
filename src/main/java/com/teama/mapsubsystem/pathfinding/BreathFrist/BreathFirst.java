package com.teama.mapsubsystem.pathfinding.BreathFrist;

import com.teama.mapsubsystem.data.MapNode;
import com.teama.mapsubsystem.data.MapEdge;
import com.teama.mapsubsystem.pathfinding.Path;
import com.teama.mapsubsystem.pathfinding.PathAlgorithm;

import java.util.*;

public class BreathFirst implements PathAlgorithm {
    private Queue<KnownPoint> open ;
    private HashMap<String,KnownPoint> visited;

    @Override
    public Path generatePath(MapNode start, MapNode end) {

        open = new LinkedList<>();
        visited = new HashMap<>();

        KnownPoint checking;
        for( checking = new KnownPoint(start, 0);
            checking.getNode().getId()!=end.getId();
            checking=open.poll())
        {
            putNodesIntoQueue(checking, checking.getLayer() + 1);
            visited.put(checking.getNode().getId(), checking);
            if( open.peek() ==null )
                throw new java.lang.RuntimeException("Cannot Generate a route from the given start and end.");
        }

        return formatOutput(collectPath(checking));
    }

    /**
     * put all the unvisited nodes into open list (no duplicates in open list)
     * @param checking is the current node
     * @param layer is the layer number for those after this node
     */
    private void putNodesIntoQueue (KnownPoint checking , int layer)
    {
        KnownPoint nextPoint;
        for (MapNode node: checking.getAdjacentNodes()) {
            nextPoint = new KnownPoint(node,layer);
            if(  (! visited.containsKey(node.getId())) && (! open.contains(nextPoint))  )
                open.add(nextPoint);
        }
    }

    private Stack<MapNode> collectPath(KnownPoint lastPoint)
    {
        Stack<MapNode> output = new Stack<>();
        output.push(lastPoint.getNode());
        for(;lastPoint.getLayer()!=0;) {
            for (MapNode node : lastPoint.getAdjacentNodes()) {
                if (visited.get(node.getId()).getLayer() == lastPoint.getLayer() - 1) {
                    output.push(node);
                    lastPoint = visited.get(node.getId());
                    break;
                }
            }
        }

        return output;
    }

    protected Path formatOutput(Stack<MapNode> finalPath)
    {
        Path output = new Path();
        MapNode currentNode = finalPath.pop(); // extract the first Node of the list.
        output.addNode(currentNode); // put the start node into it.

        MapNode nextNode;
       for(;!finalPath.empty();)
       {
           nextNode=finalPath.pop();
           output.addNode(nextNode);
           output.addEdge(getEdgeBetweenNodes(nextNode,currentNode));
           currentNode=nextNode;
       }
        return output;
    }


    private MapEdge getEdgeBetweenNodes(MapNode a, MapNode b)
    {
        for (MapEdge mapEdge : a.getEdges()) {
            if(mapEdge.getStart().getId().equals(b.getId()) || mapEdge.getEnd().getId().equals(b.getId())) {
                return mapEdge;
            }
        }
        return null;
    }


}
