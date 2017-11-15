package entities;

import entities.PathRelated.AStar;
import entities.PathRelated.PathGenerator;
import entities.db.CSVDatabaseSource;
import entities.db.JavaDatabaseSource;
import entities.db.MapDataSource;
import entities.drawing.DrawMap;

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
    private String nodefile = "csvdata/MapANodes.csv";
    private String edgefile = "csvdata/MapAedges.csv";

    private PathGenerator pathGenerator;
    //mapcoordinate

    // TODO: Make this update when the database is changed
    private Map<String, Map<String, MapNode>> nodesOnFloors = new HashMap<>();
    private Map<String, ArrayList<MapEdge>> edgesOnFloor = new HashMap<>();


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
        if (!nodesOnFloors.containsKey(floor)) {
            Map<String, MapNode> nodesOnFloor = new HashMap<>();
            for (String id : getMap().getNodeIds()) {
                MapNode n = getMap().getNode(id);
                if (n.getCoordinate().getLevel().equals(floor)) {
                    nodesOnFloor.put(n.getId(), n);
                }
            }
            nodesOnFloors.put(floor, nodesOnFloor);
        }
        return nodesOnFloors.get(floor);
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

/*
        // gives locations and names of nodes on given floor
        public HashMap<Location, String> getNodesonFloor(String floor) {
            HashMap<Location,String> nodeLocs = new HashMap<Location,String>();
            ArrayList<MapNode> nodes = mapObjects.getNodesOnFloor(floor);
            for (MapNode m: nodes) {
                nodeLocs.put(m.getCoordinate(), m.getLongDescription());
            }
            return nodeLocs;
        }
        // gives start, end of edges on given floor
        public HashMap<Location, Location> getEdgesonFloor(String floor) {
            HashMap<Location,Location> edgeLocs = new HashMap<Location,Location>();
            ArrayList<MapEdge> edges = mapObjects.getEdgesOnFloor(floor);
            for (MapEdge e: edges) {
                edgeLocs.put(e.getStart().getCoordinate(), e.getEnd().getCoordinate());
            }
            return edgeLocs;
        }

*/

}
