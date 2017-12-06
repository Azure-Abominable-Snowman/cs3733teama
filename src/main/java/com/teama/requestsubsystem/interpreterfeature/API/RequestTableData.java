package com.teama.requestsubsystem.interpreterfeature.API;

import com.teama.mapsubsystem.data.MapNode;
import com.teama.requestsubsystem.RequestStatus;
import com.teama.requestsubsystem.interpreterfeature.InterpreterRequest;
import com.teama.requestsubsystem.interpreterfeature.Language;
import javafx.beans.property.*;

/**
 * Created by aliss on 12/5/2017.
 */
public class RequestTableData {
    private final ObjectProperty<RequestStatus> status;
    private final StringProperty location;
    private final ObjectProperty<Language> requiredLanguage;
    private final IntegerProperty staffID;
    private int requestID;
    private final StringProperty note;

    public RequestTableData(InterpreterRequest r, MapNode destination) {
        this.status = new SimpleObjectProperty<RequestStatus>(r.getStatus());
        this.location = new SimpleStringProperty(destination.getId());
        this.requiredLanguage = new SimpleObjectProperty<Language>(r.getRequiredLanguage());
        this.staffID = new SimpleIntegerProperty(r.getStaffID());
        this.note = new SimpleStringProperty(r.getNote());
        this.requestID = r.getRequestID();
    }

    public int getRequestID() {
        return requestID;
    }

    public void setRequestID(int requestID) {
        this.requestID = requestID;
    }

    public String getNote() {
        return note.get();
    }

    public StringProperty noteProperty() {
        return note;
    }

    public void setNote(String note) {
        this.note.set(note);
    }



    public RequestStatus getStatus() {
        return status.get();
    }

    public ObjectProperty<RequestStatus> statusProperty() {
        return status;
    }

    public void setStatus(RequestStatus status) {
        this.status.set(status);
    }

    public String getLocation() {
        return location.get();
    }

    public StringProperty locationProperty() {
        return location;
    }

    public void setLocation(String location) {
        this.location.set(location);
    }

    public Language getRequiredLanguage() {
        return requiredLanguage.get();
    }

    public ObjectProperty<Language> requiredLanguageProperty() {
        return requiredLanguage;
    }

    public void setRequiredLanguage(Language requiredLanguage) {
        this.requiredLanguage.set(requiredLanguage);
    }

    public int getStaffID() {
        return staffID.get();
    }

    public IntegerProperty staffIDProperty() {
        return staffID;
    }

    public void setStaffID(int staffID) {
        this.staffID.set(staffID);
    }


}
