package controllers;

import boundaries.Controller;
import boundaries.ControllerInfo;
import boundaries.MainScreenController;
import entities.drawing.ImageStash;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public final class SceneEngine {

    private static Map<String, ImageStash>  bwImgs;

    static private Stage primaryStage, popOutStage, loginStage,comfirmStage;

    private static boolean adminStatus = false;

    public static boolean isAdminStatus() {
        return adminStatus;
    }

    public static void setAdminStatus(boolean adminStatus) {
        SceneEngine.adminStatus = adminStatus;
    }

    public static void setStages(Stage primaryStage) {
        SceneEngine.primaryStage = primaryStage;
        popOutStage = new Stage();
        loginStage = new Stage();
        comfirmStage = new Stage();
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

    public static void closeComfirm() {
        comfirmStage.close();
    }

    public static Map<String, ImageStash> getHospitalImageMap() {
        if(bwImgs == null) {
            bwImgs = new HashMap<>();
            // Populate image map
            bwImgs.put("L2", new ImageStash("maps/L2.png"));
            bwImgs.put("L1", new ImageStash("maps/L1.png"));
            bwImgs.put("G", new ImageStash("maps/G.png"));
            bwImgs.put("1", new ImageStash("maps/1.png"));
            bwImgs.put("2", new ImageStash("maps/2.png"));
            bwImgs.put("3", new ImageStash("maps/3.png"));
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
            c.setScene(scene);
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