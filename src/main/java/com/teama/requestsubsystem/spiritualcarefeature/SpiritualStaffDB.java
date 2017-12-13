package com.teama.requestsubsystem.spiritualcarefeature;

import com.teama.requestsubsystem.GeneralStaffDB;
import com.teama.requestsubsystem.ServiceStaff;
import com.teama.requestsubsystem.StaffDataSource;
import com.teama.requestsubsystem.StaffType;

import java.sql.*;
import java.util.ArrayList;
import java.util.logging.Logger;

/**
 * Created by aliss on 12/10/2017.
 */

public class SpiritualStaffDB implements StaffDataSource {
    private StaffDataSource generalStaffDB;
    private final Logger log = Logger.getLogger(this.getClass().getPackage().getName());
    private String dbURL;
    private String staffTable;
    private Connection conn = null;
    private Statement stmt = null;
    private PreparedStatement addStaff, removeStaff, updateStaffTable, getStaff, getQualifiedStaff, getAllStaff;
    //PreparedStatement addStaffLangTable, updateStaffLangTable, getQualifiedStaffLangs, removeStaffLangTable;

    public SpiritualStaffDB(String dbURL, String generalStaffTableName, String staffTableName) {
        this.dbURL = dbURL;
        this. staffTable = staffTableName;
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
            System.out.println( staffTable + " about to be created.");
            stmt.execute(
                    " CREATE TABLE " +  staffTable + " (" +
                            "STAFFID INTEGER NOT NULL, " +
                            "RELIGION VARCHAR(100) NOT NULL, " +
                            "FOREIGN KEY (STAFFID) REFERENCES " + generalStaffTableName + " (STAFFID), " +
                            "PRIMARY KEY (STAFFID)" +
                            ")"
            );
            log.info("Spiritual Staff table created.");
            stmt.close();

        } catch (SQLException e) {
            //e.printStackTrace();
        }
/*
        DatabaseMetaData meta = null;
        try {
            meta = conn.getMetaData();
            ResultSet res = meta.getTables(null, null, interpStaffTable,
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
            addStaff = conn.prepareStatement("INSERT INTO " +  staffTable + "  (STAFFID, RELIGION VALUES(?, ?)");
            getStaff = conn.prepareStatement("SELECT * FROM " +  staffTable + " WHERE STAFFID = ?");
            getAllStaff = conn.prepareStatement("SELECT * FROM " +  staffTable + " WHERE STAFFID = ?");
            getQualifiedStaff = conn.prepareStatement("SELECT * FROM " +  staffTable + " WHERE RELIGION = ?");
            removeStaff = conn.prepareStatement("DELETE FROM " +  staffTable + " WHERE STAFFID = ?");
            updateStaffTable = conn.prepareStatement("UPDATE " +  staffTable + " SET RELIGION = ? WHERE STAFFID = ?");

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
     * removeStaff = conn.prepareStatement("DELETE FROM " + interpStaffTable + " WHERE STAFFID = ?");

     * @param id
     * @return
     */
    @Override
    public boolean deleteStaff(int id) {
        //DELETE FROM THIS TABLE FIRST, THEN FROM GENERAL TABLE (FK CONSTRAINS)
        try {
            removeStaff.setInt(1, id);
            removeStaff.executeUpdate();
            log.info("Removed all information related to SpiritualStaff " + id);
        } catch (SQLException e) {
            e.printStackTrace();
            log.info("Failed to remove specific Spiritual Staff  member.");
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
     * getStaff = conn.prepareStatement("SELECT * FROM " + interpStaffTable + " WHERE STAFFID = ?");

     * @param staffID
     * @return
     */
    public SpiritualCareStaff getSpiritualCareStaff(int staffID) {
        ServiceStaff general = generalStaffDB.getStaff(staffID);
        SpiritualCareStaff gotten = null;
        if (general != null) {
            try {
                getStaff.setInt(1, staffID);
                ResultSet found = getStaff.executeQuery();
                SpiritualCareStaff staff = new SpiritualCareStaff(general, Religion.getReligion(found.getString("RELIGION")));
                if (staff != null && staff.getStaffID() != 0) {
                    gotten = staff;
                    log.info("Successfully found Spiritual Careperson with ID " + staffID);
                }
            } catch (SQLException e) {
                e.printStackTrace();
                log.info("Problem getting SpiritualCare staff with ID " + staffID);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return gotten;
    }



    /**
     * Returns a list of SpiritualCareStaff based on input Language
     getQualifiedStaff = conn.prepareStatement("SELECT * FROM " +  staffTable + " WHERE RELIGION = ?");

     * @param r
     * @return
     */
    // filter based on language for now
    public ArrayList<SpiritualCareStaff> findQualified(Religion r) {
        ArrayList<SpiritualCareStaff> foundStaff = new ArrayList<SpiritualCareStaff>();
        // Use all of the attributes to build a query for the database
        try {
            System.out.println(r.toString());
            getQualifiedStaff.setString(1, r.toString());
            ResultSet rs = getQualifiedStaff.executeQuery();
            while (rs.next()) {
                SpiritualCareStaff found = null;
                int staffID = rs.getInt("STAFFID");
                SpiritualCareStaff allInfo = getSpiritualCareStaff(staffID);
                if (allInfo != null) {
                    foundStaff.add(allInfo);
                    log.info("Found qualified SpiritualCareperson that practices " + r.toString());
                }
                else {
                    log.info("Problem finding qualified Spiritual Careperson. Exiting.");
                    return null;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return foundStaff;
    }


    /** Adds an interpreter staff to the InterpreterStaff DB
     *  addStaff = conn.prepareStatement("INSERT INTO " +  staffTable + "  (STAFFID, RELIGION VALUES(?, ?)");


     * @param s
     * @return
     */
    public boolean addStaff(SpiritualCareStaff s) {
        ServiceStaff genInfo = generalStaffDB.addStaff(s); // add general info first
        if (genInfo != null) {
            try {
                addStaff.setInt(1, genInfo.getStaffID());
                //addStaff.setString(2, );
                addStaff.setString(2, s.getReligion().toString());
                addStaff.executeUpdate();

                log.info("Staff member added to the SpiritualStaff Table successfully.");

                //addStaff.setString(7, s.);
                return true;
            } catch (SQLException e) {
                log.info("Failed to add Staff Member to Spiritual Table.");
                e.printStackTrace();
                return false;
            }
        }
        log.info("Problem with adding the SpiritualStaff to the General Staff DB. General Staff Info is NULL.");
        return false;
    }

    /**
     * returns all the Interpreters in the table
     * @return
     */
    public ArrayList<SpiritualCareStaff> getAllSpiritualStaff() {
        // TODO
        ArrayList<ServiceStaff> allStaff = generalStaffDB.getStaffByType(StaffType.SPIRITUAL);
        ArrayList<SpiritualCareStaff> allSpirituals = new ArrayList<>();
        for (ServiceStaff s: allStaff) {
            if (s != null) {
                SpiritualCareStaff found = getSpiritualCareStaff(s.getStaffID());
                if (found != null) {
                    allSpirituals.add(found);
                }
            }
        }
        return allSpirituals;
    }

    /**
     * updateStaffTable = conn.prepareStatement("UPDATE " +  staffTable + " SET RELIGION = ? WHERE STAFFID = ?");

     * @param s
     * @return
     */
    public boolean updateStaff(SpiritualCareStaff s) {
        if (generalStaffDB.updateStaff(s)) {
            try {
                updateStaffTable.setString(1, s.getReligion().toString());
                updateStaffTable.setInt(2, s.getStaffID());

                updateStaffTable.executeUpdate();

                log.info("Spiritual Careperson with ID " + s.getStaffID() + " successfully updated.");
                return true;
            } catch (SQLException e) {
                log.info("Failed to update Spiritual Staff with id " + s.getStaffID());
                e.printStackTrace();
            }
        }
        return false;
    }
    /*
               getStaff = conn.prepareStatement("SELECT T1.*, T2.* FROM " + staffTable + " AS T1, " +
                    interpStaffTable + " AS T2 WHERE T1.STAFFID = ? AND T1.STAFFID = T2.STAFFID");

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
