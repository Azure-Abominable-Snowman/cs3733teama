package com.teama.controllers_refactor2;

import com.teama.controllers_refactor.PopOutController;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;

//TODO update this to have all of the stuff for the staff panes
public class hamburgerDrawerController extends HamburgerController{
    @FXML
    ImageView imgBack;
    @FXML
    Pane parentPane;
    public void initialize(){
        System.out.println(this);
        System.out.println("In the controller "+parentPane);
    }
    @FXML
    public void onBackClick(MouseEvent e){
        closing.set(true);
    }
    public Pane getParentPane(){
        return parentPane;
    }
    public String getFXMLPath(){return "/MainScreenDrawers/REPLACEME_Hamburger.fxml";}
    public void onClose(){}
    public void onOpen(){closing.set(false);}
}
