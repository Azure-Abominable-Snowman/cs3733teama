package com.teama.requestsubsystem;

/**
 * Created by jakepardue on 11/14/17.
 */

import com.teama.controllers.SceneEngine;

public class RequestTable {

    static RequestTable instance = null;
    private ServiceRequestDataSource reqData;

    //the request data table
    private RequestTable(String dbURL, String requestTable) {
        reqData = new JavaDBServiceRequestData(dbURL, requestTable);
    }

    ;

    public static synchronized RequestTable getInstance() {
        if (instance == null)
            instance = new RequestTable(SceneEngine.getURL(), "requestTable");
        return instance;
    }
    //TODO add method for getting ArrayList<Request> from the request database

    public ServiceRequestDataSource getReqTable() {
        return reqData;
    }
    public void submitRequest(Request request) {
        reqData.submitRequest(request);
    }
}
