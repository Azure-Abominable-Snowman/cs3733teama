package com.teama.mapsubsystem.data;

import java.util.ArrayList;

public interface MapDataSource {
    /**
     * Gets a node from the data source
     * @param id
     * @return
     */
    default MapNodeData getNode(String id) {
        return null;
    }

    /**
     * Adds a node to the data source
     * @param node
     */
    default void addNode(MapNodeData node) {

    }

    /**
     * Removes a node from the data source
     * @param id
     */
    default void removeNode(String id) {

    }

    /**
     * Adds an edge
     * @param edge
     */
    default void addEdge(MapEdgeData edge) {

    }
    default void removeEdge(String id) {

    }

    /**
     * Gets an array of all the node id strings
     * @return
     */
    default ArrayList<String> getNodeIds() {
        return null;
    }

    /**
     * Returns an array of all the edge id strings
     * @return
     */
    default ArrayList<String> getEdgeIds() {
        return null;
    }

    /**
     * Gets a specified edge by id
     * @param id
     * @return
     */
    default MapEdgeData getEdge(String id) { return null; }

    default void close() {
    }

    default ArrayList<MapNodeData> getNodesOnFloor(String floor) {
        return null;
    }

    default ArrayList<MapEdgeData> getEdgesOnFloor(String floor) {
        return null;
    }

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
