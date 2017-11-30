package com.teama.messages;


import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

/**
 * Created by jakepardue on 11/14/17.
 */
public class EmailMessage implements SendMessage {
    private String from;
    private MimeMessage mimeMessage;

    public EmailMessage() {
        this.from = "cs3733teama@gmail.com";
        String password = "wilsonwong"; // TODO: Put this in a file somewhere, plaintext passwords are bad


        Properties properties = new Properties();
        properties.put("mail.smtp.username", "cs3733teama@gmail.com");
        properties.put("mail.smtp.password", "wilsonwong");
        properties.put("mail.smtp.protocol", "smtp");
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "465");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.socketFactory.port", "465");
        properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        properties.put("mail.smtp.socketFactory.fallback", "false");

        Authenticator a1 = new javax.mail.Authenticator() {
            //@Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(from, password);
            }
        };

        Session s = Session.getInstance(properties, a1);
        mimeMessage = new MimeMessage(s);
    }

    @Override
    public boolean sendMessage(ContactInfo contactInfo, Message message) {
        if (!contactInfo.getAvailableContactInfoTypes().contains(ContactInfoTypes.EMAIL)) {
            System.out.println("Invalid contact info!!!!!!! REEEEEEEEEE");
            return false;
        }

        try {
            mimeMessage.addRecipient(javax.mail.Message.RecipientType.TO, new InternetAddress(contactInfo.getEmailAddress()));
            if(!contactInfo.getAvailableContactInfoTypes().contains(ContactInfoTypes.TEXT)){
                mimeMessage.setSubject(message.getSubject());
            }
            mimeMessage.setText(message.getMessageContents());

            Transport.send(mimeMessage);
            System.out.println("Message was sent successfully!");
            return true;
        } catch (MessagingException e) {
            e.printStackTrace();
        }
        return false;
    }
}
