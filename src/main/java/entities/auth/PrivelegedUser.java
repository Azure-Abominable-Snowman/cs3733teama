package entities.auth;


/**
 * Created by aliss on 11/11/2017.
 */
public class PrivelegedUser {
    // holds the login credentials for an admin/staff user for verification
    private String username;
    private String password;
    private Enum<AccessType> access;

    public PrivelegedUser(String uname, String pw, Enum<AccessType> a) {
        username = uname;
        password = pw;
        access = a;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public Enum<AccessType> getAccess() {
        return access;
    }

}
