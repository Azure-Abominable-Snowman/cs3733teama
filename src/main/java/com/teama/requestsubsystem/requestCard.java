package com.teama.requestsubsystem;

public class requestCard {
    int requestID;
    int staffID;
    int xCOORD;
    int yCOORD;
    String LVL;
    String Building;
    String reqType;
    String status;
    String note;

    public requestCard(int requestID, int staffID, int xCOORD, int yCOORD, String LVL, String building, String reqType, String status, String note) {
        this.requestID = requestID;
        this.staffID = staffID;
        this.xCOORD = xCOORD;
        this.yCOORD = yCOORD;
        this.LVL = LVL;
        Building = building;
        this.reqType = reqType;
        this.status = status;
        this.note = note;
    }

    public int getRequestID(){
        return requestID;
    }

    public int getStaffID() {
        return staffID;
    }

    public int getxCOORD() {
        return xCOORD;
    }

    public int getyCOORD() {
        return yCOORD;
    }

    public String getLVL() {
        return LVL;
    }

    public String getBuilding() {
        return Building;
    }

    public String getReqType() {
        return reqType;
    }

    public String getStatus() {
        return status;
    }

    public String getNote() {
        return note;
    }


}
