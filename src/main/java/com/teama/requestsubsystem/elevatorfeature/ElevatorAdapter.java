package com.teama.requestsubsystem.elevatorfeature;

import com.teama.requestsubsystem.interpreterfeature.Language;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.Set;

public class ElevatorAdapter {
    private StringProperty firstName;
    private StringProperty lastName;
    private StringProperty email;
    private StringProperty phone;
    private ElevatorStaff elevatorBase;
    private StringProperty certifications;

    public ElevatorAdapter(ElevatorStaff elevatorStaff){
        firstName= new SimpleStringProperty(elevatorStaff.getFirstName());
        lastName= new SimpleStringProperty(elevatorStaff.getLastName());
        phone=new SimpleStringProperty(elevatorStaff.getPhoneNumber());
        email = new SimpleStringProperty(elevatorStaff.getEmail());
        String certsToAdd="";
        for(MaintenanceType t : elevatorStaff.getSpecialization()){
            System.out.println(t.toString());
            certsToAdd+=t.toString()+"\n";
        }
        certifications=new SimpleStringProperty(certsToAdd);
        this.elevatorBase=elevatorStaff;
    }

    public ElevatorStaff getElevatorBase() {
        return elevatorBase;
    }

    public String getFirstName(){
        return firstName.get();
    }
    public String getLastName() {return lastName.get();}
    public String getPhone(){return phone.get();}
    public String getEmail(){return email.get();}
    public String getCertifications(){return certifications.get();}

    public void setFirstName(String firstName){
        this.firstName.set(firstName);
    }
    public void setLastName(String lastName){this.lastName.set(lastName);}
    public void setPhone(String phone){this.phone.set(phone);}
    public void setEmail(String email){this.email.set(email);}
    public void setCertifications(Set<MaintenanceType> types){
        String certs ="";
        for(MaintenanceType t : types){
            certs+=t.toString()+"\n";
        }
        this.certifications.set(certs);
    }
}
