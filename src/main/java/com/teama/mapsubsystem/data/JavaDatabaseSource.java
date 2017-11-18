package com.teama.mapsubsystem.data;

import java.sql.*;
import java.util.ArrayList;
import java.util.Objects;
import java.util.logging.Logger;



public class JavaDatabaseSource implements MapDataSource {

    private final Logger log = Logger.getLogger(this.getClass().getPackage().getName());
    private String dbURL;
    private String nodeTable, edgeTable;
    private Connection conn = null;
    private Statement stmt = null;

    public JavaDatabaseSource(String dbURL, String nodeTable, String edgeTable) {
        this.dbURL = dbURL;
        this.nodeTable = nodeTable;
        this.edgeTable = edgeTable;

        try {
            Class.forName("org.apache.derby.jdbc.EmbeddedDriver").newInstance();
            // Get a connection
            conn = DriverManager.getConnection(dbURL);
        }
        catch (Exception except) {
            except.printStackTrace();
        }

        /* Create node if it doesn't already exist
        CREATE TABLE nodes (
            nodeID VARCHAR(10) PRIMARY KEY NOT NULL,
            xcoord INT NOT NULL,
            ycoord INT NOT NULL,
            floor VARCHAR(2) NOT NULL,
            building VARCHAR(10) NOT NULL,
            nodeType VARCHAR(4) NOT NULL,
            longName VARCHAR(130),
            shortName VARCHAR(15) NOT NULL,
            teamAssigned VARCHAR(6) NOT NULL
        )
         */
        try
        {
            stmt = conn.createStatement();
            stmt.execute(
                    " CREATE TABLE "+nodeTable+" (" +
                            "nodeID VARCHAR(10) PRIMARY KEY NOT NULL," +
                            "xcoord INT NOT NULL," +
                            "ycoord INT NOT NULL," +
                            "floor VARCHAR(2) NOT NULL," +
                            "building VARCHAR(10) NOT NULL," +
                            "nodeType VARCHAR(4) NOT NULL," +
                            "longName VARCHAR(130)," +
                            "shortName VARCHAR(130) NOT NULL," +
                            "teamAssigned VARCHAR(6) NOT NULL" +
                            ")"
            );
            stmt.close();
        }
        catch (SQLException sqlExcept)
        {
            log.info("Does the node database table already exist?");
        }

        /* Create edge table if it doesn't already exist
        CREATE TABLE table_name
        (
            edgeID VARCHAR(21) PRIMARY KEY NOT NULL,
            startNode VARCHAR(10) NOT NULL,
            endNode VARCHAR(10) NOT NULL
        )
         */
        try
        {
            stmt = conn.createStatement();
            stmt.execute(
                    " CREATE TABLE "+edgeTable+" (" +
                            "edgeID VARCHAR(21) PRIMARY KEY NOT NULL," +
                            "startNode VARCHAR(10) NOT NULL," +
                            "endNode VARCHAR(10) NOT NULL" +
                            ")"
            );
            stmt.close();
        }
        catch (SQLException sqlExcept)
        {
            log.info("Does the edge database table already exist?");
        }
    }

    private MapNode nodeFromSQL(ResultSet result) throws SQLException {
        return new MapNodeData(result.getString("NODEID"),
                new Location(result.getInt("XCOORD"), result.getInt("YCOORD"), Floor.valueOf(result.getString("FLOOR")), result.getString("BUILDING")),
                NodeType.valueOf(result.getString("NODETYPE")), result.getString("LONGNAME"),
                result.getString("SHORTNAME"), result.getString("TEAMASSIGNED"));
    }

