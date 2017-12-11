package com.teama.requestsubsystem.elevatorfeature;

import com.teama.requestsubsystem.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.logging.Logger;

/**
 * Created by aliss on 12/9/2017.
 */
public class ElevatorRequestDB implements ServiceRequestDataSource {
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

    public ElevatorRequestDB(String dbURL, String genericReqTableName, String reqTableName) {

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




        try {
            stmt = conn.createStatement();
            log.info("Preparing to create Elevator Request Table.");
            stmt.execute(
                    " CREATE TABLE " + this.requestTableName + " (" +
                            "REQUESTID INTEGER NOT NULL PRIMARY KEY, " +
                            "STAFFID INTEGER NOT NULL, " +
                            "STATUS VARCHAR(50) NOT NULL, " +
                            "PRIORITY INTEGER NOT NULL, " +
                            "MAINTENANCE VARCHAR(300) NOT NULL, " +
                            "ELEVATORID VARCHAR(10) NOT NULL, " +
                            "SERVICETIME DOUBLE, " +
                            "FOREIGN KEY(REQUESTID) REFERENCES " + genericReqTableName + " (REQUESTID))");

            System.out.println(requestTableName);
            stmt.close();
            log.info("Created the Elevator Request table.");
        } catch (SQLException e) {
            log.info("Does the Elevator Request table already exist?");
            //e.printStackTrace();
        }


        try {
            //getRequestID = conn.prepareStatement("")
            addRequest = conn.prepareStatement("INSERT INTO " + requestTableName + " (REQUESTID, STAFFID, STATUS, PRIORITY, MAINTENANCE, ELEVATORID, SERVICETIME) " +
                    " VALUES (?, ?, ?, ?, ?, ?, ?)");
            updateRequest = conn.prepareStatement("UPDATE " + requestTableName + " SET PRIORITY = ?, STAFFID = ?, MAINTENANCE = ? WHERE REQUESTID = ?");
            getRequest = conn.prepareStatement("SELECT * FROM " + requestTableName + " WHERE REQUESTID = ?");
            deleteRequest = conn.prepareStatement("DELETE FROM " + requestTableName + " WHERE REQUESTID = ?");
            fulfillRequest = conn.prepareStatement("UPDATE " + requestTableName + " SET STATUS = ?, SERVICETIME = ? " +
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
     *     addRequest = conn.prepareStatement("INSERT INTO " + requestTableName + " (REQUESTID, STAFFID, STATUS, PRIORITY, MAINTENANCE, ELEVATORID, SERVICETIME) " +
     " VALUES (?, ?, ?, ?, ?, ?, ?, ?)");
     * @param request
     * @return
     */

    public ElevatorRequest addRequest(ElevatorRequest request) {
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
            addRequest.setInt(1, reqID);
            addRequest.setInt(2, staffID);
            addRequest.setString(3, status.toString());
            addRequest.setInt(4, request.getpLevel().getValue());
            addRequest.setString(5, request.getMaintenanceType().toString()); // a request only added to db if assigned to staff member
            addRequest.setString(6, request.getBrokenElevatorID()); // not filled in yet
            addRequest.setNull(7, Types.DOUBLE);

            addRequest.executeUpdate();
            log.info("Elevator request added to Database.");
            return new ElevatorRequest(req, request.getpLevel(), request.getMaintenanceType(), request.getBrokenElevatorID());
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
     * deleteRequest = conn.prepareStatement("DELETE FROM " + requestTableName + " WHERE REQUESTID = ?");

     * @param requestID
     * @return
     */
    public boolean deleteRequest(int requestID) {
        boolean deletedElev = false;
        try {
            deleteRequest.setInt(1, requestID);
            deleteRequest.execute();
            deletedElev = true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        boolean deleteGeneral = generalInfo.deleteRequest(requestID);
        return deletedElev && deleteGeneral;

    }


    /**
     * returns a specific ElevatorRequest that was asked for
     * getRequest = conn.prepareStatement("SELECT * FROM " + requestTableName + " WHERE REQUESTID = ?");

     * @param requestID
     * @return
     */
    public ElevatorRequest getElevatorRequest(int requestID) {
        ElevatorRequest r = null;
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
                        r = new ElevatorRequest(info, PriorityLevel.getPriorityLevel(set.getInt("PRIORITY")), MaintenanceType.getMaintenanceType(set.getString("MAINTENANCE")), set.getString("ELEVATORID") );
                    }
                    else if (info.getStatus() == RequestStatus.CLOSED){
                        r = new ElevatorRequest(info, PriorityLevel.getPriorityLevel(set.getInt("PRIORITY")), MaintenanceType.getMaintenanceType(set.getString("MAINTENANCE")), set.getString("ELEVATORID"), set.getInt("SERVICETIME"));
                    }
                }
            }
            set.close();
            log.info("Retrieved desired elevator request.");
        } catch (SQLException e) {
            e.printStackTrace();
            log.severe("Failed to retrieve desired request.");
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
    public ArrayList<ElevatorRequest> getElevatorRequestsByStaff(int staffID, RequestStatus status) {
        ArrayList<ElevatorRequest> elevators = new ArrayList<>();
        try {
            getRequestStaffIDStatus.setInt(1, staffID);
            getRequestStaffIDStatus.setString(2, status.toString());
            ResultSet reqs = getRequestStaffIDStatus.executeQuery();
            while (reqs.next()) {
                int reqId = reqs.getInt("STAFFID");
                if (reqId != 0) {
                    Request generalInfo = this.generalInfo.getRequest(reqId);
                    ElevatorRequest retrieved = getElevatorRequest(reqId);
                    elevators.add(retrieved);
                }

            }
            log.info("Successfully added all elevator requests by given staff and status filters.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return elevators;
    }
    /**
     *     getRequestByStatus = conn.prepareStatement("SELECT * FROM " + requestTableName + " WHERE STATUS = ?");

     * @param status
     * @return
     */
    public ArrayList<ElevatorRequest> getAllElevatorRequests(RequestStatus status) {
        ArrayList<ElevatorRequest> requestList = new ArrayList<>();
        try {
            getRequestByStatus.setString(1, status.toString());
            ResultSet set = getRequestByStatus.executeQuery();
            while (set.next()) {
                Request info = this.generalInfo.getRequest(set.getInt("REQUESTID"));
                ElevatorRequest f = null;
                if (info.getStatus() == RequestStatus.ASSIGNED) {
                    f = new ElevatorRequest(info, PriorityLevel.getPriorityLevel(set.getInt("PRIORITY")), MaintenanceType.getMaintenanceType(set.getString("MAINTENANCE")), set.getString("ELEVATORID") );
                }
                else if (info.getStatus() == RequestStatus.CLOSED){
                    f = new ElevatorRequest(info, PriorityLevel.getPriorityLevel(set.getInt("PRIORITY")), MaintenanceType.getMaintenanceType(set.getString("MAINTENANCE")), set.getString("ELEVATORID"), set.getInt("SERVICETIME"));
                }
                requestList.add(f);
            }
            set.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return requestList;
    }

    /**
     * Ensures that the GenericRequestTable is updated (status set to CLOSED) and fills in all tracking fields
     *   fulfillRequest = conn.prepareStatement("UPDATE " + requestTableName + " SET STATUS = ?, SERVICETIME = ? " +
     "  WHERE REQUESTID = ? AND STAFFID = ?");
     * @param r
     * @return
     */
    public boolean fulfillRequest(ElevatorRequest r) {
        if (fulfillRequest(r.getRequestID())) {
            if (r.getRequestID() == 0) {
                log.info("Tried to fulfill an elevator request that was not created.");
                return false;
            } else if (r.getStatus() == RequestStatus.CLOSED) {
                log.info("Tried to fulfill an elevator request that was already closed.");
                return false;
            }
            generalInfo.fulfillRequest(r.getRequestID());
            try { //mark the request as closed in the Elevator Staff table
                fulfillRequest.setString(1, RequestStatus.CLOSED.toString());
                fulfillRequest.setDouble(2, r.getServiceTime());
                fulfillRequest.setInt(3, r.getRequestID());
                fulfillRequest.setInt(4, r.getStaffID());
                fulfillRequest.executeUpdate();
                log.info("Filled in all the tracking fields for Elevator request.");
                return true;

            } catch (SQLException e) {
                e.printStackTrace();
                log.info("Tried to fulfill Elevator request " + r.getRequestID() + " but failed.");
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
    ONLY updates the request. if a request is closed, the report is done and cannot be changed.
            updateRequest = conn.prepareStatement("UPDATE " + requestTableName + " SET PRIORITY = ?, STAFFID = ?, MAINTENANCE = ? WHERE REQUESTID = ?");

     */

    public boolean updateElevatorRequest(ElevatorRequest r) {
        if (generalInfo.updateRequest(r)) {
            try {
                updateRequest.setInt(1, r.getpLevel().getValue());
                updateRequest.setInt(2, r.getStaffID());
                updateRequest.setString(3, r.getMaintenanceType().toString());
                updateRequest.setInt(4, r.getRequestID());

                updateRequest.executeUpdate();
                return true;
            } catch (SQLException e) {
                log.info("Failed to update Elevator Request " + r.getRequestID());
                e.printStackTrace();
            }
        }
        return false;
    }
}
