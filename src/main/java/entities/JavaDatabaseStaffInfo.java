package entities;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Logger;

public class JavaDatabaseStaffInfo implements StaffInfoDataSource {

    private final Logger log = Logger.getLogger(this.getClass().getPackage().getName());
    private String dbURL, staffTable;
    private Connection conn = null;
    private Statement stmt = null;

    public JavaDatabaseStaffInfo(String dbURL, String staffTable) {
        this.staffTable = staffTable;
        this.dbURL = dbURL;

        try {
            Class.forName("org.apache.derby.jdbc.ClientDriver").newInstance();
            // Get a connection
            conn = DriverManager.getConnection(dbURL);
        }
        catch (Exception except) {
            except.printStackTrace();
        }

        // Creates the staff table if it isn't there already
        try
        {
            stmt = conn.createStatement();
            stmt.execute(
                    " CREATE TABLE "+staffTable+" (" +
                            "staffID VARCHAR(10) PRIMARY KEY NOT NULL," +
                            "staffType VARCHAR(30) NOT NULL" +
                            ")"
            );
            stmt.close();

            // Create the language->staff table as well
            // This table is always named <staffTable>_LANGUAGE
            stmt = conn.createStatement();
            stmt.execute(
                    " CREATE TABLE "+staffTable+"_LANGUAGE (" +
                            "language VARCHAR(10) PRIMARY KEY NOT NULL," +
                            "staffID VARCHAR(20) NOT NULL" +
                            ")"
            );
            stmt.close();
        }
        catch (SQLException sqlExcept)
        {
            log.info("Does the staff info database or language relation table already exist?");
        }
    }

    @Override
    public ServiceStaff findQualified(StaffAttrib attrib) {
        // Use all of the attributes to build a query for the database
        try {
            stmt = conn.createStatement();
            //stmt.executeQuery("SELECT * attrib.getType()
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
