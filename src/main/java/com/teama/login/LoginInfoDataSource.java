package com.teama.login;

/**
 * Created by aliss on 11/11/2017.
 */
public interface LoginInfoDataSource {
    LoginInfo checkCredentials(LoginInfo p);
    boolean addLoginInfo(LoginInfo p);
    void updateLoginInfo(LoginInfo p);
}
