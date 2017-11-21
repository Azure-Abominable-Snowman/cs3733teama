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

    //private String nodefile = "/csvdata/MapAnodes.csv";
    //private String edgefile = "/csvdata/MapAedges.csv";

    private MapSubsystem() {
        // TODO: automatic concatenation
        // Get all csv files from the filesystem and load them all in.
        /*File eFile = new File("csvdata/edges");
        File nFile = new File("csvdata/nodes");
        File[] eList = eFile.listFiles();
        File[] nList = nFile.listFiles();
        if(eList.length != nList.length) {
            System.out.println("Something is wrong, different number of node and edge files!");
        }
        javaDBSource = new JavaDatabaseSource(Configuration.dbURL, Configuration.nodeTable, Configuration.edgeTable);
        for(int i = 0; i < eList.length; i++) {
            System.out.println(eList[i]+" "+nList[i]);
            csvSource = new CSVDatabaseSource("/csvdata/nodes/"+nList[i].getName(), "/csvdata/edges/"+eList[i].getName()); // Reads CSV file
            javaDBSource.addAll(csvSource);
        }*/

        // load in concatenated files
        //String nodefile = "/csvdata/Mapnodes.csv";
        //String edgefile = "/csvdata/Mapedges.csv";
        //csvSource = new CSVDatabaseSource(nodefile, edgefile); // Reads CSV file
        javaDBSource = new JavaDatabaseSource(Configuration.dbURL, Configuration.nodeTable, Configuration.edgeTable);

        pathGenerator = new AStar();

        // Initially populate the tables with the data from CSV (Not needed every time)
        //javaDBSource.addAll(csvSource);
    }


    public static synchronized MapSubsystem getInstance() {
        return MapSubsystemGetter.instance;
    }

    private MapDataSource javaDBSource; // link to javaDB -- see MapDataSource interface to call functions that update DB
    private MapDataSource csvSource;


    private PathAlgorithm pathGenerator;

    @Deprecated
    public MapDataSource getMap() {
        return javaDBSource;
    }

    @Deprecated
    public void exportToCSV(String nodeFile, String edgeFile) {
        CSVDatabaseSource export = new CSVDatabaseSource(nodeFile, edgeFile);
        export.addAll(javaDBSource);
        export.close();
    }

    @Deprecated
    public PathAlgorithm getPathGenerator() {
        return pathGenerator;
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
