package com.teama.requestsubsystem;

import java.util.ArrayList;

/**
 * Created by aliss on 12/2/2017.
 */
public interface ServiceRequestDataSource {
    Request addRequest(Request request); // returns ID of created request -- make sure this is not 0
    boolean deleteRequest(int requestID);
    Request getRequest(int requestID);
    ArrayList<Request> getAllRequests(RequestStatus status); // get all requests by given RequestStatus
    ArrayList<Request> getAllRequests(RequestStatus status, RequestType type); // get all requests by given RequestStatus and Request Type
    boolean fulfillRequest(Request r); // generate report for given request; mark request as closed
    boolean updateRequest(Request r);
    void close();
}
