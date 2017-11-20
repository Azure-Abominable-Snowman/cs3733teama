package com.teama.login;

/**
 * Created by aliss on 11/11/2017.
 */
public enum AccessType {
    ADMIN("Admin"), STAFF("Staff"), USER("User");
    private final String name;
    private AccessType(String s) {
        name = s;
    }

    public String toString() {
        return this.name;
    }
};



