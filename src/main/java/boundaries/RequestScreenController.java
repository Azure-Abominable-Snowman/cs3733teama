package boundaries;

import controllers.SceneEngine;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;

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
    private ComboBox combo;
    @FXML
    private void Initialization(){
        combo.setItems(list);
    }
    ObservableList<String> list = FXCollections.observableArrayList("Food","Security","Interpreter","Transportation");



    @FXML
    private Button submit;









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





