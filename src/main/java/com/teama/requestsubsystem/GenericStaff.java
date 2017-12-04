package com.teama.requestsubsystem;

import com.teama.messages.ContactInfo;
import com.teama.messages.Provider;

/**
 * Created by aliss on 11/21/2017.
 */
public class GenericStaff implements ServiceStaff { // staff info shared among all staff members
    int staffID = 0;
    StaffType t;
    private String firstName;
    private String lastName;
    private String username;
    private ContactInfo c;



    public GenericStaff(String firstName, String lastName, ContactInfo c) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = firstName.substring(0).toLowerCase() + lastName.toLowerCase();
        this.c = c;
    }

    public GenericStaff(String firstName, String lastName, String username, ContactInfo c) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.c = c;
    }
    public void add() {
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

    public StaffType getStaffType() {
        return this.t;
    }

    public void setStaffType(StaffType s) {
        this.t = s;
    }

    public void setEmail(String email) {
        c.setEmailAddress(email);
    }

    public String getEmail() {
        return c.getEmailAddress();
    }

    public void setPhoneNumber(String phone) {
        c.setPhoneNumber(phone);
    }

    public String getPhoneNumber() {
        return c.getPhoneNumber();
    }

    public void setProvider(Provider p) {
        c.setProvider(p);
    }

    public Provider getProvider() {
        return c.getProvider();
    }


    public void setFirstName(String first) {
        firstName = first;
    }

    public void setLastName(String last) {
        lastName = last;
    }

    void setStaffID(int staffID) {
        this.staffID = staffID;
    }

    public int getStaffID() {
        return this.staffID;
    }

}
