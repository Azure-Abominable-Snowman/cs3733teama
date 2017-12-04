package com.teama.requestsubsystem.interpreterfeature;

import com.teama.mapsubsystem.data.Floor;
import com.teama.mapsubsystem.data.Location;
import com.teama.requestsubsystem.GenericRequest;
import com.teama.requestsubsystem.Request;
import com.teama.requestsubsystem.RequestStatus;
import com.teama.requestsubsystem.RequestType;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.sql.*;

import static org.junit.Assert.*;

/**
 * Created by aliss on 11/22/2017.
 */
public class InterpreterRequestDBTest {
    private String dbURL = "jdbc:derby:unittestdb;create=true";
    private String reqTable, genericReqTable;
    private Connection conn = null;
    private Statement stmt = null;
    // Make the database object to test
    InterpreterRequestDB db;

    public InterpreterRequestDBTest() {
        // this object connects directly to the db

        reqTable = "TEST_INTERP_REQUESTS";
        genericReqTable = "TEST_GENERIC_REQS";

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

            stmt.execute("DROP TABLE "+reqTable);
            stmt.execute("DROP TABLE "+genericReqTable);
            stmt.close();
            System.out.println("Deleted the previous tables");
        } catch (SQLException e) {
            System.out.println("No previous generic/specific interpreter table");
            //e.printStackTrace();
        }

        db = new InterpreterRequestDB(dbURL, genericReqTable, reqTable);
    }

    @After
    public void tearDown() {
        db.close();
    }

    @Test
    public void addRequest() throws Exception {
        assertNull(db.getRequest(1));
        Request g = new GenericRequest(new Location(1459, 2009, Floor.GROUND, "BTM"), 35791, RequestType.INTR,
                RequestStatus.ASSIGNED, "Can't read form");
        InterpreterRequest spanish = new InterpreterRequest(g, Language.Spanish);
        InterpreterRequest german = new InterpreterRequest(g, Language.German);
        InterpreterRequest russian = new InterpreterRequest(g, Language.Russian);
        assertNotNull(db.addRequest(spanish));
        assertEquals(Language.Spanish.toString(), db.getInterpreterRequest(1).getRequiredLanguage().name);
        assertNotNull(db.addRequest(german));
        assertNotNull(db.addRequest(russian));
        InterpreterRequest retrieved = db.getInterpreterRequest(3);
        assertEquals(0, retrieved.getFamilySize());

    }

    @Test
    public void deleteRequest() throws Exception {
        GenericRequest g = new GenericRequest(new Location(1459, 2009, Floor.GROUND, "BTM"), 35791,RequestType.INTR, RequestStatus.ASSIGNED, "Can't read form");
        InterpreterRequest spanish = new InterpreterRequest(g, Language.Spanish);
        InterpreterRequest german = new InterpreterRequest(g, Language.German);
        InterpreterRequest russian = new InterpreterRequest(g, Language.Russian);
        db.addRequest(spanish);
        db.addRequest(german);
        db.addRequest(russian);
        assertEquals(Language.Spanish.toString(), db.getInterpreterRequest(1).getRequiredLanguage().toString());
        db.deleteRequest(1);
        db.deleteRequest(3);
        assertNull(db.getRequest(1));
        assertEquals(Language.German.name, db.getInterpreterRequest(2).getRequiredLanguage().name);
    }

    @Test
    public void getRequest() throws Exception {
        assertNull(db.getRequest(8));

        Request g = new GenericRequest(new Location(1459, 2009, Floor.GROUND, "BTM"), 35791, RequestType.INTR,
                RequestStatus.ASSIGNED, "Can't read form");
        InterpreterRequest spanish = new InterpreterRequest(g, Language.Spanish);
        assertNotNull(db.addRequest(spanish));

        InterpreterRequest retrieved = db.getInterpreterRequest(1);
        assertNotNull(retrieved);
        assertEquals(retrieved.getRequestID(), 1);
        assertEquals(retrieved.getLocation().getxCoord(), 1459);
        assertEquals(retrieved.getLocation().getyCoord(), 2009);
        assertEquals(retrieved.getLocation().getLevel(), Floor.GROUND);
        assertEquals(retrieved.getLocation().getBuilding(), "BTM");
        assertEquals(retrieved.getStaffID(), 35791);
        assertEquals(retrieved.getReqType(), RequestType.INTR);
        assertEquals(retrieved.getStatus(), RequestStatus.ASSIGNED);
        assertEquals(retrieved.getNote(), "Can't read form");
        assertEquals(retrieved.getRequiredLanguage(), Language.Spanish);
    }

    @Test
    public void getAllRequests() throws Exception {
        /*
        GenericRequest g = new GenericRequest(new Location(1459, 2009, Floor.GROUND, "BTM"), 35791, "Can't read form");
        InterpreterRequest spanish = new InterpreterRequest(g, 3, Language.Spanish);
        InterpreterRequest german = new InterpreterRequest(g, 2, Language.German);
        InterpreterRequest russian = new InterpreterRequest(g, 1, Language.Russian);
        db.addRequest(spanish);
        db.addRequest(german);
        db.addRequest(russian);
        assertEquals(3, db.getAllRequests(RequestStatus.ASSIGNED).size());
        assertTrue(db.getAllRequests(RequestStatus.CLOSED).isEmpty());
        */
    }

    @Test
    public void fulfillRequest() throws Exception {

        GenericRequest g = new GenericRequest(new Location(1459, 2009, Floor.GROUND, "BTM"), 35791, RequestType.INTR,RequestStatus.ASSIGNED,"Can't read form");
        InterpreterRequest spanish = new InterpreterRequest(g, Language.Spanish);
        spanish = db.addRequest(spanish);
        assertEquals(RequestStatus.ASSIGNED.toString(), db.getRequest(1).getStatus().toString());
        spanish.setServiceTime(60);
        spanish.setTranslationTypes(TranslationType.WRITTEN);
        db.fulfillRequest(spanish);
        InterpreterRequest fulfilled = db.getInterpreterRequest(1);
        assertEquals(RequestStatus.CLOSED.toString(), fulfilled.getStatus().toString());

        // Reports, not implemented yet
        /*PreparedStatement getReport = conn.prepareStatement("SELECT * FROM " + reqTable + " WHERE REQUESTID = ?");
        getReport.setInt(1, 1);
        ResultSet rs = getReport.executeQuery();
        if (rs.next()) {
            assertEquals(spanish.getServiceTime(), rs.getDouble("SERVICETIME"), 0.01);
            assertEquals(spanish.getTranslType().toString(), rs.getString("TRANSTYPE"));
        }
        rs.close();
        getReport.close();*/


    }

    @Test
    public void updateRequest() throws Exception {

        GenericRequest g = new GenericRequest(new Location(1459, 2009, Floor.GROUND, "BTM"), 35791, RequestType.INTR, RequestStatus.ASSIGNED, "Cannot read form");
        InterpreterRequest german = new InterpreterRequest(g, Language.German);
        db.addRequest(german);
        InterpreterRequest germanRetrieved = db.getInterpreterRequest(1);
        assertEquals(35791, germanRetrieved.getStaffID());
        germanRetrieved.setStaffID(1919);
        db.updateRequest(germanRetrieved);
        assertEquals(1919, db.getRequest(1).getStaffID());

    }

}