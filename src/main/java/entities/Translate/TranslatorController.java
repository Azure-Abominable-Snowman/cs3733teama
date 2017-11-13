package entities.Translate;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.fxml.Initializable;
import javafx.event.ActionEvent;

import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;

public class TranslatorController implements Initializable {


    @FXML
    private Label label;
    private ResourceBundle bundle;
    private Locale locale;


    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public class TranslatorTester {

        @FXML
        private Button setEnglish;

        @FXML
        private Button setFrench;

        @FXML
        private Button setSpanish;

        @FXML
        private Label showTranslated;

        @FXML
        void btnEN(ActionEvent event) {
            loadLang("en");

        }

        @FXML
        void btnFR(ActionEvent event) {
            loadLang("es");

        }

        @FXML
        void btnSP(ActionEvent event) {
            loadLang("fr");

        }


        private void loadLang(String lang) {
            locale = new Locale(lang);
            bundle = ResourceBundle.getBundle("lang_", locale);
            label.setText(bundle.getString("label"));
        }


    }


}
