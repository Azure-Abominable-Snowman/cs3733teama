package com.teama.requestsubsystem.transportationfeature;

import com.teama.messages.ContactInfo;
import com.teama.messages.Provider;
import com.teama.requestsubsystem.GenericStaff;
import com.teama.requestsubsystem.ServiceStaff;
import com.teama.requestsubsystem.StaffType;

import java.util.Set;

/**
 * Created by jakepardue on 12/3/17.
 */
public class TransportationStaff {

    private int id;
    private Set<ModeOfTransportation> modeOfTransportation;
    private ServiceStaff genInfo;

    public TransportationStaff(ServiceStaff staffInfo, Set<ModeOfTransportation> modes){
       this.genInfo = staffInfo;
       this.modeOfTransportation = modes;
       staffInfo.setStaffType(StaffType.TRANSPORT);
    }

    public TransportationStaff(int id, TransportationStaff s){
        this.genInfo = new GenericStaff(id, s.getFirstName(), s.getLastName(), s.getUsername(), s.getContactInfo());
        this.modeOfTransportation = s.getModeOfTransportation();
        genInfo.setStaffType(StaffType.TRANSPORT);
    }

    public void setGenInfo(ServiceStaff staff) {
        genInfo.setFirstName(staff.getFirstName());
        genInfo.setLastName(staff.getLastName());
        genInfo.setPhoneNumber(staff.getPhoneNumber());
        genInfo.setEmail(staff.getEmail());
        genInfo.setProvider(staff.getProvider());
        genInfo.setUsername(staff.getUsername());
    }

    public String getFirstName() {
        return genInfo.getFirstName();
    }
    public void setFirstName(String name) {
        genInfo.setFirstName(name);
    }

    public String getLastName() {
        return genInfo.getLastName();
    }
    public void setLastName(String name) {genInfo.setLastName(name);}

    public String getPhoneNumber() {
        return genInfo.getPhoneNumber();
    }
    public void setPhone(String phone) {
        genInfo.setPhoneNumber(phone);
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
    public void setUsername(String uname) {genInfo.setUsername(uname);}

    public ContactInfo getContactInfo(){
        return genInfo.getContactInfo();
    }

    public StaffType getStaffType() {
        return genInfo.getStaffType();
    }
    public void setStaffType(StaffType t) {
        genInfo.setStaffType(t);
    }



    public Set<ModeOfTransportation> getModeOfTransportation(){
        return modeOfTransportation;
    }

    public void setModeOfTransportation(Set<ModeOfTransportation> modes){
        this.modeOfTransportation = modes;
    }





}
