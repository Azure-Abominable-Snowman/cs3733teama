package com.teama.requestsubsystem.interpreterfeature;

import com.teama.messages.ContactInfo;
import com.teama.messages.ContactInfoTypes;
import com.teama.messages.Provider;
import com.teama.requestsubsystem.GenericStaffInfo;

import java.sql.*;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;
//TODO : FINISH THE METHODS IN THIS CLASS. PERHAPS ADD A CONTACTINFO DATABASE. MAKE THE STAFF TABLE AUTOINCREMENT - THAT WILL BE THE UNIQUE STAFF ID FOR NOW. 
/**
 * Created by aliss on 11/21/2017.
 */
public class InterpreterStaffDB {
    private final Logger log = Logger.getLogger(this.getClass().getPackage().getName());
    private String dbURL, staffTable;
    private String staffTableLanguages;
    private Connection conn = null;
    private Statement stmt = null;
    PreparedStatement addStaff, removeStaffReqTable, updateStaffReqTable, getStaff, getQualifiedStaff, getContactInfo;
    PreparedStatement addStaffLangTable, updateStaffLangTable;

    public InterpreterStaffDB(String dbURL) {
        this.staffTable = "INTERPRETER_STAFF";
        this.dbURL = dbURL;
        this.staffTableLanguages = staffTable + "_LANGUAGES";

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
                            "ONDUTY VARCHAR(20) NOT NULL " +
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
/*
        try {
            stmt = conn.createStatement();
            stmt.execute(
                    "INSERT INTO "+staffTable+" VALUES('TESTSTAFF', 'Jake', 'Pardue', '6034893939', 'INTERPRETER', 'VERIZON', TRUE)"
            );
            stmt.execute(
                    "INSERT INTO "+staffTable+"_LANGUAGES VALUES('TESTSTAFF', 'English')"
            );
            stmt.close();
        } catch (SQLException e) {
            log.info("Staff language or person already added");
        }
        */
        try {
            addStaff = conn.prepareStatement("INSERT INTO " + staffTable + " VALUES(?, ?, ?, ?, ?, ?, ?)");
            getStaff = conn.prepareStatement("SELECT * FROM " + staffTable + " WHERE STAFFID = ?");
            getQualifiedStaff = conn.prepareStatement("SELECT ? FROM " + staffTable + " AS T1, " +
                    staffTableLanguages + " AS T2 WHERE T1.STAFFID = T2.STAFFID AND T2.LANGUAGE = ? AND " +
                    "T1.ONDUTY = " + " TRUE");
            getContactInfo = conn.prepareStatement("SELECT ? FROM " + staffTable + " WHERE STAFFID = ?");
            removeStaffReqTable = conn.prepareStatement("DELETE FROM " + staffTable + " WHERE STAFFID = ?");
            updateStaffReqTable = conn.prepareStatement("UPDATE " + staffTable + " SET FIRSTNAME = ?, LASTNAME = ?, PHONENUMBER = ?, PHONEPROVIDER = ?, EMAIL = ?, ONDUTY = ? WHERE STAFFID = ?");
            addStaffLangTable = conn.prepareStatement("INSERT INTO " + staffTableLanguages + " VALUES(?) WHERE STAFFID = ?");
            updateStaffLangTable = conn.prepareStatement("UPDATE " + staffTableLanguages + " SET LANGUAGE = ? WHERE STAFFID = ?");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
/*
    public ArrayList<ServiceStaff> getIntrStaff(){
        ArrayList<ServiceStaff> INTRStaff = new ArrayList<>();
        try{
            stmt = conn.createStatement();
            ResultSet s = stmt.executeQuery(
                    "SELECT * FROM "+staffTable+" WHERE STAFFTYPE = 'INTERPRETER'"
            );
            while(s.next()){
                ServiceStaff staff = new InterpreterStaff(s.getString("STAFFID"), s.getString("firstName"), s.getString("lastName"),
                        s.getString("phoneNumber"), StaffType.valueOf(s.getString("STAFFTYPE")), null, Provider.valueOf(s.getString("PHONEPROVIDER")), s.getBoolean("available"));
                INTRStaff.add(staff);
            }
            return INTRStaff;
        } catch (SQLException e)
        {
            e.printStackTrace();
        }
        System.out.println("Couldn't find staff");
        return null;
    }
    */

    // filter based on language for now
    public InterpreterStaff findQualified(Language lang) {
        InterpreterStaff foundStaff = null;
        // Use all of the attributes to build a query for the database
        try {
            getQualifiedStaff.setString(1, lang.toString());
            ResultSet rs = getQualifiedStaff.executeQuery();
            if (rs.next()) {
                Set<ContactInfoTypes> avail = new HashSet<ContactInfoTypes>();
                avail.add(ContactInfoTypes.EMAIL);
                avail.add(ContactInfoTypes.TEXT);
                avail.add(ContactInfoTypes.PHONE);
                ContactInfo c = new ContactInfo(avail, rs.getString("PHONENUMBER"), rs.getString("EMAIL"), Provider.valueOf(rs.getString("PROVIDER")));
                foundStaff = new InterpreterStaff(new GenericStaffInfo(rs.getInt("STAFFID"), rs.getString("FIRSTNAME"), rs.getString("LASTNAME"), c),
                        new InterpreterInfo(new HashSet<Language>(), CertificationType.valueOf(rs.getString("CERTIFICATION"))));
                log.info("Found qualified staff member");
                return foundStaff;
                // perform another query to find contact info types
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean addStaff(InterpreterStaff s) {
        
    }
}

/*
            log.info(query.toString());
            ResultSet result = stmt.executeQuery(query.toString());
            // If none match...
            if(!result.next()) {
                // No available members found in db
                log.info("No results found for qualified servicestaff");
                return null;
            }
            // Query returns all matching ID's
            // Another query is needed to get all attributes for the staff member
            String foundId = result.getString("STAFFID");
            log.info("FOUND "+foundId);
            result.close();

            // Retrieves all info on the found staff member
            result = stmt.executeQuery("SELECT * FROM "+staffTable+" AS T1, "+staffTableLanguages+" AS T2 WHERE T1.STAFFID='"+foundId+"' AND T2.STAFFID='"+foundId+"'");

            // Add all of the stuff in the first row
            result.next();
            String firstName = result.getString("FIRSTNAME");
            String lastName = result.getString("LASTNAME");
            String phone =  result.getString("PHONENUMBER");
            Provider provider = Provider.valueOf(result.getString("PHONEPROVIDER").toUpperCase());
            Set<Language> lanArray = new HashSet<>();
            lanArray.add(Language.valueOf(result.getString("LANGUAGE")));
            while(result.next() && (result.getString("STAFFID").equals(foundId))) {
                // Load up all of the array attributes
                lanArray.add(Language.valueOf(result.getString("LANGUAGE")));
            }

            log.info(lanArray.toString());
            switch(attrib.getType().toString()) {
                case("SECURITY"):
                    foundStaff = new SecurityStaff(foundId, firstName, lastName, phone, StaffType.SECURITY,lanArray, provider, true);
                    break;
                case("TRANSPORT"):
                    foundStaff = new TransportStaff(foundId, firstName, lastName, phone, StaffType.TRANSPORT, lanArray, provider, true);
                    break;
                case("INTERPRETER"):
                    foundStaff = new SecurityStaff(foundId, firstName, lastName, phone, StaffType.INTERPRETER, lanArray, provider,true);
                    break;
                default:
                    System.out.println("Invalid staff member requested");
                    return null;
            }
            result.close();
            stmt.close();
            return foundStaff;
        } catch(SQLException e) {
            e.printStackTrace();
        }
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

 catch (SQLException e) {
            e.printStackTrace();
        */