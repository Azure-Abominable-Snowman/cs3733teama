package boundaries;

import controllers.SceneEngine;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class MapEditorController {
    @FXML
    private Button back;

    @FXML
    private void onBackClick(ActionEvent e){
        SceneEngine.displayMainScreen();
    }
}
