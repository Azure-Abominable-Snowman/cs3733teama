package entities.db;

import com.teama.login.JavaCredentialsDB;
import com.teama.login.SystemUser;
import org.junit.Test;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;

import static com.teama.login.AccessType.ADMIN;
import static com.teama.login.AccessType.STAFF;
import static org.junit.Assert.*;

/**
 * Created by aliss on 11/11/2017.
 */
public class JavaCredentialsDBTest {
    JavaCredentialsDB db = new JavaCredentialsDB("jdbc:derby:unittestdb;create=true", "LOGIN_CREDS");
    SystemUser a = new SystemUser("user1", "randompw", STAFF);
    SystemUser b = new SystemUser("aostapenko", "notmypw", ADMIN);
    SystemUser c = new SystemUser("supersecure", "hello", ADMIN);



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

    @Test
    public void checkCredentials() {
        db.addLoginInfo(a); // STAFF
        db.addLoginInfo(b); //ADMIN
        db.addLoginInfo(c); //ADMIN
        assertNotNull(db.checkCredentials(a));
        assertNotNull(db.checkCredentials(b));
        assertNotNull(db.checkCredentials(c));
        SystemUser d = new SystemUser("supersecure", "hello", ADMIN);
        db.addLoginInfo(d);
        assertNotNull(db.checkCredentials(d));

        SystemUser wrongPW = new SystemUser("user1", "raNDOM2s", STAFF); //login with wrong password
        SystemUser wrongAccess = new SystemUser("user1", "randompw", ADMIN); //trying to login as Admin when actually Staff
        SystemUser undoc = new SystemUser("suspicious", "hello", STAFF);

        assertNull(db.checkCredentials(wrongPW));
        assertNull(db.checkCredentials(wrongAccess));
        assertNull(db.checkCredentials(undoc));



    }

}