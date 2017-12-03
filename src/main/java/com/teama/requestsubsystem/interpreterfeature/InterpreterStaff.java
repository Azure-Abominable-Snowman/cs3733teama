package com.teama.requestsubsystem.interpreterfeature;

import com.teama.messages.ContactInfo;
import com.teama.messages.Provider;
import com.teama.requestsubsystem.GenericStaff;
import com.teama.requestsubsystem.ServiceStaff;

import java.util.Set;

public class InterpreterStaff  implements ServiceStaff {
    private GenericStaff genInfo;
    //private InterpreterInfo interpSpecs;
    private Set<Language> languages;
    private CertificationType certification;
    //TODO: specify hours on duty perhaps
    public InterpreterStaff(GenericStaff i, Set<Language> langs, CertificationType certification) {
        genInfo = i;
        languages = langs;
        this.certification = certification;
    }

    public void add() {
        //TODO
    }
    public void update() {
        // TODO
    }
    public void remove() {
        // TODO
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
    public String getPhone() {
        return genInfo.getContactInfo().getPhoneNumber();
    }
    public String getEmail() {
        return genInfo.getContactInfo().getEmailAddress();
    }
    public Provider getProvider() {
        return genInfo.getContactInfo().getProvider();
    }

    public CertificationType getCertification() {
        return certification;
    }

    public Set<Language> getLanguages() {
        return languages;
    }
    public void setCertification(CertificationType c) {
        this.certification = c;
    }
    public void setLanguages(Set<Language> langs) {
        this.languages = langs;
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

    public String toString(){
        String lang = "";
        for(Language l: getLanguages()){
            lang +=l.toString() + ", ";
        }
        return getFirstName()+" "+getLastName()+"\n"+lang;
    }


    /*
    public boolean isOnDuty() {

    }
    */
}
