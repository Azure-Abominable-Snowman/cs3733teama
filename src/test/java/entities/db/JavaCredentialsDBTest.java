package entities.db;

import com.teama.login.*;
import com.teama.requestsubsystem.StaffType;
import org.junit.Before;
import org.junit.Test;

import java.sql.*;

import static com.teama.login.AccessType.ADMIN;
import static com.teama.login.AccessType.STAFF;
import static org.junit.Assert.*;

/**
 * Created by aliss on 11/11/2017.
 */
public class JavaCredentialsDBTest {
    private String dbURL = "jdbc:derby:unittestdb;create=true";
    private String credsTable;
    private Connection conn = null;
    private Statement stmt = null;
    private LoginInfoDataSource db;


    public JavaCredentialsDBTest() {
        credsTable = "LOGIN_CREDS";
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

            stmt.execute("DROP TABLE LOGIN_CREDS");
            stmt.close();
            System.out.println("Deleted the previous table");
        } catch (SQLException e) {
            System.out.println("No previous table");
            e.printStackTrace();
        }

        db = new JavaCredentialsDB(dbURL, credsTable);


    }
    LoginInfo login1 = new LoginInfo("user1", "randompw");
    LoginInfo login2 = new LoginInfo("aostapenko", "12391023");
    LoginInfo login3 = new LoginInfo("supersecure", "hello_91203SJ");
    SystemUser a = new SystemUser(login1, STAFF);
    SystemUser b = new SystemUser(login2, ADMIN);
    SystemUser c = new SystemUser(login3, ADMIN);
    SystemUser d = new SystemUser(login3, STAFF, 31, StaffType.INTERPRETER);


/*
    @Test
    public void instantiate() {
        assertNotNull(db.seeConnection());
        try {
            DatabaseMetaData meta = db.seeConnection().getMetaData();
            ResultSet res = meta.getTables(null, null, "LOGIN_CREDS", null);
            System.out.println(meta.getCatalogSeparator());
            while (res.next()) {
                assertEquals("LOGIN_CREDS", res.getString("TABLE_NAME"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    */
    @Test
    public void getUser() {
        try {
            assertNull(db.getUser(login2));
            PreparedStatement x = conn.prepareStatement("INSERT INTO " + credsTable + " (USERNAME, PASSWORD,ACCESS, STAFFID) VALUES(?,?,?, ?) ");
            x.setString(1, login1.getUsername());
            x.setInt(2, login1.getPassword().hashCode());
            x.setString(3, AccessType.STAFF.toString());
            x.setInt(4, 12312);
            x.executeUpdate();
            SystemUser found = db.getUser(login1);

            assertNotNull(found);
            assertEquals(a.getUsername(), found.getUsername());
            assertEquals(a.getAccess(), found.getAccess());
            assertEquals(a.getPassword().hashCode(), found.getPassword().hashCode());

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    @Test
    public void addUser() {
        assertNull(db.getUser(login2));
        assertTrue(db.addUser(b));
        assertTrue(db.addUser(d));
        SystemUser retrieved = db.getUser(login2);
        assertNotNull(retrieved);
        assertEquals(login2.getUsername(), retrieved.getUsername());
        assertEquals(login2.getPassword(), retrieved.getPassword());
        assertEquals(b.getAccess(), retrieved.getAccess());
        assertFalse(db.addUser(new SystemUser(b.getLoginInfo(), AccessType.ADMIN))); //make sure not to add a user if input username already exists
    }
    @Test
    public void checkCredentials() {
        db.addUser(a); // STAFF
        db.addUser(b); //ADMIN
        db.addUser(c); //ADMIN
        SystemUser checkedA = db.checkCredentials(login1);
        SystemUser checkedB = db.checkCredentials(login2);
        SystemUser checkedC = db.checkCredentials(login3);
        assertNotNull(checkedA); // make sure all added
        assertEquals(login1.getUsername(), checkedA.getUsername());
        assertEquals(login1.getPassword(), checkedA.getPassword());
        assertEquals(a.getAccess(), checkedA.getAccess());
        assertEquals(login3.getPassword(), checkedC.getPassword());
        LoginInfo foreign = new LoginInfo("suspicious", "randompw");
        LoginInfo incorrectPW = new LoginInfo(a.getUsername(), "incorrectPW");
        assertNull(db.checkCredentials(incorrectPW));
        assertNull(db.checkCredentials(foreign));
    }

    /*@Test
    public void removeUser() {
        db.addUser(a); Takes too long!
        db.addUser(c);
        assertNotNull(db.getUser(a.getLoginInfo()));
        assertNotNull(db.getUser(c.getLoginInfo()));
        db.removeUser(c);
        assertNull(db.getUser(c.getLoginInfo()));
        assertNotNull(db.getUser(a.getLoginInfo()));
    }*/

    @Test
    public void updateLoginInfo() {
        db.addUser(a);
        db.addUser(b);

        LoginInfo updatedA = new LoginInfo("newUsername", a.getPassword()); // update the username
        assertTrue(db.updateLoginInfo(a.getLoginInfo(), updatedA));
        assertNull(db.getUser(a.getLoginInfo()));
        SystemUser updatedAFound = db.getUser(updatedA);
        assertNotNull(updatedAFound);
        assertEquals(updatedA.getUsername(), updatedAFound.getUsername()); // make sure username was updated
        assertEquals(a.getPassword(), updatedAFound.getPassword()); // make sure password stayed the same
        assertEquals(a.getAccess(), updatedAFound.getAccess()); // make sure AccessType is untouched

        LoginInfo updateBPW = new LoginInfo(b.getUsername(), "someRandomNewPW"); // update password only
        assertTrue(db.updateLoginInfo(b.getLoginInfo(), updateBPW));
        SystemUser updatedBFound = db.getUser(updateBPW);
        assertEquals(b.getUsername(), updatedBFound.getUsername());
        assertEquals(updateBPW.getPassword(), updatedBFound.getPassword());
        assertEquals(b.getAccess(), updatedBFound.getAccess());

        LoginInfo totalUpdateA = new LoginInfo("user123", "usersnewPW");
        assertTrue(db.updateLoginInfo(updatedA, totalUpdateA));
        SystemUser aUpdated = db.getUser(totalUpdateA);
        assertNotNull(aUpdated);
        assertEquals(totalUpdateA.getUsername(), aUpdated.getUsername());
        assertEquals(totalUpdateA.getPassword(), aUpdated.getPassword());
        assertNotEquals(b.getUsername(), aUpdated.getUsername());
        assertEquals(a.getAccess(), aUpdated.getAccess());

        LoginInfo updateAtoB = new LoginInfo(b.getUsername(), "awjeiro2"); // updating to an existing username should fail
        assertFalse(db.updateLoginInfo(aUpdated.getLoginInfo(), updateAtoB));

        assertFalse(db.updateLoginInfo(c.getLoginInfo(), totalUpdateA)); // updating user info that doesn't exist should fial
    }

}