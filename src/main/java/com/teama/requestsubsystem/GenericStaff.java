package com.teama.requestsubsystem;

import com.teama.messages.ContactInfo;
import com.teama.messages.Provider;

/**
 * Created by aliss on 11/21/2017.
 */
public class GenericStaff implements ServiceStaff { // staff info shared among all staff members
    int staffID = 0;
    private String firstName;
    private String lastName;
    private String username;
    private ContactInfo c;


    public GenericStaff(String firstName, String lastName, ContactInfo c) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.c = c;
    }
    public GenericStaff(String firstName, String lastName, String username, ContactInfo c) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.c = c;
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
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String uname) {
        this.username = uname;
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
