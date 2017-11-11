package entities;

import java.util.Set;

public class TransportStaff extends ServiceStaff {
    public TransportStaff(String staffId, StaffType type, Set<Language> languages, boolean available) {
        super(staffId, type, languages, available);
    }
}
