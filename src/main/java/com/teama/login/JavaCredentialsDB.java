package com.teama.login;

import java.sql.*;
import java.util.logging.Logger;

import static com.teama.login.AccessType.ADMIN;
import static com.teama.login.AccessType.STAFF;

/**
 * Created by aliss on 11/11/2017.
 */
public class JavaCredentialsDB implements LoginInfoDataSource {
    private final Logger log = Logger.getLogger(this.getClass().getPackage().getName()); // Store any messages
    private Connection conn = null;
    private Statement stmt = null;
    private String dbURL, tablename;

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
                    "(USERNAME INTEGER not NULL, " +
                    "PASSWORD VARCHAR(25) not NULL, " +
                    "ACCESS VARCHAR(25) not NULL, " +
                    "PRIMARY KEY (USERNAME))";
            stmt.execute(createTable);
        } catch (SQLException e) {
            log.info("Table " + tablename + " may already exist.");

        }

        if (stmt != null) {
            try {
                stmt.close();
            } catch (SQLException e) {
                //e.printStackTrace();
            }
        }
    }

    public void addLoginInfo(PrivelegedUser p) {
        PreparedStatement pstmt = null;
        String uname = p.getUsername();
        int hash = uname.hashCode();
        String pw = p.getPassword();
        String access = "";
        if (p.getAccess() == ADMIN) {
            access = "ADMIN";
        }
        if (p.getAccess() == STAFF) {
            access = "STAFF";
        }

        try {
            PreparedStatement insert = null;
            String sql = "INSERT INTO " + tablename +
                    "(USERNAME, PASSWORD, ACCESS)" +
                    "VALUES (?, ?, ?)";
            insert = conn.prepareStatement(sql);
            insert.setInt(1, hash);
            insert.setString(2, pw);
            insert.setString(3, access);
            insert.executeUpdate();

            // Update
        } catch (SQLException e) {
            log.info("Login info already exists; update");
        }
        try {
            log.info("Updating information");
            String update = "UPDATE " + tablename +
                    " SET PASSWORD = ? " +
                    "WHERE USERNAME = ?";

            pstmt = conn.prepareStatement(update);
            pstmt.setString(1, pw);
            pstmt.setInt(2, hash);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            //e.printStackTrace();
        }
        finally {
            if (pstmt != null) {
                try {
                    pstmt.close();
                } catch (SQLException e) {
                    //e.printStackTrace();
                }

            }
        }
    }


    @Override
    public boolean checkCredentials(PrivelegedUser p) {

        boolean authorized = false;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String uname = p.getUsername();
        Integer hash = uname.hashCode();
        try {
            log.info("Searching for user " +hash);
            String search = "SELECT * " +
                            "FROM " + tablename +
                            " WHERE USERNAME = ?";
            pstmt = conn.prepareStatement(search, ResultSet.TYPE_SCROLL_INSENSITIVE,
                                                    ResultSet.CONCUR_UPDATABLE);
            pstmt.setInt(1, hash);
            rs = pstmt.executeQuery();
            //rs = pstmt.executeQuery(search);
            if (!rs.next()) {
                log.info("No such user in system: " + uname);
                return false;
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
                            authorized = true;
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
        if (pstmt != null) {
            try {
                pstmt.close();
            } catch (SQLException e) {
                //e.printStackTrace();
            }
        }
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                //e.printStackTrace();
            }
        }
        return authorized;
    }


    public Connection seeConnection() {
        return conn;
    }
}
