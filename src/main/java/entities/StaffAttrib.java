package entities;

import java.util.Set;

/**
 * Defines the attributes for a staff, including languages spoken
 * and staff type. Used to query the database for a staff member with the
 * requested attributes
 */
public class StaffAttrib {
    private Set<Language> spokenLanguage;
    private StaffType type;

    public StaffAttrib(StaffType type, Set<Language> spokenLanguages) {
        this.spokenLanguage = spokenLanguages;
        this.type = type;

    }

    public StaffType getType() {
        return type;
    }

    public Set<Language> getSpokenLanguages() {
        return spokenLanguage;
    }
}
