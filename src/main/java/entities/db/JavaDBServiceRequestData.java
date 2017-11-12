package entities.db;

import entities.Location;
import entities.servicerequests.PriorityLevel;
import entities.servicerequests.Request;
import entities.servicerequests.RequestType;

import java.sql.*;
import java.util.logging.Logger;

public class JavaDBServiceRequestData implements ServiceRequestDataSource {

    private final Logger log = Logger.getLogger(this.getClass().getPackage().getName());
    private String dbURL;
    private String requestTable;
    private Connection conn = null;
    private Statement stmt = null;

    public JavaDBServiceRequestData(String dbURL, String requestTable) {
        this.requestTable = requestTable;
        this.dbURL = dbURL;

        try {
            Class.forName("org.apache.derby.jdbc.ClientDriver").newInstance();
            // Get a connection
            conn = DriverManager.getConnection(dbURL);
        }
        catch (Exception except) {
            except.printStackTrace();
        }

        // Creates the requests table if it isn't there already
        try {
            stmt = conn.createStatement();
            stmt.execute(
                    " CREATE TABLE "+requestTable+" (" +
                            "REQUESTID VARCHAR(10) PRIMARY KEY NOT NULL," +
                            "XCOORD INT NOT NULL," +
                            "YCOORD INT NOT NULL," +
                            "LEVEL VARCHAR(20) NOT NULL," +
                            "BUILDING VARCHAR(20) NOT NULL," +
                            "REQTYPE VARCHAR(30) NOT NULL, " +
                            "PRIORITY VARCHAR(15) NOT NULL, " +
                            "NOTE VARCHAR(300) NOT NULL " +
                            ")"
            );
            stmt.close();
        }
        catch (SQLException sqlExcept) {
            log.info("Does the service request database already exist?");
        }
    }

    @Override
    public void createRequest(Request request) {
        try {
            stmt = conn.createStatement();
            stmt.execute(
                    "INSERT INTO "+requestTable+" VALUES("+request.toCSV()+")"
            );
            stmt.close();
        } catch(SQLException exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public boolean deleteRequest(Request request) {
        return deleteRequest(request.getId());
    }

    @Override
    public boolean deleteRequest(String id) {
        try {
            stmt = conn.createStatement();
            stmt.execute(
                    "DELETE FROM "+requestTable+" WHERE REQUESTID='"+id+"'"
            );
            stmt.close();
            return true;
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public Request getRequest(String id) {
        try {
            stmt = conn.createStatement();
            ResultSet set = stmt.executeQuery(
                    "SELECT * FROM "+requestTable+" WHERE REQUESTID='"+id+"'"
            );
            set.next();
            // Get info from table and return object based off of it
            Request r = new Request(id, new Location(set.getInt("XCOORD"), set.getInt("YCOORD"),
                    set.getString("LEVEL"), set.getString("BUILDING")),
                    RequestType.valueOf(set.getString("REQTYPE")), PriorityLevel.valueOf(set.getString("PRIORITY")),
                    set.getString("NOTE"));
            return r;
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void close() {
        try {
            conn.close();
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }
}
