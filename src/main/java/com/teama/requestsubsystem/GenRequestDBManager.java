package com.teama.requestsubsystem;

import com.teama.Configuration;

/**
 * Created by aliss on 12/2/2017.
 */
public class GenRequestDBManager {
    private static GenRequestDBManager ourInstance = new GenRequestDBManager();

    public static synchronized GenRequestDBManager getInstance() {
        return ourInstance;
    }


    private ServiceRequestDataSource reqDB;
    private StaffDataSource staffDB;
    private String staffTable = "ALL_STAFF";
    private String reqTable = "ALL_REQUESTS";
    private GenRequestDBManager() {
        this.reqDB = new GeneralRequestDB(Configuration.dbURL, Configuration.generalReqTable);
        this.staffDB = new GeneralStaffDB(Configuration.dbURL, Configuration.generalStaffTable);
    }

    public synchronized ServiceRequestDataSource getGenericRequestDB() {
        return this.reqDB;
    }

    public synchronized StaffDataSource getGenericStaffDB() {
        return this.staffDB;
    }


}
