package entities.Translate;

import java.util.ListResourceBundle;

public class Translator_es extends ListResourceBundle{


        private Object[][] contents = {
                {"price" , new Double(75.00) },
                {"currency" , "ES"},
        };

        @Override
        protected Object[][] getContents() {
            return contents;

        }




}
