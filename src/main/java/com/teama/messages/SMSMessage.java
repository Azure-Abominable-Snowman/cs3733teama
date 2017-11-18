package com.teama.messages;

/**
 * Created by jakepardue on 11/13/17.
 */

import java.util.EnumMap;
import java.util.Map;


public class SMSMessage implements SendMessage {


    public Map<Provider, String> providers = new EnumMap<>(Provider.class);

    private String phoneNumber;
    private Provider provider;
    private String workingAddress;
    private EmailMessage emailMessage;

    public void setMap(){
        providers.put(Provider.ATT, "@txt.att.net");
        providers.put(Provider.VERIZON, "@vtext.com");
        providers.put(Provider.TMOBILE, "@tmomail.net");
        providers.put(Provider.SPRINT, "@messaging.sprintpcs.com");
    }

    public SMSMessage(Provider prov){
        this.provider = prov;

        setMap();

        this.workingAddress = phoneNumber + providers.get(prov);
        this.emailMessage = new EmailMessage();
    }

    @Override
    public boolean sendMessage(ContactInfo contactInfo, Message message) {
        if(!contactInfo.getAvailableContactInfoTypes().contains(ContactInfoTypes.PHONE)) {
            System.out.println("No phone number available");
            return false;
        }

        ContactInfo phoneCI = new ContactInfo();
        phoneCI.setEmailAddress(workingAddress);

        return  emailMessage.sendMessage(phoneCI, message);
    }
}
