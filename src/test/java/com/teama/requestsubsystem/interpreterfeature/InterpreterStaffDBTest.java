package com.teama.requestsubsystem.interpreterfeature;

import com.teama.messages.ContactInfo;
import com.teama.messages.ContactInfoTypes;
import com.teama.messages.Provider;
import com.teama.requestsubsystem.GenericStaff;
import com.teama.requestsubsystem.ServiceStaff;
import com.teama.requestsubsystem.StaffType;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertNull;

/**
 * Created by aliss on 11/22/2017.
 */
public class InterpreterStaffDBTest {
    private String dbURL = "jdbc:derby:unittestdb;create=true";
    private String staffTable, generalStaffTable;
    private Connection conn = null;
    private Statement stmt = null;
    // Make the database object to test
    InterpreterStaffDB db;

    public InterpreterStaffDBTest() {
        // this object connects directly to the db

        staffTable = "TEST_INTERP_STAFFTABLE";
        generalStaffTable = "TEST_GENERAL_STAFFTABLE";

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
        db = new InterpreterStaffDB(dbURL, generalStaffTable, staffTable);

    }

    @After
    public void tearDown() {
        db.close();
    }

    @Test
    public void addStaff() throws Exception {
        // Test adding a staff member to the database, see if it's there and correct
        Set<ContactInfoTypes> avail = new HashSet<>();
        avail.add(ContactInfoTypes.EMAIL);
        avail.add(ContactInfoTypes.TEXT);
        avail.add(ContactInfoTypes.PHONE);
        Set<Language> langs = new HashSet<>();
        langs.add(Language.ASL);
        langs.add(Language.French);
        langs.add(Language.Moldovan);
        langs.add(Language.JAVA);
        ContactInfo c = new ContactInfo(avail, "4444441134", "wwong2@wpi.edu", Provider.ATT);
        GenericStaff g = new GenericStaff("William", "Wong", c);
        InterpreterStaff wilson = new InterpreterStaff(g, langs, CertificationType.CCHI);
        assertTrue(db.addStaff(wilson));

        // Check the generic database information
        PreparedStatement p = conn.prepareStatement("SELECT * FROM " + generalStaffTable + " WHERE STAFFID = ?");
        p.setInt(1, 1);
        ResultSet s = p.executeQuery();
        if (s.next()) {
            assertEquals(wilson.getFirstName(), s.getString("FIRSTNAME"));
            assertEquals(wilson.getLastName(), s.getString("LASTNAME"));
            assertEquals(1, s.getInt("STAFFID"));
            assertEquals(StaffType.INTERPRETER.toString(), s.getString("STAFFTYPE"));
            assertEquals(wilson.getPhoneNumber(), s.getString("PHONENUMBER"));
            assertEquals(wilson.getEmail(), s.getString("EMAIL"));
            assertEquals(wilson.getProvider().toString(), s.getString("PROVIDER"));
            assertEquals(wilson.getUsername(), s.getString("USERNAME"));
        }
        p.close();
        s.close();

        // Check the interpreter specific information
        p = conn.prepareStatement("SELECT * FROM " + staffTable + " WHERE STAFFID = ?");
        p.setInt(1, 1);
        s = p.executeQuery();
        while(s.next()) {
            assertTrue(wilson.getLanguages().contains(Language.valueOf(s.getString("LANGUAGE"))));
            assertEquals(wilson.getCertification(), CertificationType.getCertificationType(s.getString("CERTIFICATION")));
        }
        p.close();
        s.close();
    }

    @Test
    public void findQualified() throws Exception {
        Set<ContactInfoTypes> avail = new HashSet<>();
        avail.add(ContactInfoTypes.EMAIL);
        avail.add(ContactInfoTypes.TEXT);
        avail.add(ContactInfoTypes.PHONE);

        Set<Language> langs = new HashSet<>();
        langs.add(Language.Russian);
        langs.add(Language.JAVA);

        Set<Language> langs2 = new HashSet<>();
        langs2.add(Language.Luxembourgish);
        langs2.add(Language.German);
        langs2.add(Language.JAVA);

        // Test other scripts (Unicode support)
        ContactInfo c1 = new ContactInfo(avail, "4444441134", "bigboy@wpi.edu", Provider.ATT);
        GenericStaff g1 = new GenericStaff("Боевой", "водитель", c1);
        InterpreterStaff tankista = new InterpreterStaff(g1, langs, CertificationType.CCHI);

        ContactInfo c = new ContactInfo(avail, "4444441134", "willywongton@wpi.edu", Provider.VERIZON);
        GenericStaff g2 = new GenericStaff("Willy", "Wong", c);
        InterpreterStaff willyWong = new InterpreterStaff(g2, langs2, CertificationType.CDI);

        db.addStaff(tankista);
        db.addStaff(willyWong);

        // Test to see if the correct amount of people are returned
        assertEquals(1, db.findQualified(Language.Luxembourgish).size());
        assertEquals(1, db.findQualified(Language.Russian).size());
        assertEquals(1, db.findQualified(Language.German).size());
        assertEquals(2, db.findQualified(Language.JAVA).size());

        // Test to see if the correct people are returned
        ArrayList<InterpreterStaff> qualified = db.findQualified(Language.Luxembourgish);
        assertEquals(qualified.get(0).getLastName(), willyWong.getLastName());
        qualified = db.findQualified(Language.Russian);
        assertEquals(qualified.get(0).getLastName(), tankista.getLastName());

    }

