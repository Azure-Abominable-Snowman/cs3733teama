package com.teama.requestsubsystem;

import java.util.Set;

/**
 * Defines the attributes for a staff, including languages spoken
 * and staff type. Used to query the database for a staff member with the
 * requested attributes.
 * If availability is false that means either unavailable or available staff members can be matched,
 * true means only available ones can be
 */
public class StaffAttrib {
    private Set<Language> spokenLanguage;
    private StaffType type;
    private boolean available;

    public StaffAttrib(StaffType type, Set<Language> spokenLanguages) {
        this.spokenLanguage = spokenLanguages;
        this.type = type;
        this.available = available;
    }

    public StaffType getType() {
        return type;
    }

    public Set<Language> getSpokenLanguages() {
        return spokenLanguage;
    }
}
