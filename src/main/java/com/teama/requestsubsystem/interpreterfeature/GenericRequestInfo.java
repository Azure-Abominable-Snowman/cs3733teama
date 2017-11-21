package com.teama.requestsubsystem.interpreterfeature;

import com.teama.mapsubsystem.data.Location;
import com.teama.requestsubsystem.RequestType;

/**
 * Created by aliss on 11/20/2017.
 */
public class GenericRequestInfo {
    private int ID; // set by database. originally 0
    private Location location;
    private RequestType reqType;
    int staffID; // unique staff ID of assigned staff member

    //private PriorityLevel priority;
    private String additionalInfo;

    public GenericRequestInfo(Location location, RequestType reqType, int staffID, String note) {
        this.ID = 0;
        this.location = location;
        this.reqType = reqType;
        this.staffID = staffID;
        //this.priority = priority;
        this.additionalInfo = note; // for any additional details an admin may want to add about situation
    }

    public GenericRequestInfo(int requestID, Location location, RequestType reqType, int staffID, String note) {
        this.location = location;
        this.reqType = reqType;
        this.staffID = staffID;
        //this.priority = priority;
        this.additionalInfo = note; // for any additional details an admin may want to add about situation
    }

    public Location getLocation() {
        return location;
    }

    void setID(int ID) { //protected, only used by DB
        this.ID = ID;
    } // set by DB when request is entered into Request Table



    public int getRequestID() { // protected, only used by DB
        return ID;
    }

    public int getStaffID() {
        return staffID;
    }

    public void setLocation(Location location){
        this.location = location;
    };

    public RequestType getReqType() {
            return reqType;
            }

    public void setReqType(RequestType reqType){
            this.reqType = reqType;
            }

            /*
    public PriorityLevel getPriority() {
            return priority;
            }

    public void setPriority(PriorityLevel priority){
            this.priority = priority;
            }
*/
    public String getNote() {
            return additionalInfo;
            }

    public void setNote(String note){
            this.additionalInfo = note;
            }





    @Override
    public String toString(){
            return reqType +"\n" + "\n"+location.toString()+"\n"+additionalInfo;
            }
            }



