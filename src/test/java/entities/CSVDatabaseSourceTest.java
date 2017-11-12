package entities;

import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

/**
 * Created by aliss on 11/11/2017.
 */
public class CSVDatabaseSourceTest {
    @Test
    public void removeNode() throws Exception {
        String dir = System.getProperty("user.dir");
        MapDataSource dbSource = new JavaDatabaseSource("jdbc:derby://localhost:1527/testdb;create=true", "TEST_NODES", "TEST_EDGES");
        MapDataSource csvDataSource =  new CSVDatabaseSource(dir + "\\csvdata\\MapAnodes.csv",
                dir + "\\csvdata\\MapAedges.csv");
        ArrayList<String> nodeIds = csvDataSource.getNodeIds();
        for(String id : nodeIds) {
            dbSource.addNode(csvDataSource.getNode(id));
        }
        // add all edges to the database
        for(String id : csvDataSource.getEdgeIds()) {
            dbSource.addEdge(csvDataSource.getEdge(id));
        }

        csvDataSource.removeNode("AREST00103");

    }

}