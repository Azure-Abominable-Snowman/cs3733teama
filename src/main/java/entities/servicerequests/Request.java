package entities.servicerequests;

import entities.Location;

public class Request {
    private Location location;
    private RequestType reqType;
    private PriorityLevel priority;
    private String note;
    private String id;
    private boolean fulfilled = false;

    public Request(String id, Location location, RequestType reqType, PriorityLevel priority, String note, boolean fulfilled) {
        this(id, location, reqType, priority, note);
        this.fulfilled = fulfilled;
    }

    public Request(String id, Location location, RequestType reqType, PriorityLevel priority, String note) {
        this.id = id;
        this.location = location;
        this.reqType = reqType;
        this.priority = priority;
        this.note = note;
    }

    public Location getLocation() {
        return location;
    }

    public RequestType getReqType() {
        return reqType;
    }

    public PriorityLevel getPriority() {
        return priority;
    }

    public String getNote() {
        return note;
    }

    public String getId() {
        return id;
    }

    public void setFulfilled() { fulfilled = true; }

    public boolean isFulfilled() { return  fulfilled; }

    public String toSQLValues() {
        String f = "FALSE";
        if(fulfilled) {
            f = "TRUE";
        }
        return "'"+getId()+"',"+location.getxCoord()+","+location.getyCoord()+",'"+location.getLevel()+"','"
                +location.getBuilding()+"','"+getReqType()+"','"+getPriority()+"','"+getNote()+"','"+f+"'";
    }
}
