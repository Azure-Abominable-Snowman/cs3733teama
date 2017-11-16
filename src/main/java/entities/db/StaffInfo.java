package entities.db;

import controllers.SceneEngine;

public class StaffInfo {
    private static StaffInfo ourInstance = new StaffInfo();

    public static StaffInfo getInstance() {
        return ourInstance;
    }

    private StaffInfoDataSource stdb;

    private StaffInfo() {
        stdb = new JavaDatabaseStaffInfo(SceneEngine.getURL(), "STAFFINFO");
    }

    public StaffInfoDataSource getStaffInfoDB() {
        return stdb;
    }
}
