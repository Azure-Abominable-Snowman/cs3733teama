package com.teama.mapsubsystem;

import com.teama.Configuration;
import com.teama.mapsubsystem.data.*;
import com.teama.mapsubsystem.pathfinding.*;
import com.teama.mapsubsystem.pathfinding.AStar.AStar;
import com.teama.mapsubsystem.pathfinding.TextualDirection.TextDirections;

import java.util.*;

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
        // All of the files put into a set
        // Teams A through I and W for Wong's files
        Set<String> nList = new HashSet<>();
        Set<String> eList = new HashSet<>();
        for (char alphabet = 'A'; alphabet <= 'I'; alphabet++) {
            nList.add("/csvdata/nodes/Map"+alphabet+"nodes.csv");
            eList.add("/csvdata/edges/Map"+alphabet+"edges.csv");
        }
        nList.add("/csvdata/nodes/MapWnodes.csv");
        eList.add("/csvdata/edges/MapWedges.csv");

        csvSource = new CSVDatabaseSource(nList, eList, null, null); // Don't specify output files
        javaDBSource = new JavaDatabaseSource(Configuration.dbURL, Configuration.nodeTable, Configuration.edgeTable);

        pathGenerator = new PathGenerator(new AStar());

        // Initially populate the tables with the data from CSV (Not needed every time)
        javaDBSource.addAll(csvSource);
    }


    public static synchronized MapSubsystem getInstance() {
        return MapSubsystemGetter.instance;
    }

    private MapDataSource javaDBSource; // link to javaDB -- see MapDataSource interface to call functions that update DB
    private MapDataSource csvSource;


    private PathGenerator pathGenerator;

    @Deprecated
    public MapDataSource getMap() {
        return javaDBSource;
    }

    public void exportToCSV(String nodeFile, String edgeFile) {
        CSVDatabaseSource export = new CSVDatabaseSource(nodeFile, edgeFile);
        export.addAll(javaDBSource);
        export.close();
    }

    public PathGenerator getPathGenerator() {
        return pathGenerator;
    }

    public void setPathGeneratorStrategy(PathAlgorithm strategy) {
        pathGenerator = new PathGenerator(strategy);
    }


    public MapNode getNode(String id) {
        return javaDBSource.getNode(id);
    }

    public MapEdge getEdge(String id) {
        return javaDBSource.getEdge(id);
    }

    public void deleteEdge(String id) {
        javaDBSource.removeEdge(id);
    }

    public void deleteNode(String id) {
        javaDBSource.removeNode(id);
    }

    public Map<String, MapNode> getFloorNodes(Floor floor) {
        ArrayList<MapNode> nodes = javaDBSource.getNodesOnFloor(floor.toString());
        Map<String, MapNode> nodeMap = new HashMap<>();
        for(MapNode n : nodes) {
            nodeMap.put(n.getId(), n);
        }
        return nodeMap;
    }

    public Map<String, MapNode> getVisibleFloorNodes(Floor floor) {
        ArrayList<MapNode> nodes = javaDBSource.getNodesOnFloor(floor.toString());
        Map<String, MapNode> nodeMap = new HashMap<>();
        for(MapNode n : nodes) {
            if(!n.getNodeType().equals(NodeType.HALL)) {
                nodeMap.put(n.getId(), n);
            }
        }
        return nodeMap;
    }

    public Map<String, MapNode> getInvisibleFloorNodes(Floor floor) {
        return getFloorNodes(floor, NodeType.HALL);
    }

    public Map<String, MapNode> getFloorNodes(Floor floor, NodeType type) {
        ArrayList<MapNode> nodes = javaDBSource.getNodesOnFloor(floor.toString());
        Map<String, MapNode> nodeMap = new HashMap<>();
        for(MapNode n : nodes) {
            if(n.getNodeType().equals(type)) {
                nodeMap.put(n.getId(), n);
            }
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

    public MapNode getKioskNode() {
        return getNode("AINFO0020G"); // 1st floor info desk
    }
}
