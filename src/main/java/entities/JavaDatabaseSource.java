package entities;

import java.sql.*;
import java.util.ArrayList;
import java.util.Objects;


public class JavaDatabaseSource implements MapDataSource {

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
            System.out.println("Does the node database table already exist?");
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
            System.out.println("Does the edge database table already exist?");
        }
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
            ResultSet result = stmt.executeQuery("SELECT * FROM "+nodeTable+" WHERE NODEID = '"+id+"'");
            ResultSetMetaData rsmd = result.getMetaData();
            result.next();
            MapNode retrievedNode = new MapNode(result.getString(1),
                    new Location(result.getInt(2), result.getInt(3), result.getString(4), result.getString(5)),
                    NodeType.valueOf(result.getString(6)), result.getString(7), result.getString(8), result.getString(9), null);
            result.close();
            stmt.close();
            return retrievedNode;
        }
        catch (SQLException sqlExcept) {
            sqlExcept.printStackTrace();
        }
        return null;
    }

    @Override
    public ArrayList<MapEdge> getAdjacentEdges(String id) {
        return getAdjacentEdges(getNode(id));
    }

    @Override
    public ArrayList<MapEdge> getAdjacentEdges(MapNode node) {
        return node.getEdges();
    }

    @Override
    public ArrayList<MapNode> getAdjacentNodes(String id) {
        return getAdjacentNodes(getNode(id));
    }

    @Override
    public ArrayList<MapNode> getAdjacentNodes(MapNode node) {
        ArrayList<MapNode> nodes = new ArrayList<>();
        for(MapEdge n : getAdjacentEdges(node)) {
            nodes.add(n.getEnd());
        }
        return nodes;
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
        }
        catch (SQLException sqlExcept) {
            if(Objects.equals(sqlExcept.getSQLState(), "23505")) {
                try {
                    stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
                    ResultSet result = stmt.executeQuery("SELECT * FROM "+nodeTable+" WHERE NODEID = '"+node.getId()+"'");
                    result.absolute(1);
                    result.updateString(1, node.getId());
                    result.updateInt(2, node.getCoordinate().getxCoord());
                    result.updateInt(3, node.getCoordinate().getyCoord());
                    result.updateString(4, node.getCoordinate().getLevel());
                    result.updateString(5, node.getCoordinate().getBuilding());
                    result.updateString(6, node.getNodeType().name());
                    result.updateString(7, node.getLongDescription());
                    result.updateString(8, node.getShortDescription());
                    result.updateString(9, node.getTeamAssignment());
                    result.updateRow();
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
        }
        catch (SQLException sqlExcept) {
            if(Objects.equals(sqlExcept.getSQLState(), "23505")) {
                try {
                    stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
                    ResultSet result = stmt.executeQuery("SELECT * FROM "+edgeTable+" WHERE EDGEID = '"+edge.getId()+"'");
                    result.absolute(1);
                    result.updateString(1, edge.getId());
                    result.updateString(2, edge.getStart().getId());
                    result.updateString(3, edge.getEnd().getId());
                    result.updateRow();
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
        // TODO: Implement
    }

    private ArrayList<String> getIdsFromDB(String idCol, String dbName) {
        ArrayList<String> ids = new ArrayList<>();
        try
        {
            stmt = conn.createStatement();
            ResultSet result = stmt.executeQuery("SELECT "+idCol+" FROM "+dbName);
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
        return getIdsFromDB("NODEID", nodeTable);
    }

    @Override
    public ArrayList<String> getEdgeIds() {
        return getIdsFromDB("EDGEID", edgeTable);
    }

    @Override
    public MapEdge getEdge(String id) {
        try {
            stmt = conn.createStatement();
            ResultSet result = stmt.executeQuery("SELECT * FROM "+edgeTable+" WHERE EDGEID = '"+id+"'");
            result.next();
            MapNode start = getNode(result.getString(2));
            MapNode end = getNode(result.getString(3));
            MapEdge retrievedEdge = new MapEdge(result.getString(1), start, end);
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
}
