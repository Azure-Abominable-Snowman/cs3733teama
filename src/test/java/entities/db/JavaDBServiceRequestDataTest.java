package entities.db;
/*
import com.teama.mapsubsystem.data.Floor;
import com.teama.mapsubsystem.data.Location;
import com.teama.requestsubsystem.interpreterfeature.InterpreterRequestDB;
import com.teama.requestsubsystem.PriorityLevel;
import com.teama.requestsubsystem.RequestType;
import org.junit.Before;
import org.junit.Test;

import java.sql.*;

import static junit.framework.TestCase.assertEquals;

public class JavaDBServiceRequestDataTest {

    String dbURL = "jdbc:derby:testdb;create=true";
    private Connection conn = null;
    private Statement stmt = null;
    private InterpreterRequestDB db;

    @Before
    public void connect() {
        try {
            Class.forName("org.apache.derby.jdbc.ClientDriver").newInstance();
            // Get a connection
            conn = DriverManager.getConnection(dbURL);
        }
        catch (Exception except) {
            except.printStackTrace();
        }

        // Delete table from last time
        try {
            stmt = conn.createStatement();
            stmt.execute("DROP TABLE TEST_SERVICE");
            stmt.close();
        } catch(SQLException e) {
            e.printStackTrace();
        }

        db = new InterpreterRequestDB(dbURL, "TEST_SERVICE");

        // Populate with test data
    }

    @Test
    public void createRequest() throws Exception {
        // Try creating a basic request that is known to not already exist in the database
        db.submitRequest(new Request("testid", new Location(5, 10, Floor.SUBBASEMENT, "45 Francis"), RequestType.SEC,
                PriorityLevel.LOW, "testnote"));
        // Check the db to see if it's there
        try {
            stmt = conn.createStatement();
            ResultSet set = stmt.executeQuery("SELECT * FROM TEST_SERVICE WHERE REQUESTID='testid'");
            assertEquals(true, set.next()); // make sure something is found
            assertEquals("testid", set.getString("REQUESTID")); // make sure the request ID is in the database correctly
            assertEquals(5, set.getInt("XCOORD"));
            assertEquals(10, set.getInt("YCOORD"));
            assertEquals("L2", set.getString("LEVEL"));
            assertEquals("45 Francis", set.getString("BUILDING"));
            assertEquals("SEC", set.getString("REQTYPE"));
            assertEquals("LOW", set.getString("PRIORITY"));
            assertEquals("testnote", set.getString("NOTE"));
            set.close();
            stmt.close();
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void deleteRequest() throws Exception {
        // Create request, verfiy it is there, then try to delete it, then see if it was deleted
        db.submitRequest(new Request("testid", new Location(5, 10, Floor.SUBBASEMENT, "45 Francis"),
                RequestType.SEC, PriorityLevel.LOW, "testnote"));
        try {
            // Verify it is in the db
            stmt = conn.createStatement();
            ResultSet set = stmt.executeQuery("SELECT * FROM TEST_SERVICE WHERE REQUESTID='testid'");
            assertEquals(true, set.next());
            set.close();
            stmt.close();

            // Try to delete from db
            db.cancelRequest("testid");

            // See if it was removed
            stmt = conn.createStatement();
            set = stmt.executeQuery("SELECT * FROM TEST_SERVICE WHERE REQUESTID='testid'");
            assertEquals(false, set.next());
            set.close();
            stmt.close();

        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getRequest() throws Exception {
        // Create request and try to find it
        Request before = new Request("testid", new Location(5, 10, Floor.SUBBASEMENT, "45 Francis"),
                RequestType.SEC, PriorityLevel.LOW, "testnote");
        db.submitRequest(before);
        assertEquals(before.toSQLValues(), db.getRequest("testid").toSQLValues());

        // Try with fulfilled request
        Request beforeTwo = new Request("testid7", new Location(5, 10, Floor.SUBBASEMENT, "45 Francis"),
                RequestType.SEC, PriorityLevel.LOW, "testnote", true);
        db.submitRequest(beforeTwo);
        assertEquals(beforeTwo.toSQLValues(), db.getRequest("testid7").toSQLValues());

        // Have multiple similar looking requests in the db and find the correct one
        db.submitRequest(new Request("testid2", new Location(5, 10, Floor.SUBBASEMENT, "45 Francis"),
                RequestType.SEC, PriorityLevel.LOW, "testnote"));
        db.submitRequest(new Request("testid3", new Location(5, 10, Floor.SUBBASEMENT, "45 Francis"),
                RequestType.SEC, PriorityLevel.LOW, "testnote"));
        db.submitRequest(new Request("testid4", new Location(5, 10, Floor.SUBBASEMENT, "45 Francis"),
                RequestType.SEC, PriorityLevel.LOW, "testnote"));
        assertEquals(before.toSQLValues(), db.getRequest("testid").toSQLValues());
    }

    @Test
    public void fulfillRequest() throws Exception {
        // Create a request, make sure it's in the database, then fulfill it
        // See if it's correct
        Request before = new Request("testid", new Location(5, 10, Floor.SUBBASEMENT, "45 Francis"),
                RequestType.SEC, PriorityLevel.LOW, "testnote", false);
        db.submitRequest(before);

        try {
            // Verify it is in the db
            stmt = conn.createStatement();
            ResultSet set = stmt.executeQuery("SELECT * FROM TEST_SERVICE WHERE REQUESTID='testid'");
            assertEquals(true, set.next());
            set.close();
            stmt.close();

            // Fulfill the request
            db.fulfillRequest("testid");

            // See if it was fulfilled
            stmt = conn.createStatement();
            set = stmt.executeQuery("SELECT * FROM TEST_SERVICE WHERE REQUESTID='testid'");
            assertEquals(true, set.next());
            assertEquals(true, set.getBoolean("FULFILLED"));
            set.close();
            stmt.close();

        } catch(SQLException e) {
            e.printStackTrace();
        }
    }
}
*/