package entities.Translate;

import java.lang.System; //Do not need to import it but println is not working...
import java.util.Locale;
import java.util.ResourceBundle;


public class TranslateBundle {

    private String lang; //DELETE

    Locale english = new Locale("en", "US");
    ResourceBundle label1 = ResourceBundle.getBundle("Translate.properties",english);

    Locale spanish = new Locale("es", "US");
    ResourceBundle label2 = ResourceBundle.getBundle("Translate.properties",spanish);

    Locale french = new Locale("fr", "US");
    ResourceBundle label3 = ResourceBundle.getBundle("Translate.properties",french);



    System.out.println(label1.getString("label1"));


}
