package entities;

import java.util.Set;

public class InterpreterStaff extends ServiceStaff {
    public InterpreterStaff(String staffId, String firstName, String lastName, String phoneNumber, StaffType type, Set<Language> languages, boolean available) {
        super(staffId, firstName, lastName, phoneNumber, type, languages, available);
    }
}
