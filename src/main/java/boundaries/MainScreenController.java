package boundaries;

import controllers.SceneEngine;
import entities.HospitalMap;
import entities.MapEdge;
import entities.MapNode;
import entities.NodeType;
import entities.PathRelated.Path;
import entities.drawing.DrawMap;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.util.Map;


public class MainScreenController implements Controller {

    @Override
    public String getFXMLFileName() {
        return "MainScreen.fxml";
    }

    @FXML
    private Button go, login;
    @FXML
    private Button request;
    @FXML
    private Button editMap;
    @FXML
    private Button loginBtn;
    @FXML
    private Button logoutBtn;
    @FXML
    private Canvas mapCanvas;

    @FXML
    private Label alreadyLoginMsg;

    @FXML
    private ScrollPane mapPane;
    @FXML
    private Slider floorSlider;
    @FXML
    private Label floorLabel;
    @FXML
    private ComboBox<MapNode> startBox;
    @FXML
    private ComboBox<MapNode> endBox;
    @FXML
    private SplitPane directionsPane;

    private GridPane directions;

    private boolean ctrlDown = false;

    //private ArrayList<MapNode> nodes;
    //private Map<String, MapNode> nodesOnFloor;

    private DrawMap dMap;
    private HospitalMap map;

    private Stage stage;

    public void initialize() {
        hideDirections();

        dMap = new DrawMap(mapPane, mapCanvas, -2, 75, 5000, 3500);
        // load in map node coordinates from DB
        map = HospitalMap.getInstance();
        populateBoxes("G");

        // Make slider change the floor
        floorSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            String floor = "G";
            switch (newVal.intValue()) { // TODO: make an enum to get rid of this switch
                case 0:
                    floor = "L2";
                    break;
                case 1:
                    floor = "L1";
                    break;
                case 2:
                    floor = "G";
                    break;
                case 3:
                    floor = "1";
                    break;
                case 4:
                    floor = "2";
                    break;
                case 5:
                    floor = "3";
                    break;
            }
            dMap.switchFloor(floor);
            floorLabel.setText(floor);
            populateBoxes(floor);


        });
        if (SceneEngine.isAdminStatus() == false){
            hideBtn();
        }else{
            showBtn();

        }



    }

    private void populateBoxes(String floor) {
        // Populate combo boxes
        startBox.getItems().clear();
        endBox.getItems().clear();
        for (MapNode n : map.getFloorNodes(floor).values()) {
            if (!n.getNodeType().equals(NodeType.HALL)) {
                startBox.getItems().add(n);
                endBox.getItems().add(n);
            }
        }
    }

    private void hideDirections() {
        if (directionsPane.getItems().size() < 2) { // if there are too few, don't add
            return;
        }

        directionsPane.getItems().remove(1);
    }

    private void showDirections() {
        if (directionsPane.getItems().size() > 1) { // if there are directions already, don't add them
            return;
        }

        ScrollPane directionScroll = new ScrollPane();
        directions = new GridPane();
        directionScroll.setContent(directions);
        directionsPane.getItems().add(directionScroll);
        directionsPane.setDividerPosition(0, 0.75);
        // Lock it at 25%
        directionScroll.maxWidthProperty().bind(directionsPane.widthProperty().multiply(0.25));
        directionScroll.minWidthProperty().bind(directionsPane.widthProperty().multiply(0.25));

        directions.setHgap(10);
        directions.setVgap(10);

        // make the window 25% larger in the x direction to compensate for the window cutting in
        stage.setWidth(stage.getWidth() * 1.25);
    }

    private void addDirection(String text) {
        directions.add(new Label(text), 0, 0);
        directions.add(new Label(text), 0, 1);
        directions.add(new Label(text), 0, 2);
    }

    @FXML
    public void zoomIntoMap(MouseEvent e) {
        if (ctrlDown) {
            dMap.toggleZoom(e.getX(), e.getY());
        }
    }

    /**
     * Called when the stage is given to the controller, does initialization routines
     *
     * @param stage
     */
    @Override
    public void setStage(Stage stage) {
        // On resize of the stage
        ChangeListener<Number> stageSizeListener = (observable, oldValue, newValue) -> {
            dMap.updateSize();
        };
        // If the stage is resized make the canvas fill
        stage.widthProperty().addListener(stageSizeListener);
        stage.heightProperty().addListener(stageSizeListener);
        // If the pane is resized make the canvas fill
        mapPane.heightProperty().addListener(stageSizeListener);
        mapPane.widthProperty().addListener(stageSizeListener);

        this.stage = stage;

    }

    public void setScene(Scene scene) {
        scene.setOnKeyPressed(ke -> {
            if (ke.isControlDown()) { // When control down, flip a variable, otherwise flip it back
                ctrlDown = true;
            }
        });

        scene.setOnKeyReleased(ke -> {
            if (!ke.isControlDown()) {
                ctrlDown = false; // when it is released, set the variable to false
            }
        });
    }

    @FXML
    private void requestClick(ActionEvent event) {
        SceneEngine.display(RequestScreenController.class, null);

        // DEBUG: draw all the edges on the map and then print out info to the console
        for (MapNode n : map.getFloorNodes(dMap.getCurFloor()).values()) {
            System.out.print(n.getId() + " " + n.getShortDescription() + " ");
            for (MapEdge e : n.getEdges()) {
                System.out.print(e.getId() + " ");
                dMap.drawEdge(mapCanvas, e);
            }
            System.out.println("");
        }
    }

    @FXML
    private void editMapClick(ActionEvent event) {
        SceneEngine.display(MapEditorController.class, null);
        //hideDirections();
    }

    @FXML
    private void goClick(ActionEvent event) {
        dMap.reRender();

        showDirections();

        addDirection("DIRECTION!"); // Dummy add direction method (just adds sample text)

        // draw the start and end nodes in a different size and color
        Map<String, MapNode> curFloorMap = map.getFloorNodes(dMap.getCurFloor());
        MapNode start = curFloorMap.get(startBox.getValue().getId());
        MapNode end = curFloorMap.get(endBox.getValue().getId());

        if (start == null || end == null) {
            System.out.println("Invalid ID for start or end");
            return;
        }


        Path shortestPath = map.getPathGenerator().generatePath(start, end);

        // Draw the path between the two nodes
        dMap.setPath(shortestPath);
    }

    @FXML
    private void onLogoutClick(ActionEvent event){
        SceneEngine.setAdminStatus(false);
        hideBtn();

    }

    @FXML
    private void logInClick(ActionEvent event) {
        if(SceneEngine.isAdminStatus() == true){
            alreadyLoginMsg.setText("Already login");

        }else{
            SceneEngine.display(StaffLoginController.class, SceneEngine.getLoginScene(), null);
        }

    }


    private void showBtn(){
            request.setVisible(true);
            editMap.setVisible(true);
            logoutBtn.setVisible(true);
            loginBtn.setVisible(false);
    }
    private void hideBtn(){
            request.setVisible(false);
            editMap.setVisible(false);
            logoutBtn.setVisible(false);
            loginBtn.setVisible(true);

    }




}
