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

    public static TranslationType getTranslationType(String s) {
        for (TranslationType t: TranslationType.values()) {
            if (t.toString().equals(s)) {
                return t;
            }
        }
        throw new IllegalArgumentException("No such translation type, " + s);
    }
}
