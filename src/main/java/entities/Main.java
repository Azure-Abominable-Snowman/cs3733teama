package entities;

import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {
        // Create a JavaDB data source and populate with the values from the CSV files (CSV data source)
        String dir = System.getProperty("user.dir");
        MapDataSource dbSource = new JavaDatabaseSource("jdbc:derby://localhost:1527/testdb;create=true", "TEST_NODES", "TEST_EDGES");
        MapDataSource csvDataSource =  new CSVDatabaseSource(dir + "\\csvdata\\MapAnodes.csv",
                                                             dir + "\\csvdata\\MapAedges.csv");
        // add all nodes to the database
        ArrayList<String> nodeIds = csvDataSource.getNodeIds();
        for(String id : nodeIds) {
            dbSource.addNode(csvDataSource.getNode(id));
        }
        // add all edges to the database
        for(String id : csvDataSource.getEdgeIds()) {
            dbSource.addEdge(csvDataSource.getEdge(id));
        }

        // At this point the database is completely loaded with edges and nodes

        // Connect to the staff info table


        /*for(String id : dbSource.getNodeIds()) {
            for(MapEdge e : dbSource.getNode(id).getEdges()) {
                System.out.print(e.toCSV() + " ");
                System.out.print(e.getWeight() + " ");
            }
            System.out.println("");
            for(MapEdge e : csvDataSource.getNode(id).getEdges()) {
                System.out.print(e.toCSV() + " ");
            }
            System.out.println("");
            System.out.println("");
        }*/



        dbSource.close();
        csvDataSource.close();
    }
}
