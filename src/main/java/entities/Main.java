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
        dataSource.addNode(new MapNode("ACONF0010G", new Location(1671,2648,"G", "BTM"),
                NodeType.CONF, "Schlagler Lobby", "Schlagler Innovation Lobby","Team A", null));
        System.out.println(dataSource.getNode("ACONF0010G").toCSV());
    }
}
