package com.teama.requestsubsystem.interpreterfeature;

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
 * Created by aliss on 11/21/2017.
 */

public class InterpreterStaffDB implements StaffDataSource {
    private StaffDataSource generalStaffDB;
    private final Logger log = Logger.getLogger(this.getClass().getPackage().getName());
    private String dbURL;
    private String interpStaffTable;
    private Connection conn = null;
    private Statement stmt = null;
    private PreparedStatement addStaff, removeStaff, updateStaffTable, getStaff, getQualifiedStaff, getAllStaff;
    //PreparedStatement addStaffLangTable, updateStaffLangTable, getQualifiedStaffLangs, removeStaffLangTable;

    public InterpreterStaffDB(String dbURL, String generalStaffTableName, String staffTableName) {
        this.dbURL = dbURL;
        this.interpStaffTable = staffTableName;
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
            System.out.println(interpStaffTable + " about to be created.");
            stmt.execute(
                    " CREATE TABLE " + interpStaffTable + " (" +
                            "STAFFID INTEGER NOT NULL, " +
                            "LANGUAGE VARCHAR(50) NOT NULL, " +
                            "CERTIFICATION VARCHAR(100) NOT NULL, " +
                            "FOREIGN KEY (STAFFID) REFERENCES " + generalStaffTableName + " (STAFFID), " +
                            "PRIMARY KEY (STAFFID,LANGUAGE)" +
                            ")"
            );
            log.info("InterpreterStaff table created.");
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
            addStaff = conn.prepareStatement("INSERT INTO " + interpStaffTable + "  (STAFFID, LANGUAGE, CERTIFICATION) VALUES(?, ?, ?)");
            getStaff = conn.prepareStatement("SELECT * FROM " + interpStaffTable + " WHERE STAFFID = ?");
            getAllStaff = conn.prepareStatement("SELECT * FROM " + interpStaffTable + " WHERE STAFFID = ?");
            getQualifiedStaff = conn.prepareStatement("SELECT * FROM " + interpStaffTable + " WHERE LANGUAGE = ?");
            removeStaff = conn.prepareStatement("DELETE FROM " + interpStaffTable + " WHERE STAFFID = ?");
            updateStaffTable = conn.prepareStatement("UPDATE " + interpStaffTable + " SET LANGUAGE = ?, CERTIFICATION = ? WHERE STAFFID = ?");

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
            log.info("Removed all interpreter information related to InterpreterStaff " + id);
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
     * getStaff = conn.prepareStatement("SELECT * FROM " + interpStaffTable + " WHERE STAFFID = ?");

     * @param staffID
     * @return
     */
    public InterpreterStaff getInterpreterStaff(int staffID) {
        ServiceStaff general = generalStaffDB.getStaff(staffID);
        InterpreterStaff gotten = null;
        if (general != null) {
            try {
                getStaff.setInt(1, staffID);
                ResultSet found = getStaff.executeQuery();
                Set<Language> langs = new HashSet<>();
                CertificationType cert = null;
                while (found.next()) {
                    Language spoken = Language.getLanguage(found.getString("LANGUAGE"));
                    langs.add(spoken);
                    if (cert == null) {
                        cert = CertificationType.getCertificationType(found.getString("CERTIFICATION"));
                    }
                }
                InterpreterStaff interp = new InterpreterStaff(general, langs, cert);
                if (interp != null && interp.getStaffID() != 0 && !langs.isEmpty()) {
                    gotten = interp;
                    log.info("Successfully found interpreter with ID " + staffID);
                }
            } catch (SQLException e) {
                e.printStackTrace();
                log.info("Problem getting Interpreter staff with ID " + staffID);
            }
        }
        return gotten;
    }

    /**
     * Returns a list of InterpreterStaff based on input Language
     * getQualifiedStaff = conn.prepareStatement("SELECT * FROM " + interpStaffTable + " WHERE LANGUAGE = ?");

     * @param lang
     * @return
     */
    // filter based on language for now
    public ArrayList<InterpreterStaff> findQualified(Language lang) {
        ArrayList<InterpreterStaff> foundStaff = new ArrayList<InterpreterStaff>();
        // Use all of the attributes to build a query for the database
        try {
            System.out.println(lang);
            getQualifiedStaff.setString(1, lang.toString());
            ResultSet rs = getQualifiedStaff.executeQuery();
            while (rs.next()) {
                InterpreterStaff found = null;
                int staffID = rs.getInt("STAFFID");
                InterpreterStaff allInfo = getInterpreterStaff(staffID);
                if (allInfo != null) {
                    foundStaff.add(allInfo);
                    log.info("Found qualified Interpreter that speaks " + lang.toString());
                }
                else {
                    log.info("Problem finding qualified Interpreter. Exiting.");
                    return null;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return foundStaff;
    }


    /** Adds an interpreter staff to the InterpreterStaff DB
     * addStaff = conn.prepareStatement("INSERT INTO " + interpStaffTable + "  (STAFFID, LANGUAGE, CERTIFICATION) VALUES(?, ?, ?)");

     * @param s
     * @return
     */
    public boolean addStaff(InterpreterStaff s) {
        ServiceStaff genInfo = generalStaffDB.addStaff(s); // add general info first
        if (genInfo != null) {
            try {
                addStaff.setInt(1, genInfo.getStaffID());
                //addStaff.setString(2, );
                addStaff.setString(3, s.getCertification().toString());
                for (Language l: s.getLanguages()) {
                    addStaff.setString(2, l.toString());
                    addStaff.executeUpdate();
                }
                log.info("All languages spoken by staff member added to the Interpreter Table successfully.");
                log.info("Staff member added successfully to Interpreter Table.");

                //addStaff.setString(7, s.); //TODO: CHANGE THIS IF WE ACTUALLY IMPLEMENT WORK HOURS, OR DELETE
                return true;
            } catch (SQLException e) {
                log.info("Failed to add Staff Member to Interpreter Table.");
                e.printStackTrace();
                return false;
            }
        }
        log.info("Problem with adding the InterpreterStaff to the General Staff DB. General Staff Info is NULL.");
        return false;
    }

    public int addStaffForLogin(InterpreterStaff s) {
        ServiceStaff genInfo = generalStaffDB.addStaff(s); // add general info first
        if (genInfo != null) {
            try {
                addStaff.setInt(1, genInfo.getStaffID());
                //addStaff.setString(2, );
                addStaff.setString(3, s.getCertification().toString());
                for (Language l: s.getLanguages()) {
                    addStaff.setString(2, l.toString());
                    addStaff.executeUpdate();
                }
                log.info("All languages spoken by staff member added to the Interpreter Table successfully.");
                log.info("Staff member added successfully to Interpreter Table.");

                //addStaff.setString(7, s.); //TODO: CHANGE THIS IF WE ACTUALLY IMPLEMENT WORK HOURS, OR DELETE
                return genInfo.getStaffID();
            } catch (SQLException e) {
                log.info("Failed to add Staff Member to Interpreter Table for login .");
                e.printStackTrace();
                return 0;
            }
        }
        log.info("Problem with adding the InterpreterStaff to the General Staff DB. General Staff Info is NULL.");
        return 0;
    }

    /**
     * returns all the Interpreters in the table
     * @return
     */
    public ArrayList<InterpreterStaff> getAllInterpreterStaff() {
        ArrayList<ServiceStaff> allStaff = generalStaffDB.getStaffByType(StaffType.INTERPRETER);
        ArrayList<InterpreterStaff> allInterps = new ArrayList<>();
        for (ServiceStaff s: allStaff) {
            if (s != null) {
                InterpreterStaff found = getInterpreterStaff(s.getStaffID());
                if (found != null) {
                    allInterps.add(found);
                }
            }
        }
        return allInterps;
    }

    /**
     * updateStaffTable = conn.prepareStatement("UPDATE " + interpStaffTable + " SET LANGUAGE = ?, CERTIFICATION = ? WHERE STAFFID = ?");
     removeStaff = conn.prepareStatement("DELETE FROM " + interpStaffTable + " WHERE STAFFID = ?");

     * @param s
     * @return
     */
    public boolean updateStaff(InterpreterStaff s) {
        if (generalStaffDB.updateStaff(s)) {
            try {
                // FIRST REMOVE THE CURRENT ENTRIES
                removeStaff.setInt(1, s.getStaffID());
                removeStaff.executeUpdate();
                log.info("Deleted all current Language entries for udpate.");
                addStaff.setString(3, s.getCertification().toString());
                addStaff.setInt(1, s.getStaffID());
                for (Language l : s.getLanguages()) {
                    addStaff.setString(2, l.toString());
                    addStaff.executeUpdate();
                }

                log.info("Interpreter with ID " + s.getStaffID() + " successfully updated.");
                return true;
            } catch (SQLException e) {
                    log.info("Failed to update Interpreter Staff with id " + s.getStaffID());
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



