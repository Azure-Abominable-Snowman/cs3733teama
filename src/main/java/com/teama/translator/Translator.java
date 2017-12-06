package com.teama.translator;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Created by jakepardue on 12/2/17.
 */
public class Translator {

    Locale langLocale;
    ResourceBundle bundle;

    private static Translator ourInstance;

    public static Translator getInstance() {
        if(ourInstance == null){
            ourInstance = new Translator("en");
        }
        return ourInstance;
    }

    private Translator(String lang) {
        //TODO Remove langLocale form here once UI is set up
        this.langLocale = new Locale(lang);
        this.bundle = ResourceBundle.getBundle("lang", langLocale);
    }

    public String getText(String key) {
        return bundle.getString(key);
    }


    public void setLang(String lang) {
        this.langLocale = new Locale(lang);
        this.bundle = ResourceBundle.getBundle("lang", langLocale);
    }

}
