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

    public NodeType getNodeType(String name) {
        for (NodeType n : NodeType.values()) {
            if (n.toString().equals(name)) {
                return n;
            }
        }
        throw new IllegalArgumentException("No such NodeType " + name);
    }

    public static NodeType fromValue(String val) {
        for(NodeType type : NodeType.values()) {
            if(type.toString().equals(val)) {
                return type;
            }
        }
        return null;
    }

    public String toString() {
        return this.name;
    }
}
