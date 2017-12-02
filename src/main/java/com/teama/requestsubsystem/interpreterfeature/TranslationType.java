package com.teama.requestsubsystem.interpreterfeature;

/**
 * Created by aliss on 11/20/2017.
 */
public enum TranslationType {
    VERBAL("Verbal"),
    WRITTEN("Written"),
    ASL("ASL");

    private final String s;

    private TranslationType(String s) {
        this.s = s;
    }

    public String toString() {
        return s;
    }
}
