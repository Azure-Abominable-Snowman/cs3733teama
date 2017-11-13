package entities;

import entities.PathRelated.AStar;
import entities.PathRelated.PathGenerator;
import entities.db.JavaDatabaseSource;
import entities.db.MapDataSource;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by aliss on 11/11/2017.
 */
public class HospitalMap {
        static entities.HospitalMap instance = null;
        private MapDataSource rawData; // link to javaDB -- see MapDataSource interface to call functions that update DB
        private MapDataSource mapObjects; // NOTE: see the MapDataSource interface on how access the MapNodes and MapEdges for map coordinate/pathfinding
        private PathGenerator pathGenerator;
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

    public PathGenerator getPathGenerator() {
        return pathGenerator;
    }

    private HospitalMap() {
            //mapObjects = new CSVDatabaseSource(nodeFilename, edgeFilename); //reads CSV file, creates MapNodes and MapEdges
            rawData = new JavaDatabaseSource("jdbc:derby://localhost:1527/testdb;create=true", "TEST_NODES", "TEST_EDGES");
            pathGenerator = new AStar();
            /*for (String id : mapObjects.getNodeIds()) {
                rawData.addNode(mapObjects.getNode(id));
            }
            for (String id : mapObjects.getEdgeIds()) {
                rawData.addEdge(mapObjects.getEdge(id));
            }*/
        }

        public static synchronized entities.HospitalMap getInstance()
        {
            if (instance==null)
                instance = new entities.HospitalMap();
            return instance;
        }
}
