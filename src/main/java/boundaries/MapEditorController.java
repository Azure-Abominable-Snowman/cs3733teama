package boundaries;

import controllers.SceneEngine;
import entities.HospitalMap;
import entities.Location;
import entities.MapEdge;
import entities.MapNode;
import entities.drawing.DrawMap;
import javafx.beans.InvalidationListener;
import javafx.beans.binding.When;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.canvas.*;
import javafx.util.StringConverter;

import java.util.*;


public class MapEditorController implements Controller {
    @FXML
    private Canvas canvas;
    @FXML
    private ScrollPane mapPane;
    @FXML
    private ToggleButton nodeToggle, edgeToggle;

    @FXML
    private TextField name, xCoord, yCoord;

    @FXML
    private Button back,add, edit, remove;
    @FXML
    private Spinner<Integer> floor;
    private SpinnerValueFactory.IntegerSpinnerValueFactory floorSelections;
    //private Spinner<String> floor;
    //private SpinnerValueFactory.ListSpinnerValueFactory<String> floors;
    private SpinnerValueFactory.IntegerSpinnerValueFactory values;
    private ToggleGroup group = new ToggleGroup();
    private DrawMap editorMap;
    private HospitalMap map;
    public void initialize() {
        editorMap = new DrawMap(mapPane, canvas, -3, -5, 5000, 3400);
        //editorMap.drawAllEdges(canvas);
        map = HospitalMap.getInstance();
        // set up the Toggles
        //nodeToggle.setUserData("Node");
        //edgeToggle.setUserData("Edge");
        nodeToggle.setToggleGroup(group);
        edgeToggle.setToggleGroup(group);
        group.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
            public void changed(ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) {
                if (newValue != null) {
                    if (nodeToggle.isSelected()) {
                        edgeToggle.setSelected(false);
                    } else if (edgeToggle.isSelected()) { //selected Edge editor
                        nodeToggle.setSelected(false);
                    }
                }
            }
        });

        //set up the Spinner
        floorSelections = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 5, 2, 1);

        StringConverter<Integer> floorGetter = new StringConverter<Integer>() {
            @Override
            public String toString(Integer object) {
                String display = "G";
                switch (object) {
                    case 0:
                        display = "L2";
                        break;
                    case 1:
                        display = "L1";
                        break;
                    case 2:
                        display = "G";
                        break;
                    case 3:
                        display = "1";
                        break;
                    case 4:
                        display = "2";
                        break;
                    case 5:
                        display = "3";
                        break;
                }
                return display;
            }

            @Override
            public Integer fromString(String string) {
                Integer floorIndex = 2;
                switch (string) {
                    case ("L2"):
                        floorIndex = 0;
                        break;
                }
                return floorIndex;
            }
        };
        Integer g = 2;
        floorSelections.setConverter(floorGetter);
        floorSelections.setWrapAround(true);
        floorSelections.setValue(g);
        floor.setValueFactory(floorSelections);
        floor.valueProperty().addListener((observable, oldValue, newValue) -> {

                    String currFloor = floor.getValueFactory().getConverter().toString(oldValue);
                    String newFloor = floor.getValueFactory().getConverter().toString(newValue);
                    if (!currFloor.equals(newFloor)) {
                        currFloor = newFloor;
                    }
                    editorMap.switchFloor(currFloor);
                    for(MapNode n : map.getFloorNodes(editorMap.getCurFloor()).values()) {
                        System.out.print(n.getId() + " " + n.getShortDescription() + " ");
                        for (MapEdge e : n.getEdges()) {
                            System.out.print(e.getId() + " ");
                            editorMap.drawEdge(canvas, e);
                        }
                    }

                });

    }
    @FXML
    private void onAdd(ActionEvent e) {
        System.out.println("Adding a node");
        GraphicsContext gc = canvas.getGraphicsContext2D();
        canvas.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                Double xcoord = event.getX();
                Double ycoord = event.getY();
                xcoord = editorMap.convUnits(xcoord, 430, 5000);
                ycoord = editorMap.convUnits(ycoord, 200, 3400);


                if (nodeToggle.isSelected()) {
                    xCoord.setText(xcoord.toString());
                    yCoord.setText(ycoord.toString());
                    String nodeName = name.getText();
                    if (nodeName != "") {
                        MapNode newNode = HospitalMap.getInstance().addNode(xcoord, ycoord, nodeName, editorMap.getCurFloor());
                        editorMap.drawNode(newNode, 3, Color.RED);
                    }
                }
                else if (edgeToggle.isSelected()) {

                }

            }
        });
    }
    @Override
    public String getFXMLFileName() {
        return "MapEditorV2.fxml";
    }



    @FXML

    private void onBackClick(ActionEvent e){
        SceneEngine.display(MainScreenController.class, null);

    }




}


