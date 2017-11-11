package boundaries;

import controllers.SceneEngine;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class RequestScreenController {
    @FXML
    private Button back;
    @FXML
    Button fulfillRequest;

    @FXML
    private void backClick(ActionEvent event){
        SceneEngine.displayMainScreen();
    }
    @FXML
    //this handler is only connected to the first fulfill button
    //TODO create the prefabs for the fulfill sections
    private void fulfillClick(ActionEvent event){
        SceneEngine.displayFulfillRequest();
    }
}
