package com.teama.requestsubsystem.elevatorfeature;

import com.teama.mapsubsystem.data.Floor;
import com.teama.mapsubsystem.data.Location;
import com.teama.requestsubsystem.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * Created by aliss on 12/10/2017.
 */
public class ElevatorRequestDBTest {
    private String dbURL = "jdbc:derby:unittestdb;create=true";
    private String reqTable, genericReqTable;
    private Connection conn = null;
    private Statement stmt = null;
    // Make the database object to test
    ElevatorRequestDB db;

    public ElevatorRequestDBTest() {
        // this object connects directly to the db

        reqTable = "TEST_ELEV_REQUESTS";
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
            System.out.println("No previous generic/specific elevator table");
            //e.printStackTrace();
        }

        db = new ElevatorRequestDB(dbURL, genericReqTable, reqTable);
    }

    @After
    public void tearDown() {
        db.close();
    }
    @Test
    public void addRequest() throws Exception {
        assertNull(db.getRequest(1));
        Request g = new GenericRequest(new Location(1459, 2009, Floor.GROUND, "BTM"), 35791, RequestType.MAIN,
                RequestStatus.ASSIGNED, "S elevator broken");
        ElevatorRequest repairWork = new ElevatorRequest(g, PriorityLevel.HIGH, MaintenanceType.REPAIRPARTS, "elevnodeS");
        ElevatorRequest trappedPerson = new ElevatorRequest(g, PriorityLevel.HIGH, MaintenanceType.PERSONTRAPPED, "elevnodeB");
        ElevatorRequest testing = new ElevatorRequest(g, PriorityLevel.MEDIUM, MaintenanceType.SAFETYCHECKS, "elevnodeA");

        assertNotNull(db.addRequest(repairWork));
        assertNotNull(db.addRequest(trappedPerson));
        assertNotNull(db.addRequest(testing));



    }

    @Test
    public void addRequest1() throws Exception {

    }

    @Test
    public void getRequest() throws Exception {

    }

    @Test
    public void getAllRequests() throws Exception {

    }

    @Test
    public void getAllRequests1() throws Exception {

    }

    @Test
    public void fulfillRequest() throws Exception {

    }

    @Test
    public void updateRequest() throws Exception {

    }

    @Test
    public void deleteRequest() throws Exception {

    }

    @Test
    public void getElevatorRequest() throws Exception {

    }

    @Test
    public void getElevatorRequestsByStaff() throws Exception {

    }

    @Test
    public void getAllElevatorRequests() throws Exception {

    }

    @Test
    public void fulfillRequest1() throws Exception {

    }

    @Test
    public void updateElevatorRequest() throws Exception {

    }

}