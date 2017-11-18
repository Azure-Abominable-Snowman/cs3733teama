package com.teama.messages;

import java.util.HashSet;
import java.util.Set;

public class ContactInfo {
    private Set<ContactInfoTypes> availableContactInfoTypes;
    private String phoneNumber, emailAddress;

    public ContactInfo() {
        availableContactInfoTypes = new HashSet<>();
    }

    public Set<ContactInfoTypes> getAvailableContactInfoTypes() {
        return availableContactInfoTypes;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }
}
