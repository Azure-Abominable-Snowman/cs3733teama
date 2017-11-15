package entities.staff;

import java.util.Set;

/**
 * Created by jakepardue on 11/12/17.
 */
public class JanitorStaff extends ServiceStaff{
    public JanitorStaff(String staffId, String firstName, String lastName, String phoneNumber, StaffType type, Set<Language> languages, boolean available) {
        super(staffId, firstName, lastName, phoneNumber, type, languages, available);
    }
}
