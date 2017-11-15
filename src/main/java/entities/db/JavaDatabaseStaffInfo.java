package entities.db;

import boundaries.Provider;
import entities.staff.*;

import java.sql.*;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Logger;

public class JavaDatabaseStaffInfo implements StaffInfoDataSource {

    private final Logger log = Logger.getLogger(this.getClass().getPackage().getName());
    private String dbURL, staffTable;
    private String staffTableLanguages;
    private Connection conn = null;
    private Statement stmt = null;

    public JavaDatabaseStaffInfo(String dbURL, String staffTable) {
        this.staffTable = staffTable;
        this.dbURL = dbURL;
        this.staffTableLanguages = staffTable+"_LANGUAGES";

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
                            "STAFFID VARCHAR(10) PRIMARY KEY NOT NULL," +
                            "FIRSTNAME VARCHAR(30) NOT NULL, " +
                            "LASTNAME VARCHAR(30) NOT NULL, " +
                            "PHONENUMBER VARCHAR(20) NOT NULL, " +
                            "STAFFTYPE VARCHAR(30) NOT NULL," +
                            "PHONEPROVIDER VARCHAR(10) NOT NULL ,"+
                            "AVAILABLE BOOLEAN NOT NULL" +
                            ")"
            );
            stmt.close();

            // Create the language->staff table as well
            // This table is always named <staffTable>_LANGUAGE
            stmt = conn.createStatement();
            stmt.execute(
                    " CREATE TABLE "+staffTableLanguages+" (" +
                            "STAFFID VARCHAR(20) NOT NULL," +
                            "LANGUAGE VARCHAR(30) NOT NULL" +
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
        ServiceStaff foundStaff;
        // Use all of the attributes to build a query for the database
        try {
            stmt = conn.createStatement();
            StringBuilder query = new StringBuilder();
            Iterator<Language> languageIterator = null;
            if(attrib.getSpokenLanguages() != null) {
                languageIterator = attrib.getSpokenLanguages().iterator();
            } if(languageIterator == null || !languageIterator.hasNext()) { // if there is no language specified
                query.append("SELECT T1.STAFFID FROM " + staffTable + " AS T1" +
                        " WHERE T1.STAFFTYPE='" + attrib.getType() + "'");
                query.append(" AND T1.AVAILABLE='TRUE'");
            } else {
                while (languageIterator.hasNext()) { // generates intersect query
                    query.append("SELECT T1.STAFFID FROM " + staffTable + " AS T1, " + staffTableLanguages +
                            " AS T2 WHERE T1.STAFFID=T2.STAFFID AND T1.STAFFTYPE='" + attrib.getType() + "' AND T2.LANGUAGE='" + languageIterator.next() + "'");
                    query.append(" AND T1.AVAILABLE='TRUE'");
                    if (languageIterator.hasNext()) {
                        query.append(" INTERSECT ");
                    }
                }
            }
            log.info(query.toString());
            ResultSet result = stmt.executeQuery(query.toString());
            // If none match...
            if(!result.next()) {
                // No available members found in db
                log.info("No results found for qualified servicestaff");
                return null;
            }
            // Query returns all matching ID's
            // Another query is needed to get all attributes for the staff member
            String foundId = result.getString("STAFFID");
            log.info("FOUND "+foundId);
            result.close();

            // Retrieves all info on the found staff member
            result = stmt.executeQuery("SELECT * FROM "+staffTable+" AS T1, "+staffTableLanguages+" AS T2 WHERE T1.STAFFID='"+foundId+"' AND T2.STAFFID='"+foundId+"'");

            // Add all of the stuff in the first row
            result.next();
            String firstName = result.getString("FIRSTNAME");
            String lastName = result.getString("LASTNAME");
            String phone =  result.getString("PHONENUMBER");
            Set<Language> lanArray = new HashSet<>();
            lanArray.add(Language.valueOf(result.getString("LANGUAGE")));
            while(result.next() && (result.getString("STAFFID").equals(foundId))) {
                // Load up all of the array attributes
                lanArray.add(Language.valueOf(result.getString("LANGUAGE")));
            }
            Enum<Provider> provider = Provider.valueOf(result.getString("PhoneProvider"));
            log.info(lanArray.toString());
            switch(attrib.getType().toString()) {
                case("SECURITY"):
                    foundStaff = new SecurityStaff(foundId, firstName, lastName, phone, StaffType.SECURITY,lanArray, provider, true);
                    break;
                case("TRANSPORT"):
                    foundStaff = new TransportStaff(foundId, firstName, lastName, phone, StaffType.TRANSPORT, lanArray, provider, true);
                    break;
                case("INTERPRETER"):
                    foundStaff = new SecurityStaff(foundId, firstName, lastName, phone, StaffType.INTERPRETER, lanArray, provider,true);
                    break;
                default:
                    System.out.println("Invalid staff member requested");
                    return null;
            }
            result.close();
            stmt.close();
            return foundStaff;
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void close() {
        try {
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
