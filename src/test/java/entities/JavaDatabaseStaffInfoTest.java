package entities;

import org.junit.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

import static junit.framework.TestCase.assertEquals;

public class JavaDatabaseStaffInfoTest {

    JavaDatabaseStaffInfo db;
    private Connection conn = null;
    private Statement stmt = null;

    @Test
    public void testDatabaseConnection() {
        // Drop the tables from before
        try {
            conn = DriverManager.getConnection("jdbc:derby://localhost:1527/testdb;create=true");
            stmt = conn.createStatement();
            stmt.execute("DROP TABLE TEST_STAFF");
            stmt.execute("DROP TABLE TEST_STAFF_LANGUAGE");
            stmt.close();
        } catch (Exception except) {

        }
        // Create the database testdb with the table TEST_STAFF
        db = new JavaDatabaseStaffInfo("jdbc:derby://localhost:1527/testdb;create=true", "TEST_STAFF");
        try {
            Class.forName("org.apache.derby.jdbc.ClientDriver").newInstance();
            // Get a connection
            stmt = conn.createStatement();
            stmt.execute("INSERT INTO TEST_STAFF VALUES('staffOne', 'SECURITY')");
            stmt.execute("INSERT INTO TEST_STAFF VALUES('staffTwo', 'TRANSPORT')");
            stmt.execute("INSERT INTO TEST_STAFF VALUES('staffThree', 'INTERPRETER')");
            stmt.execute("INSERT INTO TEST_STAFF VALUES('staffFour', 'TRANSPORT')");
            stmt.close();
        }
        catch (Exception except) {
            except.printStackTrace();
            assertEquals(false, true); // fail the test if there is an exception
        }
    }

    @Test
    public void findQualified() throws Exception {
        // Find a security guard that speaks any language
        assertEquals(db.findQualified(new StaffAttrib(StaffType.SECURITY, null)).getStaffType(), StaffType.SECURITY);
    }

}