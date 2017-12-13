package com.teama.requestsubsystem.spiritualcarefeature;

import com.teama.mapsubsystem.data.Location;
import com.teama.requestsubsystem.Request;
import com.teama.requestsubsystem.RequestStatus;
import com.teama.requestsubsystem.RequestType;

import java.util.Date;

/**
 * Created by jakepardue on 12/10/17.
 */
public class SpiritualCareRequest implements Request {
    private Request request;
    private Religion religion;
    private SpiritualService service;
    private Date date;

    public SpiritualCareRequest( Request r, Religion rel, SpiritualService s, Date date){
        this.request = r;
        this.religion = rel;
        this.service = s;
        this.date = date;
    }

    @Override
    public void add() {

    }

    @Override
    public void remove() {

    }

    @Override
    public void fulfill() {

    }

    @Override
    public void generateReport() {

    }

    @Override
    public Location getLocation() {
        return request.getLocation();
    }

    @Override
    public void setLocation(Location location) {
        request.setLocation(location);

    }

    @Override
    public int getStaffID() {
        return request.getStaffID();
    }

    @Override
    public void setStaffID(int staffID) {
        request.setStaffID(staffID);

    }

    @Override
    public int getRequestID() {
        return request.getRequestID();
    }

    @Override
    public RequestType getReqType() {
        return request.getReqType();
    }

    @Override
    public void setReqType(RequestType r) {
        request.setReqType(r);
    }

    @Override
    public RequestStatus getStatus() {
        return request.getStatus();
    }

    @Override
    public void setStatus(RequestStatus s) {
        request.setStatus(s);
    }

    @Override
    public String getNote() {
        return request.getNote();
    }

    @Override
    public void setNote(String note) {
        request.setNote(note);
    }

    public Religion getReligion(){
        return religion;
    }

    public void setReligion(Religion religion){
        this.religion = religion;
    }

    public SpiritualService getSpiritualService(){
        return service;
    }

    public void setSpiritualService(SpiritualService s){
        this.service = s;
    }

    public Date getDate(){
        return date;
    }

    public void setDate(Date d){
        this.date = d;
    }
}
