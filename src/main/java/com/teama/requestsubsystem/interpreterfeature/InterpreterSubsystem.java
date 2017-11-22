package com.teama.requestsubsystem.interpreterfeature;

import com.teama.Configuration;
import com.teama.requestsubsystem.RequestStatus;

import java.util.ArrayList;

/**
 * Created by aliss on 11/21/2017.
 */


// MASTER CLASS FOR INTERFACING WITH THE INTERPRETER REQUEST SUBSYSTEM; FACADE
public class InterpreterSubsystem {
    private InterpreterRequestDB requestDB;
    private InterpreterStaffDB staffDB;
    private InterpreterSubsystem() {
        requestDB = new InterpreterRequestDB(Configuration.dbURL);
        staffDB = new InterpreterStaffDB(Configuration.dbURL);
    }
    private static class InterpreterHelper {
        private static final InterpreterSubsystem _instance = new InterpreterSubsystem();
    }

    public InterpreterSubsystem getInstance() {
        return InterpreterHelper._instance;
    }

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

    // finds qualified staff member based on the specified language
    public InterpreterStaff findQualfied(Language language) {
        return staffDB.findQualified(language);
    }

    // TODO
    // returns a list of all Interpreters in the system
    public ArrayList<InterpreterStaff> getAllStaff() {
        return null;
    }

    // adds an Interpreter request to the database
    public boolean addRequest(InterpreterRequest r) {
        return requestDB.addRequest(r);
    }

    // cancels the selected request
    public boolean cancelRequest(int requestID) {
        return requestDB.cancelRequest(requestID);
    }

    // updates a request that has been assigned but not closed yet. cannot update closed requests
    public boolean updateRequest(InterpreterRequest r) {
        return requestDB.updateRequest(r);
    }

    // when admin marks a request as fulfilled and fills in the generated form, the InterpReqest table and Report tables will be updated
    public boolean fulfillRequest(InterpreterRequest r) {
        return requestDB.fulfillRequest(r);
    }

    // returns all requests filtered by Request Status (ASSGINED or CLOSED). See Enum
    public ArrayList<InterpreterRequest> getAllRequests(RequestStatus s) {
        return requestDB.getAllRequests(s);
    }


}
