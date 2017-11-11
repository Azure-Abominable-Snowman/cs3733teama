package entities;

public interface StaffInfoDataSource {
    default ServiceStaff findQualified(StaffAttrib attrib) {
        return null;
    }
}
