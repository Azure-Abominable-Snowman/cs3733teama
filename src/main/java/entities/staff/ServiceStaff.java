package entities.staff;

import boundaries.Provider;

import java.util.Set;

public abstract class ServiceStaff {
    private Set<Language> languages;
    private boolean available;
    private StaffType type;
    private String staffId, firstName, lastName, phoneNumber;
    public Enum<Provider> provider;

    public ServiceStaff(String staffId, String firstName, String lastName, String phoneNumber, StaffType type,
                        Set<Language> languages, Enum<Provider> provider, boolean available) {
        this.available = available;
        this.languages = languages;
        this.type = type;
        this.staffId = staffId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.provider = provider;
    }

    public Set<Language> getLanguages() {
        return languages;
    }

    public StaffType getStaffType() {
        return type;
    }

    public String getStaffId() { return staffId; }

    public boolean isAvailable() {
        return available;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public Enum<Provider> getProvider(){ return provider; }
}
