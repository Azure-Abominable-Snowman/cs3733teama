package com.teama.requestsubsystem;

public enum StaffType {
    INTERPRETER("Interpreter"), TRANSPORT("Transport"), SECURITY("Security"), ELEVATOR("Maintenance"), JANITOR("Janitor"),
    SPIRITUAL("Spiritual Care");
    public final String title;

    private StaffType(String s) {
        this.title = s;
    }

    public String toString() {
        return title;
    }
    public static StaffType getStaff(String s) {
        for (StaffType l: StaffType.values()) {
            if (l.toString().equals(s)) {
                return l;
            }
        }
        throw new IllegalArgumentException("No such staff position, " + s);
    }
}

