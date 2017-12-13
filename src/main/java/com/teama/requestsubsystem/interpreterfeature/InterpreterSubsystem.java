package com.teama.requestsubsystem.interpreterfeature;

import com.teama.Configuration;
import com.teama.requestsubsystem.ReportSubject;
import com.teama.requestsubsystem.Request;
import com.teama.requestsubsystem.RequestDatabaseObserver;
import com.teama.requestsubsystem.RequestStatus;

import java.util.ArrayList;

/**
 * Created by aliss on 11/21/2017.
 */


// MASTER CLASS FOR INTERFACING WITH THE INTERPRETER REQUEST SUBSYSTEM; FACADE
public class InterpreterSubsystem implements ReportSubject {
    private InterpreterRequestDB requestDB;
    private InterpreterStaffDB staffDB;
    private ArrayList<RequestDatabaseObserver> observers; //different report tables
    private InterpreterRequest fulfilledRequest; // the "state"

    private InterpreterSubsystem() {
        requestDB = new InterpreterRequestDB(Configuration.dbURL, Configuration.generalReqTable, Configuration.interpReqTable);
        staffDB = new InterpreterStaffDB(Configuration.dbURL, Configuration.generalStaffTable, Configuration.interpStaffTable);
    }
    private static class InterpreterHelper {
        private static final InterpreterSubsystem _instance = new InterpreterSubsystem();
    }

    public static synchronized InterpreterSubsystem getInstance() {
        return InterpreterHelper._instance;
    }

    //adds an ArrayList
    // adds a given staff member to the database; done by admin
    public boolean addStaff(InterpreterStaff s) {
        return staffDB.addStaff(s);
    }

    // updates the information of a given staff member
    public boolean updateStaff(InterpreterStaff s) {
        return staffDB.updateStaff(s);
    }
    // deletes staff member by input ID
    public boolean removeStaff(int staffID) {
        return staffDB.deleteStaff(staffID);
    }

    // finds all qualified staff members based on the specified language
    public ArrayList<InterpreterStaff> findQualified(Language language) {
        return staffDB.findQualified(language);
    }

    // returns a list of all Interpreters in the system
    public ArrayList<InterpreterStaff> getAllStaff() {
        return staffDB.getAllInterpreterStaff();
    }

    // adds an Interpreter request to the database
    public InterpreterRequest addRequest(InterpreterRequest r) {
        return requestDB.addRequest(r);
    }

    public int addStaffGetLogin(InterpreterStaff s) {
        return staffDB.addStaffForLogin(s);
    }

    // deletes the selected request
    public boolean deleteRequest(int requestID) {
        return requestDB.deleteRequest(requestID);
    }

    // updates a request that has been assigned but not closed yet. cannot update closed requests
    public boolean updateRequest(InterpreterRequest r) {
        if (r.getStatus() != RequestStatus.CLOSED) {
            return requestDB.updateRequest(r);
        }
        return false;
    }

    public Request getGenericRequest(int id) {
        return requestDB.getRequest(id);
    }

    public InterpreterRequest getInterpreterRequest(int id) {
        return requestDB.getInterpreterRequest(id);
    }

    // when admin marks a request as fulfilled and fills in the generated form, the InterpRequest table and generic tables will be updated
    public boolean fulfillRequest(InterpreterRequest r) {
        Boolean fulfilled = requestDB.fulfillRequest(r);
        if (fulfilled) { // for report generation
            this.fulfilledRequest = r;
            notifyObservers();
        }
        return fulfilled;

    }

    // returns all requests filtered by Request Status (ASSGINED or CLOSED). See Enum
    public ArrayList<InterpreterRequest> getAllRequests(RequestStatus s) {
        return requestDB.getAllInterpreterRequests(s);
    }
    // returns all generic requests
    public ArrayList<Request> getAllGenericRequests(RequestStatus s) {
        return requestDB.getAllRequests(s);
    }

    // Gets a specific staff member
    public InterpreterStaff getIntepreterStaff(int staffID) { return staffDB.getInterpreterStaff(staffID); }

    public InterpreterStaff getStaff(int staffID){
        return staffDB.getInterpreterStaff(staffID);
    }

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



    public ArrayList<InterpreterRequest> getInterpreterRequestsByStaff(int staffID) {
        return requestDB.getInterpreterRequestsByStaff(staffID, RequestStatus.ASSIGNED);
    }
}
