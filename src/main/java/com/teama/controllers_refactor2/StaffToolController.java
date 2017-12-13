package com.teama.controllers_refactor2;

import javafx.scene.layout.Pane;

public abstract class StaffToolController {
    public abstract Pane getParentPane();
    public abstract String getFXMLPath();
    public void onClose(){}
}
