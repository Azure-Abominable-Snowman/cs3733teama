package com.teama.requestsubsystem;

/**
 * Created by aliss on 11/22/2017.
 */
public interface StaffDataSource {

    boolean addStaff(ServiceStaff s);
    boolean updateStaff(ServiceStaff s);
    boolean deleteStaff(int id);
    ServiceStaff getStaff(int staffID);

}
