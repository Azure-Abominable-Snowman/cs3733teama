package boundaries;

import controllers.SceneEngine;
import entities.HospitalMap;
import entities.MapNode;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.util.ArrayList;


public class MainScreenController implements Controller {

    @Override
    public String getFXMLFileName() {
        return "MainScreen.fxml";
    }

    @FXML
    private Button go;
    @FXML
    private Button request;
    @FXML
    private Button editMap;
    @FXML
    private Button LogIn;
    @FXML
    private ImageView map;
    @FXML
    private Canvas mapCanvas;
    @FXML
    private Pane mapPane;

    private Stage stage;

    private ArrayList<MapNode> nodes;

    public void initialize() {
        mapCanvas.toFront(); // render node dots and stuff in front of the image always

        // load in map node coordinates from DB
        HospitalMap map = HospitalMap.getInstance("csvdata/MapAedges.csv", "csvdata/MapAnodes.csv");
        ArrayList<String> nodeIds = map.getMap().getNodeIds();
        nodes = new ArrayList<>();
        for(String id : nodeIds) {
            nodes.add(map.getMap().getNode(id));
        }
    }

    private Image bwImg = new Image("maps/00_thegroundfloor.png");

    /**
     * Converts input coordinate system to output coordinate system
     */
    public double convUnits(double x, double origMaxX, double curMaxX) {
        return (x*(curMaxX/origMaxX));
    }

    private int nodeDim = 3;

    public void renderMap(Canvas c, double width, double height) {
        GraphicsContext gc = c.getGraphicsContext2D();
        gc.clearRect(0, 0, width, height);
        gc.drawImage(bwImg, 0,0, width, height);
        for(MapNode n : nodes) {
            if(n.getCoordinate().getLevel().equals("G")) {
                double nodeX = convUnits(n.getCoordinate().getxCoord(), 5000, width);
                double nodeY = convUnits(n.getCoordinate().getyCoord()+80, 3500, height);
                System.out.println(nodeX + " " + nodeY);
                gc.fillOval(nodeX, nodeY, nodeDim, nodeDim);
            }
        }
    }

    @Override
    public void setStage(Stage stage) {
        this.stage = stage;
        // On width resize of the stage
        ChangeListener<Number> stageSizeListener = (observable, oldValue, newValue) -> {
            double width = mapPane.getWidth();
            double height = mapPane.getHeight();
            System.out.println(mapPane.getWidth() + " " + mapPane.getHeight());
            //map.setFitWidth(width);
            //map.setFitHeight(height);
            mapCanvas.setWidth(width);
            mapCanvas.setHeight(height);
            renderMap(mapCanvas, width, height);
        };
        stage.widthProperty().addListener(stageSizeListener);
        stage.heightProperty().addListener(stageSizeListener);
    }

    @FXML
    private void requestClick(ActionEvent event){
        SceneEngine.display(RequestScreenController.class, null);
    }

    @FXML
    private void editMapClick(ActionEvent event){
        SceneEngine.display(MapEditorController.class, null);
    }

    @FXML
    private void goClick(ActionEvent event){
        SceneEngine.display(DirectionsController.class, null);
    }

    @FXML
    private void logInClick(ActionEvent event){
        SceneEngine.display(StaffLoginController.class, SceneEngine.getLoginScene(), null);
    }
}
