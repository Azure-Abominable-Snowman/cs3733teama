package com.teama;

public final class Configuration {
    public static final String dbURL = "jdbc:derby:database;create=true";
    //public static final String interpStaff = "STAFF_INFO";
    public static final String nodeTable = "NODES";
    public static final String edgeTable = "EDGES";
    //public static final String requestTable = "REQUEST_TABLE";
    public static final String credentialsTable = "CREDENTIALS_TABLE";
    public static final String generalReqTable = "ALL_REQUESTS";
    public static final String generalStaffTable = "ALL_STAFF";
    public static String interpStaffTable = "INTERP_STAFF";
    public static String interpReqTable = "INTERP_REQUESTS";

    public static String elevatorStaffTable = "ELEVATOR_STAFF";
    public static String elevatorReqTable = "ELEVATOR_REQUESTS";
}
