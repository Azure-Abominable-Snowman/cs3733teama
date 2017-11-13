package entities;

import entities.db.CSVDatabaseSource;
import entities.db.JavaDatabaseSource;
import entities.db.MapDataSource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by aliss on 11/11/2017.
 */
public class HospitalMap {
        static entities.HospitalMap instance = null;
        private MapDataSource rawData; // link to javaDB -- see MapDataSource interface to call functions that update DB
        private MapDataSource mapObjects; // NOTE: see the MapDataSource interface on how access the MapNodes and MapEdges for map coordinate/pathfinding
        private int widthPixels = 5000;
        private int heightPixels = 3400;
        //pathgenerator
        //mapcoordinate

    // TODO: Make this update when the database is changed
    private Map<String, Map<String, MapNode>> nodesOnFloors = new HashMap<>();

    public MapDataSource getMap() {
        return rawData;
    }

    public Map<String, MapNode> getFloorNodes(String floor) {
        if(!nodesOnFloors.containsKey(floor)) {
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

    private HospitalMap(String nodeFilename, String edgeFilename) {
            //mapObjects = new CSVDatabaseSource(nodeFilename, edgeFilename); //reads CSV file, creates MapNodes and MapEdges
            rawData = new JavaDatabaseSource("jdbc:derby://localhost:1527/testdb;create=true", "TEST_NODES", "TEST_EDGES");
            /*for (String id : mapObjects.getNodeIds()) {
                rawData.addNode(mapObjects.getNode(id));
            }
            for (String id : mapObjects.getEdgeIds()) {
                rawData.addEdge(mapObjects.getEdge(id));
            }*/
        }


        public static synchronized entities.HospitalMap getInstance(String nodeFilename, String edgeFilename)
        {
            if (instance==null)
                instance = new entities.HospitalMap(nodeFilename, edgeFilename);
            return instance;
        }


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



}
