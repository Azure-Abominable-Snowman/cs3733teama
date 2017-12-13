package com.teama.requestsubsystem;

// for use by controller to generate appropriate form
public enum RequestType {
    FOOD("Food"), SEC("Security"), TRANS("Transportation"), INTR("Interpreter"), MAIN("Elevator Maintenance"),
    SPIRITUAL("Spiritual Service");

    private final String value;

    private RequestType(String value) {
        this.value = value;
    }

    public String toString(){
        return this.value;
    }

    public static RequestType getRequestType(String s) {
        for (RequestType r: RequestType.values()) {
            if (r.toString().equals(s)) {
                return r;
            }
        }
        throw new IllegalArgumentException("No such request type " + s);
    }

}
