package com.teama.translator;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Created by jakepardue on 12/2/17.
 */
public class Translator {

    Locale langLocale;
    ResourceBundle bundle;

    private static Translator ourInstance = new Translator("");

    public static Translator getInstance() {
        return ourInstance;
    }

    private Translator(String lang) {
        langLocale = new Locale(lang);
        bundle = ResourceBundle.getBundle("lang", langLocale);
    }

    public String getText(String key) {
        return bundle.getString(key);
    }

}
