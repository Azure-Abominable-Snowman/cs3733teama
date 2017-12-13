package com.teama.requestsubsystem.elevatorfeature;

import com.teama.messages.ContactInfo;
import com.teama.messages.Provider;
import com.teama.requestsubsystem.GenericStaff;
import com.teama.requestsubsystem.ServiceStaff;
import com.teama.requestsubsystem.StaffType;

import java.util.Set;

/**
 * Created by jakepardue on 12/8/17.
 */
public class ElevatorStaff implements ServiceStaff{
    private ServiceStaff genInfo;
    private Set<MaintenanceType> specialization;


    public ElevatorStaff(ServiceStaff i, Set<MaintenanceType> specializations ) {
        genInfo = i;
        this.specialization = specializations;
        genInfo.setStaffType(StaffType.ELEVATOR);
    }


    /**
     * COPY CONSTRUCTOR. NOT TO BE USED FOR ANY OTHER PURPOSE EXCEPT COPYING EXISTING ELEVATOR STAFF.
     * @param id
     * @param s
     *
     */
    public ElevatorStaff(int id, ElevatorStaff s) {
        GenericStaff genInfo = new GenericStaff(id, s.getFirstName(), s.getLastName(), s.getUsername(), s.getContactInfo());
        this.genInfo = genInfo;
        this.specialization = s.getSpecialization();
        genInfo.setStaffType(StaffType.ELEVATOR);
    }

    public void setGenInfo(ServiceStaff staff) {
        genInfo.setFirstName(staff.getFirstName());
        genInfo.setLastName(staff.getLastName());
        genInfo.setPhoneNumber(staff.getPhoneNumber());
        genInfo.setEmail(staff.getEmail());
        genInfo.setProvider(staff.getProvider());
        genInfo.setUsername(staff.getUsername());
    }

    public void add() {

    }
    public void update() {

    }
    public void remove() {

    }
    public Set<MaintenanceType> getSpecialization() {
        return specialization;
    }

    public void setSpecialization(Set<MaintenanceType> specialization) {
        this.specialization = specialization;
    }

    public String getFirstName() {
        return genInfo.getFirstName();
    }
    public String getLastName() {
        return genInfo.getLastName();
    }
    /*public int getStaffID() {
        return interpSpecs.getStaffID();
    }
    */
    public String getPhoneNumber() {
        return genInfo.getPhoneNumber();
    }
    public String getEmail() {
        return genInfo.getEmail();
    }
    public Provider getProvider() {
        return genInfo.getProvider();
    }



    public void setUsername(String uname) {
        genInfo.setUsername(uname);
    }
    public void setPhoneNumber(String num) {
        genInfo.setPhoneNumber(num);
    }
    public StaffType getStaffType() {
        return genInfo.getStaffType();
    }

    public void setStaffType(StaffType t) {
        genInfo.setStaffType(t);
    }


    public void setFirstName(String name) {
        genInfo.setFirstName(name);
    }
    public void setLastName(String name) {
        genInfo.setLastName(name);
    }

    public void setProvider(Provider p) {
        genInfo.setProvider(p);
    }

    public void setEmail(String email) {
        genInfo.setEmail(email);
    }

    public void setPhone(String phone) {
        genInfo.setPhoneNumber(phone);
    }

    public ContactInfo getContactInfo(){
        return genInfo.getContactInfo();
    }
    public int getStaffID() {
        return genInfo.getStaffID();
    }

    public String getUsername() {
        return genInfo.getUsername();
    }


    /*
    public String toString(){
        String lang = "";
        for(Language l: getLanguages()){
            lang +=l.toString() + ", ";
        }
        return getFirstName()+" "+getLastName()+"\n"+lang;
    }
    */

}
