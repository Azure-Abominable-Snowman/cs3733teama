package boundaries;

import controllers.SceneEngine;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class DirectionsController implements Controller {
    @Override
    public String getFXMLFileName() {
        return "DirectionsScreen.fxml";
    }

    @FXML
    Button back;

    @FXML
    private void backClick(ActionEvent e){
        SceneEngine.display(MainScreenController.class, null);
    }
}
