package com.teama.requestsubsystem;

import com.teama.mapsubsystem.data.Location;

/**
 * Created by aliss on 11/20/2017.
 */
public class GenericRequestInfo {
    private Location location;
    //private RequestType reqType;
    int staffID; // unique staff ID of assigned staff member

    //private PriorityLevel priority;
    private String additionalInfo;

    public GenericRequestInfo(Location location, int staffID, String note) {
        this.location = location;
        //this.reqType = reqType;
        this.staffID = staffID;
        //this.priority = priority;
        this.additionalInfo = note; // for any additional details an admin may want to add about situation
    }

    public Location getLocation() {
        return location;
    }




    public int getStaffID() {
        return staffID;
    }
    public void setStaffID(int ID) {
        staffID = ID;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    ;

    /*
        public RequestType getReqType() {
                return reqType;
                }
        public void setReqType(RequestType reqType){
                this.reqType = reqType;
                }


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

    public void setNote(String note) {
        this.additionalInfo = note;
    }

}

/*

    @Override
    public String toString(){
            return reqType +"\n" + "\n"+location.toString()+"\n"+additionalInfo;
            }
            }

*/

