package com.teama.translator;

import java.util.Locale;
import java.util.ResourceBundle;


public class TranslateBundle {

     Locale english = new Locale("en", "US");
     ResourceBundle label1 = ResourceBundle.getBundle("Translation.properties",english);

    Locale spanish = new Locale("es", "US");
    ResourceBundle label2 = ResourceBundle.getBundle("Translation.properties",spanish);

    Locale french = new Locale("fr", "US");
    ResourceBundle label3 = ResourceBundle.getBundle("Translation.properties",french);






}