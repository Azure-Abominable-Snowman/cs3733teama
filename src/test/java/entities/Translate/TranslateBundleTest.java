package entities.Translate;

import org.junit.Test;

import java.util.Locale;
import java.util.ResourceBundle;

public class TranslateBundleTest {

    ResourceBundle bundleFR = ResourceBundle.getBundle("Translate_fr", Locale.FRANCE);


    public static void testLang(String[] args) {
        ResourceBundle bundle = ResourceBundle.getBundle("Translate");

        ResourceBundle bundleFR = ResourceBundle.getBundle("Translate_fr", Locale.FRANCE);


        //System.out.println(bundle.getString("CountryName"));
        //System.out.println(bundle.getString("CurrencyCode"));

    }



    @Test
    public void getLang() throws Exception {
        //assert
        System.out.println(bundleFR.getString("Hello"));
    }


}