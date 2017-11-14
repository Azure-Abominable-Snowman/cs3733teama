package entities;
import java.util.List;

/**
 * Created by jakepardue on 11/10/17.
 */
public class JanitorStaff implements ServiceStaff {

    private String firstName;
    private String lastName;
    private String phoneNumber;
    private boolean isAvailable;
    private List<String> spokenLanguages;

    public JanitorStaff(String firstName, String lastName, String phoneNumber, boolean isAvailable, List<String> languages){
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.isAvailable = isAvailable;
        this.spokenLanguages = languages;
    }

    @Override
    public String getFirstName() { return this.firstName; }

    @Override
    public String getLastName() { return this.lastName; }

    @Override
    public String getPhoneNumber() { return this.phoneNumber; }

    @Override
    public boolean getIsAvailable() { return isAvailable; }

    public List<String> getSpokenLanguages(){ return this.spokenLanguages; }

    public String getServiceType(){ return ServiceStaff.JAN; }

    @Override
    public void setFirstName(String newFirstName) { this.firstName = newFirstName;}

    @Override
    public void setLastName(String newLastName) { this.lastName = newLastName;}

    @Override
    public void setPhoneNumber(String newPhoneNumber) { this.phoneNumber = newPhoneNumber;}

    @Override
    public void setIsAvailable(boolean newAvailability) { this.isAvailable = newAvailability;}

    @Override
    public void setSpokenLanguages(List<String> languages) { this.spokenLanguages = languages;}
}
