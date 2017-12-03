package com.teama.requestsubsystem.securityfeature;

public enum SecurityType {
    EMERGENCY("Emergency"), BLOCK("Block"), PATIENT("Patient"), DRUG("Drug"), ESCORT("Escort"), ENFORCEMENT("Enforcement");


    private final String name;

    SecurityType(String s) {
        this.name = s;
    }

    public String toString() {
        return name;
    }

    public static SecurityType getSecType(String s) {
        for (SecurityType t : SecurityType.values()) {
            if (t.toString().equals(s)) {
                return t;
            }
        }
        throw new IllegalArgumentException("No such security type, " + s);
    }

}


