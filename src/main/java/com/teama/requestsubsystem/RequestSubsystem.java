package com.teama.requestsubsystem;

public class RequestSubsystem {
    private static RequestSubsystem ourInstance = new RequestSubsystem();

    public static RequestSubsystem getInstance() {
        return ourInstance;
    }

    private RequestSubsystem() {

    }

    public void makeRequest() {

    }

    public void fulfillRequest() {

    }
}
