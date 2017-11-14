package entities;

import java.util.List;

/**
 * Created by jakepardue on 11/10/17.
 */
public interface ServiceStaff {

    String INTR = "Interpreter";
    String SEC = "Security";
    String TRANS = "Transport";
    String JAN = "Janitor";
    String MAIN = "Maintenance";



    //getters
    String getFirstName();
    String getLastName();
    String getPhoneNumber();
    boolean getIsAvailable();
    List<String> getSpokenLanguages();

    //setters
    void setFirstName(String newFirstName);
    void setLastName(String newLastName);
    void setPhoneNumber(String newPhoneNumber);
    void setIsAvailable(boolean newAvailability);
    void setSpokenLanguages(List<String> languages);
}
