package com.teama.requestsubsystem.interpreterfeature;

import com.teama.requestsubsystem.RequestStatus;

/**
 * Created by aliss on 11/20/2017.
 */
public class InterpreterRequest {
    GenericRequestInfo info; // will be filled in
    RequestStatus r; // used in database
    int familySize;
    Language requiredLanguage;


    // THESE FIELDS WILL BE FILLED OUT AFTER THE SERVICE IS COMPLETED:
    double serviceTime;
    TranslationType type; // which type of translation was required? whether written, verbal, and/or ASL


    public InterpreterRequest(GenericRequestInfo g, int familySize, Language requiredLanguage) {
        this.info = g;
        this.r = RequestStatus.ASSIGNED;
        this.requiredLanguage = requiredLanguage;
        this.familySize = familySize;

    }
    // methods used by DB to get info
    public GenericRequestInfo getInfo() {
        return info;
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


    void setRequestID(int ID) {
        info.setID(ID);
    }

    int getRequestID() {
        return info.getRequestID();
    }

    public int getStaffID () {
        return info.getRequestID();
    }

    // info to be added for report generation


    public void setServiceTime(double time) {
        serviceTime = time;
    }

    public void setTranslationTypes(TranslationType t) {
        this.type = t;
    }

    public void updateStatus(RequestStatus newStatus) {
        r = newStatus;
    }





}
