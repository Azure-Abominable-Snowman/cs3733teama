package com.teama.requestsubsystem.spiritualcarefeature;

import com.teama.mapsubsystem.data.Floor;
import com.teama.mapsubsystem.data.Location;
import com.teama.requestsubsystem.GenericRequest;
import com.teama.requestsubsystem.Request;
import com.teama.requestsubsystem.RequestStatus;
import com.teama.requestsubsystem.RequestType;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * Created by aliss on 12/13/2017.
 */
public class SpiritualRequestDBTest {
    private String dbURL = "jdbc:derby:unittestdb;create=true";
    private String reqTable, genericReqTable;
    private Connection conn = null;
    private Statement stmt = null;
    // Make the database object to test
    SpiritualRequestDB db;

    public SpiritualRequestDBTest() {
        // this object connects directly to the db

        reqTable = "TEST_SPIR_REQUESTS";
        genericReqTable = "TEST_GENREQS_SPIR";

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

            stmt.execute("DROP TABLE " + reqTable);
            stmt.execute("DROP TABLE " + genericReqTable);
            stmt.close();
            System.out.println("Deleted the previous tables");
        } catch (SQLException e) {
            System.out.println("No previous generic/specific spiritual table");
            //e.printStackTrace();
        }

        db = new SpiritualRequestDB(dbURL, genericReqTable, reqTable);
    }

    @After
    public void tearDown() {
        db.close();
    }

    @Test
    public void addRequest() throws Exception {
        assertNull(db.getRequest(1));
        Request g = new GenericRequest(new Location(1459, 2009, Floor.GROUND, "BTM"), 35791, RequestType.SPIRITUAL,
                RequestStatus.ASSIGNED, "S elevator broken");
        SpiritualCareRequest baptism = new SpiritualCareRequest(g, Religion.CHRISTIANITY, SpiritualService.BAPTISMS, LocalDate.now());
        SpiritualCareRequest support = new SpiritualCareRequest(g, Religion.OTHER, SpiritualService.CONSULTATIONS, LocalDate.now());

        assertNotNull(db.addRequest(baptism));
        assertNotNull(db.addRequest(support));
    }
    @Test
    public void getSpiritualRequest() throws Exception {
        Request g = new GenericRequest(new Location(1459, 2009, Floor.GROUND, "BTM"), 35791, RequestType.SPIRITUAL,
                RequestStatus.ASSIGNED, "");
        SpiritualCareRequest baptism = new SpiritualCareRequest(g, Religion.CHRISTIANITY, SpiritualService.BAPTISMS, LocalDate.now());
        db.addRequest(baptism);

        SpiritualCareRequest retrieved = db.getSpiritualRequest(1);
        assertEquals(RequestStatus.ASSIGNED, retrieved.getStatus());
        assertEquals(Religion.CHRISTIANITY, retrieved.getReligion());
        assertEquals(SpiritualService.BAPTISMS, retrieved.getSpiritualService());
        assertEquals(LocalDate.now(), retrieved.getLocalDate());

    }


    @Test
    public void fulfillRequest() throws Exception {
        Request g = new GenericRequest(new Location(1459, 2009, Floor.GROUND, "BTM"), 35791, RequestType.SPIRITUAL,
                RequestStatus.ASSIGNED, "");
        SpiritualCareRequest baptism = new SpiritualCareRequest(g, Religion.CHRISTIANITY, SpiritualService.BAPTISMS, LocalDate.now());
        db.addRequest(baptism);



    }

    @Test
    public void updateRequest() throws Exception {


    }

    @Test
    public void deleteRequest() throws Exception {


    }


    @Test
    public void getSpiritualRequestsByStaff() throws Exception {


    }

    @Test
    public void getAllSpiritualRequests() throws Exception {

    }
}