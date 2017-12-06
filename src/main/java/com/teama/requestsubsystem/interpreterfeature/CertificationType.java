package com.teama.requestsubsystem.interpreterfeature;


/**
 * Created by aliss on 11/21/2017.
 */
public enum CertificationType {
    CHI("CHI"),
    CCHI("CCHI"),
    CMI("CMI"),
    CDI("CDI"),
    NONE("NONE");

    private final String name;

    private CertificationType(String name) {
        this.name = name;
    }
    @Override
    public String toString() {
        return name;
    }

    public static CertificationType getCertificationType(String s) {
        for (CertificationType c: CertificationType.values()) {
            if (c.toString().equals(s)) {
                return c;
            }
        }
        throw new IllegalArgumentException("No such Certification Type, " + s);
    }


}
