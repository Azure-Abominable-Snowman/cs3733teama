package controllers;

import boundaries.MainScreenController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public final class SceneEngine{

    static private Stage primaryStage;

    public static void setPrimaryStage(Stage primaryStage) {
        SceneEngine.primaryStage = primaryStage;
    }

    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    private void initRootLayout(){
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainScreenController.class.getResource("MainScreenController.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            primaryStage.show();

        }catch(IOException e){
            System.out.println("couldn't load screen");
        }
    }

}
