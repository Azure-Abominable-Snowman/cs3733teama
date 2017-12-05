package com.teama.requestsubsystem.transportationfeature;

import com.teama.messages.ContactInfo;
import com.teama.messages.Provider;
import com.teama.requestsubsystem.GenericStaffInfo;
import com.teama.requestsubsystem.interpreterfeature.CertificationType;
import com.teama.requestsubsystem.interpreterfeature.InterpreterInfo;
import com.teama.requestsubsystem.interpreterfeature.Language;

import java.util.Set;

/**
 * Created by jakepardue on 12/3/17.
 */
public class TransportationStaff {


    private GenericStaffInfo genericStaffInfo;
    private TransportInfo transportInfo;

    public TransportationStaff(GenericStaffInfo gInfo, TransportInfo tInfo ){
        this.genericStaffInfo = gInfo;
        this.transportInfo = tInfo;

    }

    public String getFirstName() {
        return genericStaffInfo.getFirstName();
    }

    public String getLastName() {
        return genericStaffInfo.getLastName();
    }

    public int getStaffID() {
        return transportInfo.getStaffID();
    }

    public String getPhone() {
        return genericStaffInfo.getContactInfo().getPhoneNumber();
    }

    public String getEmail() {
        return genericStaffInfo.getContactInfo().getEmailAddress();
    }

    public Provider getProvider() {
        return genericStaffInfo.getContactInfo().getProvider();
    }

    public Set<ModeOfTransportation> getModeOfTransportation() {
        return transportInfo.getModeOfTransportation();
    }

    public void setModeOfTransportation(Set<ModeOfTransportation> m) {
        transportInfo.setModeOfTransportation(m);
    }

    public void setFirstName(String name) {
        genericStaffInfo.setFirstName(name);
    }

    public void setLastName(String name) {
        genericStaffInfo.setLastName(name);
    }

    public void setProvider(Provider p) {
        genericStaffInfo.setProvider(p);
    }

    public void setEmail(String email) {
        genericStaffInfo.setEmail(email);
    }

    public void setPhone(String phone) {
        genericStaffInfo.setPhoneNumber(phone);
    }

    public ContactInfo getContactInfo(){
        return genericStaffInfo.getContactInfo();
    }

    public String toString(){
        String ans = "";
        for(ModeOfTransportation m : getModeOfTransportation()){
            ans+= m;
        }
        return getFirstName()+" "+getLastName()+"\n"+getStaffID()+"\n"+ans;
    }

}
