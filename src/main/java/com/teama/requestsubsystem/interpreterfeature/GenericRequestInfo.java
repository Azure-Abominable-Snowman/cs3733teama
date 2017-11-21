package com.teama.requestsubsystem.interpreterfeature;

import com.teama.mapsubsystem.data.Location;
import com.teama.requestsubsystem.PriorityLevel;
import com.teama.requestsubsystem.RequestType;

/**
 * Created by aliss on 11/20/2017.
 */
public class GenericRequestInfo {
    private int ID; // set by database. originally 0
    private Location location;
    private RequestType reqType;
    //private PriorityLevel priority;
    private String additionalInfo;

    public GenericRequestInfo(Location location, RequestType reqType, PriorityLevel priority, String note) {
        this.location = location;
        this.reqType = reqType;
        //this.priority = priority;
        this.additionalInfo = note; // for any additional details an admin may want to add about situation
    }

    public Location getLocation() {
        return location;
    }

    void setID(int ID) { //protected, only used by DB
        this.ID = ID;
    }

    int getID() { // protected, only used by DB
        return ID;
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



