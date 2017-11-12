package entities;

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
            Class.forName("org.apache.derby.jdbc.ClientDriver").newInstance();
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
            MapNode retrievedNode = new MapNode(result.getString("NODEID"),
                    new Location(result.getInt("XCOORD"), result.getInt("YCOORD"), result.getString("FLOOR"), result.getString("BUILDING")),
                    NodeType.valueOf(result.getString("NODETYPE")), result.getString("LONGNAME"), result.getString("SHORTNAME"), result.getString("TEAMASSIGNED"));
            result.close();
            stmt.close();
            return retrievedNode;
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return null;
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
                retrievedNode.addEdge(new MapEdge(result.getString("EDGEID"),
                        retrieveNode(result.getString("STARTNODE")), retrieveNode(result.getString("ENDNODE"))));
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
                    result.updateString("FLOOR", node.getCoordinate().getLevel());
                    result.updateString("BUILDING", node.getCoordinate().getBuilding());
                    result.updateString("NODETYPE", node.getNodeType().name());
                    result.updateString("LONGNAME", node.getLongDescription());
                    result.updateString("SHORTNAME", node.getShortDescription());
                    result.updateString("TEAMASSIGNED", node.getTeamAssignment());
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
                    result.updateString("EDGEID", edge.getId());
                    result.updateString("STARTNODE", edge.getStart().getId());
                    result.updateString("ENDNODE", edge.getEnd().getId());
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
        try {
            stmt = conn.createStatement();
            stmt.execute("DELETE FROM " + nodeTable + " WHERE NODEID='"+id+"'");
            stmt.close();
        }
        catch (SQLException sqlExcept) {
            sqlExcept.printStackTrace();
        }
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
            MapNode start = getNode(result.getString("STARTNODE"));
            MapNode end = getNode(result.getString("ENDNODE"));
            MapEdge retrievedEdge = new MapEdge(result.getString("EDGEID"), start, end);
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
