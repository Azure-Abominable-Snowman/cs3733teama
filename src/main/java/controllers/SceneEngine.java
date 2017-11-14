package controllers;

import boundaries.Controller;
import boundaries.ControllerInfo;
import entities.drawing.ImageStash;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public final class SceneEngine {

    private static Map<String, String> bwImgs;

    static private Stage primaryStage, popOutStage, loginStage;

    private String nodefile ="/resources/csvdata/MapANodes.csv";
    private String edgefile = "/resources/csvdata/MapAedges.csv";

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

    public static Map<String,  String> getHospitalImageMap() {
        if(bwImgs == null) {
            bwImgs = new HashMap<>();
            // Populate image map


            bwImgs.put("L2","maps/L2.png");
            bwImgs.put("L1","maps/L1.png");
            bwImgs.put("G", "maps/G.png");
            bwImgs.put("1", "maps/1.png");
            bwImgs.put("2", "maps/2.png");
            bwImgs.put("3", "maps/3.png");
        }

        return bwImgs;
    } // TODO: make floors an enum instead of a string


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
