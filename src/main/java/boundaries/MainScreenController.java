package boundaries;

import controllers.SceneEngine;
import entities.HospitalMap;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;


public class MainScreenController implements Controller {

    @Override
    public String getFXMLFileName() {
        return "MainScreen.fxml";
    }

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
        SceneEngine.display(RequestScreenController.class);
    }
    @FXML
    private void editMapClick(ActionEvent event){
        SceneEngine.display(MapEditorController.class);
    }
    @FXML
    private void goClick(ActionEvent event){
        SceneEngine.display(DirectionsController.class);
    }
    @FXML
    private void logInClick(ActionEvent event){
        SceneEngine.display(StaffLoginController.class, SceneEngine.getLoginScene());
    }
}
