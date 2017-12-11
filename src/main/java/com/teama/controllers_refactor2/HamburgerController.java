package com.teama.controllers_refactor2;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.layout.Pane;

public abstract class HamburgerController {
    //set this to true when the tab needs to close so the mainScreenController knows
    protected BooleanProperty closing = new SimpleBooleanProperty(false);
    public BooleanProperty getClosing(){return closing;}
    public abstract void onOpen();
    public abstract void onClose();
    //this should return the pane that everything is contained inside of
    public abstract Pane getParentPane();
    public abstract String getFXMLPath();
}
