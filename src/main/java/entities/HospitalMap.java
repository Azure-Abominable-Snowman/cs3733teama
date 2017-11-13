package entities;

import entities.db.CSVDatabaseSource;
import entities.db.JavaDatabaseSource;
import entities.db.MapDataSource;

/**
 * Created by aliss on 11/11/2017.
 */
public class HospitalMap {
        static entities.HospitalMap instance = null;
        private MapDataSource rawData; // link to javaDB -- see MapDataSource interface to call functions that update DB
        private MapDataSource mapObjects; // NOTE: see the MapDataSource interface on how access the MapNodes and MapEdges for map coordinate/pathfinding
        //pathgenerator
        //mapcoordinate

        private HospitalMap(String nodeFilename, String edgeFilename) {
            mapObjects = new CSVDatabaseSource(nodeFilename, edgeFilename); //reads CSV file, creates MapNodes and MapEdges
            rawData = new JavaDatabaseSource("jdbc:derby://localhost:1527/testdb;create=true", "A_NODES", "A_EDGES");
            for (String id : mapObjects.getNodeIds()) {
                rawData.addNode(mapObjects.getNode(id));
            }
            for (String id : mapObjects.getEdgeIds()) {
                rawData.addEdge(mapObjects.getEdge(id));
            }
        }
        public static synchronized entities.HospitalMap getInstance(String nodeFilename, String edgeFilename)
        {
            if (instance==null)
                instance = new entities.HospitalMap(nodeFilename, edgeFilename);
            return instance;
        }

}
