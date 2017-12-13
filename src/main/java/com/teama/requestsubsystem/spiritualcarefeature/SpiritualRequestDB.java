package com.teama.requestsubsystem.spiritualcarefeature;

import com.teama.requestsubsystem.*;


import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.logging.Logger;

/**
 * Created by aliss on 12/10/2017.
 */

public class SpiritualRequestDB implements ServiceRequestDataSource {
    private ServiceRequestDataSource generalInfo;
    private final Logger log = Logger.getLogger(this.getClass().getPackage().getName());
    private String dbURL;
    private String requestTableName;
    //private String reportTableName;
    private Connection conn = null;
    private Statement stmt = null;
    private PreparedStatement addRequest, getRequest, getStaffRequest, deleteRequest, fulfillRequest, getRequestStaffIDStatus, getRequestByStatus, updateRequest;
    //selectRequestByStatus, updateRequest, markClosed, getRequestID; //for request table
    private PreparedStatement addReport, updateReport, deleteReport, getReport;


    private String expectedDate = "MM/dd/yyyy";
    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(expectedDate);

    public SpiritualRequestDB(String dbURL, String genericReqTableName, String reqTableName) {

        this.requestTableName = reqTableName;
        this.dbURL = dbURL;

        generalInfo = new GeneralRequestDB(dbURL, genericReqTableName);

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
                            "REQUESTID INTEGER NOT NULL PRIMARY KEY, " +
                            "STAFFID INTEGER NOT NULL, " +
                            "STATUS VARCHAR(50) NOT NULL, " +
                            "RELIGION VARCHAR(100) NOT NULL, " +
                            "SERVICETYPE VARCHAR(100) NOT NULL, " +
                            "DATE VARCHAR(100) NOT NULL, " +
                            "FOREIGN KEY(REQUESTID) REFERENCES " + genericReqTableName + " (REQUESTID))");
            //" FOREIGN KEY(STAFFID) REFERENCES INTERPRETER_STAFF(STAFFID)" +
            //" PRIMARY KEY(REQUESTID) " +

            System.out.println(requestTableName);
            stmt.close();
            log.info("Created the Spiritual Request table.");
        } catch (SQLException e) {
            log.info("Does the Spiritual Request table already exist?");
            //e.printStackTrace();
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
            addRequest = conn.prepareStatement("INSERT INTO " + requestTableName + " (REQUESTID, STAFFID, STATUS, RELIGION, SERVICETYPE, DATE) " +
                    " VALUES (?, ?, ?, ?, ?, ?)");
            updateRequest = conn.prepareStatement("UPDATE " + requestTableName + " SET STAFFID = ?, RELIGION = ?, SERVICETYPE = ?, DATE = ? WHERE REQUESTID = ?");
            getRequest = conn.prepareStatement("SELECT * FROM " + requestTableName + " WHERE REQUESTID = ?");
            //getStaffRequest = conn.prepareStatement("SELECT * FROM " + requestTableName + " WHERE STAFFID = ?");
            deleteRequest = conn.prepareStatement("DELETE FROM " + requestTableName + " WHERE REQUESTID = ?");
            fulfillRequest = conn.prepareStatement("UPDATE " + requestTableName + " SET STATUS = ? " +
                    "  WHERE REQUESTID = ? AND STAFFID = ?");
            getRequestStaffIDStatus = conn.prepareStatement("SELECT * FROM " + requestTableName + " WHERE STAFFID = ? AND STATUS = ?");
            getRequestByStatus = conn.prepareStatement("SELECT * FROM " + requestTableName + " WHERE STATUS = ?");
            //selectRequestByStatus = conn.prepareStatement("SELECT * FROM " + requestTableName + " WHERE STATUS = ?");

            //markClosed = conn.prepareStatement("UPDATE " + requestTableName + " SET STATUS = ? WHERE REQUESTID = ?", ResultSet.CONCUR_UPDATABLE);
            //addReport = conn.prepareStatement("INSERT INTO " + reportTableName + " VALUES(?, ?, ?)");
            //deleteReport = conn.prepareStatement("DELETE FROM " + reportTableName + " WHERE REQUESTID = ?");
            //getReport = conn.prepareStatement("SELECT * FROM " + reportTableName + " WHERE REQUESTID = ?");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * addRequest = conn.prepareStatement("INSERT INTO " + requestTableName + " (REQUESTID, STAFFID, STATUS, RELIGION, SERVICETYPE, DATE) " +
     " VALUES (?, ?, ?, ?, ?, ?)");
     * @param request
     * @return
     */
    public SpiritualCareRequest addRequest(SpiritualCareRequest request) {
        int reqID = request.getRequestID();
        int staffID = request.getStaffID();
        Request req = null;
        RequestStatus status = request.getStatus();
        if (request.getRequestID() == 0) { // if it hasn't been added to the DB already, add it
            req = generalInfo.addRequest(request);
            if (req != null) { // added successfully
                reqID = req.getRequestID();
                staffID = req.getStaffID();
                status = req.getStatus();
            }
        }
        try {
            System.out.println(request.getLocalDate().format(dateFormatter));

            addRequest.setInt(1, reqID);
            addRequest.setInt(2, staffID);
            addRequest.setString(3, status.toString());
            addRequest.setString(4, request.getReligion().toString()); // a request only added to db if assigned to staff member
            addRequest.setString(5, request.getSpiritualService().toString()); // not filled in yet
            addRequest.setString(6, request.getLocalDate().format(dateFormatter));

            addRequest.executeUpdate();
            log.info("Spiritual Care request added to Database.");
            return new SpiritualCareRequest(req, request.getReligion(), request.getSpiritualService(), request.getLocalDate());
        } catch (SQLException exception) {
            exception.printStackTrace();
            return null;
        }
    }

    /**
     * for adding a generic request
     * @param request
     * @return
     */
    @Override
    public Request addRequest(Request request) {
        return generalInfo.addRequest(request);
    }

    /**
     *
     * @param requestID
     * @return
     */
    @Override
    public Request getRequest(int requestID) {
        return generalInfo.getRequest(requestID);
    }

    /**
     *
     * @param status
     * @return
     */
    @Override
    public ArrayList<Request> getAllRequests(RequestStatus status) {
        return generalInfo.getAllRequests(status);
    }

    /**
     *
     * @param status
     * @param type
     * @return
     */
    @Override
    public ArrayList<Request> getAllRequests(RequestStatus status, RequestType type) {
        return generalInfo.getAllRequests(status, type);
    } // get all requests by given RequestStatus and Request Type

    /**
     *
     * @param r
     * @return
     */
    @Override
    public boolean fulfillRequest(int r) {
        return generalInfo.fulfillRequest(r);
    }

    /**
     *
     * @param r
     * @return
     */
    @Override
    public  boolean updateRequest(Request r) {
        return generalInfo.updateRequest(r);
    }

    /**
     *
     * @param requestID
     * @return
     */
    public boolean deleteRequest(int requestID) {
        boolean deletedSpiritual = false;
        try {
            deleteRequest.setInt(1, requestID);
            deleteRequest.execute();
            deletedSpiritual = true;
        } catch(SQLException e) {
            e.printStackTrace();
        }
        boolean deletedGeneral = generalInfo.deleteRequest(requestID);
        return deletedSpiritual && deletedGeneral;
    }


    /**
     *
     * @param set
     * @return
     */
