package com.teama.mapsubsystem.data;

public enum Floor {
    //SUBBASEMENT("L2"), BASEMENT("L1"), GROUND("G"), ONE("1"), TWO("2"), THREE("3");
    THREE("3"), TWO("2"), ONE("1"), GROUND("G"), BASEMENT("L1"), SUBBASEMENT("L2");

    private final String floor;

    private Floor(String s) {
        floor = s;
    }

    public boolean equalsName(String otherName) {
        return floor.equals(otherName);
    }

    public String toString() { return this.floor; }

    public static Floor getFloor(String name) {
        for(Floor f : Floor.values()) {
            if(f.toString().equals(name)) {
                return f;
            }
        }
        throw new IllegalArgumentException("No floor by the name "+name+" found");
    }
}
