package entities;

public interface StaffInfoDataSource {

    /**
     * Returns an available staff member with the requested attributes
     * @param attrib
     * @return
     */
    default ServiceStaff findQualified(StaffAttrib attrib) {
        return null;
    }

    default void close() {

    }
}
