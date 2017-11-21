package com.teama.requestsubsystem.interpreterfeature;

public enum Language {
    English("English"), Spanish("Spanish"), French("French"), German("German"), Russian("Russian"), Cantonese("Cantonese"), Luxembourgish("Luxembourgish"),
    Moldovan("Moldovan"), Ukranian("Ukranian"), ASL("ASL");

    public final String name;

    private Language(String s) {
        this.name = s;
    }

    public String toString() {
        return name;
    }
}
