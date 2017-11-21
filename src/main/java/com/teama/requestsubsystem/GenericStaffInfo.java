package com.teama.requestsubsystem;

import com.teama.messages.ContactInfo;

/**
 * Created by aliss on 11/21/2017.
 */
public class GenericStaffInfo { // staff info shared among all staff members
    private int staffID;
    private String firstName;
    private String lastName;
    private ContactInfo c;


    public GenericStaffInfo(int staffID, String firstName, String lastName, ContactInfo c) {
        this.staffID = staffID;
        this.firstName = firstName;
        this.lastName = lastName;
        this.c = c;
    }

    public int getStaffID() {
        return staffID;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public ContactInfo getContactInfo() {
        return this.c;
    }

}
