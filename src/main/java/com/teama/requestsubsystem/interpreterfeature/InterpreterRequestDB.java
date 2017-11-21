package com.teama.requestsubsystem.interpreterfeature;

import com.teama.mapsubsystem.data.Floor;
import com.teama.mapsubsystem.data.Location;
import com.teama.requestsubsystem.PriorityLevel;
import com.teama.requestsubsystem.Request;
import com.teama.requestsubsystem.RequestType;
import com.teama.requestsubsystem.data.ServiceRequestDataSource;

import java.sql.*;
import java.util.ArrayList;
import java.util.logging.Logger;

public class InterpreterRequestDB {

    private final Logger log = Logger.getLogger(this.getClass().getPackage().getName());
    private String dbURL;
    private String tableName;
    private Connection conn = null;
    private Statement stmt = null;
    private PreparedStatement addRequest, getRequest, deleteRequest, updateRequest, getRequestID;

    public InterpreterRequestDB(String dbURL) {
        this.tableName = "INTERPRETER_REQUESTS";
        this.dbURL = dbURL;

        try {
            Class.forName("org.apache.derby.jdbc.ClientDriver").newInstance();
            // Get a connection
            conn = DriverManager.getConnection(dbURL);
        }
        catch (Exception except) {
            except.printStackTrace();
        }

        // Creates the Interpreter request table if it isn't there already
        // This request table stores the info of an interpreter request made by a staff or admin member
        // Already has an assigned staff member, location, status, as well as extra info for filling request - family size, language, note
        // Admin will later access the table to pull up a request and mark it as fulfilled by filling out the form
        // database sets ID of request; this ID links to database of InterpreterReports
        try {
            stmt = conn.createStatement();
            stmt.execute(
                    " CREATE TABLE "+ tableName +" (" +
                            "REQUESTID INTEGER PRIMARY KEY NOT NULL," +
                            "LASTNAME VARCHAR(50) NOT NULL, " +
                            "FIRSTNAME VARCHAR(50) NOT NULL, " +
                            "XCOORD INT NOT NULL," +
                            "YCOORD INT NOT NULL, " +
                            "LVL VARCHAR(20) NOT NULL," +
                            "BUILDING VARCHAR(20) NOT NULL," +
                            "STATUS VARCHAR(10) NOT NULL, " +
                            "FAMSIZE INTEGER NOT NULL, " +
                            "LANG VARCHAR(30) NOT NULL, " +
                            "NOTE VARCHAR(300) NOT NULL, " +
                            ")"
            );
            stmt.close();
        }
        catch (SQLException sqlExcept) {
            log.info("Does the service request database already exist?");
        }

        try {
            //getRequestID = conn.prepareStatement("")
            addRequest = conn.prepareStatement("INSERT INTO " + tableName + " VALUES (?,?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
            getRequest = conn.prepareStatement("SELECT * FROM " + tableName + " WHERE REQUESTID = ?");
            deleteRequest = conn.prepareStatement("DELETE FROM " + tableName + " WHERE REQUESTID = ?");
            updateRequest = conn.prepareStatement("UPDATE " + tableName + " SET XCOORD = ?, YCOORD = ?, LVL = ?, BUILDING = ?, BUILDING = ?, " +
                                                        " PRIORITY = ?, STATUS = ?, FAMSIZE = ?, LANG = ?, NOTE = ? WHERE REQUESTID = ?");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public InterpreterRequest addRequest(InterpreterRequest request) {
        try {
            addRequest.setInt(1, request.getInfo().getLocation().getxCoord());
            addRequest.setInt(2, request.getInfo().getLocation().getyCoord());
            addRequest.setString(3, request.getInfo().getLocation().getLevel().toString());
            addRequest.setString(4, request.getInfo().getLocation().getBuilding());
            addRequest.setInt(5, request.getInfo().getPriority().getValue());
            addRequest.setString(6, request.getStatus().toString());
            addRequest.setInt(7, request.getFamilySize());
            addRequest.setString(8, request.getRequiredLanguage().toString());
            addRequest.setString(9, request.getInfo().getNote());

            addRequest.executeUpdate();
            ResultSet rs = addRequest.getGeneratedKeys();
            if (rs.next()) {
                int ID = rs.getInt(1);
                request.setID(ID);
                log.info("The new request has ID " + ID);
            }
            return request;
        } catch(SQLException exception) {
            exception.printStackTrace();
            return null;
        }
    }

    public boolean cancelRequest(InterpreterRequest request) {
        return cancelRequest(request);
    }

    private boolean cancelReq(InterpreterRequest r) {
        try {
            deleteRequest.setInt(1, r.getInfo().getID());
            return true;
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public InterpreterRequest getRequest(InterpreterRequest r) {
        try {
            stmt = conn.createStatement();
            ResultSet set = stmt.executeQuery(
                    "SELECT * FROM "+requestTable+" WHERE REQUESTID='"+id+"'"
            );
            set.next();
            // Get info from table and return object based off of it
            Request r = new Request(id, new Location(set.getInt("XCOORD"), set.getInt("YCOORD"),
                    Floor.getFloor(set.getString("LEVEL")), set.getString("BUILDING")),
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
                                Floor.valueOf(set.getString("LEVEL")), set.getString("BUILDING")),
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
