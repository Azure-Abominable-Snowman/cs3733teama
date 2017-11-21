package com.teama.mapsubsystem.data;

import org.junit.Test;

import java.sql.*;

import static junit.framework.TestCase.assertEquals;

public class JavaDatabaseSourceTest {

    private String dbURL = "jdbc:derby:unittestdb;create=true";
    private String nodeTable, edgeTable;
    private Connection conn = null;
    private Statement stmt = null;
    // Make the database object to test
    JavaDatabaseSource db;

    public JavaDatabaseSourceTest() {
        // this object connects directly to the db

        nodeTable = "TEST_NODETABLE";
        edgeTable = "TEST_EDGETABLE";

        try {
            Class.forName("org.apache.derby.jdbc.EmbeddedDriver").newInstance();
            // Get a connection
            conn = DriverManager.getConnection(dbURL);
        }
        catch (Exception except) {
            except.printStackTrace();
        }

        // Delete table from last time
        try {
            stmt = conn.createStatement();
            stmt.execute("DROP TABLE TEST_NODETABLE");
            stmt.execute("DROP TABLE TEST_EDGETABLE");
            stmt.close();
        } catch(SQLException e) {
            System.out.println("No previous table");
        }

        db = new JavaDatabaseSource(dbURL, nodeTable, edgeTable);
    }

    @Test
    public void getNode() throws Exception {
        assertEquals(null, db.getNode("123456")); // doesn't exist
        PreparedStatement pstmt = null;
        pstmt = conn.prepareStatement("INSERT INTO " + nodeTable + " VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?)");
        String randID = "WNodeTeamA";
        String floor = "G";
        String building = "BTM";
        String nodeType = "ELEV";
        String longName = "Path";
        String shortName = "AStar";
        String teamAssigned = "TeamA";
        MapNode newNode = new MapNodeData(randID, new Location(35, 54, Floor.getFloor(floor), building), NodeType.ELEV, longName, shortName, teamAssigned);
        MapEdge newEdge = new MapEdgeData(randID + "_hello", randID, "hello");
        newNode.addEdge(newEdge);
        db.addEdge(newEdge);
        pstmt.setString(1, randID);
        pstmt.setInt(2, 35);
        pstmt.setInt(3, 54);
        pstmt.setString(4, floor);
        pstmt.setString(5, building);
        pstmt.setString(6, nodeType);
        pstmt.setString(7, longName);
        pstmt.setString(8, shortName);
        pstmt.setString(9, teamAssigned);
        pstmt.executeUpdate();
        MapNode found = db.getNode(randID);
        assertEquals("WNodeTeamA", found.getId());
        assertEquals(null, db.getNode("123456")); // doesn't exist
        // Add a node and see if it is there
    }

    @Test
    public void addNode() throws Exception {
        String randID = "WNodeTeamA";
        String floor = "G";
        String building = "BTM";
        String nodeType = "ELEV";
        String longName = "Path";
        String shortName = "AStar";
        String teamAssigned = "TeamA";
        MapNode newNode = new MapNodeData(randID, new Location(35, 54, Floor.getFloor(floor), building), NodeType.ELEV, longName, shortName, teamAssigned);
        MapEdge newEdge = new MapEdgeData(randID + "_hello", randID, "hello");
        newNode.addEdge(newEdge);
        assertEquals(null,db.getNode(randID));
        db.addNode(newNode);
        db.addEdge(newEdge);
        assertEquals("Path", db.getNode(randID).getLongDescription());
        newNode.setLongDescription("NotPath");
        db.addNode(newNode);
        assertEquals("NotPath", db.getNode(randID).getLongDescription());


    }
    @Test
    public void getEdge() throws Exception {
        assertEquals(null, db.getEdge("123456")); // doesn't exist

        // Add an edge and see if it is there
        stmt = conn.createStatement();
        stmt.execute("INSERT INTO "+edgeTable+" VALUES('123456', '123', '456')");

        MapEdge retrieved = db.getEdge("123456");

        assertEquals("123456", retrieved.getId()); // exists
        assertEquals("123", retrieved.getStartID());
        assertEquals("456", retrieved.getEndID());

        stmt.close();
    }

    @Test
    public void addEdge() throws Exception {
        // Try an unescaped string for the input values
        //stmt = conn.createStatement();
        assertEquals(null, db.getEdge("WWONG_WTONG"));
        MapEdge newEdge = new MapEdgeData("WWONG_WTONG", "WWONG", "WTONG");
        db.addEdge(newEdge);
        assertEquals("WWONG_WTONG", db.getEdge("WWONG_WTONG").getId());
        assertEquals("WWONG", db.getEdge("WWONG_WTONG").getStartID());
        assertEquals("WTONG", db.getEdge("WWONG_WTONG").getEndID());

        String randID = "WNodeTeamA";
        String floor = "G";
        String building = "BTM";
        String nodeType = "ELEV";
        String longName = "Path";
        String shortName = "AStar";
        String teamAssigned = "TeamA";
        MapNode newNode = new MapNodeData(randID, new Location(35, 54, Floor.getFloor(floor), building), NodeType.ELEV, longName, shortName, teamAssigned);
        newEdge.setStart(newNode); //change the start node of the edge
        System.out.println(newEdge.getId()+" "+ newEdge.getStartID()+" "+newEdge.getEndID());
        db.addEdge(newEdge); //looks up by old ID then updates stored edge ID according to the changes
        //assertEquals(randID, db.getEdge(randID+"_WTONG").getStartID()); TODO: get edge name changing working
        //assertEquals(null, db.getEdge("WWONG_WTONG"));

    }

    @Test
    public void removeNode() throws Exception {
        String randID = "WNodeTeamA";
        String floor = "G";
        String building = "BTM";
        String nodeType = "ELEV";
        String longName = "Path";
        String shortName = "AStar";
        String teamAssigned = "TeamA";
        MapNode newNode = new MapNodeData(randID, new Location(35, 54, Floor.getFloor(floor), building), NodeType.ELEV, longName, shortName, teamAssigned);
        db.addNode(newNode);
        db.removeNode(randID);
        assertEquals(null, db.getNode(randID));
    }

    @Test
    public void removeEdge() throws Exception {
        MapEdge newEdge = new MapEdgeData("WWONG_WTONG", "WWONG", "WTONG");
        db.addEdge(newEdge);
        assertEquals("WTONG", db.getEdge("WWONG_WTONG").getEndID());
        db.removeEdge("WWONG_WTONG");
        assertEquals(null, db.getEdge("WWONG_WTONG"));
    }
}