package entities.db;

import entities.Location;
import entities.servicerequests.PriorityLevel;
import entities.servicerequests.Request;
import entities.servicerequests.RequestType;

import java.sql.*;
import java.util.ArrayList;
import java.util.logging.Logger;

public class JavaDBServiceRequestData implements ServiceRequestDataSource {

    private final Logger log = Logger.getLogger(this.getClass().getPackage().getName());
    private String dbURL;
    private String requestTable;
    private Connection conn = null;
    private Statement stmt = null;
    private int curId;

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
                            "NOTE VARCHAR(300) NOT NULL, " +
                            "FULFILLED BOOLEAN NOT NULL" +
                            ")"
            );
            stmt.close();
        }
        catch (SQLException sqlExcept) {
            log.info("Does the service request database already exist?");
        }

        // Get the latest ID number from the database
        curId = getLatestId();
    }

    @Override
    public void submitRequest(Request request) {
        try {
            stmt = conn.createStatement();
            stmt.execute(
                    "INSERT INTO "+requestTable+" VALUES("+request.toSQLValues()+")"
            );
            stmt.close();
        } catch(SQLException exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public boolean cancelRequest(Request request) {
        return cancelRequest(request.getId());
    }

    @Override
    public boolean cancelRequest(String id) {
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
                    set.getString("NOTE"), set.getBoolean("FULFILLED")
            );
            return r;
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public ArrayList<Request> getRequest(){
        ArrayList<Request> requestList = new ArrayList<>();
        try {
            stmt = conn.createStatement();
            ResultSet set = stmt.executeQuery(
                    "SELECT * FROM "+requestTable
            );
            while(set.next()) {

                Request r = new Request(set.getString("REQUESTID"),
                        new Location(set.getInt("XCOORD"), set.getInt("YCOORD"),
                                set.getString("LEVEL"), set.getString("BUILDING")),
                        RequestType.valueOf(set.getString("REQTYPE")),
                        PriorityLevel.valueOf(set.getString("PRIORITY")),
                        set.getString("NOTE"),
                        set.getBoolean("FULFILLED")
                );
                requestList.add(r);
            }

        } catch(SQLException e){
            e.printStackTrace();
        }
        return requestList;
    }

    @Override
    public boolean fulfillRequest(String id) {
        try {
            stmt = conn.createStatement();
            ResultSet set = stmt.executeQuery("SELECT FULFILLED FROM "+requestTable+" WHERE REQUESTID='"+id+"'");
            // If it doesn't exist or it has already been fulfilled, return false.
            if(!set.next() || set.getBoolean("FULFILLED")) {
                return false;
            }
            set.close();
            stmt.execute("UPDATE "+requestTable+" SET FULFILLED=TRUE WHERE REQUESTID='"+id+"'");
            stmt.close();
            return true;
        } catch(SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public void close() {
        try {
            conn.close();
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    private int getLatestId() {
        try {
            stmt = conn.createStatement();
            ResultSet set = stmt.executeQuery(
                    "SELECT MAX(CAST(REQUESTID AS INT)) FROM "+requestTable
            );
            set.next();
            return set.getInt(1);
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return 1;
    }

    /*public int getNextId() {
        curId++;
        return curId;
    }*/

    public int getNextId() {
        try{
            stmt = conn.createStatement();
            ResultSet set = stmt.executeQuery(
                    "SELECT MAX(CAST(REQUESTID AS INT)) FROM "+requestTable
            );
            set.next();
           // int i = Integer.parseInt(set.getString(1)) + 1;
            int i = set.getInt(1)+1;
            System.out.println(i);
            return i;
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return 1;
    }
}
