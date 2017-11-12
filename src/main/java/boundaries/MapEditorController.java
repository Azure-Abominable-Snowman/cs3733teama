package boundaries;

import controllers.SceneEngine;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class MapEditorController implements Controller {

    @Override
    public String getFXMLFileName() {
        return "MapEditor.fxml";
    }

    @FXML
    private Button back;

    @FXML
    private void onBackClick(ActionEvent e) {
        SceneEngine.display(MainScreenController.class);

    }
}


