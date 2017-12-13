package com.teama.translator;

import com.teama.controllers_refactor2.UTF8Control;

import java.util.Locale;
import java.util.ResourceBundle;

public class Translator {

    private Locale langLocale;
    private ResourceBundle bundle;

    private static Translator ourInstance;

    public static synchronized Translator getInstance() {
        if(ourInstance == null){
            ourInstance = new Translator("en");
        }
        return ourInstance;
    }

    private Translator(String lang) {
        //TODO Remove langLocale form here once UI is set up
        this.langLocale = new Locale(lang);
        this.bundle = ResourceBundle.getBundle("lang", langLocale, new UTF8Control());
    }

    public String getText(String key) {
        return bundle.getString(key);
    }


    public void setLang(String lang) {
        this.langLocale = new Locale(lang);
        this.bundle = ResourceBundle.getBundle("lang", langLocale, new UTF8Control());
    }

    public ResourceBundle getNewBundle(){

        return this.bundle = ResourceBundle.getBundle("lang", langLocale , new UTF8Control());

    }

}
