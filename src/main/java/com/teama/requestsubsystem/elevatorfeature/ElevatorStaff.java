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
public class ElevatorStaff {
    private ServiceStaff genInfo;
    private Set<MaintenanceType> typesToWorkOn;

    public ElevatorStaff(ServiceStaff i,Set<MaintenanceType> types ) {
        genInfo = i;
        this.typesToWorkOn = types;
        genInfo.setStaffType(StaffType.MAINTENANCE);
    }

    public ElevatorStaff(int id, ElevatorStaff s, Set<MaintenanceType> types) {
        GenericStaff genInfo = new GenericStaff(id, s.getFirstName(), s.getLastName(), s.getUsername(), s.getContactInfo());
        this.genInfo = genInfo;
        this.typesToWorkOn = types;
        genInfo.setStaffType(StaffType.INTERPRETER);
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
