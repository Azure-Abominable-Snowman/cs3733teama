package entities;

import boundaries.Controller;
import controllers.SceneEngine;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application{

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("../MainScreen.fxml"));
            Parent root = loader.load();
            Controller c = (Controller) loader.getController();
            c.setStage(primaryStage);
            primaryStage.setTitle("Brigham and Women's Hospital Kiosk");
            primaryStage.setScene(new Scene(root));
            primaryStage.show();
            SceneEngine.setStages(primaryStage);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
