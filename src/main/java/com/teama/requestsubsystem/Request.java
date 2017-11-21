package com.teama.requestsubsystem;

import com.teama.requestsubsystem.interpreterfeature.GenericRequestInfo;

public abstract class Request {
    RequestStatus status; // see Enum. For tracking whether request is open, closed, or assigned -> will be used to synchronize generation of reports.
    GenericRequestInfo info; // for storing basic information shared across all requests - location, priority, request type, etc.

    public Request() { // will probably never use this
        this.status = RequestStatus.OPEN;
        info = null;
    }

    public Request(RequestStatus status, GenericRequestInfo info) {
        this.status = status;
        this.info = info;
    }

    public RequestStatus getStatus() {
        return status;
    }
    public void updateStatus(RequestStatus updated) {
        status = updated;
    }
    public GenericRequestInfo getBasicRequestInfo() {
        return info;
    }

    public String toSQLValues() {
        String reqStatus = status.toString();
        String sqlString = info.toSQLValues() + reqStatus+"'";
        return sqlString;
    }

    @Override
    public String toString(){
        return info.toString();
    }

    public abstract void fillRequest(); // each specific Request (InterpreterRequest, SecurityRequest) will have its own filling methods. Request will be loaded from database with all fields filled except those the assignee must fill out.


}
