package com.teama.requestsubsystem;

import com.teama.controllers.SceneEngine;
import com.teama.requestsubsystem.data.JavaDatabaseStaffInfo;
import com.teama.requestsubsystem.data.StaffInfoDataSource;

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
