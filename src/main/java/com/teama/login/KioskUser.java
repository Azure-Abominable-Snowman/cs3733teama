package com.teama.login;

import com.teama.Configuration;

/**
 * Created by aliss on 11/19/2017.
 */
public class KioskUser {
    private LoginInfoDataSource loginDatabase = new JavaCredentialsDB(Configuration.dbURL, Configuration.credentialsTable);
    private AccessType access = AccessType.USER;
    private KioskUser() {
    }

    private static class ViewKioskUser {
        private static final KioskUser instance = new KioskUser();
    }

    // get an instance of the KioskUser
    public static KioskUser getInstance() {
        return ViewKioskUser.instance;
    }

    //returns original LoginInfo object if login credentials are found and confirmed
    //returns null if user not found / error in login credentials
    public LoginInfo checkCredentials(LoginInfo p) {
        LoginInfo check = loginDatabase.checkCredentials(p);
        if (check != null){
            this.access = check.getAccess();
        }
        return check;
    }

    // returns AccessType of current user
    public AccessType getCurrentAccessType() {
        return access;
    }

    // add a user to the Database
    public boolean addUser(LoginInfo p) {
        return loginDatabase.addLoginInfo(p);
    }
    //assumes user already exists in DB -- only done when successfully signed in
    public void updateUser(LoginInfo p) {
        loginDatabase.updateLoginInfo(p);
    }
}
