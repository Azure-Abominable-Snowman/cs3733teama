package com.teama.requestsubsystem.spiritualcarefeature;

import com.teama.Configuration;
import com.teama.requestsubsystem.Request;
import com.teama.requestsubsystem.RequestStatus;

import java.util.ArrayList;

/**
 * Created by aliss on 12/12/2017.
 */
public class SpiritualCareSubsystem {
    private SpiritualRequestDB requestDB;
    private SpiritualStaffDB staffDB;
    //private ArrayList<RequestDatabaseObserver> observers; //different report tables
    //private SpiritualCareRequest fulfilledRequest; // the "state"

    private SpiritualCareSubsystem() {
        requestDB = new SpiritualRequestDB(Configuration.dbURL, Configuration.generalReqTable, Configuration.spiritualReqTable);
        staffDB = new SpiritualStaffDB(Configuration.dbURL, Configuration.generalStaffTable, Configuration.spiritualStaffTable);
    }
    private static class SpiritualCareHelper {
        private static final SpiritualCareSubsystem _instance = new SpiritualCareSubsystem();
    }

    public static synchronized SpiritualCareSubsystem getInstance() {
        return SpiritualCareSubsystem.SpiritualCareHelper._instance;
    }

    //adds an ArrayList
    // adds a given staff member to the database; done by admin
    public boolean addStaff(SpiritualCareStaff s) {
        return staffDB.addStaff(s);
    }

    // updates the information of a given staff member
    public boolean updateStaff(SpiritualCareStaff s) {
        return staffDB.updateStaff(s);
    }
    // deletes staff member by input ID
    public boolean removeStaff(int staffID) {
        return staffDB.deleteStaff(staffID);
    }

    // finds all qualified staff members based on the specified language
    public ArrayList<SpiritualCareStaff> findQualified(Religion religion) {
        return staffDB.findQualified(religion);
    }

    // returns a list of all Interpreters in the system
    public ArrayList<SpiritualCareStaff> getAllStaff() {
        return staffDB.getAllSpiritualStaff();
    }

    // adds an Interpreter request to the database
    public SpiritualCareRequest addRequest(SpiritualCareRequest r) {
        return requestDB.addRequest(r);
    }

    // deletes the selected request
    public boolean deleteRequest(int requestID) {
        return requestDB.deleteRequest(requestID);
    }

    // updates a request that has been assigned but not closed yet. cannot update closed requests
    public boolean updateRequest(SpiritualCareRequest r) {
        if (r.getStatus() != RequestStatus.CLOSED) {
            return requestDB.updateRequest(r);
        }
        return false;
    }

    public Request getGenericRequest(int id) {
        return requestDB.getRequest(id);
    }

    public SpiritualCareRequest getSpiritualRequest(int id) {
        return requestDB.getSpiritualRequest(id);
    }

    // when admin marks a request as fulfilled and fills in the generated form, the InterpRequest table and generic tables will be updated
    public boolean fulfillRequest(SpiritualCareRequest r) {
        Boolean fulfilled = requestDB.fulfillRequest(r);
        /*
        if (fulfilled) { // for report generation
            this.fulfilledRequest = r;
            notifyObservers();
        }
        */
        return fulfilled;

    }

    // returns all requests filtered by Request Status (ASSGINED or CLOSED). See Enum
    public ArrayList<SpiritualCareRequest> getAllRequests(RequestStatus s) {
        return requestDB.getAllSpiritualRequests(s);
    }
    // returns all generic requests
    public ArrayList<Request> getAllGenericRequests(RequestStatus s) {
        return requestDB.getAllRequests(s);
    }

    // Gets a specific staff member
    public SpiritualCareStaff getSpiritualCareStaff(int staffID) { return staffDB.getSpiritualCareStaff(staffID); }

    public SpiritualCareStaff getStaff(int staffID){
        return staffDB.getSpiritualCareStaff(staffID);
    }
/*
    public void notifyObservers() {
        if(observers!=null) {
            for (RequestDatabaseObserver o : observers) {
                o.update();
            }
        }
    }

    public void attachObserver(RequestDatabaseObserver obs) {
        observers.add(obs);
    }

    public InterpreterRequest getReport() {
        return this.fulfilledRequest;
    }

*/

    public ArrayList<SpiritualCareRequest> getSpiritualCareRequestsByStaff(int staffID) {
        return requestDB.getSpiritualRequestsByStaff(staffID, RequestStatus.ASSIGNED);
    }
}
