package entities;

import java.util.Set;

public class InterpreterStaff extends ServiceStaff {

    public InterpreterStaff(String staffId, StaffType type, Set<Language> languages, boolean available) {
        super(staffId, type, languages, available);
    }
}
