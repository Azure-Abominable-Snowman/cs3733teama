package com.teama.requestsubsystem.interpreterfeature;

import com.teama.mapsubsystem.data.Location;
import com.teama.requestsubsystem.GenericRequestInfo;
import com.teama.requestsubsystem.RequestStatus;

/**
 * Created by aliss on 11/20/2017.
 */
public class InterpreterRequest {
    private GenericRequestInfo info; // will be filled in
    private RequestStatus r; // used in database
    private int familySize;
    private Language requiredLanguage;
    private int id = 0; // only set by db

// may want to add a filter for CertificationType in the future

    // THESE FIELDS WILL BE FILLED OUT AFTER THE SERVICE IS COMPLETED:
    double serviceTime;
    TranslationType type; // which type of translation was required? whether written, verbal, and/or ASL


    public InterpreterRequest(GenericRequestInfo g, int familySize, Language requiredLanguage) {
        this.info = g;
        this.r = RequestStatus.OPEN;
        this.requiredLanguage = requiredLanguage;
        this.familySize = familySize;

    }

    // ONLY USED BY INTERPRETER REQUEST DB
    InterpreterRequest(GenericRequestInfo g, RequestStatus s, int familySize, Language requiredLanguage, int id) {
        this.info = g;
        this.r = s;
        this.requiredLanguage = requiredLanguage;
        this.familySize = familySize;
        this.id = id;
    }


    // methods used by DB to get info
    public GenericRequestInfo getInfo() {
        return info;
    }
    protected void setRequestID(int ID) { //protected, only used by DB
        this.id = ID;
    } // set by DB when request is entered into Request Table


    public Location getLocation() {
        return info.getLocation();
    }

    public String getNote() {
        return info.getNote();
    }
    public RequestStatus getStatus() {
        return r;
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
        return this.id;
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

    public void updateStatus(RequestStatus newStatus) {
        r = newStatus;
    }

    public String toString(){
        return "Type: Interpreter \n" + "Location: " + getLocation().toString() + "\n" + "RequestStatus: "+ r.toString() + "\n" + "Staff ID: "+ Integer.toString(getInfo().getStaffID());
    }





}
