package com.teama.requestsubsystem.securityfeature;

import com.teama.requestsubsystem.GenericRequest;
import com.teama.requestsubsystem.RequestStatus;
import com.teama.mapsubsystem.data.Location;

public class SecurityRequest {
    private GenericRequest info;
    private RequestStatus r;
    private SecurityType type;
    private SecurityLevel urgency;
    private int id = 0;

    double serviceTime;


    public SecurityRequest(GenericRequest gr, SecurityType type, SecurityLevel urgency) {
        this.info = gr;
        this.r = RequestStatus.ASSIGNED;
        this.type = type;
        this.urgency = urgency;
    }

    SecurityRequest(GenericRequest gr, RequestStatus rs, SecurityType type, int id) {
        this.info = gr;
        this.r = rs;
        this.type = type;
        this.id = id;
    }


    //possible methods for DB
    public GenericRequest getInfo() {return info;}
    protected void setRequestID(int ID) {this.id = ID;}

    public Location getLocation() {return info.getLocation();}

    public String getNote(){return info.getNote();}

    public RequestStatus getStatus() {return r;}

    public SecurityType getSecType() {return type;}
    public SecurityLevel getUrgency() {return urgency;}
    public double getServiceTime() {return serviceTime;}
    public int getRequestID() {return this.id;}
    public int getStaffID() {return info.getStaffID();}



    public void setServiceTime(double time) {serviceTime = time;}
    public void updateStatus(RequestStatus newStatus) {r = newStatus;}


}
