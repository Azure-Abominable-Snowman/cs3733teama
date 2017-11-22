package com.teama.requestsubsystem.interpreterfeature;

import com.teama.mapsubsystem.data.Floor;
import com.teama.mapsubsystem.data.Location;
import com.teama.requestsubsystem.GenericRequestInfo;
import com.teama.requestsubsystem.RequestStatus;

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

        // Create the report table linked to request table by foreign key
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
            updateRequest = conn.prepareStatement("UPDATE " + requestTableName + " SET STAFFID = ?, XCOORD = ?, YCOORD = ?, LVL = ?, BUILDING = ?, " +
                    " STATUS = ?, FAMSIZE = ?, LANG = ?, NOTE = ? WHERE REQUESTID = ?");
            selectRequestByStatus = conn.prepareStatement("SELECT * FROM " + requestTableName + " WHERE STATUS = ?");

            markClosed = conn.prepareStatement("UPDATE " + requestTableName + " SET STATUS = ? WHERE REQUESTID = ?");
            addReport = conn.prepareStatement("INSERT INTO " + reportTableName + " VALUES(?, ?, ?)");
            deleteReport = conn.prepareStatement("DELETE FROM " + reportTableName + " WHERE REQUESTID = ?");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public boolean addRequest(InterpreterRequest request) {
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
            return true;
        } catch (SQLException exception) {
            exception.printStackTrace();
            return false;
        }
    }

    public boolean cancelRequest(int requestID) {
        return cancelRequest(requestID);
    }

    private boolean cancelReq(int requestID) {
        try {
            deleteRequest.setInt(1, requestID);
            deleteRequest.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private InterpreterRequest requestFromResultSet(ResultSet set) {
        try {
            InterpreterRequest r = new InterpreterRequest(new GenericRequestInfo(set.getInt("REQUESTID"), new Location(set.getInt("XCOORD"), set.getInt("YCOORD"),
                    Floor.getFloor(set.getString("LVL")), set.getString("BUILDING")), set.getInt("STAFFID"),
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
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public ArrayList<InterpreterRequest> getAllRequests(RequestStatus status) {
        ArrayList<InterpreterRequest> requestList = new ArrayList<>();
        try {
            selectRequestByStatus.setString(1, status.toString());
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

    public boolean fulfillRequest(InterpreterRequest r) {
        try { //mark the request as closed from the RequestTable
            markClosed.setString(1, RequestStatus.CLOSED.toString());
            markClosed.setInt(2, r.getRequestID());
            markClosed.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            log.info("Could not mark Request " + r.getRequestID() + " as Closed...");
            return false;
        }
        try {
            addReport.setInt(1, r.getRequestID());
            addReport.setDouble(2, r.getServiceTime());
            addReport.setString(3, r.getTranslType().toString());
            addReport.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
            log.info("Tried to add a report for request " + r.getRequestID() + " but failed.");
            return false;
        }
        return true;
        // fill in the report table with newly-entered info
    }

    public void close() {
        try {
            conn.close();
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    /*
    ONLY updates the request. if a request is closed, the report is done and cannot be changed.
     */
    public boolean updateRequest(InterpreterRequest r) {
        try {
            updateRequest.setInt(1, r.getStaffID());
            updateRequest.setInt(2, r.getLocation().getxCoord());
            updateRequest.setInt(3, r.getLocation().getyCoord());
            updateRequest.setString(4, r.getLocation().getLevel().toString());
            updateRequest.setString(5, r.getLocation().getBuilding());
            updateRequest.setString(6, r.getStatus().toString());
            updateRequest.setInt(7, r.getFamilySize());
            updateRequest.setString(8, r.getRequiredLanguage().toString());
            updateRequest.setString(9, r.getNote());
            updateRequest.setInt(10, r.getRequestID());

            updateRequest.executeUpdate();
            return true;
        } catch (SQLException e) {
            log.info("Failed to update Request " + r.getRequestID());
            e.printStackTrace();
            return false;
        }
    }


}


