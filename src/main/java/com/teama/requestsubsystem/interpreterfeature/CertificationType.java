package com.teama.requestsubsystem.interpreterfeature;

/**
 * Created by aliss on 11/21/2017.
 */
public enum CertificationType {
    CHI("CHI"),
    CCHI("CCHI"),
    CMI("CMI"),
    CDI("CDI");

    private final String name;

    private CertificationType(String name) {
        this.name = name;
    }
    @Override
    public String toString() {
        return name;
    }


}
