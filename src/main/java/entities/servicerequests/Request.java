package entities.servicerequests;

import entities.Location;
import entities.MapNode;

public class Request {
    private Location location;
    private Enum<RequestType> reqType;
    private Enum<PriorityLevel> priority;
    private String note;
    private String id;
    private boolean fulfilled = false;

    public Request(String id, Location location, RequestType reqType, PriorityLevel priority, String note, boolean fulfilled) {
        this(id, location, reqType, priority, note);
        this.fulfilled = fulfilled;
    }

    public Request(String id, Location location, Enum<RequestType> reqType, Enum<PriorityLevel> priority, String note) {
        this.id = id;
        this.location = location;
        this.reqType = reqType;
        this.priority = priority;
        this.note = note;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location){
        this.location = location;
    };

    public Enum<RequestType> getReqType() {
        return reqType;
    }

    public void setReqType(RequestType reqType){
        this.reqType = reqType;
    }

    public Enum<PriorityLevel> getPriority() {
        return priority;
    }

    public void setPriority(PriorityLevel priority){
        this.priority = priority;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note){
        this.note = note;
    }

    public String getId() {
        return id;
    }

    public void setId(String id){
        this.id = id;
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
