package entities;

public interface MapDataSource {
    default MapNode getNode(String id) {
        return null;
    }

    default MapEdge getAdjacentEdges(String id) {
        return null;
    }

    default MapEdge getAdjacentNodes(String id) {
        return null;
    }

    default void addNode(MapNode node) {

    }

    default void removeNode(String id) {

    }

    default void addEdge(MapEdge edge) {

    }
}
