package entities.db;

import com.teama.login.JavaCredentialsDB;
import com.teama.login.LoginInfo;
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
    JavaCredentialsDB db = new JavaCredentialsDB("jdbc:derby:testdb;create=true", "LOGIN_CREDS");
    LoginInfo a = new LoginInfo("user1", "randompw", STAFF);
    LoginInfo b = new LoginInfo("aostapenko", "notmypw", ADMIN);
    LoginInfo c = new LoginInfo("supersecure", "hello", ADMIN);



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
        assertTrue(db.checkCredentials(a));
        assertTrue(db.checkCredentials(b));
        assertTrue(db.checkCredentials(c));
        LoginInfo d = new LoginInfo("supersecure", "hello", ADMIN);
        db.addLoginInfo(d);
        assertTrue(db.checkCredentials(d));

        LoginInfo wrongPW = new LoginInfo("user1", "raNDOM2s", STAFF); //login with wrong password
        LoginInfo wrongAccess = new LoginInfo("user1", "randompw", ADMIN); //trying to login as Admin when actually Staff
        LoginInfo undoc = new LoginInfo("suspicious", "hello", STAFF);

        assertFalse(db.checkCredentials(wrongPW));
        assertFalse(db.checkCredentials(wrongAccess));
        assertFalse(db.checkCredentials(undoc));



    }

}