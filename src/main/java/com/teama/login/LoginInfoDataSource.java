package com.teama.login;

/**
 * Created by aliss on 11/11/2017.
 */
public interface LoginInfoDataSource {
    SystemUser checkCredentials(LoginInfo p);
    boolean addUser(SystemUser p);
    boolean updateLoginInfo(LoginInfo old, LoginInfo newLogin); // if someone wants to change username or password
    boolean removeUser(SystemUser p);
    SystemUser getUser(LoginInfo l);
}
