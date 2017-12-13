package com.teama.requestsubsystem.elevatorfeature;

import com.teama.requestsubsystem.GeneralStaffDB;
import com.teama.requestsubsystem.ServiceStaff;
import com.teama.requestsubsystem.StaffDataSource;
import com.teama.requestsubsystem.StaffType;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

/**
 * Created by aliss on 12/9/2017.
 */
public class ElevatorStaffDB implements  StaffDataSource {
    private StaffDataSource generalStaffDB;
    private final Logger log = Logger.getLogger(this.getClass().getPackage().getName());
    private String dbURL;
    private String elevatorStaffTable;
    private Connection conn = null;
    private Statement stmt = null;
    private PreparedStatement addStaff, removeStaff, updateStaffTable, getStaff, getQualifiedStaff, getAllStaff;
    //PreparedStatement addStaffLangTable, updateStaffLangTable, getQualifiedStaffLangs, removeStaffLangTable;

    public ElevatorStaffDB(String dbURL, String generalStaffTableName, String staffTableName) {
        this.dbURL = dbURL;
        this.elevatorStaffTable = staffTableName;
        generalStaffDB = new GeneralStaffDB(dbURL, generalStaffTableName);
        //this.generalStaffTable = generalStaffTable;
        try {
            Class.forName("org.apache.derby.jdbc.EmbeddedDriver").newInstance();
            // Get a connection
            conn = DriverManager.getConnection(this.dbURL);
        } catch (Exception except) {
            except.printStackTrace();
        }


        // Create the InterpreterStaff table as well
        // This table is always named <staffTable>_LANGUAGE
        try {
            stmt = conn.createStatement();
            System.out.println(elevatorStaffTable + " about to be created.");
            stmt.execute(
                    " CREATE TABLE " + elevatorStaffTable + " (" +
                            "STAFFID INTEGER NOT NULL, " +
                            "SPECIALIZATION VARCHAR(200) NOT NULL, " +
                            "FOREIGN KEY (STAFFID) REFERENCES " + generalStaffTableName + " (STAFFID), " +
                            "PRIMARY KEY (STAFFID,SPECIALIZATION)" +
                            ")"
            );
            log.info("Elevator Staff table created.");
            stmt.close();

        } catch (SQLException e) {
            //e.printStackTrace();
        }
/*
        DatabaseMetaData meta = null;
        try {
            meta = conn.getMetaData();
            ResultSet res = meta.getTables(null, null, elevatorStaffTable,
                    new String[] {"TABLE"});
            while (res.next()) {
                System.out.println(
                        "   "+res.getString("TABLE_CAT")
                                + ", "+res.getString("TABLE_SCHEM")
                                + ", "+res.getString("TABLE_NAME")
                                + ", "+res.getString("TABLE_TYPE")
                                + ", "+res.getString("REMARKS"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

*/

        try {
            addStaff = conn.prepareStatement("INSERT INTO " + elevatorStaffTable + "  (STAFFID, SPECIALIZATION) VALUES(?, ?)");
            getStaff = conn.prepareStatement("SELECT * FROM " + elevatorStaffTable + " WHERE STAFFID = ?");
            getAllStaff = conn.prepareStatement("SELECT * FROM " + elevatorStaffTable + " WHERE STAFFID = ?");
            getQualifiedStaff = conn.prepareStatement("SELECT * FROM " + elevatorStaffTable + " WHERE SPECIALIZATION = ?");
            removeStaff = conn.prepareStatement("DELETE FROM " + elevatorStaffTable + " WHERE STAFFID = ?");
            updateStaffTable = conn.prepareStatement("UPDATE " + elevatorStaffTable + " SET SPECIALIZATION = ? WHERE STAFFID = ?");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public ServiceStaff addStaff(ServiceStaff s) {
        return generalStaffDB.addStaff(s);
    }

    @Override
    public boolean updateStaff(ServiceStaff s) {
        return generalStaffDB.updateStaff(s);
    }

    /** Removes staff from the interpreter staff DB and from the generic staff DB
     * removeStaff = conn.prepareStatement("DELETE FROM " + elevatorStaffTable + " WHERE STAFFID = ?");

     * @param id
     * @return
     */
    @Override
    public boolean deleteStaff(int id) {
        //DELETE FROM THIS TABLE FIRST, THEN FROM GENERAL TABLE (FK CONSTRAINS)
        try {
            removeStaff.setInt(1, id);
            removeStaff.executeUpdate();
            log.info("Removed all interpreter information related to Elevator staff with ID:  " + id);
        } catch (SQLException e) {
            e.printStackTrace();
            log.info("Failed to remove specific staff member.");
            return false;
        }
        if (generalStaffDB.deleteStaff(id)) {
            return true;
        }
        return false;
    }
    @Override
    public ArrayList<ServiceStaff> getStaffByType(StaffType t) {
        return generalStaffDB.getStaffByType(t);
    }
    @Override
    public ArrayList<ServiceStaff> getAllStaff() {
        return generalStaffDB.getAllStaff();
    }
    @Override
    public ServiceStaff getStaff(int staffID) {
        return generalStaffDB.getStaff(staffID);
    }

    /**
     getStaff = conn.prepareStatement("SELECT * FROM " + elevatorStaffTable + " WHERE STAFFID = ?");

     * @param staffID
     * @return
     */
    public ElevatorStaff getElevatorStaff(int staffID) {
        ServiceStaff general = generalStaffDB.getStaff(staffID);
        ElevatorStaff gotten = null;
        if (general != null) {
            try {
                getStaff.setInt(1, staffID);
                ResultSet found = getStaff.executeQuery();
                Set<MaintenanceType> skills = new HashSet<>();
                while (found.next()) {
                    System.out.println((MaintenanceType.getMaintenanceType(found.getString("SPECIALIZATION"))));
                    MaintenanceType skill = MaintenanceType.getMaintenanceType((found.getString("SPECIALIZATION")));
                    skills.add(skill);
                }
               ElevatorStaff elev = new ElevatorStaff(general, skills);
                if (elev != null && elev.getStaffID() != 0 && !skills.isEmpty()) {
                    gotten = elev;
                    log.info("Successfully found elevator staff with ID " + staffID);
                }
            } catch (SQLException e) {
                e.printStackTrace();
                log.info("Problem getting elevator staff with ID " + staffID);
            }
        }
        return gotten;
    }

    /**
     *  returns a list of ElevatorStaff who are qualified to perform the needed elevator maintenance work
     *  getQualifiedStaff = conn.prepareStatement("SELECT * FROM " + elevatorStaffTable + " WHERE SPECIALIZATION = ?");

     * @param requiredTask
     * @return
     */
    public ArrayList<ElevatorStaff> findQualified(MaintenanceType requiredTask) {
        ArrayList<ElevatorStaff> foundStaff = new ArrayList<ElevatorStaff>();
        // Use all of the attributes to build a query for the database
        try {
            //System.out.println(requiredTask.toString());
            getQualifiedStaff.setString(1, requiredTask.toString());
            ResultSet rs = getQualifiedStaff.executeQuery();
            while (rs.next()) {
                ElevatorStaff found = null;
                int staffID = rs.getInt("STAFFID");
                ElevatorStaff allInfo = getElevatorStaff(staffID);
                if (allInfo != null) {
                    foundStaff.add(allInfo);
                    log.info("Found qualified Elevator repairman who can perform task: " + requiredTask.toString());
                }
                else {
                    log.info("Problem finding qualified Elevator staff for task. Exiting.");
                    return null;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return foundStaff;
    }


    /**
     * adds a new Elevator Staff to th eDB
     * addStaff = conn.prepareStatement("INSERT INTO " + elevatorStaffTable + "  (STAFFID, SPECIALIZATION) VALUES(?, ?)");

     * @param s
     * @return
     */
    public boolean addStaff(ElevatorStaff s) {
        ServiceStaff genInfo = generalStaffDB.addStaff(s); // add general info first
        if (genInfo != null) {
            try {
                addStaff.setInt(1, genInfo.getStaffID());
                //addStaff.setString(2, );
                for (MaintenanceType m: s.getSpecialization()) {
                    addStaff.setString(2, m.toString());
                    System.out.println(m.toString());
                    addStaff.executeUpdate();
                }
                log.info("All elevator repair skills of staff member added to the Elevator Table successfully.");
                log.info("Staff member added successfully to Elevator Table.");

                //addStaff.setString(7, s.); //TODO: CHANGE THIS IF WE ACTUALLY IMPLEMENT WORK HOURS, OR DELETE
                return true;
            } catch (SQLException e) {
                log.severe("Failed to add Staff Member to Elevator Table.");
                e.printStackTrace();
                return false;
            }
        }
        log.severe("Problem with adding the ElevatorStaff to the General Staff DB. General Staff Info is NULL.");
        return false;
    }

    /**
     * returns all the Elevator  in the table
     * @return
     */
    public ArrayList<ElevatorStaff> getAllElevatorStaff() {
        ArrayList<ServiceStaff> allStaff = generalStaffDB.getStaffByType(StaffType.ELEVATOR);
        ArrayList<ElevatorStaff> allElevs = new ArrayList<>();
        for (ServiceStaff s: allStaff) {
            if (s != null) {
               ElevatorStaff found = getElevatorStaff(s.getStaffID());
                if (found != null) {
                    allElevs.add(found);
                }
            }
        }
        return allElevs;
    }

    /**
     updateStaffTable = conn.prepareStatement("UPDATE " + elevatorStaffTable + " SET SPECIALIZATION = ? WHERE STAFFID = ?");
     addStaff = conn.prepareStatement("INSERT INTO " + elevatorStaffTable + "  (STAFFID, SPECIALIZATION) VALUES(?, ?)");

     removeStaff = conn.prepareStatement("DELETE FROM " + elevatorStaffTable + " WHERE STAFFID = ?");

     * @param s
     * @return
     */
    public boolean updateStaff(ElevatorStaff s) {
        if (generalStaffDB.updateStaff(s)) {
            try {
                // FIRST REMOVE THE CURRENT ENTRIES
                removeStaff.setInt(1, s.getStaffID());
                removeStaff.executeUpdate();
                log.info("Deleted all current skill entries for update.");
                addStaff.setInt(1, s.getStaffID());
                for (MaintenanceType m : s.getSpecialization()) {
                    addStaff.setString(2, m.toString());
                    addStaff.executeUpdate();
                }

                log.info("Elevator Repairman with ID " + s.getStaffID() + " successfully updated.");
                return true;
            } catch (SQLException e) {
                log.info("Failed to update Elevator Staff with id " + s.getStaffID());
                e.printStackTrace();
            }
        }
        return false;
    }
    /*
               getStaff = conn.prepareStatement("SELECT T1.*, T2.* FROM " + staffTable + " AS T1, " +
                    elevatorStaffTable + " AS T2 WHERE T1.STAFFID = ? AND T1.STAFFID = T2.STAFFID");

     */


/*
            removeStaffReqTable = conn.prepareStatement("DELETE FROM " + staffTable + " WHERE STAFFID = ?");

 */




    public void close() {
        try {
            conn.close();
            generalStaffDB.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
