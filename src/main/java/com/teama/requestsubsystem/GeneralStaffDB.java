package com.teama.requestsubsystem;

import com.teama.login.AccessType;
import com.teama.login.LoginInfo;
import com.teama.login.LoginSubsystem;
import com.teama.login.SystemUser;
import com.teama.messages.ContactInfo;
import com.teama.messages.ContactInfoTypes;
import com.teama.messages.Provider;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

/**
 * Created by aliss on 12/2/2017.
 */
public class GeneralStaffDB implements StaffDataSource{
    // TODO: add username to the info
    private final Logger log = Logger.getLogger(this.getClass().getPackage().getName());
    private String dbURL, staffTable;
    //private String staffTableLanguages;
    private Connection conn = null;
    private Statement stmt = null;
    private PreparedStatement addStaff, removeStaff, updateStaff, getStaff,  getStaffByType, getAllStaff;

    public GeneralStaffDB(String dbURL, String staffTableName) {
        this.dbURL = dbURL;
        this.staffTable = staffTableName;

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
                            "STAFFTYPE VARCHAR(100) NOT NULL, " +
                            "FIRSTNAME VARCHAR(50) NOT NULL, " +
                            "LASTNAME VARCHAR(50) NOT NULL, " +
                            "PHONENUMBER VARCHAR(30) NOT NULL, " +
                            "EMAIL VARCHAR(100) NOT NULL, " +
                            "PROVIDER VARCHAR(100) NOT NULL, " +
                            "USERNAME VARCHAR(100) NOT NULL" +
                            //"ONDUTY VARCHAR(20) NOT NULL " +
                            ")"
            );
            log.info("Staff table created.");
            stmt.close();
        } catch (SQLException sqlExcept) {
            log.info("Does the staff info database or language relation table already exist?");
            //sqlExcept.printStackTrace();
        }


        try {
            addStaff = conn.prepareStatement("INSERT INTO " + staffTable + " (STAFFTYPE, FIRSTNAME, LASTNAME, PHONENUMBER, EMAIL, PROVIDER, USERNAME) VALUES(?, ?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
            getStaff = conn.prepareStatement("SELECT * FROM " + staffTable + " WHERE STAFFID = ?");
            getAllStaff = conn.prepareStatement("SELECT * FROM " + staffTable);

            getStaffByType = conn.prepareStatement("SELECT * FROM " + staffTable + " WHERE STAFFTYPE = ?");
            /*
            getQualifiedStaff = conn.prepareStatement("SELECT * FROM " + staffTable + " AS T1, " +
                    staffTableLanguages + " AS T2 WHERE T1.STAFFID = T2.STAFFID AND T2.LANGUAGE = ?");
                    */
            //getContactInfo = conn.prepareStatement("SELECT ? FROM " + staffTable + " WHERE STAFFID = ?");
            removeStaff = conn.prepareStatement("DELETE FROM " + staffTable + " WHERE STAFFID = ?");
            updateStaff = conn.prepareStatement("UPDATE " + staffTable + " SET FIRSTNAME = ?, LASTNAME = ?, PHONENUMBER = ?, EMAIL = ?, PROVIDER = ?, " +
                    "USERNAME = ? WHERE STAFFID = ?");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



    /**
     * Returns a ServiceStaff based on the input id
     *             getStaff = conn.prepareStatement("SELECT * FROM " + staffTable + " WHERE STAFFID = ?");

     * @param id
     *
     * @return
     */
    public ServiceStaff getStaff(int id) {
        ServiceStaff found = null;
        try {
            getStaff.setInt(1, id);
            ResultSet rs = getStaff.executeQuery();
            if (rs.next()) {
                found = rsToStaff(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();

        }
        return found;

    }
    private ServiceStaff rsToStaff(ResultSet rs) {
        GenericStaff found = null;

        try {
            Set<ContactInfoTypes> avail = new HashSet<ContactInfoTypes>();
            avail.add(ContactInfoTypes.EMAIL);
            avail.add(ContactInfoTypes.TEXT);
            avail.add(ContactInfoTypes.PHONE);

            ContactInfo c = new ContactInfo(avail, rs.getString("PHONENUMBER"), rs.getString("EMAIL"), Provider.getFromString(rs.getString("PROVIDER")));
            found = new GenericStaff(rs.getString("FIRSTNAME"), rs.getString("LASTNAME"), rs.getString("USERNAME"), c);
            found.setStaffID(rs.getInt("STAFFID")); // set the staff ID
            log.info("Found staff member with ID " + rs.getInt("STAFFID"));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return found;
    }
/*
            removeStaffReqTable = conn.prepareStatement("DELETE FROM " + staffTable + " WHERE STAFFID = ?");

 */
    private boolean addNewStaffLogin(ServiceStaff s) {
        LoginInfo defaultLogin = new LoginInfo(s.getUsername(), "defaultPW");
        SystemUser newStaff = new SystemUser(defaultLogin, AccessType.STAFF, s.getStaffID(), s.getStaffType());
        log.info("Added new login info for user with username " + s.getUsername());
        return LoginSubsystem.getInstance().addUser(newStaff);
    }

    /**
     * addStaff = conn.prepareStatement("INSERT INTO " + staffTable + " (STAFFTYPE, FIRSTNAME, LASTNAME, PHONENUMBER, EMAIL, PROVIDER, USERNAME) VALUES(?, ?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
     * @param s
     * @return
     */
    @Override
    public ServiceStaff addStaff(ServiceStaff s) {
        ServiceStaff added = null;
        try {
            addStaff.setString(1, s.getStaffType().toString());
            addStaff.setString(2, s.getFirstName());
            addStaff.setString(3, s.getLastName());
            addStaff.setString(4, s.getPhoneNumber());
            addStaff.setString(5, s.getEmail());
            addStaff.setString(6, s.getProvider().toString());
            addStaff.setString(7, s.getUsername());
            //addStaff.setString(7, s.); //TODO: CHANGE THIS IF WE ACTUALLY IMPLEMENT WORK HOURS, OR DELETE
            addStaff.executeUpdate();
            ResultSet id = addStaff.getGeneratedKeys();
            if(!id.next()) {
                log.severe("Staff wasn't added correctly to the database");
                return null;
            }
            ServiceStaff complete = getStaff(id.getInt(1));
            /*
            if (!addNewStaffLogin(complete)) {
                log.severe("Failed to create login info.");
            }
            */
            return complete;


        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    /*
               updateStaff = conn.prepareStatement("UPDATE " + staffTable + " SET FIRSTNAME = ?, LASTNAME = ?, PHONENUMBER = ?, EMAIL = ?, PROVIDER = ?" +
                        " CERTIFICATION = ?, WHERE STAFFID = ?");
                updateStaffLangTable = conn.prepareStatement("UPDATE " + staffTableLanguages + " SET LANGUAGE = ? WHERE STAFFID = ?");

     */
    @Override
    public ArrayList<ServiceStaff> getAllStaff() {
        ArrayList<ServiceStaff> allStaff = new ArrayList<>();

        try {
            ResultSet all = getAllStaff.executeQuery();
            while (all.next()) {
                ServiceStaff gottenStaff = rsToStaff(all);
                if (gottenStaff != null) {
                    allStaff.add(gottenStaff);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
        return allStaff;
    }

    /**
     * Returns a list of ServiceStaff that are of a specific type
     * getStaffByType = conn.prepareStatement("SELECT * FROM " + staffTable + " WHERE STAFFTYPE = ?");

     * @param t
     * @return
     */
    @Override
    public ArrayList<ServiceStaff> getStaffByType(StaffType t) {
        ArrayList<ServiceStaff> allStaffType = new ArrayList<>();
        try {
            getStaffByType.setString(1, t.toString());
            ResultSet found = getStaffByType.executeQuery();
            while (found.next()) {
                ServiceStaff staff = rsToStaff(found);
                if (staff != null) {
                    allStaffType.add(staff);
                }
            }
            if (found != null) {
                found.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
        return allStaffType;
    }



    /**
     *
     * updateStaff = conn.prepareStatement("UPDATE " + staffTable + " SET FIRSTNAME = ?, LASTNAME = ?, PHONENUMBER = ?, EMAIL = ?, PROVIDER = ?, " +
     "USERNAME = ? WHERE STAFFID = ?");
     * @param s
     * @return
     */
    @Override
    public boolean updateStaff(ServiceStaff s) {
        try {
            updateStaff.setString(1, s.getFirstName());
            updateStaff.setString(2, s.getLastName());
            updateStaff.setString(3, s.getPhoneNumber());
            updateStaff.setString(4, s.getEmail());
            updateStaff.setString(5, s.getProvider().toString());
            updateStaff.setString(6, s.getUsername());
            updateStaff.setInt(7, s.getStaffID());
            updateStaff.executeUpdate();
            log.info("Staff member successfully updated.");

            return true;


        } catch (SQLException e) {
            log.info("Failed to update staff member General Interpreter info.");
            e.printStackTrace();
            return false;
        }
    }

    /**
     *
     * @param id
     * @return
     */

    public boolean deleteStaff(int id) {
        try {
            removeStaff.setInt(1, id);
            removeStaff.executeUpdate();
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
