package com.teama.requestsubsystem.interpreterfeature;

import com.teama.messages.ContactInfo;
import com.teama.messages.Provider;
import com.teama.requestsubsystem.GenericStaff;
import com.teama.requestsubsystem.ServiceStaff;
import com.teama.requestsubsystem.StaffType;

import java.util.Set;

public class InterpreterStaff  implements ServiceStaff {
    private ServiceStaff genInfo;
    //private InterpreterInfo interpSpecs;
    private Set<Language> languages;
    private CertificationType certification;
    //TODO: specify hours on duty perhaps
    public InterpreterStaff(ServiceStaff i, Set<Language> langs, CertificationType certification) {
        genInfo = i;
        languages = langs;
        this.certification = certification;
        genInfo.setStaffType(StaffType.INTERPRETER);
    }

    public InterpreterStaff(int id, InterpreterStaff s) {
        GenericStaff genInfo = new GenericStaff(id, s.getFirstName(), s.getLastName(), s.getUsername(), s.getContactInfo());
        this.genInfo = genInfo;
        this.languages = s.getLanguages();
        this.certification = s.getCertification();
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
        InterpreterSubsystem.getInstance().addStaff(this);
    }
    public void update() {
        InterpreterSubsystem.getInstance().updateStaff(this);
    }
    public void remove() {
        InterpreterSubsystem.getInstance().removeStaff(this.getStaffID());
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

    public CertificationType getCertification() {
        return certification;
    }
    public String getUsername() {
        return genInfo.getUsername();
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
    public int getStaffID() {
        return genInfo.getStaffID();
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
