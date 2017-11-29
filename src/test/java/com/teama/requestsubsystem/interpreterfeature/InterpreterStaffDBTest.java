package com.teama.requestsubsystem.interpreterfeature;

import com.teama.messages.ContactInfo;
import com.teama.messages.ContactInfoTypes;
import com.teama.messages.Provider;
import com.teama.requestsubsystem.GenericStaffInfo;
import org.junit.Before;
import org.junit.Test;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;

/**
 * Created by aliss on 11/22/2017.
 */
public class InterpreterStaffDBTest {
    private String dbURL = "jdbc:derby:unittestdb;create=true";
    private String staffTable, langTable;
    private Connection conn = null;
    private Statement stmt = null;
    // Make the database object to test
    InterpreterStaffDB db;

    public InterpreterStaffDBTest() {
        // this object connects directly to the db

        staffTable = "TEST_STAFFTABLE";
        langTable = "TEST_LANGTABLE";

        try {
            Class.forName("org.apache.derby.jdbc.EmbeddedDriver").newInstance();
            // Get a connection
            conn = DriverManager.getConnection(dbURL);
        }
        catch (Exception except) {
            except.printStackTrace();
        }
/*
        // Delete table from last time
        try {
            stmt = conn.createStatement();
            stmt.execute("DROP TABLE TEST_LANGTABLE"); // drop the report table first because it has foreign key in request table

            stmt.execute("DROP TABLE TEST_STAFFTABLE");
            stmt.close();
            System.out.println("Deleted the previous tables");
        } catch(SQLException e) {
            //System.out.println("No previous table");

           e.printStackTrace();
        }

        db = new InterpreterStaffDB(dbURL, staffTable, langTable);
        */
    }

    @Before
    public void setup() {
        try {
            stmt = conn.createStatement();
            stmt.execute("DROP TABLE TEST_LANGTABLE"); // drop the report table first because it has foreign key in request table

            stmt.execute("DROP TABLE TEST_STAFFTABLE");
            stmt.close();
            System.out.println("Deleted the previous tables");
        } catch (SQLException e) {
            //System.out.println("No previous table");

            e.printStackTrace();
        }
        db = new InterpreterStaffDB(dbURL, staffTable, langTable);

    }

    @Test
    public void addStaff() throws Exception {
        Set<ContactInfoTypes> avail = new HashSet<ContactInfoTypes>();
        avail.add(ContactInfoTypes.EMAIL);
        avail.add(ContactInfoTypes.TEXT);
        avail.add(ContactInfoTypes.PHONE);
        Set<Language> langs = new HashSet<>();
        langs.add(Language.ASL);
        langs.add(Language.French);
        langs.add(Language.Moldovan);
        langs.add(Language.JAVA);
        ContactInfo c = new ContactInfo(avail, "4444441134", "wwong2@wpi.edu", Provider.ATT);
        GenericStaffInfo g = new GenericStaffInfo("William", "Wong", c);
        InterpreterInfo i = new InterpreterInfo(langs, CertificationType.CCHI);
        InterpreterStaff wilson = new InterpreterStaff(g, i);
        assertTrue(db.addStaff(wilson));
        PreparedStatement p = conn.prepareStatement("SELECT * FROM " + staffTable + " WHERE STAFFID = ?");
        p.setInt(1, 1);
        ResultSet s = p.executeQuery();
        if (s.next()) {
            assertEquals(CertificationType.CCHI.toString(), s.getString("CERTIFICATION"));
            assertEquals(wilson.getFirstName(), s.getString("FIRSTNAME"));
        }
        p.close();
        s.close();
    }

    @Test
    public void findQualified() throws Exception {

        Set<ContactInfoTypes> avail = new HashSet<ContactInfoTypes>();
        avail.add(ContactInfoTypes.EMAIL);
        avail.add(ContactInfoTypes.TEXT);
        avail.add(ContactInfoTypes.PHONE);
        ContactInfo c = new ContactInfo(avail, "4444441134", "wwong2@wpi.edu", Provider.ATT);

        Set<Language> langs = new HashSet<>();
        langs.add(Language.ASL);
        langs.add(Language.French);
        langs.add(Language.Moldovan);
        langs.add(Language.JAVA);

        Set<Language> langs2 = new HashSet<>();
        langs2.add(Language.Moldovan);
        langs2.add(Language.German);
        InterpreterInfo i2 = new InterpreterInfo(langs2, CertificationType.CCHI);


        GenericStaffInfo g = new GenericStaffInfo("William", "Wong", c);
        InterpreterInfo i = new InterpreterInfo(langs, CertificationType.CCHI);
        InterpreterStaff wilson = new InterpreterStaff(g, i);
        GenericStaffInfo g2 = new GenericStaffInfo("Joe", "J", c);
        InterpreterStaff joe = new InterpreterStaff(g2, i2);

        db.addStaff(joe);
        db.addStaff(wilson);

        assertTrue((db.findQualified(Language.Luxembourgish)).isEmpty());
        assertEquals(2, db.findQualified(Language.Moldovan).size());
        assertEquals(1, db.findQualified(Language.German).size());
        ArrayList<InterpreterStaff> qualified = db.findQualified(Language.ASL);
        assertEquals(qualified.get(0).getLastName(), wilson.getLastName());
    }

