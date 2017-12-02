package com.teama.requestsubsystem.interpreterfeature;

import com.teama.requestsubsystem.RequestStatus;

import java.util.ArrayList;

/**
 * Created by aliss on 11/22/2017.
 */
public interface InterpreterRequestInfoSource {
    boolean addRequest(InterpreterRequest request);
    boolean deleteRequest(int requestID);
    InterpreterRequest getRequest(int requestID);
    ArrayList<InterpreterRequest> getAllRequests(RequestStatus status); // get all requests by given RequestStatus
    boolean fulfillRequest(InterpreterRequest r); // generate report for given request; mark request as closed
    boolean updateRequest(InterpreterRequest r);
    void close();

}
