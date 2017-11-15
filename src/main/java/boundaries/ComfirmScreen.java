package boundaries;
import controllers.SceneEngine;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

import java.awt.event.ActionEvent;


public class ComfirmScreen implements Controller{

    @FXML
    private Button comfirm;


    @FXML
    private Button cancel;

    @FXML
    private void onBackClick(ActionEvent event){
        SceneEngine.closeComfirm();
    }



}