    @Test
    public void getStaff() throws Exception {
        Set<ContactInfoTypes> avail = new HashSet<ContactInfoTypes>();
        avail.add(ContactInfoTypes.EMAIL);
        avail.add(ContactInfoTypes.TEXT);
        avail.add(ContactInfoTypes.PHONE);
        Set<Language> langs = new HashSet<>();
        langs.add(Language.ASL);
        langs.add(Language.French);
        langs.add(Language.Moldovan);
        langs.add(Language.JAVA);
        ContactInfo c = new ContactInfo(avail, "4444441134", "wwong2@wpi.edu", Provider.ATT);
        GenericStaffInfo g = new GenericStaffInfo("William", "Wong", c);
        InterpreterInfo i = new InterpreterInfo(langs, CertificationType.CCHI);
        InterpreterStaff wilson = new InterpreterStaff(g, i);
        GenericStaffInfo g2 = new GenericStaffInfo("Joe", "J", c);
        InterpreterStaff joe = new InterpreterStaff(g2, i);
        db.addStaff(joe);
        db.addStaff(wilson);
        assertNotNull(db.getStaff(1));
        assertNotNull(db.getStaff(2));
        assertEquals(joe.getLastName(), db.getStaff(1).getLastName());
        assertEquals(wilson.getFirstName(), db.getStaff(2).getFirstName());
        for (InterpreterStaff s : db.getAllStaff()) {
            System.out.println(s.getLastName());
        }

    }

    @Test
    public void updateStaff() throws Exception {
        Set<ContactInfoTypes> avail = new HashSet<ContactInfoTypes>();
        avail.add(ContactInfoTypes.EMAIL);
        avail.add(ContactInfoTypes.TEXT);
        avail.add(ContactInfoTypes.PHONE);
        Set<Language> langs = new HashSet<>();
        langs.add(Language.ASL);
        langs.add(Language.French);
        langs.add(Language.Moldovan);
        langs.add(Language.JAVA);
        ContactInfo c = new ContactInfo(avail, "4444441134", "wwong2@wpi.edu", Provider.ATT);
        GenericStaffInfo g = new GenericStaffInfo("William", "Wong", c);
        InterpreterInfo i = new InterpreterInfo(langs, CertificationType.CCHI);
        InterpreterStaff wilson = new InterpreterStaff(g, i);
        db.addStaff(wilson);
        assertEquals(wilson.getFirstName(), db.getStaff(1).getFirstName());
        InterpreterStaff retrieved = db.getStaff(1);
        retrieved.setFirstName("Wilson");
        db.updateStaff(retrieved);
        assertEquals("Wong", db.getStaff(1).getLastName());
        assertEquals(retrieved.getFirstName(), db.getStaff(1).getFirstName());


    }

    @Test
    public void deleteStaff() throws Exception {
        Set<ContactInfoTypes> avail = new HashSet<ContactInfoTypes>();
        avail.add(ContactInfoTypes.EMAIL);
        avail.add(ContactInfoTypes.TEXT);
        avail.add(ContactInfoTypes.PHONE);
        Set<Language> langs = new HashSet<>();
        langs.add(Language.ASL);
        langs.add(Language.French);
        langs.add(Language.Moldovan);
        langs.add(Language.JAVA);
        ContactInfo c = new ContactInfo(avail, "4444441134", "wwong2@wpi.edu", Provider.ATT);
        GenericStaffInfo g = new GenericStaffInfo("William", "Wong", c);
        InterpreterInfo i = new InterpreterInfo(langs, CertificationType.CCHI);
        InterpreterStaff wilson = new InterpreterStaff(g, i);
        db.addStaff(wilson);
        db.deleteStaff(1);
        assertNull(db.getStaff(1));

    }

}