package com.teama.requestsubsystem.interpreterfeature;

import com.teama.requestsubsystem.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.logging.Logger;

public class InterpreterRequestDB implements ServiceRequestDataSource {
    private ServiceRequestDataSource generalInfo;
    private final Logger log = Logger.getLogger(this.getClass().getPackage().getName());
    private String dbURL;
    private String requestTableName;
    //private String reportTableName;
    private Connection conn = null;
    private Statement stmt = null;
    private PreparedStatement addRequest, getRequest, deleteRequest, fulfillRequest, getRequestStaffIDStatus, getRequestByStatus, updateRequest;
            //selectRequestByStatus, updateRequest, markClosed, getRequestID; //for request table
    private PreparedStatement addReport, updateReport, deleteReport, getReport;

    public InterpreterRequestDB(String dbURL, String genericReqTableName, String reqTableName) {

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
                            "LANG VARCHAR(30) NOT NULL, " +
                            "FAMSIZE INTEGER, " +
                            "SERVICETIME DOUBLE, " +
                            "TRANSTYPE VARCHAR(40), " +
                            "FOREIGN KEY(REQUESTID, STAFFID) REFERENCES " + genericReqTableName + " (REQUESTID))");
            //" FOREIGN KEY(STAFFID) REFERENCES INTERPRETER_STAFF(STAFFID)" +
                            //" PRIMARY KEY(REQUESTID) " +

            System.out.println(requestTableName);
            stmt.close();
            log.info("Created the Interpreter Request table.");
        } catch (SQLException e) {
            log.info("Does the Interpreter Request table already exist?");
            e.printStackTrace();
        }
