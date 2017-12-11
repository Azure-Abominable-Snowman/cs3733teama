package com.teama.controllers_refactor2;

import javafx.fxml.FXML;
import javafx.scene.layout.Pane;

public class MapEditorController extends StaffToolController {
    @FXML Pane parentPane;
    public Pane getParentPane(){
        return parentPane;
    }
    public String getFXMLPath(){
        return "/MainScreenDrawers/MapEditor_refactor.fxml";
    }
}
