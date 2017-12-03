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



    @Override
    public MapNode getNode(String id) {
        return nodeCash.get(id);
    }

    @Override
    public MapNode getNode(String description, boolean longDescription) {
        if(longDescription) return longDescribeToNode.get(description);
        else return describeToNode.get(description);
    }

    @Override
    public void addNode(MapNode node) {

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
        return null;
    }

    @Override
    public MapEdge getEdge(String id) {
        return null;
    }

    @Override
    public void close() {
        dataSource.close();
    }

    @Override
    public ArrayList<MapNode> getNodesOnFloor(String floor) {
        return floorNode.get(floor);
    }

    @Override
    public ArrayList<MapEdge> getEdgesOnFloor(String floor) {
        return null;
    }
}
