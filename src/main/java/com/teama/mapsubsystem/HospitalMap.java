package com.teama.mapsubsystem;

import com.teama.controllers.SceneEngine;
import com.teama.mapsubsystem.data.MapNode;
import com.teama.mapsubsystem.pathfinding.AStar;
import com.teama.mapsubsystem.pathfinding.PathGenerator;
import com.teama.mapsubsystem.data.CSVDatabaseSource;
import com.teama.mapsubsystem.data.JavaDatabaseSource;
import com.teama.mapsubsystem.data.MapDataSource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by aliss on 11/11/2017.
 */
public class HospitalMap {

    static HospitalMap instance = null;
    private MapDataSource javaDBSource; // link to javaDB -- see MapDataSource interface to call functions that update DB
    private MapDataSource csvSource;

    private int widthPixels = 5000;
    private int heightPixels = 3400;

    private String nodefile = "/csvdata/MapAnodes.csv";
    private String edgefile = "/csvdata/MapAedges.csv";

    private PathGenerator pathGenerator;

    public MapDataSource getMap() {
        return javaDBSource;
    }

    public void exportToCSV(String nodeFile, String edgeFile) {
        //csvSource.addAll(javaDBSource);
        CSVDatabaseSource export = new CSVDatabaseSource(nodeFile, edgeFile);
        export.addAll(javaDBSource);
        export.close();
    }

    public Map<String, MapNode> getFloorNodes(String floor) {
        ArrayList<MapNode> nodes = javaDBSource.getNodesOnFloor(floor);
        Map<String, MapNode> nodeMap = new HashMap<>();
        for(MapNode n : nodes) {
            nodeMap.put(n.getId(), n);
        }
        return nodeMap;
    }

    public PathGenerator getPathGenerator() {
        return pathGenerator;
    }

    private HospitalMap() {
        csvSource = new CSVDatabaseSource(nodefile, edgefile); // Reads CSV file
        javaDBSource = new JavaDatabaseSource(SceneEngine.getURL(), "NODES", "EDGES");
        pathGenerator = new AStar();

        // Initially populate the tables with the data from CSV
        javaDBSource.addAll(csvSource);
    }


    public static synchronized HospitalMap getInstance()

    {
        if (instance == null)
            instance = new HospitalMap();
        return instance;
    }
}
