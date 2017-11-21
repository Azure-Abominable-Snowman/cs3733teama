package com.teama.requestsubsystem.interpreterfeature;

import com.teama.mapsubsystem.data.Floor;
import com.teama.mapsubsystem.data.Location;
import com.teama.requestsubsystem.RequestType;

import java.sql.*;
import java.util.ArrayList;
import java.util.logging.Logger;

public class InterpreterRequestDB {

    private final Logger log = Logger.getLogger(this.getClass().getPackage().getName());
    private String dbURL;
    private String requestTableName;
    private String reportTableName;
    private Connection conn = null;
    private Statement stmt = null;
    private PreparedStatement addRequest, getRequest, deleteRequest, selectRequestByStatus, updateRequest, markClosed, getRequestID; //for request table
    private PreparedStatement addReport, updateReport, deleteReport;

    public InterpreterRequestDB(String dbURL) {
        this.requestTableName = "INTERPRETER_REQUESTS";
        this.reportTableName = "INTERPRETER_REPORTS";
        this.dbURL = dbURL;

        try {
            Class.forName("org.apache.derby.jdbc.ClientDriver").newInstance();
            // Get a connection
            conn = DriverManager.getConnection(dbURL);
        } catch (Exception except) {
            except.printStackTrace();
        }

        // Creates the Interpreter request table if it isn't there already
        // This request table stores the info of an interpreter request made by a staff or admin member
        // Already has an assigned staff member, location, status, as well as extra info for filling request - family size, language, note
        // Admin will later access the table to pull up a request and mark it as fulfilled by filling out the form
        // database sets ID of request; this ID links to database of InterpreterReports


        // create the
        try {
            stmt = conn.createStatement();
            stmt.execute(
                    " CREATE TABLE " + requestTableName + " (" +
                            "REQUESTID INTEGER NOT NULL AUTO_INCREMENT," +
                            "STAFFID INTEGER NOT NULL, " +
                            "XCOORD INT NOT NULL," +
                            "YCOORD INT NOT NULL, " +
                            "LVL VARCHAR(20) NOT NULL," +
                            "BUILDING VARCHAR(20) NOT NULL," +
                            "STATUS VARCHAR(10) NOT NULL, " +
                            "FAMSIZE INTEGER NOT NULL, " +
                            "LANG VARCHAR(30) NOT NULL, " +
                            "NOTE VARCHAR(300) NOT NULL, " +
                            " FOREIGN KEY(STAFFID) REFERENCES INTERPRETER_STAFF(STAFFID)" +
                            " PRIMARY KEY(REQUESTID) " +
                            ")"
            );
            stmt.close();
        } catch (SQLException e) {
            log.info("Interpreter Request Table already exists.");
            e.printStackTrace();
        }

        // report table linked to request table by foreign key
        try {
            stmt = conn.createStatement();
            stmt.execute(" CREATE TABLE " + reportTableName + " (" +
                    "REQUESTID INTEGER PRIMARY KEY NOT NULL, " +
                    "SERVICETIME DOUBLE NOT NULL, " +
                    "TRANSTYPE VARCHAR(40) NOT NULL, " +
                    "FOREIGN KEY (REQUESTID) " +
                    "REFERENCES " + requestTableName + "(REQUESTID)" +
                    ")"
            );
        } catch (SQLException e) {
            log.info("Interpreter Report Table already exists.");
            e.printStackTrace();
        }


        try {
            //getRequestID = conn.prepareStatement("")
            addRequest = conn.prepareStatement("INSERT INTO " + requestTableName + " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
            getRequest = conn.prepareStatement("SELECT * FROM " + requestTableName + " WHERE REQUESTID = ?");
            deleteRequest = conn.prepareStatement("DELETE FROM " + requestTableName + " WHERE REQUESTID = ?");
            updateRequest = conn.prepareStatement("UPDATE " + requestTableName + " SET XCOORD = ?, YCOORD = ?, LVL = ?, BUILDING = ?, BUILDING = ?, " +
                    " PRIORITY = ?, STATUS = ?, FAMSIZE = ?, LANG = ?, NOTE = ? WHERE REQUESTID = ?");
            selectRequestByStatus = conn.prepareStatement("SELECT * FROM " + requestTableName + " WHERE STATUS = ?");

            markClosed = conn.prepareStatement("UPDATE " + requestTableName + " SET STATUS = ? WHERE REQUESTID = ?");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public InterpreterRequest addRequest(InterpreterRequest request) {
        try {

            addRequest.setInt(1, request.getStaffID());
            addRequest.setInt(2, request.getInfo().getLocation().getxCoord());
            addRequest.setInt(3, request.getInfo().getLocation().getyCoord());
            addRequest.setString(4, request.getInfo().getLocation().getLevel().toString());
            addRequest.setString(5, request.getInfo().getLocation().getBuilding());
            //addRequest.setInt(5, request.getInfo().getPriority().getValue());
            addRequest.setString(6, request.getStatus().toString());
            addRequest.setInt(7, request.getFamilySize());
            addRequest.setString(8, request.getRequiredLanguage().toString());
            addRequest.setString(9, request.getInfo().getNote());

            addRequest.executeUpdate();
            ResultSet rs = addRequest.getGeneratedKeys();
            if (rs.next()) {
                int ID = rs.getInt(1);
                request.setRequestID(ID);
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
            deleteRequest.setInt(1, r.getInfo().getRequestID());
            return true;
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private InterpreterRequest requestFromResultSet(ResultSet set) {
        try {
            InterpreterRequest r = new InterpreterRequest(new GenericRequestInfo( set.getInt("REQUESTID"), new Location(set.getInt("XCOORD"), set.getInt("YCOORD"),
                    Floor.getFloor(set.getString("LVL")), set.getString("BUILDING")), RequestType.INTR, set.getInt("STAFFID"),
                    set.getString("NOTE")), set.getInt("FAMSIZE"), Language.valueOf(set.getString("LANG")));
            return r;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public InterpreterRequest getRequest(int requestID) {
        try {
            getRequest.setInt(1, requestID);
            ResultSet set = getRequest.executeQuery();
            set.next();
            // Get info from table and return object based off of it
            InterpreterRequest r = requestFromResultSet(set);
            set.close();
            return r;
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public ArrayList<InterpreterRequest> getAllRequests(RequestType type) {
        ArrayList<InterpreterRequest> requestList = new ArrayList<>();
        try {
            selectRequestByStatus.setString(1, type.toString());
            ResultSet rs = selectRequestByStatus.executeQuery();
            while (rs.next()) {
                InterpreterRequest r = requestFromResultSet(rs);
                requestList.add(r);
            }
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return requestList;
    }
/*
    public boolean fulfillRequest(int id) {
        try {
            markClosed.setString(1, RequestStatus.CLOSED.toString());
            markClosed.setInt(2, id);
            // TODO : MAKE A REPORT
            /*
            ResultSet set = stmt.executeQuery("SELECT FULFILLED FROM "+requestTable+" WHERE REQUESTID='"+id+"'");
            // If it doesn't exist or it has already been fulfilled, return false.
            if(!set.next() || set.getBoolean("FULFILLED")) {
                return false;
            }
            set.close();
            //stmt.execute("UPDATE "+requestTable+" SET FULFILLED=TRUE WHERE REQUESTID='"+id+"'");
            //stmt.close();
            return true;
        } catch(SQLException e) {
            e.printStackTrace();
        }

        return false;

    }

  */
    public void close() {
        try {
            conn.close();
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }
    //TODO: REPORT PREPAREDSTATEMENTS
}
