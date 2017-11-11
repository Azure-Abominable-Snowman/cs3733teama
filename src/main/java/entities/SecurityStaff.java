package entities;

import java.util.Set;

public class SecurityStaff extends ServiceStaff {
    public SecurityStaff(String staffId, StaffType type, Set<Language> languages, boolean available) {
        super(staffId, type, languages, available);
    }
}
