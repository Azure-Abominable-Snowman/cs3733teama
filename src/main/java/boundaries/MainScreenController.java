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
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Map;


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
    private Canvas mapCanvas;
    @FXML
    private ScrollPane mapPane;
    @FXML
    private Slider floorSlider;
    @FXML
    private Label floorLabel;

    private Stage stage;

    private ArrayList<MapNode> nodes;

    private Map<String, Image> bwImgs = SceneEngine.getHospitalImageMap();
    private Image bwImg;

    public void initialize() {
        bwImg = bwImgs.get("G");
        // load in map node coordinates from DB
        HospitalMap map = HospitalMap.getInstance("csvdata/MapAedges.csv", "csvdata/MapAnodes.csv");
        ArrayList<String> nodeIds = map.getMap().getNodeIds();
        nodes = new ArrayList<>();
        for(String id : nodeIds) {
            nodes.add(map.getMap().getNode(id));
        }

        // Make slider change the floor
        floorSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
                switch(newVal.intValue()) { // TODO: make an enum to get rid of this switch
                    case 0:
                        switchFloor("L2");
                        break;
                    case 1:
                        switchFloor("L1");
                        break;
                    case 2:
                        switchFloor("G");
                        break;
                    case 3:
                        switchFloor("1");
                        break;
                    case 4:
                        switchFloor("2");
                        break;
                    case 5:
                        switchFloor("3");
                        break;
                }
            });
    }

    /**
     * Converts input coordinate system to output coordinate system
     */
    public double convUnits(double x, double origMaxX, double curMaxX) {
        return (x*(curMaxX/origMaxX));
    }

    private int nodeDim = 3;

    public void renderMap(Canvas c, double width, double height) {
        renderMap(c, width, height, curFloor);
    }

    private String curFloor = "G";
    public void renderMap(Canvas c, double width, double height, String floor) {
        if(!floor.equals(curFloor)) {
            curFloor = floor;
            bwImg = bwImgs.get(floor);
        }
        mapCanvas.setWidth(width);
        mapCanvas.setHeight(height);
        GraphicsContext gc = c.getGraphicsContext2D();
        gc.clearRect(0, 0, width, height);
        gc.drawImage(bwImg, 0,0, width, height);
        for(MapNode n : nodes) {
            if(n.getCoordinate().getLevel().equals(floor)) {
                // TODO: figure out why these fudge constants are necessary
                double nodeX = convUnits(n.getCoordinate().getxCoord()-5, 5000, width);
                double nodeY = convUnits(n.getCoordinate().getyCoord()+75, 3500, height);
                //System.out.println(nodeX + " " + nodeY);
                gc.fillOval(nodeX, nodeY, nodeDim, nodeDim);
            }
        }
    }

    private double zoomUnit = 3;
    private boolean zoomed = false;
    private double curZoom = 1;

    @FXML
    public void zoomIntoMap(MouseEvent e) {
        if(!zoomed) {
            System.out.println(e.getX() + " " + e.getY());
            //System.out.println("O: "+mapPane.getHvalue()+" "+mapPane.getVvalue());
            renderMap(mapCanvas, mapPane.getWidth() * zoomUnit, mapPane.getHeight() * zoomUnit);
            //System.out.println("A: "+mapPane.getHvalue()+" "+mapPane.getVvalue());
            mapPane.setHvalue(e.getX()); // set so it's in the center of the screen
            mapPane.setVvalue(e.getY());
            //System.out.println("A2: "+mapPane.getHvalue()+" "+mapPane.getVvalue());
            zoomed = true;
            curZoom = 3;
        } else {
            renderMap(mapCanvas, mapPane.getWidth(), mapPane.getHeight());
            zoomed = false;
            curZoom = 1;
        }
    }

    @Override
    public void setStage(Stage stage) {
        this.stage = stage;
        // On width resize of the stage
        ChangeListener<Number> stageSizeListener = (observable, oldValue, newValue) -> {
            // set min and max for the scrollview as well as render the map
            double width = mapPane.getWidth();
            double height = mapPane.getHeight();
            mapPane.setVmax(height);
            mapPane.setHmax(width);
            renderMap(mapCanvas, width, height);
        };
        stage.widthProperty().addListener(stageSizeListener);
        stage.heightProperty().addListener(stageSizeListener);
    }

    private void switchFloor(String newFloor) {
        renderMap(mapCanvas, mapPane.getWidth(), mapPane.getHeight(), newFloor);
        floorLabel.setText(newFloor);
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
