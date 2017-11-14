package boundaries;

import controllers.SceneEngine;
import entities.HospitalMap;
import entities.Location;
import entities.MapEdge;
import entities.MapNode;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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
import javafx.scene.canvas.*;
import java.util.ArrayList;
import java.util.HashMap;


public class MapEditorController implements Controller {
    @FXML
    private Canvas canvas;

    @FXML
    private ToggleButton nodeToggle, edgeToggle;

    @FXML
    private TextField name, xCoord, yCoord;

    @FXML
    private Button back,add, edit, remove;
    @FXML
    private Spinner floor;
    private ToggleGroup group = new ToggleGroup();

    public void initialize() {

        nodeToggle.setToggleGroup(group);
        edgeToggle.setToggleGroup(group);
        group.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
            public void changed(ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) {
                if (newValue != null) {
                    System.out.println(group.getSelectedToggle().getUserData());
                    if (nodeToggle.isSelected()) {
                        edgeToggle.setSelected(false);
                    } else if (edgeToggle.isSelected()) { //selected Edge editor
                        nodeToggle.setSelected(false);
                    }
                }
            }
        });

    }
    @Override
    public String getFXMLFileName() {
        return "MapEditorV2-test.fxml";
    }



    @FXML

    private void onBackClick(ActionEvent e){
        SceneEngine.display(MainScreenController.class, null);

    }
    @FXML
    //When an editor is selected, display all the nodes and the edges to the screen
    private void onEditorSelect(ActionEvent e) {


    /*
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


