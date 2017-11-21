package com.teama.mapsubsystem.data;

/**
 * Defines a node type
 */
public enum NodeType {
    HALL("Hallway"), REST("Restroom"), ELEV("Elevator"),
    STAI("Staircase"), DEPT("Medical Department"), LABS("Labs"),
    INFO("Information"), CONF("Conference Room"), EXIT("Exit"), RETL("Retail"),
    SERV("Service"), BATH("Bathroom");

    private final String name;

    private NodeType(String s) {
        name = s;
    }

    public boolean equalsName(String otherName) {
        return name.equals(otherName);
    }

    public String toString() {
        return this.name;
    }
}
