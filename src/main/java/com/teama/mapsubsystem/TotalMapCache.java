package com.teama.mapsubsystem;

import com.teama.mapsubsystem.data.Floor;
import com.teama.mapsubsystem.data.MapDataSource;
import com.teama.mapsubsystem.data.MapEdge;
import com.teama.mapsubsystem.data.MapNode;

import java.util.ArrayList;
import java.util.HashMap;



public class TotalMapCache extends MapCache {

    ArrayList<String> nodeIds;
    HashMap<String,MapNode> nodeCash ;
    HashMap<String,MapNode> describeToNode;
    HashMap<String,MapNode> longDescribeToNode;
    ArrayList<String> edgeIds;
    HashMap<String,MapEdge> edgeCash ;

    HashMap<String,ArrayList<MapNode>> floorNode;
    HashMap<String,ArrayList<MapEdge>> floorEdge;

    public TotalMapCache(MapDataSource dataSource) {
        super(dataSource);
        // pointer stored into MapDataSource dataSource.

        nodeIds = new ArrayList<>();
        nodeCash = new HashMap<>();
        describeToNode = new HashMap<>();
        longDescribeToNode = new HashMap<>();
        edgeIds = new ArrayList<>();
        edgeCash = new HashMap<>();

        floorNode = new HashMap<>();
        floorEdge = new HashMap<>();


        initialCash ();
    }



    private void initialCash ()
    {
        // node related.
        nodeIds = dataSource.getNodeIds();

        for (String id : nodeIds) {
            nodeCash.put(id,dataSource.getNode(id));
        }
        for (MapNode mapNode : nodeCash.values()) {
            describeToNode.put(mapNode.getShortDescription(),mapNode);
        }
        for (MapNode mapNode : nodeCash.values()) {
            longDescribeToNode.put(mapNode.getLongDescription(),mapNode);
        }

        // edge related.
        edgeIds=dataSource.getEdgeIds();
        for (String edgeId : edgeIds) {
            edgeCash.put(edgeId,dataSource.getEdge(edgeId));
        }

        for (Floor floor : Floor.values()) {
            floorNode.put(floor.toString(),dataSource.getNodesOnFloor(floor.toString()));
        }

        for (Floor floor : Floor.values()) {
            floorEdge.put(floor.toString(),dataSource.getEdgesOnFloor(floor.toString()));
        }

    }


    // TODO: do a check in all the gets to prevent case of missing.

    @Override
    public MapNode getNode(String id) {
        MapNode result = nodeCash.get(id);
        if( result == null ) {
            result = dataSource.getNode(id);
            addmissedNode(result);
        }
        return nodeCash.get(id);
    }

    @Override
    public MapNode getNode(String description, boolean longDescription) {
        HashMap<String, MapNode> localDescribToNode;
        if (longDescription) localDescribToNode = longDescribeToNode; // todo check the meaning for the boolean pram.
        else localDescribToNode = describeToNode;
        MapNode temp = localDescribToNode.get(description);
        if (temp == null) {
            temp = dataSource.getNode(description, longDescription);
            addmissedNode(temp);
        }
        return temp;
    }

    @Override
    public void addNode(MapNode node) {
        dataSource.addNode(node); // first add into dataSource.
        // adding to cache list. //TODO not done.

    }

    @Override
    public void removeNode(String id) {

    }

    @Override
    public void addEdge(MapEdge edge) {

    }

    @Override
    public void removeEdge(String id) {

    }

    @Override
    public ArrayList<String> getNodeIds() {
        return nodeIds;
    }

    @Override
    public ArrayList<String> getEdgeIds() {
        return edgeIds;
    }

    @Override
    public MapEdge getEdge(String id) {
        return edgeCash.get(id);
    }

    @Override
    public ArrayList<MapNode> getNodesOnFloor(String floor) {
        return floorNode.get(floor);
    }

    @Override
    public ArrayList<MapEdge> getEdgesOnFloor(String floor) {
        return floorEdge.get(floor);
    }

    @Override
    public void close() {
        dataSource.close();
    }


    /**
     * this will reset the dataSource, then rerun the initialCash
     * @param newSource the new source that the cache will rely on.
     */
    public void reset(MapDataSource newSource)
    {
        dataSource  = newSource;
        initialCash();
    }

    /**
     * In the case of dataSource contains it but cache doesn't.
     * @param node the node that is missed.
     */
    private void addmissedNode(MapNode node)
    {
        //TODO poplate this, maybe add node can reuse this.
    }

    /**
     * In the case of dataSource contains it but cache doesn't.
     * @param edge the node that is missed.
     */
    private void addmissedEdge(MapEdge edge)
    {
        //TODO poplate this, maybe add edge can reuse this.
    }

}
