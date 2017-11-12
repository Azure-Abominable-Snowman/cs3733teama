package entities.staff;

import java.util.Set;

public class TransportStaff extends ServiceStaff {
    public TransportStaff(String staffId, String firstName, String lastName, String phoneNumber, StaffType type, Set<Language> languages, boolean available) {
        super(staffId, firstName, lastName, phoneNumber, type, languages, available);
    }
}
