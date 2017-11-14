package boundaries;

import controllers.SceneEngine;
import entities.HospitalMap;
import entities.Location;
import entities.MapEdge;
import entities.MapNode;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;


import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;


public class MapEditorController implements Controller {
    @FXML
    private Canvas canvas;
    @FXML
    private ImageView map;
    @FXML
    private ToggleButton nodeToggle, edgeToggle;
    @FXML
    private TextField name, xCoord, yCoord;

    @FXML
    private Button back,add, edit, remove;

    public void initialize() {
        /*
        ObservableList<String> options =
                FXCollections.observableArrayList("Node", "Edge");
        editorType.setItems(options);
        canvas = new Canvas();
        map = new ImageView();
        */
    }
    @Override
    public String getFXMLFileName() {
        return "MapEditorV2Test.fxml";
    }



    @FXML

    private void onBackClick(ActionEvent e){
        SceneEngine.display(MainScreenController.class, null);

    }
    @FXML
    //When an editor is selected, display all the nodes and the edges to the screen
    private void onEditorSelect(ActionEvent e) {
        // get floor (String) from user
        String selectedFloor = null;
        //display all Nodes and Edges for given floor
        //Nodes to Display:
        int numNodes, numEdges;
        //HashMap<Location,String> nodes = HospitalMap.getInstance("csvdata/MapAedges.csv", "csvdata/MapAnodes.csv").getNodesonFloor(selectedFloor);
        //HashMap<Location,Location> edges = HospitalMap.getInstance("csvdata/MapAedges.csv", "csvdata/MapAnodes.csv").getEdgesonFloor(selectedFloor);
        //numNodes = nodes.keySet().size();
        //numEdges = nodes.keySet().size();
/*
        for (int i = 0; i<numNodes; i++) {
            Circle node = new Circle(); //draw each node on the screen
            node.setCenterX(100);
            node.setRadius(3);
            node.setCenterY(30);
            //canvas.getg
        }
        for (int i = 0; i<numEdges; i++) {
            Line line = new Line();

        }
        */
    }

    @FXML
    private void onNodeEditor(ActionEvent e) {

    }

    @FXML
    private void onEdgeEditor(ActionEvent e) {

    }



}


