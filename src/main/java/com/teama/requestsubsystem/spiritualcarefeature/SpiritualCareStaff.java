package com.teama.requestsubsystem.spiritualcarefeature;

import com.teama.messages.ContactInfo;
import com.teama.messages.Provider;
import com.teama.requestsubsystem.GenericStaff;
import com.teama.requestsubsystem.ServiceStaff;
import com.teama.requestsubsystem.StaffType;

/**
 * Created by jakepardue on 12/10/17.
 */
public class SpiritualCareStaff {

    private ServiceStaff genInfo;
    private Religion religion;

    public SpiritualCareStaff(ServiceStaff s, Religion r){
        this.genInfo = s;
        this.religion = r;
        genInfo.setStaffType(StaffType.SPIRITUAL);
    }

    public SpiritualCareStaff(int id, SpiritualCareStaff s){
        GenericStaff info = new GenericStaff(id, s.getFirstName(), s.getLastName(), s.getUsername(), s.getContactInfo());
        this.genInfo = info;
        this.religion = s.getReligion();
        genInfo.setStaffType(StaffType.SPIRITUAL);
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
    public String getLastName() { return genInfo.getLastName(); }

    public String getPhoneNumber() {
        return genInfo.getPhoneNumber();
    }
    public void setPhoneNumber(String num) {
        genInfo.setPhoneNumber(num);
    }

    public String getEmail() {
        return genInfo.getEmail();
    }
    public void setEmail(String email) {
        genInfo.setEmail(email);
    }

    public Provider getProvider() {
        return genInfo.getProvider();
    }
    public void setProvider(Provider p) {
        genInfo.setProvider(p);
    }

    public String getUsername() {
        return genInfo.getUsername();
    }
    public void setUsername(String uname) {
        genInfo.setUsername(uname);
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

    public void setPhone(String phone) {
        genInfo.setPhoneNumber(phone);
    }

    public ContactInfo getContactInfo(){
        return genInfo.getContactInfo();
    }
    public int getStaffID() {
        return genInfo.getStaffID();
    }

    public Religion getReligion(){ return religion; }
    public void setReligion(Religion r){ this.religion = r;}

    public String toString(){
        return getFirstName() + getLastName() + ", " + getReligion();
    }





}
