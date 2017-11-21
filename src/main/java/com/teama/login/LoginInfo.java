package com.teama.login;


/**
 * Created by aliss on 11/11/2017.
 */
public class LoginInfo {
    // holds the login credentials for an admin/staff user for verification
    private String username;
    private String password;
    private AccessType access;

    public LoginInfo(String uname, String pw, AccessType a) {
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

    public AccessType getAccess() {
        return access;
    }

}
