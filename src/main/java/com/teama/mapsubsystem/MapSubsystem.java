package com.teama.mapsubsystem;

import com.teama.Configuration;
import com.teama.mapsubsystem.data.*;
import com.teama.mapsubsystem.pathfinding.AStar;
import com.teama.mapsubsystem.pathfinding.Path;
import com.teama.mapsubsystem.pathfinding.PathAlgorithm;
import com.teama.mapsubsystem.pathfinding.TextDirections;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by aliss on 11/11/2017.
 *
 * Facade for all low level map details
 */
public class MapSubsystem {

    private static class MapSubsystemGetter {
        private static final MapSubsystem instance = new MapSubsystem();
    }

    private MapSubsystem() {
        csvSource = new CSVDatabaseSource(nodefile, edgefile); // Reads CSV file
        javaDBSource = new JavaDatabaseSource(Configuration.dbURL, Configuration.nodeTable, Configuration.edgeTable);
        pathGenerator = new AStar();

        // Initially populate the tables with the data from CSV
        javaDBSource.addAll(csvSource);
    }


    public static synchronized MapSubsystem getInstance() {
        return MapSubsystemGetter.instance;
    }

    private MapDataSource javaDBSource; // link to javaDB -- see MapDataSource interface to call functions that update DB
    private MapDataSource csvSource;

    private String nodefile = "/csvdata/MapAnodes.csv";
    private String edgefile = "/csvdata/MapAedges.csv";

    private PathAlgorithm pathGenerator;

    @Deprecated
    public MapDataSource getMap() {
        return javaDBSource;
    }

    @Deprecated
    public void exportToCSV(String nodeFile, String edgeFile) {
        //csvSource.addAll(javaDBSource);
        CSVDatabaseSource export = new CSVDatabaseSource(nodeFile, edgeFile);
        export.addAll(javaDBSource);
        export.close();
    }

    @Deprecated
    public PathAlgorithm getPathGenerator() {
        return pathGenerator;
    }

    public MapNode getNode(String id) {
        return null;
    }

    public MapEdge getEdge(String id) {
        return null;
    }

    public void deleteEdge() {

    }

    public void deleteNode() {

    }

    public Map<String, MapNode> getFloorNodes(Floor floor) {
        ArrayList<MapNode> nodes = javaDBSource.getNodesOnFloor(floor.toString());
        Map<String, MapNode> nodeMap = new HashMap<>();
        for(MapNode n : nodes) {
            nodeMap.put(n.getId(), n);
        }
        return nodeMap;
    }

    // make format enum
    public void export() {

    }

    public TextDirections getDirections(Path path) {
        return null;
    }

    public TextDirections getDirections(MapNode front, MapNode end) {
        return null;
    }

    public Path getPath(MapNode start, MapNode end) {
        return null;
    }
}
