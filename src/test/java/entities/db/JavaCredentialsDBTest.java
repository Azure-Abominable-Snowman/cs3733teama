package entities.db;

import entities.auth.PrivelegedUser;
import org.junit.Test;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;

import static entities.auth.AccessType.ADMIN;
import static entities.auth.AccessType.STAFF;
import static org.junit.Assert.*;

/**
 * Created by aliss on 11/11/2017.
 */
public class JavaCredentialsDBTest {
    JavaCredentialsDB db = new JavaCredentialsDB("jdbc:derby:testdb;create=true", "LOGIN_CREDS");
    PrivelegedUser a = new PrivelegedUser("user1", "randompw", STAFF);
    PrivelegedUser b = new PrivelegedUser("aostapenko", "notmypw", ADMIN);
    PrivelegedUser c = new PrivelegedUser("supersecure", "hello", ADMIN);



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
        PrivelegedUser d = new PrivelegedUser("supersecure", "hello", ADMIN);
        db.addLoginInfo(d);
        assertTrue(db.checkCredentials(d));

        PrivelegedUser wrongPW = new PrivelegedUser("user1", "raNDOM2s", STAFF); //login with wrong password
        PrivelegedUser wrongAccess = new PrivelegedUser("user1", "randompw", ADMIN); //trying to login as Admin when actually Staff
        PrivelegedUser undoc = new PrivelegedUser("suspicious", "hello", STAFF);

        assertFalse(db.checkCredentials(wrongPW));
        assertFalse(db.checkCredentials(wrongAccess));
        assertFalse(db.checkCredentials(undoc));



    }

}