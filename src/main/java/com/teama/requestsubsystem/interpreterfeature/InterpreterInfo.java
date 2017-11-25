package com.teama.requestsubsystem.interpreterfeature;

import java.util.Set;

/**
 * Created by aliss on 11/21/2017.
 */
public class InterpreterInfo { //info specific to interpreters
    private int id = 0; // set only by DB
    private Set<Language> languages;
    private CertificationType certification;

    public InterpreterInfo(Set<Language> langs, CertificationType certification) {
        languages = langs;
        this.certification = certification;
    }
    public InterpreterInfo(int id, Set<Language> langs, CertificationType certification) {
        this.id = id;
        languages = langs;
        this.certification = certification;
    }

    public int getStaffID() {
        return id;
    }
    public CertificationType getCertification() {
        return certification;
    }

    public void setCertification(CertificationType s) {
        certification = s;
    }

    public Set<Language> getLanguages() {
        return languages;
    }

    public void setLanguages(Set<Language> langs) {
        this.languages = langs;
    }
}


