package com.teama.requestsubsystem.interpreterfeature;

import com.teama.mapsubsystem.data.Location;
import com.teama.requestsubsystem.Request;
import com.teama.requestsubsystem.RequestStatus;
import com.teama.requestsubsystem.RequestType;

/**
 * Created by aliss on 11/20/2017.
 */
public class InterpreterRequest implements Request {
    private Request info; // will be filled in
    private int familySize;
    private Language requiredLanguage;
// may want to add a filter for CertificationType in the future

    // THESE FIELDS WILL BE FILLED OUT AFTER THE SERVICE IS COMPLETED:
    double serviceTime;
    TranslationType type; // which type of translation was required? whether written, verbal, and/or ASL

    /**
     * for making an interpreter request that has not been fulfilled yet
     * @param g
     * @param requiredLanguage
     */
    public InterpreterRequest(Request g, Language requiredLanguage) {
        this.info = g;
        this.requiredLanguage = requiredLanguage;
        this.familySize = 0;
        this.serviceTime = 0;
        this.type = null;
    }

    /**
     * for making an interpreter request that is fulfilled
     * @param g
     * @param requiredLanguage
     * @param familySize
     * @param serviceTime
     * @param type
     */

    public InterpreterRequest(Request g, Language requiredLanguage, int familySize, double serviceTime, TranslationType type) {
        this.info = g;
        this.requiredLanguage = requiredLanguage;
        this.familySize = familySize;
        this.serviceTime = serviceTime;
        this.type = type;
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

    public void setStatus(RequestStatus s) {
        info.setStatus(s);
    }

    public void setReqType(RequestType t) {
        info.setReqType(t);
    }
    public RequestType getReqType(){
        return info.getReqType();
    }

    public String getNote() {
        return info.getNote();
    }

    public void setNote(String note) {
        info.setNote(note);
    }

    public RequestStatus getStatus() {
        return this.info.getStatus();
    }

    public int getFamilySize() {
        return familySize;
    }

    public Language getRequiredLanguage() {
        return requiredLanguage;
    }

    public double getServiceTime() {
        return serviceTime;
    }

    public TranslationType getTranslType() {
        return type;
    }

    public int getRequestID() { // protected, only used by DB
        return this.info.getRequestID();
    }

    public int getStaffID () {
        return info.getStaffID();
    }

    // info to be added for report generation

    public void setStaffID(int ID) {
        info.setStaffID(ID);
    }
    public void setServiceTime(double time) {
        serviceTime = time;
    }

    public void setTranslationTypes(TranslationType t) {
        this.type = t;
    }

    public TranslationType getTranslationType() {
        return this.type;
    }

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
/*
    public String toString(){
        return "Type: Interpreter \n" + "Location: " + getLocation().toString() + "\n" + "RequestStatus: "+ r.toString() + "\n" + "Staff ID: "+ Integer.toString(getInfo().getStaffID());
    }

    public void setRequestStatus(RequestStatus r){
        this.r = r;
    }


*/


}
