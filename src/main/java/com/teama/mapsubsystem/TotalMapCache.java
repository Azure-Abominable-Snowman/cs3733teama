package com.teama.mapsubsystem;

import com.teama.mapsubsystem.data.MapDataSource;
import com.teama.mapsubsystem.data.MapEdge;
import com.teama.mapsubsystem.data.MapNode;

import java.util.ArrayList;
import java.util.HashMap;

public class TotalMapCache extends MapCache {
    public TotalMapCache(MapDataSource dataSource) {
        super(dataSource);
        nodeCash = new HashMap<>();
        edgeCash = new HashMap<>();
    }

    HashMap<String,MapNode> nodeCash ;
    HashMap<String,MapEdge> edgeCash ;

    @Override
    public MapNode getNode(String id) {
        // Try hashmap

        // If not, look for in the db
        // if found, put in hashmap w/ cache algorithm

        // If not found, return null as normal

        return null;
    }

    @Override
    public MapNode getNode(String description, boolean longDescription) {
        return null;
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
        return null;
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
        return null;
    }

    @Override
    public ArrayList<MapEdge> getEdgesOnFloor(String floor) {
        return null;
    }
}
