package entities.servicerequests;

public enum PriorityLevel {
    LOW(1), MEDIUM(2), HIGH(3);

    private final int value;

    private PriorityLevel(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
