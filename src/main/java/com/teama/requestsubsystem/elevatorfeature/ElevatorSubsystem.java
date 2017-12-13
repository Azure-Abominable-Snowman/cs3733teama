package com.teama.requestsubsystem.elevatorfeature;

import com.teama.Configuration;
import com.teama.requestsubsystem.Request;
import com.teama.requestsubsystem.RequestDatabaseObserver;
import com.teama.requestsubsystem.RequestStatus;

import java.util.ArrayList;

/**
 * Created by jakepardue on 12/8/17.
 */
public class ElevatorSubsystem {
    private ElevatorRequestDB requestDB;
    private ElevatorStaffDB staffDB;
    private ArrayList<RequestDatabaseObserver> observers; //different report tables
    private ElevatorRequest fulfilledRequest; // the "state"

    private ElevatorSubsystem() {
        requestDB = new ElevatorRequestDB(Configuration.dbURL, Configuration.generalReqTable, Configuration.elevatorReqTable);
        staffDB = new ElevatorStaffDB(Configuration.dbURL, Configuration.generalStaffTable, Configuration.elevatorStaffTable);
    }
    private static class ElevatorHelper {
        private static final ElevatorSubsystem _instance = new ElevatorSubsystem();
    }

    public static synchronized ElevatorSubsystem getInstance() {
        return ElevatorHelper._instance;
    }

    //adds an ArrayList
    // adds a given staff member to the database; done by admin
    public boolean addStaff(ElevatorStaff s) {
        return staffDB.addStaff(s);
    }

    // updates the information of a given staff member
    public boolean updateStaff(ElevatorStaff s) {
        return staffDB.updateStaff(s);
    }
    // deletes staff member by input ID
    public boolean removeStaff(int staffID) {
        return staffDB.deleteStaff(staffID);
    }

    // finds all qualified staff members based on the specified language
    public ArrayList<ElevatorStaff> findQualified(MaintenanceType requiredTask) {
        return staffDB.findQualified(requiredTask);
    }

    // returns a list of all Interpreters in the system
    public ArrayList<ElevatorStaff> getAllStaff() {
        return staffDB.getAllElevatorStaff();
    }

    // adds an Interpreter request to the database
    public ElevatorRequest addRequest(ElevatorRequest r) {
        return requestDB.addRequest(r);
    }

    // deletes the selected request
    public boolean deleteRequest(int requestID) {
        return requestDB.deleteRequest(requestID);
    }

    // updates a request that has been assigned but not closed yet. cannot update closed requests
    public boolean updateRequest(ElevatorRequest r) {
        if (r.getStatus() != RequestStatus.CLOSED) {
            return requestDB.updateRequest(r);
        }
        return false;
    }

    public Request getGenericRequest(int id) {
        return requestDB.getRequest(id);
    }

    public ElevatorRequest getElevatorRequest(int id) {
        return requestDB.getElevatorRequest(id);
    }

    // when admin marks a request as fulfilled and fills in the generated form, the InterpRequest table and generic tables will be updated
    public boolean fulfillRequest(ElevatorRequest r) {
        Boolean fulfilled = requestDB.fulfillRequest(r);
        if (fulfilled) { // for report generation
            this.fulfilledRequest = r;
            notifyObservers();
        }
        return fulfilled;

    }

    // returns all requests filtered by Request Status (ASSGINED or CLOSED). See Enum
    public ArrayList<ElevatorRequest> getAllRequests(RequestStatus s) {
        return requestDB.getAllElevatorRequests(s);
    }
    // returns all generic requests
    public ArrayList<Request> getAllGenericRequests(RequestStatus s) {
        return requestDB.getAllRequests(s);
    }

    // Gets a specific staff member
    public ElevatorStaff getElevatorStaff(int staffID) { return staffDB.getElevatorStaff(staffID); }

    public ElevatorStaff getStaff(int staffID){
        return staffDB.getElevatorStaff(staffID);
    }

    public void notifyObservers() {
        for (RequestDatabaseObserver o: observers){
            o.update();
        }
    }

    public void attachObserver(RequestDatabaseObserver obs) {
        observers.add(obs);
    }

    public ElevatorRequest getReport() {
        return this.fulfilledRequest;
    }
}
