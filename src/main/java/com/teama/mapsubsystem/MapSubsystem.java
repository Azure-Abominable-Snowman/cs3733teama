package com.teama.mapsubsystem;

import com.teama.controllers.SceneEngine;
import com.teama.mapsubsystem.data.MapNodeData;
import com.teama.mapsubsystem.pathfinding.AStar;
import com.teama.mapsubsystem.pathfinding.PathAlgorithm;
import com.teama.mapsubsystem.data.CSVDatabaseSource;
import com.teama.mapsubsystem.data.JavaDatabaseSource;
import com.teama.mapsubsystem.data.MapDataSource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by aliss on 11/11/2017.
 *
 * Facade for all low level map details
 */
public class MapSubsystem {

    static MapSubsystem instance = null;
    private MapDataSource javaDBSource; // link to javaDB -- see MapDataSource interface to call functions that update DB
    private MapDataSource csvSource;

    private String nodefile = "/csvdata/MapAnodes.csv";
    private String edgefile = "/csvdata/MapAedges.csv";

    private PathAlgorithm pathGenerator;

    public MapDataSource getMap() {
        return javaDBSource;
    }

    public void exportToCSV(String nodeFile, String edgeFile) {
        //csvSource.addAll(javaDBSource);
        CSVDatabaseSource export = new CSVDatabaseSource(nodeFile, edgeFile);
        export.addAll(javaDBSource);
        export.close();
    }

    public Map<String, MapNodeData> getFloorNodes(String floor) {
        ArrayList<MapNodeData> nodes = javaDBSource.getNodesOnFloor(floor);
        Map<String, MapNodeData> nodeMap = new HashMap<>();
        for(MapNodeData n : nodes) {
            nodeMap.put(n.getId(), n);
        }
        return nodeMap;
    }

    public PathAlgorithm getPathGenerator() {
        return pathGenerator;
    }

    private MapSubsystem() {
        csvSource = new CSVDatabaseSource(nodefile, edgefile); // Reads CSV file
        javaDBSource = new JavaDatabaseSource(SceneEngine.getURL(), "NODES", "EDGES");
        pathGenerator = new AStar();

        // Initially populate the tables with the data from CSV
        javaDBSource.addAll(csvSource);
    }


    public static synchronized MapSubsystem getInstance()

    {
        if (instance == null)
            instance = new MapSubsystem();
        return instance;
    }
}
