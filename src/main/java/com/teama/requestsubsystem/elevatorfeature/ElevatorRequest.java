package com.teama.requestsubsystem.elevatorfeature;

import com.teama.mapsubsystem.data.Location;
import com.teama.requestsubsystem.PriorityLevel;
import com.teama.requestsubsystem.Request;
import com.teama.requestsubsystem.RequestStatus;
import com.teama.requestsubsystem.RequestType;
/**
 * Created by jakepardue on 12/8/17.
 */
public class ElevatorRequest implements Request {
    private Request info;
    private PriorityLevel pLevel;
    private MaintenanceType maintenanceType;
    private String brokenElevatorID;

    // THESE FIELDS WILL BE FILLED OUT AFTER THE SERVICE IS COMPLETED:
    double serviceTime;


    /**
     * for making an interpreter request that has not been fulfilled yet
     * @param g
     */
    public ElevatorRequest(Request g, PriorityLevel level, MaintenanceType m, String nodeID) {
        this.info = g;
        this.pLevel = level;
        this.serviceTime = 0;
        this.maintenanceType = m;
        this.brokenElevatorID = nodeID;
    }

    /**
     * for making an interpreter request that is fulfilled
     * @param g
     * @param serviceTime
     */

    public ElevatorRequest(Request g, double serviceTime) {
        this.info = g;
        this.serviceTime = serviceTime;
    }


    public Request getInfo() {
        return info;
    }
/*
protected void setRequestID(int ID) { //protected, only used by DB
    this.id = ID;
} // set by DB when request is entered into Request Table
*/

    public Location getLocation() {
        return info.getLocation();
    }
    public void setLocation(Location s) {
        info.setLocation(s);
    }

    public RequestStatus getStatus() {
    return this.info.getStatus();
}
    public void setStatus(RequestStatus s) {
        info.setStatus(s);
    }

    public RequestType getReqType(){
    return info.getReqType();
}
    public void setReqType(RequestType t) {
        info.setReqType(t);
    }


    public String getNote() {
        return info.getNote();
    }
    public void setNote(String note) {
        info.setNote(note);
    }

    public double getServiceTime() {
        return serviceTime;
    }
    public void setServiceTime(double time) {
        serviceTime = time;
    }

    public int getRequestID() { // protected, only used by DB
        return this.info.getRequestID();
    }

    public int getStaffID () {
        return info.getStaffID();
    }
    public void setStaffID(int ID) {
    info.setStaffID(ID);
}

    public MaintenanceType getMaintenanceType(){ return this.maintenanceType;}
    public void setMaintenanceType(MaintenanceType m){ this.maintenanceType = m;}


    public PriorityLevel getpLevel() { return pLevel; }
    public void setpLevel(PriorityLevel pLevel) { this.pLevel = pLevel; }

    // info to be added for report generation

    public void updateStatus(RequestStatus newStatus) {
        this.info.setStatus(newStatus);
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


    public String toString(){
        return "Type: Elevator \n" + "Location: " + getLocation().toString() + "\n" + "RequestStatus: "+ getReqType().toString() + "\n" + "Staff ID: "+ Integer.toString(getInfo().getStaffID());
    }

/*

    public void setRequestStatus(RequestStatus r){
        this.r = r;
    }

*/

}
