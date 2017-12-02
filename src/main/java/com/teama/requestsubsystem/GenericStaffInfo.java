package com.teama.requestsubsystem;

import com.teama.messages.ContactInfo;
import com.teama.messages.Provider;

/**
 * Created by aliss on 11/21/2017.
 */
public class GenericStaffInfo { // staff info shared among all staff members
    private String firstName;
    private String lastName;
    private ContactInfo c;


    public GenericStaffInfo(String firstName, String lastName, ContactInfo c) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.c = c;
    }
    // used only by DB



    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public ContactInfo getContactInfo() {
        return this.c;
    }

    public void setEmail(String email) {
        c.setEmailAddress(email);
    }

    public void setPhoneNumber(String phone) {
        c.setPhoneNumber(phone);
    }

    public void setProvider(Provider p) {
        c.setProvider(p);
    }

    public void setFirstName(String first) {
        firstName = first;
    }
    public void setLastName(String last) {
        lastName = last;
    }

}
