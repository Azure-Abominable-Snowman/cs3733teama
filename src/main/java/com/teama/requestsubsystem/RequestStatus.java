package com.teama.requestsubsystem;

/**
 * Created by aliss on 11/20/2017.
 */
public enum RequestStatus {
    //OPEN("Open"),
    ASSIGNED("Assigned"),
    CLOSED("Closed");

    private final String status;
    private RequestStatus(String s) {
        status = s;
    }
    public boolean equalsStatus(String other) {
        return status.equals(other);
    }
    public String toString() {
        return this.status;
    }
}
