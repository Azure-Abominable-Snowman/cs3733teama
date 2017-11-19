package com.teama.mapsubsystem.data;

import org.junit.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

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

        // Add a node and see if it is there
    }

    @Test
    public void addNode() throws Exception {
    }

    @Test
    public void addEdge() throws Exception {
        // Try an unescaped string for the input values
        /*stmt = conn.createStatement();
        String unescaped = "ab'cde''''fffgggh";
        stmt.execute("INSERT INTO "+edgeTable+" VALUES('"+unescaped+"', '541', '986')");

        MapEdge retrieved = db.getEdge(unescaped);

        assertEquals("ab\'cde\'\'\'\'fffgggh", retrieved.getId()); // exists
        assertEquals("541", retrieved.getStartID());
        assertEquals("986", retrieved.getEndID());

        stmt.close();*/
    }

    @Test
    public void removeNode() throws Exception {
    }

    @Test
    public void removeEdge() throws Exception {
    }

    @Test
    public void getNodeIds() throws Exception {
    }

    @Test
    public void getEdgeIds() throws Exception {
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
    public void close() throws Exception {
    }

    @Test
    public void getNodesOnFloor() throws Exception {
    }

    @Test
    public void getEdgesOnFloor() throws Exception {
    }

}