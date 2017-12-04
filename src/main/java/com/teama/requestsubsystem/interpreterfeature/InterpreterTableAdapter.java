package com.teama.requestsubsystem.interpreterfeature;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.Set;

/*
   This is an adapter class for the interpreter so it can be displayed in a TableView
 */
public class InterpreterTableAdapter {
    private StringProperty firstName;
    private StringProperty lastName;
    private StringProperty languages;
    private StringProperty certification;
    private StringProperty phone;
    private StringProperty email;
    private InterpreterStaff interpreterBase;

    public InterpreterTableAdapter(InterpreterStaff interpreter){
        firstName= new SimpleStringProperty(interpreter.getFirstName());
        lastName= new SimpleStringProperty(interpreter.getLastName());
        String langsToAdd ="";
        for(Language l : interpreter.getLanguages()){
            langsToAdd+=l.toString()+"\n";
        }
        languages =new SimpleStringProperty(langsToAdd);
        certification=new SimpleStringProperty(interpreter.getCertification().toString());
        phone=new SimpleStringProperty(interpreter.getPhoneNumber());
        email = new SimpleStringProperty(interpreter.getEmail());
        this.interpreterBase=interpreter;
    }
    public String getFirstName(){
        return firstName.get();
    }
    public String getLastName() {return lastName.get();}
    public String getLanguages(){return languages.get();}
    public String getCertification(){return certification.get();}
    public String getPhone(){return phone.get();}
    public String getEmail(){return email.get();}
    public InterpreterStaff getInterpreter(){return interpreterBase;}
    public void setCertification(CertificationType certification){this.certification.set(certification.toString());}
    public void setLanguages(Set<Language> languages){
        String langsToAdd ="";
        for(Language l : languages){
            langsToAdd+=l.toString()+"\n";
        }
        this.languages.set(langsToAdd);
    }
    public void setFirstName(String firstName){
        this.firstName.set(firstName);
    }
    public void setLastName(String lastName){this.lastName.set(lastName);}
    public void setPhone(String phone){this.phone.set(phone);}
    public void setEmail(String email){this.email.set(email);}
}
