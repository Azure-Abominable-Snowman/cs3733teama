package com.teama.requestsubsystem;

import com.teama.messages.ContactInfo;
import com.teama.messages.Provider;

/**
 * Created by aliss on 12/2/2017.
 */
public interface ServiceStaff {
    void add();
    void remove();
    void update();

    public String getFirstName();
    public void setFirstName(String first);

    public StaffType getStaffType();
    public void setStaffType(StaffType t);

    public String getLastName();
    public void setLastName(String last);

    public String getUsername();

    public void setUsername(String uname);

    public ContactInfo getContactInfo();

    public void setEmail(String email);

    public String getEmail();

    public void setPhoneNumber(String phone);

    public String getPhoneNumber();

    public void setProvider(Provider p);

    public Provider getProvider();

    public int getStaffID();



}
