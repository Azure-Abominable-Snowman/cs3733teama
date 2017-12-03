package com.teama.requestsubsystem;

import com.teama.mapsubsystem.data.Floor;
import com.teama.mapsubsystem.data.Location;

import java.sql.*;
import java.util.ArrayList;
import java.util.logging.Logger;

/**
 * Created by aliss on 12/2/2017.
 */
public class GeneralRequestDB implements ServiceRequestDataSource {
    private final Logger log = Logger.getLogger(this.getClass().getPackage().getName());
    private String dbURL;
    private String requestTableName;
    private String reportTableName;
    private Connection conn = null;
    private Statement stmt = null;
    private PreparedStatement addRequest, getRequest, deleteRequest, selectRequestByStatus, updateRequest, markStatus, getRequestID; //for request table
    private PreparedStatement addReport, updateReport, deleteReport, getReport;

    public GeneralRequestDB(String dbURL, String reqTableName, String reportTableName) {

        this.requestTableName = reqTableName;
        this.reportTableName = reportTableName;
        this.dbURL = dbURL;

        try {
            Class.forName("org.apache.derby.jdbc.EmbeddedDriver").newInstance();
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
            log.info("Preparing to create Request Table.");
            stmt.execute(
                    " CREATE TABLE " + this.requestTableName + " (" +
                            "REQUESTID INTEGER NOT NULL PRIMARY KEY GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1), " +
                            "STAFFID INTEGER NOT NULL, " +
                            "XCOORD INTEGER NOT NULL," +
                            "YCOORD INTEGER NOT NULL, " +
                            "LVL VARCHAR(20) NOT NULL," +
                            "BUILDING VARCHAR(20) NOT NULL, " +
                            "REQTYPE VARCHAR(50) NOT NULL, " +
                            "STATUS VARCHAR(10) NOT NULL, " +
                            "NOTE VARCHAR(300) NOT NULL" +
                            //" FOREIGN KEY(STAFFID) REFERENCES INTERPRETER_STAFF(STAFFID)" +
                            //" PRIMARY KEY(REQUESTID) " +
                            ")"
            );
            stmt.close();
            log.info("Created the general request table.");
        } catch (SQLException e) {
            log.info("Does the General Request table already exist?");
            e.printStackTrace();
        }


        /*
        DatabaseMetaData meta = null;
        try {
            meta = conn.getMetaData();
            ResultSet res = meta.getTables(null, null, reqTableName,
                    new String[] {"TABLE"});
            while (res.next()) {
                System.out.println(
                        "   "+res.getString("TABLE_CAT")
                                + ", "+res.getString("TABLE_SCHEM")
                                + ", "+res.getString("TABLE_NAME")
                                + ", "+res.getString("TABLE_TYPE")
                                + ", "+res.getString("REMARKS"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        */


        try {
            //getRequestID = conn.prepareStatement("")
            addRequest = conn.prepareStatement("INSERT INTO " + requestTableName + " (STAFFID, XCOORD, YCOORD, LVL, BUILDING, REQTYPE, STATUS, NOTE) " +
                    " VALUES (?, ?, ?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
            getRequest = conn.prepareStatement("SELECT * FROM " + requestTableName + " WHERE REQUESTID = ?");
            deleteRequest = conn.prepareStatement("DELETE FROM " + requestTableName + " WHERE REQUESTID = ?");
            updateRequest = conn.prepareStatement("UPDATE " + requestTableName + " SET STAFFID = ?, XCOORD = ?, YCOORD = ?, LVL = ?, BUILDING = ?, " +
                    " STATUS = ?, FAMSIZE = ?, LANG = ?, NOTE = ? WHERE REQUESTID = ?");
            selectRequestByStatus = conn.prepareStatement("SELECT * FROM " + requestTableName + " WHERE STATUS = ?");

            markStatus = conn.prepareStatement("UPDATE " + requestTableName + " SET STATUS = ? WHERE REQUESTID = ?", ResultSet.CONCUR_UPDATABLE);
            addReport = conn.prepareStatement("INSERT INTO " + reportTableName + " VALUES(?, ?, ?)");
            deleteReport = conn.prepareStatement("DELETE FROM " + reportTableName + " WHERE REQUESTID = ?");
            getReport = conn.prepareStatement("SELECT * FROM " + reportTableName + " WHERE REQUESTID = ?");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * adds a Request to the generic request table
     * @param request
     * @return
     */
    @Override
    public Request addRequest(Request request) {
        GenericRequest newReq = null;
        try {
            addRequest.setInt(1, request.getStaffID());
            addRequest.setInt(2, request.getLocation().getxCoord());
            addRequest.setInt(3, request.getLocation().getyCoord());
            addRequest.setString(4, request.getLocation().getLevel().toString());
            addRequest.setString(5, request.getLocation().getBuilding());
            addRequest.setString(6, request.getReqType().toString());
            addRequest.setString(7, request.getStatus().toString());
            addRequest.setString(8, request.getNote());
            addRequest.executeUpdate();
            ResultSet rs = addRequest.getGeneratedKeys();
            int ID = 0;
            if (rs.next()) {
                ID = rs.getInt("REQUESTID");
                newReq = new GenericRequest(request.getLocation(), request.getStaffID(), request.getReqType(), request.getStatus(), request.getNote());
                newReq.setRequestID(ID);
                log.info("Added request with ID " + ID);

            }


        } catch (SQLException e) {
            //e.printStackTrace();
            log.info("Failed to add new Request to the generic Request table.");
        }
        return newReq;
    }

    /**
     * Deletes a Request from the General Request Table
     * @param requestID
     * @return
     */
    public boolean deleteRequest(int requestID) {
        //TODO
        return false;
    }

    private GenericRequest reqFromRS(ResultSet rs) {
        GenericRequest found = null;
        try {
            found = new GenericRequest(new Location(rs.getInt("XCOORD"), rs.getInt("YCOORD"), Floor.getFloor(rs.getString("LVL")), rs.getString("BUILDING")), rs.getInt("STAFFID"),
                    RequestType.getRequestType(rs.getString("REQTYPE")), RequestStatus.getRequestStatus(rs.getString("STATUS")), rs.getString("NOTE"));
            found.setRequestID(rs.getInt("REQUESTID"));

        } catch (SQLException e) {
            //e.printStackTrace();
        }
        return found;
    }
    /**
     * Retrieves a Request by input requestID
     *             getRequest = conn.prepareStatement("SELECT * FROM " + requestTableName + " WHERE REQUESTID = ?");

     * @param requestID
     * @return
     */
    public Request getRequest(int requestID) {
        GenericRequest found = null;
        log.info("Looking for request with ID " + requestID);
        try {
            getRequest.setInt(1, requestID);
            ResultSet rs = getRequest.executeQuery();
            if (rs.next()) {
                found = reqFromRS(rs);
                if (found != null) {
                    log.info("Retrieved an " + found.getReqType().toString() + " request with ID " + rs.getInt("REQUESTID"));
                }
            }
        } catch (SQLException e) {
            log.info("Failed to find the given request with ID " + requestID);
            //e.printStackTrace();
        }

        return found;
    }

    /**
     *
     * @param status
     * @return
     */
    public ArrayList<Request> getAllRequests(RequestStatus status) {
        //TODO
        return null;
    } // get all requests by given RequestStatus
    public ArrayList<Request> getAllRequests(RequestStatus status, RequestType type) {
        //TODO
        return null;
    } // get all requests by given RequestStatus and Request Type

    /**
     *  Marks the request as Closed.
     *              markStatus= conn.prepareStatement("UPDATE " + requestTableName + " SET STATUS = ? WHERE REQUESTID = ?", ResultSet.CONCUR_UPDATABLE);

     * @param r
     * @return
     */
    public boolean fulfillRequest(Request r) {
        
        return false;
    }// generate report for given request; mark request as closed
    public  boolean updateRequest(Request r) {
        //TODO
        return true;
    }
    public void close() {
        try {
            conn.close();
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }
}