/*
        // Create the report table linked to request table by foreign key
        try {
            stmt = conn.createStatement();
            stmt.execute(" CREATE TABLE " + this.reportTableName + " (" +
                    "REQUESTID INTEGER PRIMARY KEY NOT NULL, " +
                    "SERVICETIME DOUBLE NOT NULL, " +
                    "TRANSTYPE VARCHAR(40) NOT NULL, " +
                    "FOREIGN KEY (REQUESTID) " +
                    "REFERENCES " + requestTableName + "(REQUESTID)" +
                    ")"
            );
            stmt.close();
        } catch (SQLException e) {
            log.info("Does the report table already exist?");
            //e.printStackTrace();
        }
        */
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
            addRequest = conn.prepareStatement("INSERT INTO " + requestTableName + " (REQUESTID, STAFFID, STATUS, LANG, FAMSIZE, SERVICETIME, TRANSTYPE) " +
                    " VALUES (?, ?, ?, ?, ?, ?, ?)");
            getRequest = conn.prepareStatement("SELECT * FROM " + requestTableName + " WHERE REQUESTID = ?");
            deleteRequest = conn.prepareStatement("DELETE FROM " + requestTableName + " WHERE REQUESTID = ?");
            fulfillRequest = conn.prepareStatement("UPDATE " + requestTableName + " SET STATUS = ?, LANG = ?, FAMSIZE = ?, SERVICETIME = ?, TRANSTYPE = ? " +
                    "  WHERE REQUESTID = ? AND STAFFID = ?)");
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
     * For adding a new InterpreterRequest to the InterpreterRequestDB
     * @param request
     * @return
     */
    public boolean addRequest(InterpreterRequest request) {
        int reqID = request.getRequestID();
        int staffID = request.getStaffID();
        RequestStatus status = request.getStatus();
        if (request.getRequestID() == 0) { // if it hasn't been added to the DB already, add it
            Request req = generalInfo.addRequest(request);
            if (req != null) { // added successfully
                reqID = req.getRequestID();
                staffID = req.getStaffID();
                status = req.getStatus();
            }
        }
        try {
            addRequest.setInt(1, reqID);
            addRequest.setInt(2, staffID);
            addRequest.setString(3, status.toString());
            addRequest.setString(4, request.getRequiredLanguage().toString()); // a request only added to db if assigned to staff member
            addRequest.setNull(5, Types.DOUBLE); // not filled in yet
            addRequest.setNull(6, Types.VARCHAR);
            addRequest.setNull(7, Types.VARCHAR);

            addRequest.executeUpdate();
            log.info("Interpreter request added to Database.");
            return true;
        } catch (SQLException exception) {
            exception.printStackTrace();
            return false;
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
        boolean deletedInterp = generalInfo.deleteRequest(requestID);
        boolean deletedGeneral = generalInfo.deleteRequest(requestID);
        return deletedInterp && deletedGeneral;
    }

    /**
     *
     * @param requestID
     * @return
     */
    private boolean deleteReq(int requestID) {
        try {
            deleteReport.setInt(1, requestID);
            deleteReport.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {

            deleteRequest.setInt(1, requestID);
            deleteRequest.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
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
     * @param requestID
     * @return
     */
    public InterpreterRequest getInterpreterRequest(int requestID) {
        InterpreterRequest r = null;
        try {
            getRequest.setInt(1, requestID);
            ResultSet set = getRequest.executeQuery();
            if (set.next()) {
                // Get info from table and return object based off of it
                System.out.println(set.getInt("REQUESTID"));

                //log.info("Found a request with ID " + r.getRequestID());
                Request info = generalInfo.getRequest(requestID);
                if (info != null) {
                    if (info.getStatus() == RequestStatus.ASSIGNED) {
                        r = new InterpreterRequest(info, Language.getLanguage(set.getString("LANG")));
                    }
                    else if (info.getStatus() == RequestStatus.CLOSED){
                        r = new InterpreterRequest(info, Language.getLanguage(set.getString("LANG")), set.getInt("FAMSIZE"), set.getDouble("SERVICETIME"),
                                TranslationType.getTranslationType(set.getString("TRANSTYPE")));
                    }
                }
            }
            set.close();
        } catch (SQLException e) {
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
    public ArrayList<InterpreterRequest> getInterpreterRequestsByStaff(int staffID, RequestStatus status) {
        ArrayList<InterpreterRequest> interpreters = new ArrayList<>();
        try {
            getRequestStaffIDStatus.setInt(1, staffID);
            getRequestStaffIDStatus.setString(2, status.toString());
            ResultSet reqs = getRequestStaffIDStatus.executeQuery();
            while (reqs.next()) {
                int reqId = reqs.getInt("STAFFID");
                if (reqId != 0) {
                    Request generalInfo = this.generalInfo.getRequest(reqId);
                    InterpreterRequest found = null;
                    if (status == RequestStatus.ASSIGNED) {
                        found = new InterpreterRequest(generalInfo, Language.getLanguage(reqs.getString("LANG")));
                    }
                    else if (status == RequestStatus.CLOSED) {
                        found = new InterpreterRequest(generalInfo, Language.getLanguage(reqs.getString("LANG")), reqs.getInt("FAMSIZE"), reqs.getDouble("SERVICETIME"),
                                TranslationType.getTranslationType(reqs.getString("TRANSTYPE")));
                    }
                    interpreters.add(found);

                }

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return interpreters;
    }
    /**
     *     getRequestByStatus = conn.prepareStatement("SELECT * FROM " + requestTableName + " WHERE STATUS = ?");

     * @param status
     * @return
     */
    public ArrayList<InterpreterRequest> getAllInterpreterRequests(RequestStatus status) {
        ArrayList<InterpreterRequest> requestList = new ArrayList<>();
        try {
            getRequestByStatus.setString(1, status.toString());
            ResultSet rs = getRequestByStatus.executeQuery();
            while (rs.next()) {
                Request generalInfo = this.generalInfo.getRequest(rs.getInt("REQUESTID"));
                InterpreterRequest f = null;
                if (status == RequestStatus.ASSIGNED) {
                    f = new InterpreterRequest(generalInfo, Language.getLanguage(rs.getString("LANG")));
                }
                else if (status == RequestStatus.CLOSED) {
                    f = new InterpreterRequest(generalInfo, Language.getLanguage(rs.getString("LANG")), rs.getInt("FAMSIZE"), rs.getDouble("SERVICETIME"),
                            TranslationType.getTranslationType(rs.getString("TRANSTYPE")));
                }
                requestList.add(f);
            }
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return requestList;
    }

    /**
     * Ensures that the GenericRequestTable is updated (status set to CLOSED) and fills in all tracking fields
     * fulfillRequest = conn.prepareStatement("UPDATE " + requestTableName + " SET LANG = ?, FAMSIZE = ?, SERVICETIME = ?, TRANSTYPE = ? " +
     "  WHERE REQUESTID = ? AND STAFFID = ?");
     * @param r
     * @return
     */
    public boolean fulfillRequest(InterpreterRequest r) {
        System.out.println(r.getRequestID());
        if (r.getRequestID() == 0) {
            log.info("Tried to fulfill a request that was not created.");
            return false;
        }
        generalInfo.fulfillRequest(r.getRequestID());
        try { //mark the request as closed in the Interpreter table
            fulfillRequest.setString(1, RequestStatus.CLOSED.toString());
            fulfillRequest.setString(2, r.getRequiredLanguage().toString());
            fulfillRequest.setInt(3, r.getFamilySize());
            fulfillRequest.setDouble(4, r.getServiceTime());
            fulfillRequest.setString(5, r.getTranslType().toString());
            fulfillRequest.setInt(6, r.getRequestID());
            fulfillRequest.setInt(7,r.getStaffID());
            fulfillRequest.executeUpdate();
            log.info("Filled in all the tracking fields for Interpreter request.");

        }  catch (SQLException e) {
            e.printStackTrace();
            log.info("Tried to add a report for request " + r.getRequestID() + " but failed.");
            return false;
        }
        return true;

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
    ONLY updates the request. if a request is closed, the report is done and cannot be changed.
     */
    /*
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
    */


}