/*
    private InterpreterRequest requestFromResultSet(ResultSet set) {
        try {
            InterpreterRequest r = new InterpreterRequest(new GenericRequest(new Location(set.getInt("XCOORD"), set.getInt("YCOORD"),
                    Floor.getFloor(set.getString("LVL")), set.getString("BUILDING")), set.getInt("STAFFID"),
                    set.getString("NOTE")), RequestStatus.getRequestStatus(set.getString("STATUS")), set.getInt("FAMSIZE"), Language.valueOf(set.getString("LANG")), set.getInt("REQUESTID"));
            r.setRequestID(set.getInt("REQUESTID"));
            return r;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

*/
    /**
     * returns a specific InterpreterRequest that was asked for
     * "REQUESTID INTEGER NOT NULL PRIMARY KEY, " +
     "STAFFID INTEGER NOT NULL, " +
     "STATUS VARCHAR(50) NOT NULL, " +
     "RELIGION VARCHAR(100) NOT NULL, " +
     "SERVICETYPE VARCHAR(100) NOT NULL, " +
     "DATE VARCHAR(100) NOT NULL, " +
     "FOREIGN KEY(REQUESTID) REFERENCES " + genericReqTableName + " (REQUESTID))");
     * @param requestID
     * @return
     */
    public SpiritualCareRequest getSpiritualRequest(int requestID) {
        SpiritualCareRequest r = null;
        try {
            getRequest.setInt(1, requestID);
            ResultSet set = getRequest.executeQuery();
            if (set.next()) {

                // Get info from table and return object based off of it
                System.out.println(set.getInt("REQUESTID"));

                //log.info("Found a request with ID " + r.getRequestID());
                Request info = generalInfo.getRequest(requestID);
                if (info != null) {
                    //LocalDate date = LocalDate.parse(set.getString("DATE"), dateFormatter);
                    //System.out.println(set.getString("DATE"));
                    //System.out.println(date);
                    r = reqFromResultSet(set, info);
                    if (r!= null) {
                        log.info("Successfully retrieved spiritual care request.");
                    }
                }
            }
            set.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return r;
    }
    private SpiritualCareRequest reqFromResultSet(ResultSet set, Request info) {
        SpiritualCareRequest r = null;
        LocalDate date = null;
        try {
            date = LocalDate.parse(set.getString("DATE"), dateFormatter);
            System.out.println(set.getString("DATE"));
            System.out.println(date);
            r = new SpiritualCareRequest(info, Religion.getReligion(set.getString("RELIGION")), SpiritualService.getSpiritualService(set.getString("SERVICETYPE")),
                    date);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return r;
    }

    /**
     * getRequestStaffIDStatus = conn.prepareStatement("SELECT * FROM " + requestTableName + " WHERE STAFFID = ? AND STATUS = ?");
     * returns all Requests  of an input RequestStatus for a given staff member
     * @param staffID
     * @param status
     * @return
     */
    public ArrayList<SpiritualCareRequest> getSpiritualRequestsByStaff(int staffID, RequestStatus status) {
        // TODO
        ArrayList<SpiritualCareRequest> spiritualCare = new ArrayList<>();
        try {
            getRequestStaffIDStatus.setInt(1, staffID);
            getRequestStaffIDStatus.setString(2, status.toString());
            ResultSet reqs = getRequestStaffIDStatus.executeQuery();
            while (reqs.next()) {
                int reqId = reqs.getInt("STAFFID");
                if (reqId != 0) {
                    Request generalInfo = this.generalInfo.getRequest(reqId);
                    SpiritualCareRequest found = reqFromResultSet(reqs, generalInfo);
                    spiritualCare.add(found);

                }

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return spiritualCare;
    }
    /**
     *     getRequestByStatus = conn.prepareStatement("SELECT * FROM " + requestTableName + " WHERE STATUS = ?");

     * @param status
     * @return
     */
    public ArrayList<SpiritualCareRequest> getAllSpiritualRequests(RequestStatus status) {
        // todo
        ArrayList<SpiritualCareRequest> requestList = new ArrayList<>();
        try {
            getRequestByStatus.setString(1, status.toString());
            ResultSet rs = getRequestByStatus.executeQuery();
            while (rs.next()) {
                Request generalInfo = this.generalInfo.getRequest(rs.getInt("REQUESTID"));
                SpiritualCareRequest f = null;
                f = reqFromResultSet(rs, generalInfo);
                if (f != null) {
                    requestList.add(f);
                }
                else {
                    System.out.println("Failed to create a request of the given status when retrieving all SpiritualRequests by status");
                }
            }
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return requestList;
    }

    /**
     * Ensures that the GenericRequestTable is updated (status set to CLOSED) and fills in all tracking fields
     * fulfillRequest = conn.prepareStatement("UPDATE " + requestTableName + " SET STATUS = ? " +
     "  WHERE REQUESTID = ? AND STAFFID = ?");
     * @param r
     * @return
     */
    public boolean fulfillRequest(SpiritualCareRequest r) {
        if (fulfillRequest(r.getRequestID())) {
            if (r.getRequestID() == 0) {
                log.info("Tried to fulfill a request that was not created.");
                return false;
            } else if (r.getStatus() == RequestStatus.CLOSED) {
                log.info("Tried to fulfill a request that was already closed.");
                return false;
            }
            generalInfo.fulfillRequest(r.getRequestID());
            try { //mark the request as closed in the Interpreter table
                fulfillRequest.setString(1, RequestStatus.CLOSED.toString());
                fulfillRequest.setInt(2, r.getRequestID());
                fulfillRequest.setInt(3, r.getStaffID());
                fulfillRequest.executeUpdate();
                log.info("Fulfilled care request.");
                return true;

            } catch (SQLException e) {
                e.printStackTrace();
                log.info("Tried to add a report for request " + r.getRequestID() + " but failed.");
                return false;
            }
        }
        return false;

    }


    /**
     *
     */
    public void close() {
        try {
            conn.close();
            generalInfo.close();
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     *
     * @param r
     * @return
     */
    /*
     updateRequest = conn.prepareStatement("UPDATE " + requestTableName + " SET STAFFID = ?, RELIGION = ?, SERVICETYPE = ?, DATE = ? WHERE REQUESTID = ?");


     */

    public boolean updateSpiritualRequest(SpiritualCareRequest r) {
        if (generalInfo.updateRequest(r)) {
            try {
                updateRequest.setInt(1, r.getStaffID());
                updateRequest.setString(2, r.getReligion().toString());
                updateRequest.setString(3, r.getSpiritualService().toString());
                updateRequest.setString(4, r.getLocalDate().format(dateFormatter));

                updateRequest.executeUpdate();
                log.info("Updated the Spiritual Care Request " + r.getRequestID());
                return true;
            } catch (SQLException e) {
                log.info("Failed to update Request " + r.getRequestID());
                e.printStackTrace();
            }
        }
        return false;
    }

}

