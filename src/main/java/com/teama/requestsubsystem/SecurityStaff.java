package com.teama.requestsubsystem;

import com.teama.controllers.Provider;

import java.util.Set;

public class SecurityStaff extends ServiceStaff {
    public SecurityStaff(String staffId, String firstName, String lastName, String phoneNumber, StaffType type, Set<Language> languages, Enum<Provider> prov, boolean available) {
        super(staffId, firstName, lastName, phoneNumber, type, languages, prov, available);
    }
}
