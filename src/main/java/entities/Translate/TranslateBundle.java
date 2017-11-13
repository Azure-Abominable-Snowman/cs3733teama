package entities.Translate;

import java.util.Locale;
import java.util.ResourceBundle;


public class TranslateBundle {

    Locale english = new Locale("en", "US");
    ResourceBundle label1 = ResourceBundle.getBundle("Translate.properties",english);

    Locale spanish = new Locale("es", "US");
    ResourceBundle label2 = ResourceBundle.getBundle("Translate.properties",spanish);

    Locale french = new Locale("fr", "US");
    ResourceBundle label3 = ResourceBundle.getBundle("Translate.properties",french);






}
