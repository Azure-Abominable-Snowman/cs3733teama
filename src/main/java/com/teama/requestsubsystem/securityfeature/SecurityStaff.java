package com.teama.requestsubsystem.securityfeature;

import com.teama.requestsubsystem.GenericStaffInfo;
import com.teama.messages.Provider;
import java.util.Set;

public class SecurityStaff {
    private GenericStaffInfo info;
    private SecurityInfo securitySpecs;

    public SecurityStaff(GenericStaffInfo i, SecurityInfo s){
        info = i;
        securitySpecs = s;
    }

    public String getFirstName(){return info.getFirstName();}
    public String getLastName(){return info.getLastName();}
    public int getStaffID(){return securitySpecs.getStaffID();}
    public String getPhone(){return info.getContactInfo().getPhoneNumber();}
    public String getEmail(){return info.getContactInfo().getEmailAddress();}
    public Provider getProvider(){return info.getContactInfo().getProvider();}
    public Set<SecurityType> getSecType(){return securitySpecs.getSecType();}



    public void setFirstName(String name) {info.setFirstName(name);}
    public void setLastName(String name) {info.setLastName(name);}
    public void setProvider(Provider p){info.setProvider(p);}
    public void setEmail(String email){info.setEmail(email);}
    public void setPhone(String phone){info.setPhoneNumber(phone);}
}




