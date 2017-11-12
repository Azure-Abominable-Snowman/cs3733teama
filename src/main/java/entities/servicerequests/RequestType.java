package entities.servicerequests;

public enum RequestType {
    FOOD("Food"), SEC("Security"), TRANS("Transportation"), INTR("Interpreter");

    private final String value;

    private RequestType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
