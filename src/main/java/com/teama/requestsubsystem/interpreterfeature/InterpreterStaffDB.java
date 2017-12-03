package com.teama.requestsubsystem.interpreterfeature;

import com.teama.messages.ContactInfo;
import com.teama.messages.ContactInfoTypes;
import com.teama.messages.Provider;
import com.teama.requestsubsystem.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;
/**
 * Created by aliss on 11/21/2017.
 */

public class InterpreterStaffDB implements StaffDataSource {
    private StaffDataSource generalStaffDB = GenRequestDBManager.getInstance().getGenericStaffDB();
    private final Logger log = Logger.getLogger(this.getClass().getPackage().getName());
    private String dbURL, staffTable;
    private String staffTableLanguages;
    private Connection conn = null;
    private Statement stmt = null;
    PreparedStatement addStaff, removeStaffTable, updateStaffTable, getStaff, getQualifiedStaff, getQualifiedStaffInfo, getAllStaff;
    PreparedStatement addStaffLangTable, updateStaffLangTable, getQualifiedStaffLangs, removeStaffLangTable;

    public InterpreterStaffDB(String dbURL, String staffTableName, String languageTableName) {
        this.dbURL = dbURL;
        this.staffTable = staffTableName;
        this.staffTableLanguages = languageTableName;

        try {
            Class.forName("org.apache.derby.jdbc.EmbeddedDriver").newInstance();
            // Get a connection
            conn = DriverManager.getConnection(this.dbURL);
        } catch (Exception except) {
            except.printStackTrace();
        }

        // Creates the staff table if it isn't there already; link to request table by staffID
        try {
            stmt = conn.createStatement();
            stmt.execute(
                    " CREATE TABLE " + staffTable + " (" +
                            "STAFFID INTEGER NOT NULL PRIMARY KEY GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1), " +
                            "FIRSTNAME VARCHAR(50) NOT NULL, " +
                            "LASTNAME VARCHAR(50) NOT NULL, " +
                            "PHONENUMBER VARCHAR(30) NOT NULL, " +
                            "EMAIL VARCHAR(100) NOT NULL, " +
                            "PROVIDER VARCHAR(100) NOT NULL, " +
                            "CERTIFICATION VARCHAR(30) NOT NULL " +
                            //"ONDUTY VARCHAR(20) NOT NULL " +
                            ")"
            );
            log.info("Staff table created.");
            stmt.close();
        } catch (SQLException sqlExcept) {
            //log.info("Does the staff info database or language relation table already exist?");
            sqlExcept.printStackTrace();
        }
        // Create the language->staff table as well
        // This table is always named <staffTable>_LANGUAGE
        try {
            stmt = conn.createStatement();
            System.out.println(staffTableLanguages + " about to be created.");
            stmt.execute(
                    " CREATE TABLE " + staffTableLanguages + " (" +
                            "STAFFID INTEGER NOT NULL, " +
                            "LANGUAGE VARCHAR(50) NOT NULL, " +
                            "FOREIGN KEY (STAFFID) REFERENCES " + staffTable + " (STAFFID), " +
                            "PRIMARY KEY (STAFFID,LANGUAGE)" +
                            ")"
            );
            log.info("Language table created.");
            stmt.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        DatabaseMetaData meta = null;
        try {
            meta = conn.getMetaData();
            ResultSet res = meta.getTables(null, null, staffTableLanguages,
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



        try {
            addStaff = conn.prepareStatement("INSERT INTO " + staffTable + " (FIRSTNAME, LASTNAME, PHONENUMBER, EMAIL, PROVIDER, CERTIFICATION) VALUES(?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
            getStaff = conn.prepareStatement("SELECT * FROM " + staffTable + " WHERE STAFFID = ?");
            getAllStaff = conn.prepareStatement("SELECT * FROM " + staffTable);
            /*
            getQualifiedStaff = conn.prepareStatement("SELECT * FROM " + staffTable + " AS T1, " +
                    staffTableLanguages + " AS T2 WHERE T1.STAFFID = T2.STAFFID AND T2.LANGUAGE = ?");
                    */
            getQualifiedStaff = conn.prepareStatement("SELECT STAFFID FROM " + staffTableLanguages + " WHERE LANGUAGE = ?");
            getQualifiedStaffInfo = conn.prepareStatement("SELECT * FROM " + staffTable + " WHERE STAFFID = ?");
            getQualifiedStaffLangs = conn.prepareStatement("SELECT * FROM " + staffTableLanguages + " WHERE STAFFID = ?");
            //getContactInfo = conn.prepareStatement("SELECT ? FROM " + staffTable + " WHERE STAFFID = ?");
            removeStaffTable = conn.prepareStatement("DELETE FROM " + staffTable + " WHERE STAFFID = ?");
            updateStaffTable = conn.prepareStatement("UPDATE " + staffTable + " SET FIRSTNAME = ?, LASTNAME = ?, PHONENUMBER = ?, EMAIL = ?, PROVIDER = ?, CERTIFICATION = ? WHERE STAFFID = ?");
            addStaffLangTable = conn.prepareStatement("INSERT INTO " + staffTableLanguages + " VALUES(?, ?)");
            updateStaffLangTable = conn.prepareStatement("UPDATE " + staffTableLanguages + " SET LANGUAGE = ? WHERE STAFFID = ?");
            removeStaffLangTable = conn.prepareStatement("DELETE FROM " + staffTableLanguages + " WHERE STAFFID = ?");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    // filter based on language for now
    public ArrayList<InterpreterStaff> findQualified(Language lang) {
        ArrayList<InterpreterStaff> foundStaff = new ArrayList<InterpreterStaff>();
        // Use all of the attributes to build a query for the database
        try {
            getQualifiedStaff.setString(1, lang.toString());
            ResultSet rs = getQualifiedStaff.executeQuery();
            while (rs.next()) {
                InterpreterStaff found = null;
                int staffID = rs.getInt("STAFFID");
                getQualifiedStaffInfo.setInt(1, staffID); // get the rest of the info about the staff member
                ResultSet info = getQualifiedStaffInfo.executeQuery();
                if (info.next()) {
                    Set<ContactInfoTypes> avail = new HashSet<ContactInfoTypes>();
                    avail.add(ContactInfoTypes.EMAIL);
                    avail.add(ContactInfoTypes.TEXT);
                    avail.add(ContactInfoTypes.PHONE);
                    Set<Language> langs = new HashSet<>();
                    getQualifiedStaffLangs.setInt(1, staffID);
                    ResultSet rsLangs = getQualifiedStaffLangs.executeQuery();
                    while (rsLangs.next()) {
                        langs.add(Language.getLanguage(rsLangs.getString("LANGUAGE")));
                        log.info("Found a language " + rsLangs.getString("LANGUAGE"));
                    }
                    ContactInfo c = new ContactInfo(avail, info.getString("PHONENUMBER"), info.getString("EMAIL"), Provider.getFromString(info.getString("PROVIDER")));
                    found = new InterpreterStaff(new GenericStaff(info.getString("FIRSTNAME"), info.getString("LASTNAME"), c),
                            new InterpreterInfo(info.getInt("STAFFID"), langs, CertificationType.getCertificationType(info.getString("CERTIFICATION"))));
                    log.info("Found qualified staff member");
                    foundStaff.add(found);
                }

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return foundStaff;
    }
    public boolean addStaff(ServiceStaff s) {
        //TODO
        return false;
    }
    public boolean updateStaff(ServiceStaff s) {
        //TODO
        return false;
    }
    public boolean deleteStaff(int id) {
        //TODO
        return false;
    }
    public ServiceStaff getStaff(int staffID) {
        //TODO
        return null;
    }
    /*
    addStaff = conn.prepareStatement("INSERT INTO " + staffTable + " VALUES(?, ?, ?, ?, ?, ?, ?)");
    addStaffLangTable = conn.prepareStatement("INSERT INTO " + staffTableLanguages + " VALUES(?) WHERE STAFFID = ?");
     " CREATE TABLE " + staffTable + " (" +
                            "STAFFID INTEGER PRIMARY KEY NOT NULL AUTO_INCREMENT," +
                            "FIRSTNAME VARCHAR(50) NOT NULL, " +
                            "LASTNAME VARCHAR(50) NOT NULL, " +
                            "PHONENUMBER VARCHAR(30) NOT NULL, " +
                            "EMAIL VARCHAR(100) NOT NULL, " +
                            "PROVIDER VARCHAR(100) NOT NULL, " +
                            "CERTIFICATION VARCHAR(30) NOT NULL, " +
                            "ONDUTY VARCHAR(20) NOT NULL " +
                            ")"
            );
            " CREATE TABLE " + staffTableLanguages + " (" +
                            "STAFFID INTEGER NOT NULL," +
                            "LANGUAGE VARCHAR(50) NOT NULL " +
                            "FOREIGN KEY(STAFFID) REFERENCES " + staffTable +
                            ")
     */

    public boolean addStaff(InterpreterStaff s) {
        try {
            addStaff.setString(1, s.getFirstName());
            addStaff.setString(2, s.getLastName());
            addStaff.setString(3, s.getPhone());
            addStaff.setString(4, s.getEmail());
            addStaff.setString(5, s.getProvider().toString());
            addStaff.setString(6, s.getCertification().toString());
            //addStaff.setString(7, s.); //TODO: CHANGE THIS IF WE ACTUALLY IMPLEMENT WORK HOURS, OR DELETE
            addStaff.executeUpdate();

            // add to Language Table
            ResultSet rs = addStaff.getGeneratedKeys();
            if (rs.next()) {
                int ID = rs.getInt(1);
                Set<Language> langs = s.getLanguages();
                for (Language l: langs) { // add each spoken language to the Language table
                    addStaffLangTable.setInt(1, ID);
                    addStaffLangTable.setString(2, l.toString());
                    log.info("Added " + l.toString());
                    addStaffLangTable.executeUpdate();
                }
                log.info("All languages spoken by staff member added to the Interpreter Language table successfully.");
                log.info("Staff member added successfully to Interpreter Table.");

                return true;
            }

            else {
                log.info("Not possible for an interpreter to have 0 languages. Check something.");
                return false; // what kind of interpreter speaks no languages?
            }
        } catch (SQLException e) {
            log.info("Failed to add Staff Member to Interpreter Table.");
            e.printStackTrace();
            return false;
        }
    }
/*
           updateStaffTable = conn.prepareStatement("UPDATE " + staffTable + " SET FIRSTNAME = ?, LASTNAME = ?, PHONENUMBER = ?, EMAIL = ?, PROVIDER = ?" +
                    " CERTIFICATION = ?, WHERE STAFFID = ?");
            updateStaffLangTable = conn.prepareStatement("UPDATE " + staffTableLanguages + " SET LANGUAGE = ? WHERE STAFFID = ?");

 */
    public ArrayList<InterpreterStaff> getAllStaff() {
        ArrayList<InterpreterStaff> allStaff = new ArrayList<>();
        try {
            ResultSet all = getAllStaff.executeQuery();
            while (all.next()) {
                InterpreterStaff gottenStaff = rsToStaff(all);
                if (gottenStaff != null) {
                    allStaff.add(gottenStaff);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return allStaff;
    }
    public boolean updateStaff(InterpreterStaff s) {
         try {
            updateStaffTable.setString(1, s.getFirstName());
            updateStaffTable.setString(2, s.getLastName());
            updateStaffTable.setString(3, s.getPhone());
            updateStaffTable.setString(4, s.getEmail());
            updateStaffTable.setString(5, s.getProvider().toString());
            updateStaffTable.setString(6, s.getCertification().toString());
            updateStaffTable.setInt(7, s.getStaffID());
            updateStaffTable.executeUpdate();
            log.info("Staff member successfully updated.");
             try {
                 removeStaffLangTable.setInt(1, s.getStaffID()); // first remove all languages, then re-add
                 System.out.println(s.getStaffID());
                 removeStaffLangTable.executeUpdate();
                 log.info("Executed delete languages");
                 getQualifiedStaffLangs.setInt(1, s.getStaffID());
                 ResultSet rsLangs = getQualifiedStaffLangs.executeQuery();
                 while (rsLangs.next()) {
                     log.info("Should have deleted, but Found a language " + rsLangs.getString("LANGUAGE"));
                 }
             }
             catch (SQLException e) {
                 log.info("Failed to clear existing languages...");
                 e.printStackTrace();
             }

            // add to Language Table
            Set<Language> langs = s.getLanguages();
            for (Language l: langs) {

                try {
                    addStaffLangTable.setInt(1, s.getStaffID());
                    addStaffLangTable.setString(2, l.toString());
                    addStaffLangTable.executeUpdate();
                    log.info("Added language " + l.toString());
                    getQualifiedStaffLangs.setInt(1, s.getStaffID());
                    ResultSet rsLangs = getQualifiedStaffLangs.executeQuery();
                    while (rsLangs.next()) {
                        log.info("After re-adding, Found a language " + rsLangs.getString("LANGUAGE"));
                    }
                }
                catch (SQLException e) {
                    log.info("Failed to update staff member Language Table.");
                    e.printStackTrace();
                    return false;
                }
            }
            return true;


        } catch (SQLException e) {
            log.info("Failed to update staff member General Interpreter info.");
            e.printStackTrace();
            return false;
        }
    }
    /*
               getStaff = conn.prepareStatement("SELECT T1.*, T2.* FROM " + staffTable + " AS T1, " +
                    staffTableLanguages + " AS T2 WHERE T1.STAFFID = ? AND T1.STAFFID = T2.STAFFID");

     */
    private InterpreterStaff rsToStaff(ResultSet rs) {
        InterpreterStaff found = null;

        try {
            Set<ContactInfoTypes> avail = new HashSet<ContactInfoTypes>();
            avail.add(ContactInfoTypes.EMAIL);
            avail.add(ContactInfoTypes.TEXT);
            avail.add(ContactInfoTypes.PHONE);
            Set<Language> langs = new HashSet<>();
            getQualifiedStaffLangs.setInt(1, rs.getInt("STAFFID"));
            ResultSet rsLangs = getQualifiedStaffLangs.executeQuery();
            while (rsLangs.next()) {
                langs.add(Language.getLanguage(rsLangs.getString("LANGUAGE")));
                log.info("Found a language " + rsLangs.getString("LANGUAGE"));

            }
            ContactInfo c = new ContactInfo(avail, rs.getString("PHONENUMBER"), rs.getString("EMAIL"), Provider.getFromString(rs.getString("PROVIDER")));
            found = new InterpreterStaff(new GenericStaff(rs.getString("FIRSTNAME"), rs.getString("LASTNAME"), c),
                    new InterpreterInfo(rs.getInt("STAFFID"), langs, CertificationType.valueOf(rs.getString("CERTIFICATION"))));

            log.info("Found staff member with ID " + rs.getInt("STAFFID"));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return found;
    }
    // finds staff by ID
    public InterpreterStaff getStaff(int id) {
        InterpreterStaff found = null;
        try {
            getStaff.setInt(1, id);
            ResultSet rs = getStaff.executeQuery();
            if (rs.next()) {
                found = rsToStaff(rs);
                /*
                for (Language l: found.getLanguages()) {
                    System.out.print(l.toString());
                }
                */
                /*
                Set<ContactInfoTypes> avail = new HashSet<ContactInfoTypes>();
                avail.add(ContactInfoTypes.EMAIL);
                avail.add(ContactInfoTypes.TEXT);
                avail.add(ContactInfoTypes.PHONE);
                Set<Language> langs = new HashSet<>();
                getQualifiedStaffLangs.setInt(1, rs.getInt("STAFFID"));
                ResultSet rsLangs = getQualifiedStaffLangs.executeQuery();
                while (rsLangs.next()) {
                    langs.add(Language.getLanguage(rsLangs.getString("LANGUAGE")));
                }
                ContactInfo c = new ContactInfo(avail, rs.getString("PHONENUMBER"), rs.getString("EMAIL"), Provider.valueOf(rs.getString("PROVIDER")));
                found = new InterpreterStaff(new GenericStaff(rs.getString("FIRSTNAME"), rs.getString("LASTNAME"), c),
                        new InterpreterInfo(rs.getInt("STAFFID"), langs, CertificationType.valueOf(rs.getString("CERTIFICATION"))));
                log.info("Found staff member with ID " + rs.getInt("STAFFID"));
                // perform another query to find contact info types
                */
            }
        } catch (SQLException e) {
            e.printStackTrace();

        }
        return found;

    }
/*
            removeStaffReqTable = conn.prepareStatement("DELETE FROM " + staffTable + " WHERE STAFFID = ?");

 */

    public boolean deleteStaff(int id) {
        try {
            removeStaffLangTable.setInt(1, id);
            removeStaffLangTable.executeUpdate();
            log.info("Removed all languages related to Staff " + id);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            removeStaffTable.setInt(1, id);
            removeStaffTable.executeUpdate();
            log.info("Successfully removed staff member " + id);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            log.info("Failed to delete staff member " + id);
            return false;
        }

    }

    @Override
    public ArrayList<ServiceStaff> getStaffByType(StaffType t){
        // TODO
        return null;
    }

    public void close() {
        try {
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}



