package com.teama.login;

import com.teama.Configuration;

/**
 * Created by aliss on 11/19/2017.
 */

// Facade for interacting with Login; used by Login Controller and controllers that need to see the access priveleges of the current user
public class LoginSubsystem {
    private LoginInfoDataSource loginDatabase;
    private SystemUser user = new SystemUser(); // by default is Guest with no LoginInfo; updated in checkCredentials if valid login
    private LoginSubsystem() {
        loginDatabase = new JavaCredentialsDB(Configuration.dbURL, Configuration.credentialsTable);
    }

    private static class LoginSubsystemGetter {
        private static final LoginSubsystem instance = new LoginSubsystem();
    }

    // get an instance of the LoginSubsystem
    public static LoginSubsystem getInstance() {
        return LoginSubsystemGetter.instance;
    }

    //Input: LoginInfo for user that tried to sign in
    // Output: boolean; True if valid login credentials, false otherwise.
    // sets global system user to the appropriate staff/admin credentials if login successful
    public boolean checkCredentials(LoginInfo l) {
        SystemUser check = loginDatabase.checkCredentials(l); // pass the LoginInfo to the database; get a System User back
        if (check != null){
            user = check; // update the current SystemUser from the default
            return true;
        }
        return false;
    }

    // returns AccessType of current user
    public AccessType getCurrentAccessType() {
        return user.getAccess();
    }

    public SystemUser getSystemUser() {
        return this.user;
    }

    // add a user to the Database
    public boolean addUser(SystemUser p) {
        return loginDatabase.addUser(p);
    }

    //assumes user already exists in DB -- only done when successfully signed in. Takes old login info and new login info,
    public boolean updateUser(LoginInfo old, LoginInfo newLogin) {
        boolean updateResult =  loginDatabase.updateLoginInfo(old, newLogin);
        if (updateResult) { // if update successful
            user.setLoginInfo(newLogin); // update the current user info
        }
        return updateResult;
    }
}
