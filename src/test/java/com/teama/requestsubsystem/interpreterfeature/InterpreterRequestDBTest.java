package com.teama.requestsubsystem.interpreterfeature;

import com.teama.mapsubsystem.data.Floor;
import com.teama.mapsubsystem.data.Location;
import com.teama.requestsubsystem.GenericRequestInfo;
import com.teama.requestsubsystem.RequestStatus;
import org.junit.Before;
import org.junit.Test;

import java.sql.*;

import static org.junit.Assert.*;

/**
 * Created by aliss on 11/22/2017.
 */
public class InterpreterRequestDBTest {
    private String dbURL = "jdbc:derby:unittestdb;create=true";
    private String reqTable, reportTable;
    private Connection conn = null;
    private Statement stmt = null;
    // Make the database object to test
    InterpreterRequestDB db;

    public InterpreterRequestDBTest() {
        // this object connects directly to the db

        reqTable = "TEST_REQUESTS";
        reportTable = "TEST_REPORTS";

        try {
            Class.forName("org.apache.derby.jdbc.EmbeddedDriver").newInstance();
            // Get a connection
            conn = DriverManager.getConnection(dbURL);
        } catch (Exception except) {
            except.printStackTrace();
        }
    }
    @Before
    public void setup() {
        try {
            stmt = conn.createStatement();

            stmt.execute("DROP TABLE TEST_REPORTS"); // drop the report table first because it has foreign key in request table
            stmt.execute("DROP TABLE TEST_REQUESTS");
            stmt.close();
            System.out.println("Deleted the previous tables");
        } catch (SQLException e) {
            System.out.println("No previous table");
            e.printStackTrace();
        }

        db = new InterpreterRequestDB(dbURL, reqTable, reportTable);


    }


    @Test
    public void addRequest() throws Exception {
        assertNull(db.getRequest(1));
        GenericRequestInfo g = new GenericRequestInfo(new Location(1459, 2009, Floor.GROUND, "BTM"), 35791, "Can't read form");
        InterpreterRequest spanish = new InterpreterRequest(g, 3, Language.Spanish);
        InterpreterRequest german = new InterpreterRequest(g, 2, Language.German);
        InterpreterRequest russian = new InterpreterRequest(g, 1, Language.Russian);
        assertTrue(db.addRequest(spanish));
        assertEquals(Language.Spanish.toString(), db.getRequest(1).getRequiredLanguage().name);
        assertTrue(db.addRequest(german));
        assertTrue(db.addRequest(russian));
        InterpreterRequest retrieved = db.getRequest(3);
        assertEquals(1, retrieved.getFamilySize());

    }

    @Test
    public void deleteRequest() throws Exception {
        GenericRequestInfo g = new GenericRequestInfo(new Location(1459, 2009, Floor.GROUND, "BTM"), 35791, "Can't read form");
        InterpreterRequest spanish = new InterpreterRequest(g, 3, Language.Spanish);
        InterpreterRequest german = new InterpreterRequest(g, 2, Language.German);
        InterpreterRequest russian = new InterpreterRequest(g, 1, Language.Russian);
        db.addRequest(spanish);
        db.addRequest(german);
        db.addRequest(russian);
        assertEquals(Language.Spanish.toString(), db.getRequest(1).getRequiredLanguage().name);
        db.deleteRequest(1);
        db.deleteRequest(3);
        assertNull(db.getRequest(1));
        assertEquals(Language.German.name, db.getRequest(2).getRequiredLanguage().name);
    }

    @Test
    public void getRequest() throws Exception {
        assertNull(db.getRequest(8));

        PreparedStatement p = conn.prepareStatement("INSERT INTO " + reqTable + "(STAFFID, XCOORD, YCOORD, LVL, BUILDING, STATUS, FAMSIZE, LANG, NOTE) " +
        " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)");
        p.setInt(1, 139);
        p.setInt(2, 1293);
        p.setInt(3, 64);
        p.setString(4, "G");
        p.setString(5, "BTM");
        p.setString(6, RequestStatus.ASSIGNED.toString());
        p.setInt(7, 12);
        p.setString(8, Language.French.toString());
        p.setString(9, "note");
        p.executeUpdate();
        assertNotNull(db.getRequest(1));
        assertEquals(1293, db.getRequest(1).getLocation().getxCoord());
        assertEquals("note", db.getRequest(1).getNote());


    }

    @Test
    public void getAllRequests() throws Exception {
        GenericRequestInfo g = new GenericRequestInfo(new Location(1459, 2009, Floor.GROUND, "BTM"), 35791, "Can't read form");
        InterpreterRequest spanish = new InterpreterRequest(g, 3, Language.Spanish);
        InterpreterRequest german = new InterpreterRequest(g, 2, Language.German);
        InterpreterRequest russian = new InterpreterRequest(g, 1, Language.Russian);
        db.addRequest(spanish);
        db.addRequest(german);
        db.addRequest(russian);
        assertEquals(3, db.getAllRequests(RequestStatus.ASSIGNED).size());
        assertTrue(db.getAllRequests(RequestStatus.CLOSED).isEmpty());
    }

    @Test
    public void fulfillRequest() throws Exception {
        GenericRequestInfo g = new GenericRequestInfo(new Location(1459, 2009, Floor.GROUND, "BTM"), 35791, "Can't read form");
        InterpreterRequest spanish = new InterpreterRequest(g, 3, Language.Spanish);
        db.addRequest(spanish);
        assertEquals(RequestStatus.ASSIGNED.toString(), db.getRequest(1).getStatus().toString());
        spanish.setServiceTime(60);
        spanish.setTranslationTypes(TranslationType.WRITTEN);
        db.fulfillRequest(spanish);
        InterpreterRequest fulfilled = db.getRequest(1);

        assertEquals(RequestStatus.CLOSED.toString(), fulfilled.getStatus().toString());
        PreparedStatement getReport = conn.prepareStatement("SELECT * FROM " + reportTable + " WHERE REQUESTID = ?");
        getReport.setInt(1, 1);
        ResultSet rs = getReport.executeQuery();
        if (rs.next()) {
            assertEquals(spanish.getServiceTime(), rs.getDouble("SERVICETIME"), 0.01);
            assertEquals(spanish.getTranslType().toString(), rs.getString("TRANSTYPE"));
        }
        rs.close();
        getReport.close();

    }

    @Test
    public void updateRequest() throws Exception {
        GenericRequestInfo g = new GenericRequestInfo(new Location(1459, 2009, Floor.GROUND, "BTM"), 35791, "Cannot read form");
        InterpreterRequest german = new InterpreterRequest(g, 45, Language.German);
        db.addRequest(german);
        InterpreterRequest germanRetrieved = db.getRequest(1);
        assertEquals(35791, germanRetrieved.getStaffID());
        germanRetrieved.setStaffID(1919);
        db.updateRequest(germanRetrieved);
        assertEquals(1919, db.getRequest(1).getStaffID());
    }

}