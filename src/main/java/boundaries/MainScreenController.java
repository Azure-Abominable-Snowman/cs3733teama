package boundaries;

import controllers.SceneEngine;
import entities.HospitalMap;

import entities.Location;
import entities.MapEdge;
import entities.MapNode;
import entities.PathRelated.Path;
import entities.drawing.DrawMap;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

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

    //private ArrayList<MapNode> nodes;
    //private Map<String, MapNode> nodesOnFloor;

    private DrawMap dMap;
    private HospitalMap map;

    public void initialize() {
        dMap = new DrawMap(mapPane, mapCanvas, -5, 75, 5000, 3500);
        // load in map node coordinates from DB
        map = HospitalMap.getInstance();

        // Make slider change the floor
        floorSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            String floor = "G";
            switch(newVal.intValue()) { // TODO: make an enum to get rid of this switch
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
            });
    }

    @FXML
    public void zoomIntoMap(MouseEvent e) {
        dMap.toggleZoom(e.getX(), e.getY());
    }

    @Override
    public void setStage(Stage stage) {
        // On resize of the stage
        ChangeListener<Number> stageSizeListener = (observable, oldValue, newValue) -> {
            dMap.updateSize();
        };
        stage.widthProperty().addListener(stageSizeListener);
        stage.heightProperty().addListener(stageSizeListener);
    }

    @FXML
    private void requestClick(ActionEvent event) {
        //SceneEngine.display(RequestScreenController.class, null);

        // DEBUG: draw all the edges on the map and then print out info to the console
        for(MapNode n : map.getFloorNodes(dMap.getCurFloor()).values()) {
            System.out.print(n.getId()+" "+n.getShortDescription()+" ");
            for(MapEdge e : n.getEdges()) {
                System.out.print(e.getId()+" ");
                dMap.drawEdge(mapCanvas, e);
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
        Map<String, MapNode> curFloorMap = map.getFloorNodes(dMap.getCurFloor());
        MapNode start = curFloorMap.get(startBox.getText());
        MapNode end = curFloorMap.get(endBox.getText());

        if(start == null || end == null) {
            System.out.println("Invalid ID for start or end");
            return;
        }


        int nodeDim = dMap.getNodeDim();
        dMap.drawNode(start, nodeDim*3, Color.RED);
        dMap.drawNode(end, nodeDim*3, Color.RED);


        Path shortestPath = map.getPathGenerator().generatePath(start, end);

        for(MapNode n : shortestPath.getNodes()) {
            System.out.println(n.getId());
        }

        // Draw the path between the two nodes
        dMap.drawPath(shortestPath);
    }

    @FXML
    private void logInClick(ActionEvent event){
        SceneEngine.display(StaffLoginController.class, SceneEngine.getLoginScene(), null);
    }
}
