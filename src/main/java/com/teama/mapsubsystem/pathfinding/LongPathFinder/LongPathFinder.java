package com.teama.mapsubsystem.pathfinding.LongPathFinder;

import com.teama.mapsubsystem.data.MapEdge;
import com.teama.mapsubsystem.data.MapNode;
import com.teama.mapsubsystem.pathfinding.Path;
import com.teama.mapsubsystem.pathfinding.PathAlgorithm;

import java.util.ArrayList;
import java.util.HashMap;

import static java.lang.Math.abs;
import static java.lang.Math.sqrt;

public class LongPathFinder implements PathAlgorithm{
    protected MapNode start, end;
    private HashMap<String, MapNode> disableNodes ;
    private HashMap<String,MapNode> nonuseableNodes ;
    private long mapsizeCounter =0;

    @Override
    public Path generatePath(MapNode start, MapNode end) {
        this.start=start;
        this.end=end;
        if(disableNodes==null) disableNodes= new HashMap<String, MapNode>();

        nonuseableNodes = new HashMap<>(disableNodes);
        //return formOutput((longRouteFromThisLessMemory(start)));
        return formOutput(longestRouteFromThis(start,new HashMap<>(disableNodes)));

    }

    // by removing itself from the hashMap, it only need one global copy of the nodes that have already been through or can't
    private LongRoute longRouteFromThisLessMemory(MapNode start)
    {
            nonuseableNodes.put(start.getId(), start);
        //end condition
        if(start.getId().equals(end.getId())){
            nonuseableNodes.remove(start.getId());
            return new LongRoute(start);
        }
        // not the end, keep looking
        LongRoute longest = new LongRoute(start);
        for (MapEdge edge : start.getEdges()) {
            MapNode nextNode = adjacentNode(edge, start);
            if (nonuseableNodes.containsKey(nextNode.getId())) {
                continue;
            }
            LongRoute thisRoute = longRouteFromThisLessMemory(nextNode);
            if(thisRoute == null)  continue;
            thisRoute.addNodeToBack(start,edge.getWeight());
            if(thisRoute.getDistance() > longest.getDistance()) {
                longest = thisRoute;
            }
        }
        if(longest.getDistance()==0){
            nonuseableNodes.remove(start.getId());
            return null;
        }
       // nonuseableNodes.remove(start.getId());
        return  longest;
    }

    private LongRoute longestRouteFromThis(MapNode start, HashMap<String,MapNode> unusableNode) {
        unusableNode.put(start.getId(), start);

        //end condition
        if(start.getId().equals(end.getId())){
            return new LongRoute(start);
        }

        // not the end, keep looking
        LongRoute longest = new LongRoute(start);
        for (MapEdge edge : start.getEdges()) {
            MapNode nextNode = adjacentNode(edge, start);
            if (unusableNode.containsKey(nextNode.getId())) {
                continue;
            }
            LongRoute thisRoute = longestRouteFromThis(nextNode, new HashMap<>(unusableNode));
            if(thisRoute == null){
                continue;
            }
            thisRoute.addNodeToBack(start,edge.getWeight());
            if(thisRoute.getDistance() > longest.getDistance()) {
                longest = thisRoute;
            }
        }
        if(longest.getDistance()==0){
            return null;
        }
        return  longest;
    }


    @Override
    public Path generatePath(MapNode start, MapNode end, ArrayList<MapNode> disableNodes){
        this.disableNodes=grabDisableNodes(disableNodes);
        return generatePath(start, end);
    }

    /////// helpers


    protected HashMap<String,MapNode> grabDisableNodes(ArrayList<MapNode> nodes){
        HashMap<String,MapNode> temp = new HashMap<>();
        for(int i = 0; i < nodes.size(); i++){
            temp.put(nodes.get(i).getId(), nodes.get(i));
        }
        return temp;
    }


    /**
     * This helper function is to use the abs value of coordinates difference to calculate difference.
     * @param n1 is the start node.
     * @param n2 is the end node.
     * @return returns the sum of x coord diff and y coord diff.
     */
    public static int calDistance(MapNode n1, MapNode n2)
    {
        double x = (double) abs(n1.getCoordinate().getxCoord() - n2.getCoordinate().getxCoord());
        double y = (double) abs(n1.getCoordinate().getyCoord() - n2.getCoordinate().getxCoord());
        return (int) sqrt ( x*x+y*y ) ;
    }


    protected MapNode adjacentNode(MapEdge e, MapNode n)
    {
        if(e.getStart().getId().equals(n.getId())) return e.getEnd();
        else return e.getStart();
    }

    private Path formOutput(LongRoute route) {
        if(route == null){
            throw new java.lang.RuntimeException("Cannot generate a route from the given start and end.");
        }
        ArrayList<MapNode> reversePath = route.getRoute();

        Path output = new Path();
        MapNode currentNode = reversePath.get(reversePath.size()-1); // extract the first Node of the list.
        output.addNode(currentNode); // put the start node into it.
        MapNode nextNode;
        for(int i =reversePath.size()-2;i>-1;--i)
        {
            nextNode=reversePath.get(i); // extract the second node
            output.addNode(nextNode);             //store the next node
            output.addEdge(getEdgeBetweenNodes(nextNode,currentNode)); // store the edge between them.
            currentNode=nextNode;   // move forward one step.
        }
        return output;
    }


    public static MapEdge getEdgeBetweenNodes(MapNode a, MapNode b)
    {
        for (MapEdge mapEdge : a.getEdges()) {
            if(mapEdge.getStart().getId().equals(b.getId()) || mapEdge.getEnd().getId().equals(b.getId())) {
                return mapEdge;
            }
        }
        return null;
    }

}
