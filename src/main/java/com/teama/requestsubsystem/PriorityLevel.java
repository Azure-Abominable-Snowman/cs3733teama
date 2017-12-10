package com.teama.requestsubsystem;
// not currently used
public enum PriorityLevel {
    LOW(1), MEDIUM(2), HIGH(3);

    private final int value;

    PriorityLevel(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static PriorityLevel getPriorityLevel(int level) {
        for (PriorityLevel p: PriorityLevel.values()) {
            if (p.getValue() == level) {
                return p;
            }
        }
        throw new IllegalArgumentException("No such priority level, " + level + ", exists.");
    }
}
