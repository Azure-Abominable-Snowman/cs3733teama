package entities;

public interface ServiceStaff {
    default Language getLanguage() {
        return null;
    }

    default StaffType getStaffType() {
        return null;
    }
}
