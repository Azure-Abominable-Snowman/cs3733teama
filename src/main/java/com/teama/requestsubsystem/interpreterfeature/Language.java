package com.teama.requestsubsystem.interpreterfeature;

public enum Language {
    English("English"), Spanish("Spanish"), French("French"), German("German"), Russian("Russian"), Cantonese("Cantonese"), Luxembourgish("Luxembourgish"),
    Moldovan("Moldovan"), Ukranian("Ukranian"), ASL("ASL"), JAVA("JAVA");

    public final String name;

    Language(String s) {
        this.name = s;
    }

    public String toString() {
        return name;
    }

    public static Language getLanguage(String s) {
        for (Language l: Language.values()) {
            if (l.toString().equals(s)) {
                return l;
            }
        }
        throw new IllegalArgumentException("No such language, " + s);
    }
}
