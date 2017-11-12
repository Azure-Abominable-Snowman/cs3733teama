package boundaries;

import controllers.SceneEngine;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

/**
 * Created by aliss on 11/12/2017.
 */
public class MapEditorController {

        private Button back;

        @FXML
        private void onBackClick(ActionEvent e){
            SceneEngine.displayMainScreen();
        }
    }


