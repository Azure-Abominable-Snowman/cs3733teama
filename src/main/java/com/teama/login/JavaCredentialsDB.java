package com.teama.login;

import com.teama.requestsubsystem.StaffType;

import java.sql.*;
import java.util.logging.Logger;

/**
 * Created by aliss on 11/11/2017.
 */
public class JavaCredentialsDB implements LoginInfoDataSource {
    // TODO: ADD STAFFID LINKED TO THIS USER
    private final Logger log = Logger.getLogger(this.getClass().getPackage().getName()); // Store any messages
    private Connection conn = null;
    private Statement stmt = null;
    private String dbURL, tablename;
    PreparedStatement addLogin, checkLogin, updateLogin, getLogin, removeUser, updatePW, updateUName, addStaffType;

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
                    "(USERNAME VARCHAR(25) NOT NULL, " +
                    "PASSWORD INTEGER NOT NULL, " +
                    "ACCESS VARCHAR(25) NOT NULL, " +
                    "STAFFID INTEGER NOT NULL, " +
                    "STAFFTYPE VARCHAR(50), " +
                    "PRIMARY KEY (USERNAME))";
            stmt.execute(createTable);



        } catch (SQLException e) {
            log.info("Table " + tablename + " may already exist.");
            System.out.println(e.getErrorCode());
            //e.printStackTrace();

        }

        Statement st = null;
        try {
            st = conn.createStatement();
            ResultSet rset = st.executeQuery("SELECT * FROM " + tablename);
            ResultSetMetaData md = rset.getMetaData();
            for (int i=1; i<=md.getColumnCount(); i++)
            {
                System.out.println(md.getColumnLabel(i));
            }
        } catch (SQLException e) {
            //e.printStackTrace();
        }


        try {
            addLogin = conn.prepareStatement("INSERT INTO " + tablename + " (USERNAME, PASSWORD, ACCESS, STAFFID) VALUES (?, ?, ?, ?)");
            addStaffType = conn.prepareStatement("UPDATE " + tablename + " SET STAFFTYPE = ? WHERE STAFFID = ?");
            getLogin = conn.prepareStatement("SELECT * FROM " + tablename + " WHERE USERNAME = ? ");
            updateLogin = conn.prepareStatement("UPDATE " + tablename + " SET USERNAME = ?, PASSWORD = ?" + " WHERE USERNAME = ?",
                    ResultSet.CONCUR_UPDATABLE); // can only update username/password, not privelege level
            updatePW = conn.prepareStatement("UPDATE " + tablename + " SET PASSWORD = ?" + " WHERE USERNAME = ?");
            updateUName = conn.prepareStatement("UPDATE " + tablename + " SET USERNAME = ?" + " WHERE USERNAME = ?");
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

    /**
     *  addLogin = conn.prepareStatement("INSERT INTO " + tablename + " VALUES (?, ?, ?, ?)");

     * @param p
     * @return
     */
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
            addLogin.setInt(4, p.getStaffID());
            addLogin.executeUpdate();


            //insert.close();
            // Update
        } catch (SQLException e) {
            log.info("Failed to add login information for user with username " + uname);
            e.printStackTrace();
            return false;
        }
        if (p.getStaffType() != null) {
            try {
                addStaffType.setString(1, p.getStaffType().toString());
                addStaffType.setInt(2, p.getStaffID());
                addStaffType.executeUpdate();
                log.info("Added a new " + p.getStaffType().toString());

                log.info("Added login information for Staff member " + p.getStaffID());
            } catch (SQLException e) {
                //e.printStackTrace();
                return false;
            }
        }
        return true;
    }
    /*
    private int basicHash(String s) {
        int hash = 11;
        for (int i = 0; i<s.length(); i++) {
            hash += (s.charAt(i))
        }
    }
    */
    public SystemUser getUser(LoginInfo c) {
        SystemUser found = null;
        try {
            getLogin.setString(1,c.getUsername());
            ResultSet rs = getLogin.executeQuery();
            if (rs.next()) {
                int staffID = rs.getInt("STAFFID");
                if (staffID != 0){
                    found = new SystemUser(new LoginInfo(rs.getString("USERNAME"), c.getPassword()), AccessType.getAccessType(rs.getString("ACCESS")), rs.getInt("STAFFID"),
                            StaffType.getStaff(rs.getString("STAFFTYPE")));
                }
                else {
                    found = new SystemUser(new LoginInfo(rs.getString("USERNAME"), c.getPassword()), AccessType.getAccessType(rs.getString("ACCESS")));
                }

            }
        } catch (SQLException e) {
            log.info("Failed to get the user with given login info, where username is " + c.getUsername());
            e.printStackTrace();
        }
        return found;
    }

    // TODO

    @Override
    public boolean updateLoginInfo(LoginInfo old, LoginInfo newLogin) {
        //PreparedStatement insert = null;
        boolean updatedUser = true;
        boolean updatingUserName = false;
        String oldUname = old.getUsername();
        String newUname = newLogin.getUsername();
        int oldHash = old.getPassword().hashCode();
        int newHash = newLogin.getPassword().hashCode();
        // updateUName = conn.prepareStatement("UPDATE " + tablename + " SET USERNAME = ?" + " WHERE USERNAME = ?");
        SystemUser s = getUser(old);
        if (s == null) {
            return false;
        }
        if (!(oldUname.equals(newUname))) { //updating the username
            updatingUserName = true;
            try {
                updateUName.setString(1, newUname);
                updateUName.setString(2, oldUname);
                updateUName.executeUpdate();
                log.info("Updated the username for user with old username " + oldUname);
            } catch (SQLException e) {
                //e.printStackTrace();
                updatedUser = false;
            }
        }
        if (!(oldHash == newHash) && updatedUser) { //update the password, only if username update was successful (if username is updated)
            // updatePW = conn.prepareStatement("UPDATE " + tablename + " SET PASSWORD = ?" + " WHERE USERNAME = ?");
            try {
                updatePW.setInt(1, newHash);
                if (updatingUserName) {
                    updatePW.setString(2, newUname);
                }
                else {
                    updatePW.setString(2, oldUname);
                }
                updatePW.executeUpdate();
                updatedUser = true;
            } catch (SQLException e) {
                log.info("Could not update the password for user.");
                e.printStackTrace();
                updatedUser = false;
            }
        }
        return updatedUser;
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
            log.info("Searching for user " +uname);
            checkLogin.setString(1, uname);
            checkLogin.setInt(2, pwHash);
            //checkLogin.setString(3, access);
            rs = checkLogin.executeQuery();

            if (rs.next()) {
                if (rs.getInt("PASSWORD") == (l.getPassword().hashCode())) { // compare the hashes to see if password correct
                    log.info("Correct password.");
                    user = new SystemUser(l, AccessType.getAccessType(rs.getString("ACCESS")));
                } else {
                    log.info("Password incorrect for user " + uname);
                }
            }
            else {
                  log.info("No such username, " + l.getUsername() + ", is in the system.");
                }

        } catch (SQLException e) {
            //e.printStackTrace();
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
