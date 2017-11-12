package boundaries;

import controllers.SceneEngine;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class DirectionsController {
    @FXML
    Button back;

    @FXML
    private void backClick(ActionEvent e){
        SceneEngine.displayMainScreen();
    }
}
