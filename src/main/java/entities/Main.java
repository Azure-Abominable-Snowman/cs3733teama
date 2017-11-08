package entities;

import java.util.ArrayList;

public class Main {
    /*
     * Write a program that demonstrates reading from the CSV files and creating the JavaDB tables from them.
     * The read data from the tables will be turned into Java objects. Finally, modify values in the Java objects
     * update the JavaDB database and generate updated CSV files.
     */
    public static void main(String[] args) {
        // Create a JavaDB data source and populate with the values from the CSV files (CSV data source)
        MapDataSource dbSource = new JavaDatabaseSource("jdbc:derby://localhost:1527/testdb", "TEST_NODES", "TEST_EDGES");
        MapDataSource csvDataSource =  new CSVDatabaseSource("C:\\Users\\raysc\\Documents\\CS3733\\cs3733teama\\csvdata\\MapAnodes.csv",
                                                             "C:\\Users\\raysc\\Documents\\CS3733\\cs3733teama\\csvdata\\MapAedges.csv");
        // add all nodes to the database
        ArrayList<String> nodeIds = csvDataSource.getNodeIds();
        for(String id : nodeIds) {
            dbSource.addNode(csvDataSource.getNode(id));
        }
        // add all edges to the database
        for(String id : csvDataSource.getEdgeIds()) {
            dbSource.addEdge(csvDataSource.getEdge(id));
        }

        // at this point all of the data is in the database tables, now we are going to make some edits the data
        // and reupload it to the database
        // Also, every time an edit is made in the CSV data source it is reflected in the CSV file on the disk,
        // so there is no need to generate it specially

        // Edit it so that all nodes swap their short and long description
        // Reflect these changes in the CSVDataSource and JavaDBDataSource
        for(String id : nodeIds) {
            MapNode n = dbSource.getNode(id);
            String shortDes = n.getShortDescription();
            n.setShortDescription(n.getLongDescription());
            n.setLongDescription(shortDes);
            dbSource.addNode(n);
            csvDataSource.addNode(n);
        }

        // Edit it so that all edges swap their start and end points
        // Reflect these changes in the CSVDataSource and JavaDBDataSource
        for(String id : csvDataSource.getEdgeIds()) {
            MapEdge e = csvDataSource.getEdge(id);
            MapNode s = e.getStart();
            e.setStart(e.getEnd());
            e.setEnd(s);
            dbSource.addEdge(e);
            csvDataSource.addEdge(e);
        }
    }
}
