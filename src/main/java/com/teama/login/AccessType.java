package com.teama.login;

/**
 * Created by aliss on 11/11/2017.
 */
public enum AccessType {
    ADMIN("Admin"), STAFF("Staff"), GUEST("Guest");
    private final String name;
    private AccessType(String s) {
        name = s;
    }

    public String toString() {
        return this.name;
    }

    public static AccessType getAccessType(String s) {
        for (AccessType a: AccessType.values()) {
            if (a.toString().equals(s)){
                return a;
            }
        }
        throw new IllegalArgumentException("No AccessType defined for " + s);
    }

    public boolean equalTo(AccessType a) {
        return a.toString().equals(this.toString());
    }
};



