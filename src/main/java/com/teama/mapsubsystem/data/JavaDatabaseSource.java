package com.teama.mapsubsystem.data;

import java.sql.*;
import java.util.ArrayList;
import java.util.Objects;
import java.util.logging.Logger;



public class JavaDatabaseSource implements MapDataSource {

    private final Logger log = Logger.getLogger(this.getClass().getPackage().getName());
    private String dbURL;
    private String nodeTable, edgeTable, disabledNodeDB;
    private Connection conn = null;
    private Statement stmt = null;
    private PreparedStatement addEdgeStmt, addNodeStmt, selectNodeStmt, selectNodeEdgesStmt,
            selectEdgeStmt, removeNodeStmt, removeEdgeStmt, updateEdgeStmt, updateNodeStmt,
            getByLongDescriptionStmt, getByShortDescriptionStmt;

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

        // Create all of the statement templates needed
        try {
            addEdgeStmt = conn.prepareStatement("INSERT INTO "+edgeTable+" VALUES(?, ?, ?)");
            this.addNodeStmt = conn.prepareStatement("INSERT INTO " +nodeTable+ " VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?)");
            this.updateEdgeStmt = conn.prepareStatement("UPDATE " + edgeTable +
                    " SET EDGEID = ?, STARTNODE = ?, ENDNODE = ? WHERE EDGEID = ?");
            this.updateNodeStmt = conn.prepareStatement("UPDATE "+ nodeTable +
                    " SET XCOORD = ?, YCOORD = ?, FLOOR = ?, BUILDING = ?, NODETYPE = ?, LONGNAME = ?, SHORTNAME = ?, TEAMASSIGNED = ? " +
                    "WHERE NODEID = ?");
            this.selectNodeStmt = conn.prepareStatement("SELECT * FROM "+nodeTable+" WHERE NODEID = ?");
            this.selectNodeEdgesStmt = conn.prepareStatement("SELECT * FROM "+edgeTable+" WHERE STARTNODE = ? OR ENDNODE = ?");
            this.selectEdgeStmt = conn.prepareStatement("SELECT * FROM "+edgeTable+" WHERE EDGEID = ?");
            this.removeNodeStmt = conn.prepareStatement("DELETE FROM " + nodeTable + " WHERE NODEID = ?");
            this.removeEdgeStmt = conn.prepareStatement("DELETE FROM " + edgeTable + " WHERE EDGEID = ?");
            this.getByLongDescriptionStmt = conn.prepareStatement("SELECT * FROM "+nodeTable+" WHERE LONGNAME = ?");
            this.getByShortDescriptionStmt = conn.prepareStatement("SELECT * FROM "+nodeTable+" WHERE SHORTNAME = ?");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private MapNode nodeFromSQL(ResultSet result) throws SQLException {
        return new MapNodeData(result.getString("NODEID"),
                new Location(result.getInt("XCOORD"), result.getInt("YCOORD"), Floor.getFloor(result.getString("FLOOR")), result.getString("BUILDING")),
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
            //stmt = conn.createStatement();
            selectNodeStmt.setString(1, id);
            ResultSet result = selectNodeStmt.executeQuery();
            if (!result.next()) {
                log.info("No such node");
                return null;
            }
            MapNode retrievedNode = nodeFromSQL(result);
            result.close();
            //stmt.close();
            return retrievedNode;
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private MapEdgeData edgeFromSQL(ResultSet result) throws SQLException {
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
            /*
            stmt = conn.createStatement();
            ResultSet result = stmt.executeQuery("SELECT * FROM "+edgeTable+" WHERE STARTNODE='"+id+"' OR ENDNODE='"+id+"'");
            */
            //stmt = conn.createStatement();
            //selectNodeStmt.setString(1, id);
            //ResultSet result = selectNodeStmt.executeQuery();
            //stmt.close();
            selectNodeEdgesStmt.setString(1, id);
            selectNodeEdgesStmt.setString(2, id);
            ResultSet result = selectNodeEdgesStmt.executeQuery();

            MapNode retrievedNode = retrieveNode(id);
            if(retrievedNode == null) {
                log.info("Error in getNode. Requested node doesn't exist in the database");
                return null;
            }

            //selectNodeStmt.setString(1, id);
            //ResultSet result2 = selectNodeStmt.executeQuery();

            while(result.next()) {
                retrievedNode.addEdge(edgeFromSQL(result));
            }
            //stmt.close();
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
            //stmt = conn.createStatement();
            addNodeStmt.setString(1, node.getId());
            addNodeStmt.setInt(2, node.getCoordinate().getxCoord());
            addNodeStmt.setInt(3, node.getCoordinate().getyCoord());
            addNodeStmt.setString(4, (node.getCoordinate().getLevel()).toString());
            addNodeStmt.setString(5,  (node.getCoordinate().getBuilding()));
            addNodeStmt.setString(6, node.getNodeType().name());
            addNodeStmt.setString(7, node.getLongDescription());
            addNodeStmt.setString(8, node.getShortDescription());
            addNodeStmt.setString(9, node.getTeamAssignment());
            addNodeStmt.executeUpdate();

//            this.addNodeStmt = conn.prepareStatement("INSERT INTO " +nodeTable+ " VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?)");

            log.info("Adding node " + node.getLongDescription());
        }
        catch (SQLException sqlExcept) {
            if(Objects.equals(sqlExcept.getSQLState(), "23505")) {
                try {
                    //stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
                    //ResultSet result = stmt.executeQuery("SELECT * FROM "+nodeTable+" WHERE NODEID = '"+node.getId()+"'");
                    //XCOORD = ?, YCOORD = ?, FLOOR = ?, BUILDING = ?, NODETYPE = ?, LONGNAME = ?, SHORTNAME = ?, TEAMASSIGNED = ? " +
                    //"WHERE NODEID = ?"
                    updateNodeStmt.setInt(1, node.getCoordinate().getxCoord());
                    updateNodeStmt.setInt(2, node.getCoordinate().getyCoord());
                    updateNodeStmt.setString(3, (node.getCoordinate().getLevel()).toString());
                    updateNodeStmt.setString(4, (node.getCoordinate().getBuilding()));
                    updateNodeStmt.setString(5, (node.getNodeType().name()));
                    updateNodeStmt.setString(6, node.getLongDescription());
                    updateNodeStmt.setString(7, node.getShortDescription());
                    updateNodeStmt.setString(8, node.getTeamAssignment());
                    updateNodeStmt.setString(9, node.getId());
                    updateNodeStmt.executeUpdate();
                    log.info("Updated the given node.");

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
            //stmt = conn.createStatement();
            addEdgeStmt.setString(1, edge.getId());
            addEdgeStmt.setString(2, edge.getStartID());
            addEdgeStmt.setString(3, edge.getEndID());
            addEdgeStmt.executeUpdate();
            //stmt.close();
            log.info("Adding a new edge with ID " + edge.getId());
        }
        catch (SQLException sqlExcept) { // happens when it needs to be updated
            if(Objects.equals(sqlExcept.getSQLState(), "23505")) {
                try {

                    String updatedEdgeID = edge.getStartID() + "_" + edge.getEndID(); //update Edge ID to reflect changes
                    log.info("Edge " + edge.getId() + " was updated to " + updatedEdgeID);

                    //TODO: get preparedstatement working, for some reason isn't working
                    /*updateEdgeStmt.setString(1, updatedEdgeID);
                    updateEdgeStmt.setString(2, edge.getStartID());
                    updateEdgeStmt.setString(3, edge.getEndID());
                    updateEdgeStmt.setString(4, edge.getId()); //look up by old edge ID
                    updateEdgeStmt.executeUpdate();*/

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

            // Delete all of its connecting edges
            MapNode n = getNode(id);
            for(MapEdge e : n.getEdges()) {
                removeEdge(e.getId());
            }

            //stmt = conn.createStatement();
            removeNodeStmt.setString(1, id);
            removeNodeStmt.executeUpdate();
            log.info("Goodbye node.");
            //stmt.execute("DELETE FROM " + nodeTable + " WHERE NODEID='"+id+"'");
            //stmt.close();
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
            //stmt = conn.createStatement();
            removeEdgeStmt.setString(1, id);
            removeEdgeStmt.executeUpdate();
            //stmt.execute("DELETE FROM " + edgeTable + " WHERE EDGEID='" + id + "'");
            //stmt.execute("DELETE FROM " + edgeTable + " WHERE EDGEID='" + otherID + "'");
            //stmt.close();
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
            //stmt = conn.createStatement();
            //ResultSet result = stmt.executeQuery("SELECT * FROM "+edgeTable+" WHERE EDGEID = '"+id+"'");
            selectEdgeStmt.setString(1, id);
            ResultSet result = selectEdgeStmt.executeQuery();
            if(!result.next()) {
                return null;
            }
            MapEdge retrievedEdge = new MapEdgeData(result.getString("EDGEID"), result.getString("STARTNODE"), result.getString("ENDNODE"));
            result.close();
            //stmt.close();
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

    /*@Override
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
    }*/

    @Override
    public MapNode getNode(String description, boolean longDescription) {
        try
        {
            ResultSet result;
            if(longDescription) {
                getByLongDescriptionStmt.setString(1, description);
                result = getByLongDescriptionStmt.executeQuery();
            } else {
                getByShortDescriptionStmt.setString(1, description);
                result = getByShortDescriptionStmt.executeQuery();
            }
            if(!result.next()) {
                log.info("Not found in the database");
                return null;
            }
            return getNode(result.getString("NODEID"));
        }
        catch (SQLException sqlExcept) {
            sqlExcept.printStackTrace();
        }
        return null;
    }
}