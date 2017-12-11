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
import java.util.ArrayList;

import static org.junit.Assert.*;

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
        genericReqTable = "TEST_GENREQS_ELEV";

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
    public void fulfillRequest() throws Exception {
        Request g = new GenericRequest(new Location(1459, 2009, Floor.GROUND, "BTM"), 35791, RequestType.MAIN,
                RequestStatus.ASSIGNED, "S elevator broken");
        ElevatorRequest repairWork = new ElevatorRequest(g, PriorityLevel.HIGH, MaintenanceType.REPAIRPARTS, "elevnodeS");
        db.addRequest(repairWork);

        ElevatorRequest toFulfill = db.getElevatorRequest(1);
        assertEquals(RequestStatus.ASSIGNED, toFulfill.getStatus());
        assertEquals(0, toFulfill.getServiceTime(), 0.1);

        toFulfill.setServiceTime(1412);
        assertTrue(db.fulfillRequest(toFulfill));

        ElevatorRequest fulfilled = db.getElevatorRequest(1);
        assertEquals(1412, fulfilled.getServiceTime(), 0.1);
        assertEquals(RequestStatus.CLOSED, fulfilled.getStatus());
        assertEquals(g.getNote(), fulfilled.getNote());

    }

    @Test
    public void updateRequest() throws Exception {
        Request g = new GenericRequest(new Location(1459, 2009, Floor.GROUND, "BTM"), 35791, RequestType.MAIN,
                RequestStatus.ASSIGNED, "S elevator broken");
        ElevatorRequest repairWork = new ElevatorRequest(g, PriorityLevel.HIGH, MaintenanceType.REPAIRPARTS, "elevnodeS");
        db.addRequest(repairWork);
        ElevatorRequest retrieved = db.getElevatorRequest(1);
        assertEquals(PriorityLevel.HIGH, retrieved.getpLevel());
        retrieved.setNote("changing note");
        retrieved.setpLevel(PriorityLevel.LOW);
        assertTrue(db.updateElevatorRequest(retrieved));

        ElevatorRequest updated = db.getElevatorRequest(1);
        assertEquals(retrieved.getNote(), updated.getNote());
        assertEquals(retrieved.getpLevel(), updated.getpLevel());

    }

    @Test
    public void deleteRequest() throws Exception {
        Request g = new GenericRequest(new Location(1459, 2009, Floor.GROUND, "BTM"), 35791, RequestType.MAIN,
                RequestStatus.ASSIGNED, "S elevator broken");
        ElevatorRequest repairWork = new ElevatorRequest(g, PriorityLevel.HIGH, MaintenanceType.REPAIRPARTS, "elevnodeS");
        ElevatorRequest trappedPerson = new ElevatorRequest(g, PriorityLevel.HIGH, MaintenanceType.PERSONTRAPPED, "elevnodeB");
        ElevatorRequest testing = new ElevatorRequest(g, PriorityLevel.MEDIUM, MaintenanceType.SAFETYCHECKS, "elevnodeA");

        db.addRequest(repairWork);
        db.addRequest(trappedPerson);
        db.addRequest(testing);

        assertTrue(db.deleteRequest(3));
        assertNull(db.getRequest(3));
        assertNull(db.getElevatorRequest(3));


    }

    @Test
    public void getElevatorRequest() throws Exception {
        Request g = new GenericRequest(new Location(1459, 2009, Floor.GROUND, "BTM"), 35791, RequestType.MAIN,
                RequestStatus.ASSIGNED, "S elevator broken");
        ElevatorRequest repairWork = new ElevatorRequest(g, PriorityLevel.HIGH, MaintenanceType.REPAIRPARTS, "elevnodeS");
        ElevatorRequest trappedPerson = new ElevatorRequest(g, PriorityLevel.HIGH, MaintenanceType.PERSONTRAPPED, "elevnodeB");
        ElevatorRequest testing = new ElevatorRequest(g, PriorityLevel.MEDIUM, MaintenanceType.SAFETYCHECKS, "elevnodeA");

        db.addRequest(repairWork);
        db.addRequest(trappedPerson);
        db.addRequest(testing);

        ElevatorRequest retrieved = db.getElevatorRequest(1);
        assertNotNull(retrieved);
        assertEquals(retrieved.getMaintenanceType(), MaintenanceType.REPAIRPARTS);
        assertEquals(retrieved.getpLevel(), PriorityLevel.HIGH);
        assertEquals("elevnodeS", retrieved.getBrokenElevatorID());
        assertEquals(RequestStatus.ASSIGNED, retrieved.getStatus());


    }

    @Test
    public void getElevatorRequestsByStaff() throws Exception {
        Request g = new GenericRequest(new Location(1459, 2009, Floor.GROUND, "BTM"), 35791, RequestType.MAIN,
                RequestStatus.ASSIGNED, "S elevator broken");
        Request g2 = new GenericRequest(new Location(1459, 2009, Floor.GROUND, "BTM"), 37, RequestType.MAIN,
                RequestStatus.ASSIGNED, "S elevator broken");
        ElevatorRequest repairWork = new ElevatorRequest(g, PriorityLevel.HIGH, MaintenanceType.REPAIRPARTS, "elevnodeS");
        ElevatorRequest trappedPerson = new ElevatorRequest(g, PriorityLevel.HIGH, MaintenanceType.PERSONTRAPPED, "elevnodeB");
        ElevatorRequest testing = new ElevatorRequest(g, PriorityLevel.MEDIUM, MaintenanceType.SAFETYCHECKS, "elevnodeA");
        ElevatorRequest other = new ElevatorRequest(g2, PriorityLevel.HIGH, MaintenanceType.REPAIRPARTS, "elevnodeD");
        db.addRequest(repairWork);
        db.addRequest(trappedPerson);
        db.addRequest(testing);
        db.addRequest(other);
        ArrayList<ElevatorRequest> byStaff = db.getElevatorRequestsByStaff(g.getStaffID(), g.getStatus());
        assertEquals(3, byStaff.size(), 0.1);




    }

    @Test
    public void getAllElevatorRequests() throws Exception {
        Request g = new GenericRequest(new Location(1459, 2009, Floor.GROUND, "BTM"), 35791, RequestType.MAIN,
                RequestStatus.ASSIGNED, "S elevator broken");
        Request g2 = new GenericRequest(new Location(1459, 2009, Floor.GROUND, "BTM"), 37, RequestType.MAIN,
                RequestStatus.ASSIGNED, "S elevator broken");
        ElevatorRequest repairWork = new ElevatorRequest(g, PriorityLevel.HIGH, MaintenanceType.REPAIRPARTS, "elevnodeS");
        ElevatorRequest trappedPerson = new ElevatorRequest(g, PriorityLevel.HIGH, MaintenanceType.PERSONTRAPPED, "elevnodeB");
        ElevatorRequest testing = new ElevatorRequest(g, PriorityLevel.MEDIUM, MaintenanceType.SAFETYCHECKS, "elevnodeA");
        ElevatorRequest other = new ElevatorRequest(g2, PriorityLevel.HIGH, MaintenanceType.REPAIRPARTS, "elevnodeD");
        db.addRequest(repairWork);
        db.addRequest(trappedPerson);
        db.addRequest(testing);
        db.addRequest(other);
        ArrayList<ElevatorRequest> all = db.getAllElevatorRequests(RequestStatus.ASSIGNED);
        assertEquals(4, all.size(), 0.1);
        repairWork.setServiceTime(500);
        ElevatorRequest addedRepair = db.getElevatorRequest(1);
        db.fulfillRequest(addedRepair);
        ArrayList<ElevatorRequest> closed = db.getAllElevatorRequests(RequestStatus.CLOSED);
        assertEquals(1, closed.size());
        ArrayList<ElevatorRequest> assigned = db.getAllElevatorRequests(RequestStatus.ASSIGNED);
        assertEquals(3, assigned.size());


    }


}