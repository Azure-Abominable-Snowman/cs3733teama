package boundaries;

import controllers.SceneEngine;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class RequestScreenController implements Controller {

    @Override
    public String getFXMLFileName() {
        return "RequestScreen.fxml";
    }

    @FXML
    private Button back;
    @FXML
    Button fulfillRequest;

    @FXML
    private void backClick(ActionEvent event){
        SceneEngine.display(MainScreenController.class, null);
    }
    @FXML
    //this handler is only connected to the first fulfill button
    //TODO create the prefabs for the fulfill sections
    private void fulfillClick(ActionEvent event){
        SceneEngine.display(FulfillReqController.class, SceneEngine.getPopOutStage(), null);
    }
}
