package entities;

import java.util.ArrayList;

/**
 * Defines the attributes for a staff, including languages spoken
 * and staff type. Used to query the database for a staff member with the
 * requested attributes
 */
public class StaffAttrib {
    private ArrayList<Language> spokenLanguage;
    private StaffType type;

    public StaffAttrib(StaffType type, ArrayList<Language> spokenLanguages) {
        this.spokenLanguage = spokenLanguages;
        this.type = type;

    }

    public StaffType getType() {
        return type;
    }

    public ArrayList<Language> getSpokenLanguage() {
        return spokenLanguage;
    }
}
