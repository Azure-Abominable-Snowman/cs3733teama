package com.teama.mapsubsystem.data;

import java.util.ArrayList;

public interface MapDataSource {
    /**
     * Gets a node from the data source
     * @param id
     * @return
     */
    MapNode getNode(String id);

    default MapNode getNode(String description, boolean longDescription) {
        System.out.println("Cannot search by description in this data source");
        return null;
    }

    /**
     * Adds a node to the data source
     * @param node
     */
    void addNode(MapNode node);

    /**
     * Removes a node from the data source
     * @param id
     */
    void removeNode(String id);

    /**
     * Adds an edge
     * @param edge
     */
    void addEdge(MapEdge edge);

    void removeEdge(String id);

    /**
     * Gets an array of all the node id strings
     * @return
     */
    ArrayList<String> getNodeIds();

    /**
     * Returns an array of all the edge id strings
     * @return
     */
    ArrayList<String> getEdgeIds();

    /**
     * Gets a specified edge by id
     * @param id
     * @return
     */
    MapEdge getEdge(String id);

    void close();

    ArrayList<MapNode> getNodesOnFloor(String floor);

    ArrayList<MapEdge> getEdgesOnFloor(String floor);

    /**
     * Adds everything from the selected data source to this one
     * @param dataSource
     */
    default void addAll(MapDataSource dataSource) {
        for (String id : dataSource.getNodeIds()) {
            addNode(dataSource.getNode(id));
        }
        for (String id : dataSource.getEdgeIds()) {
            addEdge(dataSource.getEdge(id));
        }
    }
}
