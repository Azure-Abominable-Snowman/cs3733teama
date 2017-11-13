package entities.Translate;

import java.util.ListResourceBundle;

public class Translator  extends ListResourceBundle {


    private Object[][] contents = {
            {"price" , new Double(75.00) },
            {"currency" , "EN"},
    };

    @Override
    protected Object[][] getContents() {
        return contents;

    }
}
