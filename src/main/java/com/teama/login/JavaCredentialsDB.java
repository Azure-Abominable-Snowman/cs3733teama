package com.teama.login;

import java.sql.*;
import java.util.logging.Logger;

/**
 * Created by aliss on 11/11/2017.
 */
public class JavaCredentialsDB implements LoginInfoDataSource {
    private final Logger log = Logger.getLogger(this.getClass().getPackage().getName()); // Store any messages
    private Connection conn = null;
    private Statement stmt = null;
    private String dbURL, tablename;
    PreparedStatement addLogin, checkLogin, updateLogin, getLogin, removeUser;

    public JavaCredentialsDB(String dbURL, String tablename) {
        this.dbURL = dbURL;
        this.tablename = tablename;

        try {
            Class.forName("org.apache.derby.jdbc.EmbeddedDriver").newInstance();
            conn = DriverManager.getConnection(dbURL);
            stmt = conn.createStatement();

        } catch (SQLException e) {
            log.info("Could not establish a connection for JavaCredentialsDB");
            //e.printStackTrace();
        } catch (IllegalAccessException e) {
            //e.printStackTrace();
        } catch (InstantiationException e) {
            //e.printStackTrace();
        } catch (ClassNotFoundException e) {
            //e.printStackTrace();
        }
        // Create table
        try {
            String createTable = "CREATE TABLE " + tablename +
                    "(USERNAME VARCHAR(25) not NULL, " +
                    "PASSWORD INTEGER not NULL, " +
                    "ACCESS VARCHAR(25) not NULL, " +
                    "PRIMARY KEY (USERNAME))";
            stmt.execute(createTable);
        } catch (SQLException e) {
            log.info("Table " + tablename + " may already exist.");

        }

        try {
            addLogin = conn.prepareStatement("INSERT INTO " + tablename + " VALUES (?, ?, ?)");
            getLogin = conn.prepareStatement("SELECT * FROM " + tablename + " WHERE USERNAME = ? ");
            updateLogin = conn.prepareStatement("UPDATE " + tablename + " SET PASSWORD = ?, ACCESS = ?" + " WHERE USERNAME = ?",
                    ResultSet.CONCUR_UPDATABLE);
            checkLogin = conn.prepareStatement("SELECT * FROM " + tablename + " WHERE USERNAME = ? AND PASSWORD = ?", ResultSet.TYPE_SCROLL_INSENSITIVE,
                     ResultSet.CONCUR_UPDATABLE);
            removeUser = conn.prepareStatement("DELETE FROM " + tablename + " WHERE USERNAME = ?");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (stmt != null) {
            try {
                stmt.close();
            } catch (SQLException e) {
                //e.printStackTrace();
            }
        }
    }
    @Override
    public boolean addUser(SystemUser p) {
        //PreparedStatement pstmt = null;
        String uname = p.getUsername();
        String pw = p.getPassword();
        int pw_hash = pw.hashCode();
        String access = p.getAccess().toString();

        try {
            addLogin.setString(1, uname);
            addLogin.setInt(2, pw_hash);
            addLogin.setString(3, access);
            /*
            String sql = "INSERT INTO " + tablename +
                    "(USERNAME, PASSWORD, ACCESS)" +
                    "VALUES (?, ?, ?)";
            insert = conn.prepareStatement(sql);
            insert.setString(1, uname);
            insert.setInt(2, pw_hash);
            insert.setString(3, access);
            */
            addLogin.executeUpdate();
            //insert.close();
            // Update
        } catch (SQLException e) {
            log.info("Login info already exists");
            return false;
        }
        return true;
    }

    // TODO

    @Override
    public boolean updateLoginInfo(LoginInfo old, LoginInfo newLogin) {
        //PreparedStatement insert = null;
        String uname = old.getUsername();
        Integer pw_hash = old.getPassword().hashCode();
        //String access = p.getAccess().toString();
        /*
        String sql = "UPDATE " + tablename +
                "(USERNAME, PASSWORD, ACCESS)" +
                "VALUES (?, ?, ?)";
        insert = conn.prepareStatement(sql);
        insert.setString(1, uname);
        insert.setInt(2, pw_hash);
        insert.setString(3, access);
        */

        try { //TODO: first check that username to be updated to doesn't already exist, then update
            updateLogin.setInt(1, pw_hash);
            //updateLogin.setString(2, access);
            updateLogin.setString(3, uname);
            updateLogin.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
return false;
        //insert.close();
    }

    @Override
    // returns null if no SystemUser found for given user
    public SystemUser checkCredentials(LoginInfo l) {

        //boolean authorized = false;
        //PreparedStatement pstmt = null;
        ResultSet rs = null;
        SystemUser user = null;
        String uname = l.getUsername();
        Integer pwHash = l.getPassword().hashCode();
        try {
            System.out.println(uname+" "+pwHash);
            log.info("Searching for user " +uname);
            checkLogin.setString(1, uname);
            checkLogin.setInt(2, pwHash);
            //checkLogin.setString(3, access);
            rs = checkLogin.executeQuery();

            if (!rs.next()) {
                log.info("No such user in system: " + uname);
            }
            else { // found a user!
                rs.beforeFirst(); // reset the resultset to the beginning
                if (rs.next()) {
                    //System.out.println(rs.getString("PASSWORD"));
                    //System.out.println(p.getPassword());
                    if (rs.getString("PASSWORD").equals(l.getPassword())) {
                        log.info("Correct password.");
                        user = new SystemUser(l, AccessType.valueOf(rs.getString("ACCESS")));
                    } else {
                        log.info("Password incorrect for user " + uname);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                //e.printStackTrace();
            }
        }
        return user;
    }

    @Override
    public boolean removeUser(SystemUser p) {
        try {
            removeUser.setString(1, p.getUsername());
            removeUser.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            log.info("User " + p.getUsername() + " could not be removed.");
            return false;
        }
    }

    public Connection seeConnection() {
        return conn;
    }
}
