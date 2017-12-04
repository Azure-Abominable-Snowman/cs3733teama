package com.teama.requestsubsystem;

import java.util.ArrayList;

/**
 * Created by aliss on 11/22/2017.
 */
public interface StaffDataSource {

    ServiceStaff addStaff(ServiceStaff s);
    boolean updateStaff(ServiceStaff s);
    boolean deleteStaff(int id);
    ServiceStaff getStaff(int staffID);
    ArrayList<ServiceStaff> getAllStaff();
    ArrayList<ServiceStaff> getStaffByType(StaffType t);
    void close();
}
