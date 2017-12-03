package com.teama.controllers;

/**
 * Created by jakepardue on 11/28/17.
*/

import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import com.teama.requestsubsystem.interpreterfeature.Language;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;



import javax.security.auth.callback.LanguageCallback;

public class InterpReqController{

        @FXML
        private AnchorPane parentPane;

        @FXML
        private JFXTextField txtFamilySize;
        protected String familySize;

        @FXML
        private JFXComboBox<Language> cmbLang = new JFXComboBox<Language>();
        protected Language language;

        public void initialize(){
                //set up Language ComboBox
                cmbLang.getItems().clear();
                cmbLang.getItems().addAll(
                        Language.English, Language.Spanish, Language.French, Language.German, Language.Russian, Language.Cantonese, Language.Luxembourgish,
                        Language.Moldovan, Language.Ukranian, Language.ASL, Language.JAVA
                );
        }

        public Language getLanguage(){
            return cmbLang.getSelectionModel().getSelectedItem();
        }

        public String getFamilySize(){
            return txtFamilySize.getText();
        }




}

