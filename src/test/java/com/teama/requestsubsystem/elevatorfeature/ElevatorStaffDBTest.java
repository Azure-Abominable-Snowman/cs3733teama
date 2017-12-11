package com.teama.requestsubsystem.elevatorfeature;

import com.teama.messages.ContactInfo;
import com.teama.messages.ContactInfoTypes;
import com.teama.messages.Provider;
import com.teama.requestsubsystem.GenericStaff;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;

/**
 * Created by aliss on 12/10/2017.
 */
public class ElevatorStaffDBTest {
    private String dbURL = "jdbc:derby:unittestdb;create=true";
    private String staffTable, generalStaffTable;
    private Connection conn = null;
    private Statement stmt = null;
    // Make the database object to test
    ElevatorStaffDB db;

    public ElevatorStaffDBTest() {
        // this object connects directly to the db

        staffTable = "TEST_ELEV_STAFFTABLE";
        generalStaffTable = "TEST_GEN_STAFF_ELEV";

        try {
            Class.forName("org.apache.derby.jdbc.EmbeddedDriver").newInstance();
            // Get a connection
            conn = DriverManager.getConnection(dbURL);
        }
        catch (Exception except) {
            except.printStackTrace();
        }
    }

    /**
     * Before anything, reset the general and specific table
     */
    @Before
    public void setup() {
        try {
            stmt = conn.createStatement();
            stmt.execute("DROP TABLE "+staffTable); // drop the specific staff table first
            stmt.close();
            stmt = conn.createStatement();
            stmt.execute("DROP TABLE "+ generalStaffTable); // Drop general staff table
            stmt.close();
            System.out.println("Deleted the previous tables");
        } catch (SQLException e) {
            System.out.println("No previous staff and/or general table");
            //e.printStackTrace();
        }
        db = new ElevatorStaffDB(dbURL, generalStaffTable, staffTable);

    }

    @After
    public void tearDown() {
        db.close();
    }

    @Test
    public void addStaff() throws Exception {
        Set<ContactInfoTypes> avail = new HashSet<>();
        avail.add(ContactInfoTypes.EMAIL);
        avail.add(ContactInfoTypes.TEXT);
        avail.add(ContactInfoTypes.PHONE);
        ContactInfo c = new ContactInfo(avail, "4444441134", "wwong2@wpi.edu", Provider.ATT);
        GenericStaff g = new GenericStaff("William", "Wong", c);
        Set<MaintenanceType> specializations = new HashSet<>();
        specializations.add(MaintenanceType.CODECHECK);
        specializations.add(MaintenanceType.PERSONTRAPPED);
        ElevatorStaff wilson = new ElevatorStaff(g, specializations);
        assertTrue(db.addStaff(wilson));
    }
    @Test
    public void getStaff() throws Exception {
        Set<ContactInfoTypes> avail = new HashSet<>();
        avail.add(ContactInfoTypes.EMAIL);
        avail.add(ContactInfoTypes.TEXT);
        avail.add(ContactInfoTypes.PHONE);
        ContactInfo c = new ContactInfo(avail, "4444441134", "wwong2@wpi.edu", Provider.ATT);
        GenericStaff g = new GenericStaff("William", "Wong", c);
        Set<MaintenanceType> specializations = new HashSet<>();
        specializations.add(MaintenanceType.CODECHECK);
        specializations.add(MaintenanceType.PERSONTRAPPED);
        ElevatorStaff wilson = new ElevatorStaff(g, specializations);
        db.addStaff(wilson);
        ElevatorStaff found = db.getElevatorStaff(1);
        assertNotNull(found);
        assertEquals(2, found.getSpecialization().size(), 0.1);


    }

    @Test
    public void updateStaff() throws Exception {
        Set<ContactInfoTypes> avail = new HashSet<>();
        avail.add(ContactInfoTypes.EMAIL);
        avail.add(ContactInfoTypes.TEXT);
        avail.add(ContactInfoTypes.PHONE);
        ContactInfo c = new ContactInfo(avail, "4444441134", "wwong2@wpi.edu", Provider.ATT);
        GenericStaff g = new GenericStaff("William", "Wong", c);
        Set<MaintenanceType> specializations = new HashSet<>();
        specializations.add(MaintenanceType.CODECHECK);
        specializations.add(MaintenanceType.PERSONTRAPPED);
        ElevatorStaff wilson = new ElevatorStaff(g, specializations);
        db.addStaff(wilson);
        ElevatorStaff found = db.getElevatorStaff(1);

        Set<MaintenanceType> moreSkills = found.getSpecialization();
        moreSkills.add(MaintenanceType.REPAIRPARTS);
        moreSkills.add(MaintenanceType.TESTS);
        found.setSpecialization(moreSkills);
        assertTrue(db.updateStaff(found));
        ElevatorStaff updated = db.getElevatorStaff(1);
        assertEquals(4, updated.getSpecialization().size(), 0.1);

    }

    @Test
    public void deleteStaff() throws Exception {
        Set<ContactInfoTypes> avail = new HashSet<>();
        avail.add(ContactInfoTypes.EMAIL);
        avail.add(ContactInfoTypes.TEXT);
        avail.add(ContactInfoTypes.PHONE);
        ContactInfo c = new ContactInfo(avail, "4444441134", "wwong2@wpi.edu", Provider.ATT);
        GenericStaff g = new GenericStaff("William", "Wong", c);
        Set<MaintenanceType> specializations = new HashSet<>();
        specializations.add(MaintenanceType.CODECHECK);
        specializations.add(MaintenanceType.PERSONTRAPPED);
        ElevatorStaff wilson = new ElevatorStaff(g, specializations);
        db.addStaff(wilson);
        assertTrue(db.deleteStaff(1));
        assertNull(db.getElevatorStaff(1));

    }





    @Test
    public void findQualified() throws Exception {
        Set<ContactInfoTypes> avail = new HashSet<>();
        avail.add(ContactInfoTypes.EMAIL);
        avail.add(ContactInfoTypes.TEXT);
        avail.add(ContactInfoTypes.PHONE);
        ContactInfo c = new ContactInfo(avail, "4444441134", "wwong2@wpi.edu", Provider.ATT);
        GenericStaff g = new GenericStaff("William", "Wong", c);
        Set<MaintenanceType> specializations = new HashSet<>();
        specializations.add(MaintenanceType.CODECHECK);
        specializations.add(MaintenanceType.PERSONTRAPPED);
        ElevatorStaff wilson = new ElevatorStaff(g, specializations);

        Set<MaintenanceType> otherSpecs = new HashSet<>();
        otherSpecs.add(MaintenanceType.CODECHECK);
        otherSpecs.add(MaintenanceType.SAFETYCHECKS);
        otherSpecs.add(MaintenanceType.REPAIRPARTS);

        ElevatorStaff superWilson = new ElevatorStaff(g, otherSpecs);
        db.addStaff(wilson);
        db.addStaff(superWilson);
        ArrayList<ElevatorStaff> codeCheckers = db.findQualified(MaintenanceType.CODECHECK);
        assertEquals(2, codeCheckers.size(), 0.1);
        ArrayList<ElevatorStaff> personTrapped = db.findQualified(MaintenanceType.PERSONTRAPPED);
        assertEquals(1, personTrapped.size(), 0.1);
        ElevatorStaff qual = personTrapped.get(0);
        assertEquals(1, qual.getStaffID());
        assertTrue(db.findQualified(MaintenanceType.TESTS).isEmpty());

    }



}