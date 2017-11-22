package com.teama.requestsubsystem.interpreterfeature;

import com.teama.messages.ContactInfo;
import com.teama.messages.ContactInfoTypes;
import com.teama.messages.Provider;
import com.teama.requestsubsystem.GenericStaffInfo;

import java.sql.*;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;
/**
 * Created by aliss on 11/21/2017.
 */

public class InterpreterStaffDB implements InterpreterStaffInfoSource {
    private final Logger log = Logger.getLogger(this.getClass().getPackage().getName());
    private String dbURL, staffTable;
    private String staffTableLanguages;
    private Connection conn = null;
    private Statement stmt = null;
    PreparedStatement addStaff, removeStaffReqTable, updateStaffReqTable, getStaff, getQualifiedStaff, getContactInfo;
    PreparedStatement addStaffLangTable, updateStaffLangTable;

    public InterpreterStaffDB(String dbURL, String staffTableName, String languageTableName) {
        this.staffTable = staffTableName;
        this.dbURL = dbURL;
        this.staffTableLanguages = languageTableName;

        try {
            Class.forName("org.apache.derby.jdbc.EmbeddedDriver").newInstance();
            // Get a connection
            conn = DriverManager.getConnection(dbURL);
        } catch (Exception except) {
            except.printStackTrace();
        }

        // Creates the staff table if it isn't there already; link to request table by staffID
        try {
            stmt = conn.createStatement();
            stmt.execute(
                    " CREATE TABLE " + staffTable + " (" +
                            "STAFFID INTEGER PRIMARY KEY NOT NULL AUTO_INCREMENT," +
                            "FIRSTNAME VARCHAR(50) NOT NULL, " +
                            "LASTNAME VARCHAR(50) NOT NULL, " +
                            "PHONENUMBER VARCHAR(30) NOT NULL, " +
                            "EMAIL VARCHAR(100) NOT NULL, " +
                            "PROVIDER VARCHAR(100) NOT NULL, " +
                            "CERTIFICATION VARCHAR(30) NOT NULL, " +
                            //"ONDUTY VARCHAR(20) NOT NULL " +
                            ")"
            );
            stmt.close();

            // Create the language->staff table as well
            // This table is always named <staffTable>_LANGUAGE
            stmt = conn.createStatement();
            stmt.execute(
                    " CREATE TABLE " + staffTableLanguages + " (" +
                            "STAFFID INTEGER NOT NULL," +
                            "LANGUAGE VARCHAR(50) NOT NULL " +
                            "FOREIGN KEY(STAFFID) REFERENCES " + staffTable +
                            ")"
            );
            stmt.close();
        } catch (SQLException sqlExcept) {
            log.info("Does the staff info database or language relation table already exist?");
        }

        try {
            addStaff = conn.prepareStatement("INSERT INTO " + staffTable + " VALUES(?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
            getStaff = conn.prepareStatement("SELECT * FROM " + staffTable + " WHERE STAFFID = ?");
            getQualifiedStaff = conn.prepareStatement("SELECT ? FROM " + staffTable + " AS T1, " +
                    staffTableLanguages + " AS T2 WHERE T1.STAFFID = T2.STAFFID AND T2.LANGUAGE = ? AND " +
                    "T1.ONDUTY = " + " TRUE");
            getContactInfo = conn.prepareStatement("SELECT ? FROM " + staffTable + " WHERE STAFFID = ?");
            removeStaffReqTable = conn.prepareStatement("DELETE FROM " + staffTable + " WHERE STAFFID = ?");
            updateStaffReqTable = conn.prepareStatement("UPDATE " + staffTable + " SET FIRSTNAME = ?, LASTNAME = ?, PHONENUMBER = ?, EMAIL = ?, PHONEPROVIDER = ?, " +
                    " CERTIFICATION = ?, WHERE STAFFID = ?");
            addStaffLangTable = conn.prepareStatement("INSERT INTO " + staffTableLanguages + " VALUES(?) WHERE STAFFID = ?");
            updateStaffLangTable = conn.prepareStatement("UPDATE " + staffTableLanguages + " SET LANGUAGE = ? WHERE STAFFID = ?");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    // filter based on language for now
    public Set<InterpreterStaff> findQualified(Language lang) {
        Set<InterpreterStaff> foundStaff = new HashSet<InterpreterStaff>();
        // Use all of the attributes to build a query for the database
        try {
            getQualifiedStaff.setString(1, lang.toString());
            ResultSet rs = getQualifiedStaff.executeQuery();
            while (rs.next()) {
                InterpreterStaff found = null;
                Set<ContactInfoTypes> avail = new HashSet<ContactInfoTypes>();
                avail.add(ContactInfoTypes.EMAIL);
                avail.add(ContactInfoTypes.TEXT);
                avail.add(ContactInfoTypes.PHONE);
                ContactInfo c = new ContactInfo(avail, rs.getString("PHONENUMBER"), rs.getString("EMAIL"), Provider.valueOf(rs.getString("PROVIDER")));
                found = new InterpreterStaff(new GenericStaffInfo(rs.getInt("STAFFID"), rs.getString("FIRSTNAME"), rs.getString("LASTNAME"), c),
                        new InterpreterInfo(new HashSet<Language>(), CertificationType.valueOf(rs.getString("CERTIFICATION"))));
                log.info("Found qualified staff member");
                foundStaff.add(found);
                // perform another query to find contact info types
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return foundStaff;
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
            log.info("Staff member added successfully to Interpreter Table.");

            // add to Language Table
            ResultSet rs = addStaff.getGeneratedKeys();
            if (rs.next()) {
                int ID = rs.getInt(1);
                Set<Language> langs = s.getLanguages();
                for (Language l: langs) { // add each spoken language to the Language table
                    addStaffLangTable.setInt(1, ID);
                    addStaffLangTable.setString(2, l.toString());
                    addStaffLangTable.executeUpdate();
                }
                log.info("All languages spoken by staff member added to the Interpreter Language table successfully.");
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
           updateStaffReqTable = conn.prepareStatement("UPDATE " + staffTable + " SET FIRSTNAME = ?, LASTNAME = ?, PHONENUMBER = ?, EMAIL = ?, PROVIDER = ?" +
                    " CERTIFICATION = ?, WHERE STAFFID = ?");
            updateStaffLangTable = conn.prepareStatement("UPDATE " + staffTableLanguages + " SET LANGUAGE = ? WHERE STAFFID = ?");

 */

    public boolean updateStaff(InterpreterStaff s) {
         try {
            updateStaffReqTable.setString(1, s.getFirstName());
            updateStaffReqTable.setString(2, s.getLastName());
            updateStaffReqTable.setString(3, s.getPhone());
            updateStaffReqTable.setString(4, s.getEmail());
            updateStaffReqTable.setString(5, s.getProvider().toString());
            updateStaffReqTable.setString(6, s.getCertification().toString());
            updateStaffLangTable.setInt(7, s.getStaffID());
            updateStaffReqTable.executeUpdate();
            log.info("Staff member successfully updated.");

            // add to Language Table
            Set<Language> langs = s.getLanguages();
            for (Language l: langs) {
                try {

                    updateStaffLangTable.setString(1, l.toString());
                    updateStaffLangTable.setInt(2, s.getStaffID());
                    updateStaffLangTable.executeUpdate();
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
            removeStaffReqTable = conn.prepareStatement("DELETE FROM " + staffTable + " WHERE STAFFID = ?");

 */

    public boolean deleteStaff(int id) {
        try {
            removeStaffReqTable.setInt(1, id);
            log.info("Successfully removed staff member " + id);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            log.info("Failed to delete staff member " + id);
            return false;
        }

    }
    public void close() {
        try {
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}



