package entities;

import entities.PathRelated.AStar;
import entities.PathRelated.PathGenerator;
import entities.db.CSVDatabaseSource;
import entities.db.JavaDatabaseSource;
import entities.db.MapDataSource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by aliss on 11/11/2017.
 */
public class HospitalMap {

    static entities.HospitalMap instance = null;
    private MapDataSource javaDBSource; // link to javaDB -- see MapDataSource interface to call functions that update DB
    private MapDataSource csvSource;
    // private MapDataSource mapObjects; // NOTE: see the MapDataSource interface on how access the MapNodes and MapEdges for map coordinate/pathfinding

    private int widthPixels = 5000;
    private int heightPixels = 3400;

    private String javaDBURL = "jdbc:derby://localhost:1527/testdb;create=true";
    private String nodefile = "/csvdata/MapAnodes.csv";
    private String edgefile = "/csvdata/MapAedges.csv";

    private PathGenerator pathGenerator;

    public MapDataSource getMap() {
        return javaDBSource;
    }

    public void exportToCSV() {
        csvSource.addAll(javaDBSource);
    }
    public MapNode createNode(Integer xCoord, Integer yCoord, String name, String curFloor) {
        if (curFloor.length() == 1) {
            String full = "0" + curFloor;
            curFloor = full;
        }
        String ID = "A"+"HALL" + "001" +curFloor;
        MapNode m = new MapNode(ID, new Location(xCoord.intValue(), yCoord.intValue(), curFloor, "BMT"), NodeType.HALL, name, "", "A", null);
        return m;
    }

    public Map<String, MapNode> getFloorNodes(String floor) {
        ArrayList<MapNode> nodes = javaDBSource.getNodesOnFloor(floor);
        Map<String, MapNode> nodeMap = new HashMap<>();
        for(MapNode n : nodes) {
            nodeMap.put(n.getId(), n);
        }
        return nodeMap;
    }
    /*
    public ArrayList<MapEdge> getFloorEdges(String floor) {
        if (!edgesOnFloor.containsKey(floor)) {
            ArrayList<MapEdge> edges = new ArrayList<MapEdge>();
            for (String id: getMap().getEdgeIds()) {
                MapEdge e = getMap().getEdge(id);
                if (e.getStart().getCoordinate().getLevel().equals(e.getEnd().getCoordinate().getLevel())) {
                    edges.add(e);
                }
            }
            edgesOnFloor.put(floor, edges);
        }
        System.out.println(edgesOnFloor.get(floor).size());
        return edgesOnFloor.get(floor);
    }
    */

    public PathGenerator getPathGenerator() {
        return pathGenerator;
    }

    private HospitalMap() {
        csvSource = new CSVDatabaseSource(nodefile, edgefile); // Reads CSV file
        javaDBSource = new JavaDatabaseSource(javaDBURL, "NODES", "EDGES");
        pathGenerator = new AStar();

        // Initially populate the tables with the data from CSV
        javaDBSource.addAll(csvSource);
    }


    public static synchronized entities.HospitalMap getInstance()

    {
        if (instance == null)
            instance = new entities.HospitalMap();
        return instance;
    }
}
