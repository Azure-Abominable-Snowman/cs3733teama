package com.teama.requestsubsystem;

import com.teama.mapsubsystem.data.Location;

/**
 * Created by aliss on 12/2/2017.
 */
public interface Request {
    //public String toSQLValues();
    public void add();
    public void remove();
    public void fulfill();
    public void generateReport();
    public Location getLocation();

    public void setLocation(Location location);

    public int getStaffID();

    public void setStaffID(int staffID);

    // used by DB to set the ID
    public int getRequestID();

    public RequestType getReqType();

    public void setReqType(RequestType r);

    public RequestStatus getStatus();

    public void setStatus(RequestStatus s);

    public String getNote();

    public void setNote(String note);
}
