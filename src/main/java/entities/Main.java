package entities;

public class Main {
    /*
     * Write a program that demonstrates reading from the CSV files and creating the JavaDB tables from them.
     * The read data from the tables will be turned into Java objects. Finally, modify values in the Java objects
     * update the JavaDB database and generate updated CSV files.
     */
    public static void main(String[] args) {
        // Create a JavaDB source and a CSV source
        MapDataSource dataSource = new JavaDatabaseSource("jdbc:derby://localhost:1527/testdb", "TEST_NODES", "TEST_EDGES");
        MapNode testNode = new MapNode("WELEV00H01", new Location(2735,2648,"G", "BTM"),
                NodeType.CONF, "Schlagler Lobby", "Schlagler Innovation Lobby","Team A", null);
        dataSource.addNode(testNode);
        // Test CSV
        System.out.println(dataSource.getNode("WELEV00H01").toCSV());
        MapDataSource csv = new CSVDatabaseSource("C:\\Users\\raysc\\Documents\\CS3733\\cs3733teama\\csvdata\\MapWnodes.csv",
                                                  "C:\\Users\\raysc\\Documents\\CS3733\\cs3733teama\\csvdata\\MapWedges.csv");
        csv.addNode(testNode);
        System.out.println("T: "+csv.getNode("WELEV00H01").toCSV());
    }
}
