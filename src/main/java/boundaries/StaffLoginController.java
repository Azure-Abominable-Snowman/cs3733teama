package boundaries;

import controllers.SceneEngine;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;

public class StaffLoginController {

    @FXML
    private AnchorPane loginPlane;

    @FXML
    private GridPane loginGrid;

    @FXML
    private Text AdminID;

    @FXML
    private Text passWord;

    @FXML
    private TextField IDfield;

    @FXML
    private TextField passwordField;

    @FXML
    private Button login;

    @FXML
    private Button reset;

    @FXML
    private Text admintext;

    @FXML
    private void onBackClick(ActionEvent event){
        SceneEngine.displayMainScreen();
        SceneEngine.closeLogin();
    }
}
