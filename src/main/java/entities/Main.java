package entities;

import boundaries.Controller;
import boundaries.MainScreenController;
import controllers.SceneEngine;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application{

    private boolean userType = false;

    public Boolean getUserType() {
        return userType;
    }

    public void setUserType(Boolean userType) {
        this.userType = userType;
    }

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
            Scene scene = new Scene(root);
            c.setScene(scene);
            primaryStage.setTitle("Brigham and Women's Hospital Kiosk");
            primaryStage.setScene(scene);
            primaryStage.show();
            SceneEngine.setStages(primaryStage);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
