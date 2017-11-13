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
        SceneEngine.display(MainScreenController.class);
    }

    @FXML
    Button request;

    @FXML
    Button editMap;

    @FXML
    private void requestClick(ActionEvent event){
        SceneEngine.display(RequestScreenController.class);
    }

    @FXML
    private void editMapClick(ActionEvent event){
        SceneEngine.display(MapEditorController.class);
    }

}
