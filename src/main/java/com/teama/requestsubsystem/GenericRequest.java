package com.teama.requestsubsystem;

import com.teama.mapsubsystem.data.Location;

/**
 * Created by aliss on 11/20/2017.
 */
public class GenericRequest  implements Request{
    private int requestID; // ID of request
    private int staffID; // ID of assigned Staff member
    private Location location; // (end) location of request
    private RequestType reqType; // type of request
    private RequestStatus status; // Open, Assigned, or Closed
    private String additionalInfo; // note field

    public GenericRequest(Location location, int staffID, RequestType type, RequestStatus s, String note) {
        this.requestID = 0;
        this.location = location;
        this.reqType = type;
        this.status = s;
        this.staffID = staffID;
        this.additionalInfo = note; // for any additional details an admin may want to add about situation
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public int getStaffID() {
        return staffID;
    }

    public void setStaffID(int staffID) {
        this.staffID = staffID;
    }

    // used by DB to set the ID
    void setRequestID(int ID) {
        this.requestID = ID;
    }

    public int getRequestID() {
        return this.requestID;
    }

    public RequestType getReqType() {
        return this.reqType;
    }

    public void setReqType(RequestType r) {
        this.reqType = r;
    }

    public RequestStatus getStatus() {
        return this.status;
    }

    public void setStatus(RequestStatus s) {
        this.status = s;
    }

    public String getNote() {
        return additionalInfo;
    }

    public void setNote(String note) {
        this.additionalInfo = note;
    }



    public void add() {
        //TODO

    }
    public void remove() {
        //TODO

    }
    public void fulfill() {
        //TODO

    }

    public void generateReport() {
        //TODO

    }



}

/*

    @Override
    public String toString(){
            return reqType +"\n" + "\n"+location.toString()+"\n"+additionalInfo;
            }
            }

*/

