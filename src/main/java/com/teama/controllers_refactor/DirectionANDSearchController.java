package com.teama.controllers_refactor;

import com.jfoenix.controls.JFXTextField;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.effect.BlendMode;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

public class DirectionANDSearchController {

    Boolean accessibilityMode = false;



    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private JFXTextField yourLocationBar;

    @FXML
    private ImageView accessibilityBtn;

    @FXML
    private JFXTextField destinationBar;

    @FXML
    private ImageView switchBtn;

    @FXML
    private ImageView closeBtn;


    @FXML
    void onAccessibilityBtnClicked(MouseEvent event) {
        System.out.println(accessibilityMode);
        if(!accessibilityMode) {
            accessibilityBtn.setBlendMode(BlendMode.SRC_ATOP);
            accessibilityMode = true;
        }else if(accessibilityMode) {
            accessibilityBtn.setBlendMode(BlendMode.SOFT_LIGHT);
            accessibilityMode = false;
        }
    }

    @FXML
    void onDestinationBarClicked(ActionEvent event) {

    }



    // Dealing with the closeBtn
    @FXML
    void onClosedBtnClicked(MouseEvent event) {
    }

    @FXML
    void onClosedBtnHovered(MouseEvent event) {
        closeBtn.setBlendMode(BlendMode.SRC_ATOP);
    }

    @FXML
    void onClosedBtnExited(MouseEvent event) {
        closeBtn.setBlendMode(BlendMode.SOFT_LIGHT);
    }

    //Dealing with the SwitchBtn
    @FXML
    void onSwitchBtnHovered(MouseEvent event) {
        switchBtn.setBlendMode(BlendMode.SRC_ATOP);
    }

    @FXML
    void onSwitchBtnExited(MouseEvent event) {
        switchBtn.setBlendMode(BlendMode.SOFT_LIGHT);
    }

    @FXML
    void onSwitchBtnClicked(MouseEvent event) {
        String yourLocation = yourLocationBar.getText();
        String destination = destinationBar.getText();
        yourLocationBar.setText(destination);
        destinationBar.setText(yourLocation);
    }


    @FXML
    void onYourLocationBarClicked(ActionEvent event) {

    }





}
