package com.teama.requestsubsystem;

public enum RequestType {
    FOOD("Food"), SEC("Security"), TRANS("Transportation"), INTR("Interpreter"), MAIN("Maintenance");

    private final String value;

    private RequestType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public String toString(){
        return this.value;
    }

}
