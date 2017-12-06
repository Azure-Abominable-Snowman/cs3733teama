package com.teama.mapsubsystem.pathfinding;

public enum TurnType {
    TURNLEFT("turnleft"), TURNLEFTSLIGHT("turnleftslight"), TURENLEFTSHARP("turnleftsharp"),
    TURNRIGHT("turnright"), TURNRIGHTSLIGHT("turnrightslight"), TURNRIGHTSHARP("turnrightsharp"),
    REVERSE("reverse"), STRAIGHT("straight"), START("start"), END("end"),INTONEWFLOOR("intonewfloor"),
    ELEVATOR("elevator"),STAIR("stair");

    private final String turnType;

    private TurnType(String turn) {
        turnType = turn;
    }

    public boolean equalsName(String otherName) {
        return turnType.equals(otherName);
    }

    public String toString() {
        return this.turnType;
    }

    public static TurnType getTurn(String name) {
        for(TurnType t : TurnType.values()) {
            if(t.toString().equals(name)) {
                return t;
            }
        }
        throw new IllegalArgumentException("No turn by the name "+name+" found");
    }

}
