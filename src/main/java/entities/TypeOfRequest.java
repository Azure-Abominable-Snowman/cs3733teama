package entities;

/**
 * Created by jakepardue on 11/10/17.
 */
public enum TypeOfRequest {
    FOOD("Food"), SEC("Security"), TRANS("Transportation"), INTR("Interpretor"), JAN("Janitor"), MAIN("Maintenance");

    private String typeOfRequest;

    TypeOfRequest(String str1) {
        this.typeOfRequest = str1;
    }

    public boolean equalsTypeOfRequest(String str){
        return this.typeOfRequest.equals(str);
    }
}
