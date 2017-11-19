package com.teama.controllers;

import javafx.scene.Scene;
import javafx.stage.Stage;

public interface Controller {
    default String getFXMLFileName() { return ""; }

    default void setControllerInfo(ControllerInfo info) { }

    default void setStage(Stage stage) { }

    default void setScene(Scene scene) { }
}
