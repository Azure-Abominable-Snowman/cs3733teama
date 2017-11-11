package entities;

import java.util.Set;

public abstract class ServiceStaff {
    private Set<Language> languages;
    private boolean available;
    private StaffType type;
    private String staffId;

    public ServiceStaff(String staffId, StaffType type, Set<Language> languages, boolean available) {
        this.available = available;
        this.languages = languages;
        this.type = type;
        this.staffId = staffId;
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
}
