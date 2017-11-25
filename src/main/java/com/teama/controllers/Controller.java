package com.teama.controllers;

import javafx.scene.Scene;
import javafx.stage.Stage;

public interface Controller {
    String getFXMLFileName();

    default void setStage(Stage stage) { }

    default void setScene(Scene scene) { }
}
