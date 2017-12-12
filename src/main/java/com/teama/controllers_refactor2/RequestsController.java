package com.teama.controllers_refactor2;

import javafx.fxml.FXML;
import javafx.scene.layout.Pane;

public class RequestsController extends StaffToolController {
    @FXML
    Pane parentPane;

    @Override
    public Pane getParentPane() {
        return parentPane;
    }

    @Override
    public String getFXMLPath() {
        return "/MainScreenDrawers/RequestPane_refactor.fxml";
    }
}
