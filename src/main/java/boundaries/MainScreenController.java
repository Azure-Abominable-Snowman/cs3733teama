package boundaries;

import controllers.SceneEngine;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;



public class MainScreenController {
    @FXML
    Button go;
    @FXML
    Button request;
    @FXML
    Button editMap;
    @FXML
    Button LogIn;

    @FXML
    private void requestClick(ActionEvent event){
        SceneEngine.displayRequestScreen();
    }
    @FXML
    private void editMapClick(ActionEvent event){
        SceneEngine.displayMapEditor();
    }
    @FXML
    private void goClick(ActionEvent event){
        SceneEngine.displayDirectionsScreen();
    }
    @FXML
    private void logInClick(ActionEvent event){
        SceneEngine.displayLoginScreen();
    }
}
