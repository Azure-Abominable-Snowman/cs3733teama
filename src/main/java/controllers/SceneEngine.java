package controllers;

import boundaries.Controller;
import boundaries.ControllerInfo;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public final class SceneEngine {

    static private Stage primaryStage, popOutStage, loginStage;

    public static void setStages(Stage primaryStage) {
        SceneEngine.primaryStage = primaryStage;
        popOutStage = new Stage();
        loginStage = new Stage();
    }

    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    public static Stage getPopOutStage() {
        return popOutStage;
    }

    public static Stage getLoginScene() {
        return loginStage;
    }

    public static void closePopOut(){
        popOutStage.close();
    }

    public static void closeLogin() {
        loginStage.close();
    }


    public static void display(Class<? extends Controller> newController, Stage stage, ControllerInfo info) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(newController.getResource("../"+newController.newInstance().getFXMLFileName()));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            // Get newly created controller
            Controller c = (Controller) loader.getController();
            if(info != null) {
                // Pass it data
                c.setControllerInfo(info);
            }
            c.setStage(stage);
            stage.setScene(scene);
            stage.show();

        }catch(IOException e){
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
    }

    public static void display(Class<? extends Controller> newController, ControllerInfo info) {
        display(newController, getPrimaryStage(), info);
    }

}
