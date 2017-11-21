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
    PreparedStatement addLogin, checkLogin, updateLogin;

    public JavaCredentialsDB(String dbURL, String tablename) {
        this.dbURL = dbURL;
        this.tablename = tablename;

        try {
            Class.forName("org.apache.derby.jdbc.ClientDriver").newInstance();
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
            updateLogin = conn.prepareStatement("UPDATE " + tablename + " SET PASSWORD = ?, ACCESS = ?" + " WHERE USERNAME = ?",
                    ResultSet.CONCUR_UPDATABLE);
            checkLogin = conn.prepareStatement("SELECT * FROM " + tablename + " WHERE USERNAME = ? AND PASSWORD = ? AND ACCESS = ?", ResultSet.TYPE_SCROLL_INSENSITIVE,
                     ResultSet.CONCUR_UPDATABLE);
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

    public boolean addLoginInfo(LoginInfo p) {
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

    @Override
    public void updateLoginInfo(LoginInfo p) {
        //PreparedStatement insert = null;
        String uname = p.getUsername();
        Integer pw_hash = p.getPassword().hashCode();
        String access = p.getAccess().toString();
        /*
        String sql = "UPDATE " + tablename +
                "(USERNAME, PASSWORD, ACCESS)" +
                "VALUES (?, ?, ?)";
        insert = conn.prepareStatement(sql);
        insert.setString(1, uname);
        insert.setInt(2, pw_hash);
        insert.setString(3, access);
        */

        try {
            updateLogin.setInt(1, pw_hash);
            updateLogin.setString(2, access);
            updateLogin.setString(3, uname);
            updateLogin.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        //insert.close();
    }

    @Override
    public LoginInfo checkCredentials(LoginInfo p) {

        //boolean authorized = false;
        //PreparedStatement pstmt = null;
        ResultSet rs = null;
        String uname = p.getUsername();
        Integer pwHash = p.getPassword().hashCode();
        String access = p.getAccess().toString();
        try {
            log.info("Searching for user " +uname);
            checkLogin.setString(1, uname);
            checkLogin.setInt(2, pwHash);
            checkLogin.setString(3, access);
            rs = checkLogin.executeQuery();

            //rs = pstmt.executeQuery(search);
            if (!rs.next()) {
                log.info("No such user in system: " + uname);
                rs.close();
                return null;
            }
            else {
                rs.beforeFirst();
                while (rs.next()) {
                    //System.out.println(rs.getString("PASSWORD"));
                    //System.out.println(p.getPassword());
                    if (rs.getString("PASSWORD").equals(p.getPassword())) {
                        log.info("Correct password.");
                        if (rs.getString("ACCESS").equals(p.getAccess().toString())) {
                            log.info("Authorized Access for user " + uname);
                            //authorized = true;
                        } else {
                            log.info("Illegal access by user " + uname);
                        }
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
        return p;
    }


    public Connection seeConnection() {
        return conn;
    }
}
