package com.teama.messages;

public class Message {
    private String subject;
    private String messageContents;

    public Message(String messageContents){
        this.messageContents = messageContents;
    }

    public Message(String subject, String messageContents){
        this.subject = subject;
        this.messageContents = messageContents;
    }

    public String getSubject() { return subject; }
    public String getMessageContents() {return messageContents;}

    public void setSubject(String str){
        this.subject = str;
    }

    public void setMessageContents(String str){
        this.messageContents = str;
    }
}
