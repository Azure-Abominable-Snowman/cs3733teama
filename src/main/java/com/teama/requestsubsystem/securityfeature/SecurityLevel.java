package com.teama.requestsubsystem.securityfeature;

public enum SecurityLevel {
    HIGH("1"), MEDIUM("2"), LOW("3");

    private final String s;

    SecurityLevel(String s) {
        this.s = s;
    }

    public String toString() {
        return s;
    }

}

