package com.teama.requestsubsystem.transportationfeature;

import com.teama.mapsubsystem.data.Location;
import com.teama.requestsubsystem.*;
import com.teama.requestsubsystem.interpreterfeature.Language;

/**
 * Created by jakepardue on 12/4/17.
 */
public class TransportationRequest {
    private Request info; // will be filled in
    private RequestStatus r; // used in database
    private PriorityLevel p;
    private ModeOfTransportation requiredModeOfTransportation;
    private String endLocation;
    private int id = 0; // only set by db

    double serviceTime;

    public TransportationRequest(Request g, PriorityLevel p, ModeOfTransportation m, String s){
        this.info = g;
        this.p = p;
        this.requiredModeOfTransportation = m;
        this.r = RequestStatus.ASSIGNED;
        this.endLocation = s;
    }

    public TransportationRequest(Request g, PriorityLevel p, RequestStatus s, ModeOfTransportation m, int id, String str){
        this.info = g;
        this.r = s;
        this.p = p;
        this.requiredModeOfTransportation = m;
        this.id = id;
        this.endLocation = str;
    }

    public TransportationRequest(Request g,ModeOfTransportation m, double serviceTime) {
        this.info = g;
        this.requiredModeOfTransportation = m;
        this.serviceTime = serviceTime;
    }

    public Request getInfo() {
        return info;
    }

    protected void setRequestID(int ID) { //protected, only used by DB
        this.id = ID;
    }

    public PriorityLevel getPriority(){return p;}

    public void setPriority(PriorityLevel p){ this.p = p;}

    public Location getLocation() {
    return info.getLocation();
}

    public String getNote() {
        return info.getNote();
    }

    public RequestStatus getStatus() {
        return r;
    }

    public void setRequestStatus(RequestStatus r){  this.r = r;}

    public ModeOfTransportation getRequiredModeOfTransportation(){
        return requiredModeOfTransportation;
    }

    public void setRequiredModeOfTransportation(ModeOfTransportation m){
        this.requiredModeOfTransportation = m;
    }

    public String getEndLocation(){ return endLocation;}

    public void setEndLocation(String newLocation){ this.endLocation = newLocation;}

    public double getServiceTime() {
        return serviceTime;
    }

    public int getRequestID() { // protected, only used by DB
        return this.id;
    }

    public int getStaffID () {
        return info.getStaffID();
    }

    public void setStaffID(int ID) {
        info.setStaffID(ID);
    }

    public void setServiceTime(double time) {
        serviceTime = time;
    }

    public RequestType getRequestType(){
        return RequestType.TRANS;
    }

}


