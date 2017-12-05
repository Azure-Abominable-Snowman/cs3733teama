package com.teama.messages;

import java.util.HashSet;
import java.util.Set;

public class ContactInfo {
    private Set<ContactInfoTypes> availableContactInfoTypes;
    private String phoneNumber, emailAddress;
    private Provider provider;

    public ContactInfo() {
        availableContactInfoTypes = new HashSet<>();
    }
    public ContactInfo(Set<ContactInfoTypes> t, String num, String email, Provider prov) {
        availableContactInfoTypes = t;
        phoneNumber = num;
        emailAddress = email;
        provider = prov;
    }
    public Set<ContactInfoTypes> getAvailableContactInfoTypes() {
        return availableContactInfoTypes;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        availableContactInfoTypes.add(ContactInfoTypes.PHONE);
        availableContactInfoTypes.add(ContactInfoTypes.TEXT);
        this.phoneNumber = phoneNumber;
    }

    /*public Provider getPhoneProvider() {
        return provider;
    }

    public void setPhoneProvider(Provider provider) {
        this.provider = provider;
    }*/

    public String getEmailAddress() {
        return emailAddress;
    }
    public Provider getProvider() {
        return this.provider;
    }

    public void setEmailAddress(String emailAddress) {
        availableContactInfoTypes.add(ContactInfoTypes.EMAIL);
        this.emailAddress = emailAddress;
    }

    public void setProvider(Provider p) {
        provider = p;
    }
}

//TODO: add a DB for Staff Contact Info linked to the Staff Database