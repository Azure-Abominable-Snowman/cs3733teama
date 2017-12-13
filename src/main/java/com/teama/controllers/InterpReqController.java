package com.teama.controllers;

/**
 * Created by jakepardue on 11/28/17.
*/

import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import com.teama.requestsubsystem.interpreterfeature.Language;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;

public class InterpReqController {

        @FXML
        private AnchorPane parentPane;

        @FXML
        private JFXComboBox<Language> cmbLang;
        protected Language language;

        public void initialize(){
                //set up Language ComboBox
                cmbLang.getItems().clear();
                cmbLang.getItems().addAll(Language.values());
        }

        public Language getLanguage(){
                System.out.println(cmbLang);
            return cmbLang.getSelectionModel().getSelectedItem();
        }


        public void clearRequest(){
                cmbLang.getSelectionModel().clearSelection();

        }




}