    /**
     * Only gets a node, doesn't set the edges property
     * @param id
     * @return
     */
    private MapNode retrieveNode(String id) {
        try {
            stmt = conn.createStatement();
            ResultSet result = stmt.executeQuery("SELECT * FROM " + nodeTable + " WHERE NODEID = '" + id + "'");
            result.next();
            MapNode retrievedNode = nodeFromSQL(result);
            result.close();
            stmt.close();
            return retrievedNode;
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private MapEdge edgeFromSQL(ResultSet result) throws SQLException {
        return new MapEdgeData(result.getString("EDGEID"), result.getString("STARTNODE"), result.getString("ENDNODE"));
    }

    /**
     * Returns a node using an ID from the database
     * @param id
     * @return
     */
    @Override
    public MapNode getNode(String id) {
        try
        {
            stmt = conn.createStatement();
            ResultSet result = stmt.executeQuery("SELECT * FROM "+edgeTable+" WHERE STARTNODE='"+id+"' OR ENDNODE='"+id+"'");
            MapNode retrievedNode = retrieveNode(id);
            if(retrievedNode == null) {
                System.out.println("Requested node doesn't exist in the database");
                return null;
            }
            while(result.next()) {
                retrievedNode.addEdge(edgeFromSQL(result));
            }
            stmt.close();
            return retrievedNode;
        }
        catch (SQLException sqlExcept) {
            sqlExcept.printStackTrace();
        }
        return null;
    }

    /**
     * Inserts a new node into the database, if the node exists it is updated
     * @param node
     */
    @Override
    public void addNode(MapNode node) {
        try {
            stmt = conn.createStatement();
            stmt.execute("INSERT INTO " + nodeTable + " VALUES ("+node.toSQLVals()+")");
            stmt.close();
            log.info("Adding node " + node.getLongDescription());
        }
        catch (SQLException sqlExcept) {
            if(Objects.equals(sqlExcept.getSQLState(), "23505")) {
                try {
                    stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
                    ResultSet result = stmt.executeQuery("SELECT * FROM "+nodeTable+" WHERE NODEID = '"+node.getId()+"'");
                    result.absolute(1);
                    result.updateString("NODEID", node.getId());
                    result.updateInt("XCOORD", node.getCoordinate().getxCoord());
                    result.updateInt("YCOORD", node.getCoordinate().getyCoord());
                    result.updateString("FLOOR", node.getCoordinate().getLevel().toString());
                    result.updateString("BUILDING", node.getCoordinate().getBuilding());
                    result.updateString("NODETYPE", node.getNodeType().name());
                    result.updateString("LONGNAME", node.getLongDescription());
                    result.updateString("SHORTNAME", node.getShortDescription());
                    result.updateString("TEAMASSIGNED", node.getTeamAssignment());
                    result.updateRow();
                    result.close();
                    stmt.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            } else {
                sqlExcept.printStackTrace();
            }
        }
    }

    /**
     * Inserts a new node into the database, if the node exists it is updated
     * @param edge
     */
    @Override
    public void addEdge(MapEdge edge) {
        try {
            stmt = conn.createStatement();
            stmt.execute("INSERT INTO " + edgeTable + " VALUES ("+edge.toSQLVals()+")");
            stmt.close();
            log.info("Adding a new edge with ID " + edge.getId());
        }
        catch (SQLException sqlExcept) {
            if(Objects.equals(sqlExcept.getSQLState(), "23505")) {
                try {
                    stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
                    ResultSet result = stmt.executeQuery("SELECT * FROM "+edgeTable+" WHERE EDGEID = '"+edge.getId()+"'");
                    result.absolute(1);
                    result.updateString("EDGEID", edge.getId());
                    result.updateString("STARTNODE", edge.getStartID());
                    result.updateString("ENDNODE", edge.getEndID());
                    result.updateRow();
                    result.close();
                    stmt.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            } else {
                sqlExcept.printStackTrace();
            }
        }
    }

    /**
     * Removes a node from the database, removes all connected edges as well
     * @param id
     */
    @Override
    public void removeNode(String id) {


        try {
            stmt = conn.createStatement();
            stmt.execute("DELETE FROM " + nodeTable + " WHERE NODEID='"+id+"'");
            stmt.close();
        }
        catch (SQLException sqlExcept) {
            sqlExcept.printStackTrace();
        }
    }
    public void removeEdge(String id) {
        /*String[] ids = id.split("_");
        String start = ids[0];
        String end = ids[0];
        String otherID = end + "_" + start;*/
        try {
            stmt = conn.createStatement();

            stmt.execute("DELETE FROM " + edgeTable + " WHERE EDGEID='" + id + "'");
            //stmt.execute("DELETE FROM " + edgeTable + " WHERE EDGEID='" + otherID + "'");
            stmt.close();
            log.info("Deleted edge with ID " + id);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private ArrayList<String> getColFromDb(String col, String dbName) {
        ArrayList<String> ids = new ArrayList<>();
        try {
            stmt = conn.createStatement();
            ResultSet result = stmt.executeQuery("SELECT "+col+" FROM "+dbName);
            while(result.next()) {
                ids.add(result.getString(1));
            }
            result.close();
            stmt.close();
            return ids;
        }
        catch (SQLException sqlExcept) {
            sqlExcept.printStackTrace();
        }
        return null;
    }

    @Override
    public ArrayList<String> getNodeIds() {
        return getColFromDb("NODEID", nodeTable);
    }

    @Override
    public ArrayList<String> getEdgeIds() {
        return getColFromDb("EDGEID", edgeTable);
    }

    @Override
    public MapEdge getEdge(String id) {
        try {
            stmt = conn.createStatement();
            ResultSet result = stmt.executeQuery("SELECT * FROM "+edgeTable+" WHERE EDGEID = '"+id+"'");
            result.next();
            //MapNode start = getNode(result.getString("STARTNODE"));
            //MapNode end = getNode(result.getString("ENDNODE"));
            MapEdge retrievedEdge = new MapEdgeData(result.getString("EDGEID"), result.getString("STARTNODE"), result.getString("ENDNODE"));
            result.close();
            stmt.close();
            return retrievedEdge;
        }
        catch (SQLException sqlExcept) {
            sqlExcept.printStackTrace();
        }
        return null;
    }

    @Override
    public void close() {
        try {
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public ArrayList<MapNode> getNodesOnFloor(String floor) {
        ArrayList<MapNode> nodes = new ArrayList<>();
        try {
            stmt = conn.createStatement();
            ResultSet result = stmt.executeQuery("SELECT * FROM "+nodeTable+" WHERE FLOOR='"+floor+"'");

            while(result.next()) {
                nodes.add(nodeFromSQL(result));
            }
            result.close();
            // Get edges for all of the nodes retrieved
            for(MapNode n : nodes) {
                result = stmt.executeQuery("SELECT * FROM "+edgeTable+" WHERE STARTNODE='"+n.getId()+"' OR ENDNODE='"+n.getId()+"'");
                while (result.next()) {
                    n.addEdge(edgeFromSQL(result));
                }
                result.close();
            }
            stmt.close();
            return nodes;
        }
        catch (SQLException sqlExcept) {
            sqlExcept.printStackTrace();
        }
        return null;
    }

    @Override
    public ArrayList<MapEdge> getEdgesOnFloor(String floor) {
        ArrayList<MapEdge> edges = new ArrayList<>();
        try {
            stmt = conn.createStatement();
            ResultSet result = stmt.executeQuery("SELECT * FROM "+edgeTable+" WHERE FLOOR='"+floor+"'");
            while(result.next()) {
                edges.add(edgeFromSQL(result));
            }
            result.close();
            stmt.close();
            return edges;
        }
        catch (SQLException sqlExcept) {
            sqlExcept.printStackTrace();
        }
        return null;
    }
}