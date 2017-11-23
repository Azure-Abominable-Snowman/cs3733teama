package com.teama.requestsubsystem.interpreterfeature;

import com.teama.messages.Provider;
import com.teama.requestsubsystem.GenericStaffInfo;

import java.util.Set;

public class InterpreterStaff  {
    private GenericStaffInfo info;
    private InterpreterInfo interpSpecs;
    //TODO: specify hours on duty perhaps
    public InterpreterStaff(GenericStaffInfo i, InterpreterInfo j) {
        info = i;
        interpSpecs = j;
    }

    public String getFirstName() {
        return info.getFirstName();
    }
    public String getLastName() {
        return info.getLastName();
    }
    public int getStaffID() {
        return interpSpecs.getStaffID();
    }
    public String getPhone() {
        return info.getContactInfo().getPhoneNumber();
    }
    public String getEmail() {
        return info.getContactInfo().getEmailAddress();
    }
    public Provider getProvider() {
        return info.getContactInfo().getProvider();
    }
    public CertificationType getCertification() {
        return interpSpecs.getCertification();
    }
    public Set<Language> getLanguages() {
        return interpSpecs.getLanguages();
    }
    public void setCertification(CertificationType c) {
        interpSpecs.setCertification(c);
    }
    public void setLanguages(Set<Language> langs) {
        interpSpecs.setLanguages(langs);
    }
    public void setFirstName(String name) {
        info.setFirstName(name);
    }
    public void setLastName(String name) {
        info.setLastName(name);
    }

    public void setProvider(Provider p) {
        info.setProvider(p);
    }

    public void setEmail(String email) {
        info.setEmail(email);
    }

    public void setPhone(String phone) {
        info.setPhoneNumber(phone);
    }


    /*
    public boolean isOnDuty() {

    }
    */
}
