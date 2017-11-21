package com.teama.requestsubsystem;

import com.teama.messages.Provider;
import com.teama.requestsubsystem.interpreterfeature.Language;

import java.util.Set;

public class SecurityStaff extends ServiceStaff {
    public SecurityStaff(String staffId, String firstName, String lastName, String phoneNumber, StaffType type, Set<Language> languages, Provider prov, boolean available) {
        super(staffId, firstName, lastName, phoneNumber, type, languages, prov, available);
    }
}
