package com.teama.mapsubsystem.data;

public enum Floor {
    SUBBASEMENT("L2"), BASEMENT("L1"), GROUND("G"), ONE("1"), TWO("2"), THREE("3");

    private final String floor;

    private Floor(String s) {
        floor = s;
    }

    public boolean equalsName(String otherName) {
        return floor.equals(otherName);
    }

    public String toString() { return this.floor; }

}
