package com.teama.requestsubsystem;

/**
 * Created by aliss on 11/20/2017.
 */
public enum RequestStatus {
    //OPEN("Open"), // may need to add this..
    ASSIGNED("Assigned"),
    CLOSED("Closed");

    private final String status;
    RequestStatus(String s) {
        status = s;
    }

    public boolean equalsStatus(String other) {
        return status.equals(other);
    }
    public static RequestStatus getRequestStatus(String s) {
        for (RequestStatus r: RequestStatus.values()) {
            if (r.toString().equals(s)) {
                return r;
            }
        }
        throw new IllegalArgumentException("No such status " + s);
    }

    public String toString() {
        return this.status;
    }
}