    @Test
    public void getStaff() throws Exception {

        Set<ContactInfoTypes> avail = new HashSet<>();
        avail.add(ContactInfoTypes.EMAIL);
        avail.add(ContactInfoTypes.TEXT);
        avail.add(ContactInfoTypes.PHONE);

        Set<Language> langs = new HashSet<>();
        langs.add(Language.Russian);
        langs.add(Language.JAVA);

        Set<Language> langs2 = new HashSet<>();
        langs2.add(Language.Luxembourgish);
        langs2.add(Language.German);
        langs2.add(Language.JAVA);

        // Test other scripts (Unicode support)
        ContactInfo c1 = new ContactInfo(avail, "4444441134", "bigboy@wpi.edu", Provider.ATT);
        GenericStaff g1 = new GenericStaff("Боевой", "водитель", c1);
        InterpreterStaff tankista = new InterpreterStaff(g1, langs, CertificationType.CCHI);

        ContactInfo c = new ContactInfo(avail, "4444441134", "willywongton@wpi.edu", Provider.VERIZON);
        GenericStaff g2 = new GenericStaff("Willy", "Wong", c);
        InterpreterStaff willyWong = new InterpreterStaff(g2, langs2, CertificationType.CDI);

        db.addStaff(tankista);
        db.addStaff(willyWong);

        assertNotNull(db.getStaff(1));
        assertNotNull(db.getStaff(2));
        assertEquals(tankista.getLastName(), db.getStaff(1).getLastName());
        assertEquals(willyWong.getFirstName(), db.getStaff(2).getFirstName());

        for (ServiceStaff s : db.getAllStaff()) {
            System.out.println(s.getLastName());
        }


    }

    @Test
    public void updateStaff() throws Exception {

        Set<ContactInfoTypes> avail = new HashSet<>();
        avail.add(ContactInfoTypes.EMAIL);
        avail.add(ContactInfoTypes.TEXT);
        avail.add(ContactInfoTypes.PHONE);

        Set<Language> langs = new HashSet<>();
        langs.add(Language.Luxembourgish);
        langs.add(Language.German);
        langs.add(Language.JAVA);

        ContactInfo c = new ContactInfo(avail, "4444441134", "willywongton@wpi.edu", Provider.VERIZON);
        GenericStaff g2 = new GenericStaff("Willy", "Wong", c);
        InterpreterStaff willyWong = new InterpreterStaff(g2, langs, CertificationType.CDI);

        db.addStaff(willyWong);

        assertEquals(willyWong.getFirstName(), db.getStaff(1).getFirstName());
        InterpreterStaff retrieved = db.getInterpreterStaff(1);
        langs.add(Language.German);
        retrieved.setFirstName("WillyWillyWongTong");
        retrieved.setLanguages(langs);
        db.updateStaff(retrieved);
        assertEquals("WillyWillyWongTong", db.getInterpreterStaff(1).getFirstName());
        assertEquals(retrieved.getLastName(), db.getInterpreterStaff(1).getLastName());
        assertEquals(retrieved.getLanguages().size(), db.getInterpreterStaff(1).getLanguages().size());


    }

    @Test
    public void deleteStaff() throws Exception {

        Set<ContactInfoTypes> avail = new HashSet<>();
        avail.add(ContactInfoTypes.EMAIL);
        avail.add(ContactInfoTypes.TEXT);
        avail.add(ContactInfoTypes.PHONE);

        Set<Language> langs = new HashSet<>();
        langs.add(Language.Luxembourgish);
        langs.add(Language.German);
        langs.add(Language.JAVA);

        ContactInfo c = new ContactInfo(avail, "4444441134", "willywongton@wpi.edu", Provider.VERIZON);
        GenericStaff g2 = new GenericStaff("Willy", "Wong", c);
        InterpreterStaff willyWong = new InterpreterStaff(g2, langs, CertificationType.CDI);

        db.addStaff(willyWong);

        db.deleteStaff(1);

        assertNull(db.getStaff(1));
    }

}