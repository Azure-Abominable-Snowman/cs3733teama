package boundaries;

import controllers.SceneEngine;
import entities.HospitalMap;

import entities.Location;
import entities.MapEdge;
import entities.MapNode;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.HashMap;
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
    @FXML
    private TextField startBox;
    @FXML
    private TextField endBox;


    private Stage stage;

    private ArrayList<MapNode> nodes;
    private Map<String, MapNode> nodesOnFloor;

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

    /**
     * Creates new location with the X and Y coordinates suitable for drawing on the map
     * @param loc
     * @return
     */
    private Location convNodeCoords(Location loc) {
        Location newLoc = new Location(loc);
        newLoc.setxCoord(loc.getxCoord()-5);
        newLoc.setyCoord(loc.getyCoord()+75);
        return newLoc;
    }

    private void drawNode(Canvas c, GraphicsContext gc, MapNode n, int size, Paint pointColor) {
        double width = c.getWidth();
        double height = c.getHeight();
        Location drawLoc = convNodeCoords(n.getCoordinate());
        double nodeX = convUnits(drawLoc.getxCoord(), 5000, width);
        double nodeY = convUnits(drawLoc.getyCoord(), 3500, height);
        gc.setFill(pointColor);
        gc.fillOval(nodeX, nodeY, size, size);
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
        nodesOnFloor = new HashMap<>();
        for(MapNode n : nodes) {
            if(n.getCoordinate().getLevel().equals(floor)) {
                // TODO: figure out why these fudge constants are necessary
                drawNode(c, gc, n, nodeDim, Color.BLACK);
                nodesOnFloor.put(n.getId(), n);
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
        renderMap(mapCanvas, mapPane.getWidth() * curZoom, mapPane.getHeight() * curZoom, newFloor);
        floorLabel.setText(newFloor);
    }

    private void drawEdge(Canvas c, MapEdge edge) {
        double width = c.getWidth();
        double height = c.getHeight();
        GraphicsContext gc = c.getGraphicsContext2D();
        Location start = convNodeCoords(edge.getStart().getCoordinate());
        Location end = convNodeCoords(edge.getEnd().getCoordinate());
        gc.strokeLine(
                convUnits(start.getxCoord(), 5000, width),
                convUnits(start.getyCoord(), 3500, height),
                convUnits(end.getxCoord(), 5000, width),
                convUnits(end.getyCoord(), 3500, height));
    }

    @FXML
    private void requestClick(ActionEvent event){
        //SceneEngine.display(RequestScreenController.class, null);
        for(MapNode n : nodesOnFloor.values()) {
            System.out.print(n.getId()+" "+n.getShortDescription()+" ");
            for(MapEdge e : n.getEdges()) {
                System.out.print(e.getId()+" ");
                drawEdge(mapCanvas, e);
            }
            System.out.println("");
        }
    }

    @FXML
    private void editMapClick(ActionEvent event){
        SceneEngine.display(MapEditorController.class, null);
    }

    @FXML
    private void goClick(ActionEvent event){
        //SceneEngine.display(DirectionsController.class, null);

        // draw the start and end nodes in a different size and color
        MapNode start = nodesOnFloor.get(startBox.getText());
        MapNode end = nodesOnFloor.get(endBox.getText());

        if(start == null || end == null) {
            System.out.println("Invalid ID for start or end");
            return;
        }

        drawNode(mapCanvas, mapCanvas.getGraphicsContext2D(), start, nodeDim*3, Color.RED);
        drawNode(mapCanvas, mapCanvas.getGraphicsContext2D(), end, nodeDim*3, Color.RED);
    }

    @FXML
    private void logInClick(ActionEvent event){
        SceneEngine.display(StaffLoginController.class, SceneEngine.getLoginScene(), null);
    }
}
