package boundaries;

import controllers.SceneEngine;
import entities.*;
import entities.drawing.DrawMap;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.util.ArrayList;
import java.util.Map;


public class MapEditorController implements Controller {
    @FXML
    private Canvas canvas;
    @FXML
    private ScrollPane mapPane;
    @FXML
    private ToggleButton nodeToggle, edgeToggle, addToggle, editToggle, deleteToggle;
    @FXML
    private MenuButton nodeType;
    @FXML
    private TextField name, xCoord, yCoord;

    @FXML
    private Button back, confirm;
    @FXML
    private Spinner<Integer> floor;
    private SpinnerValueFactory.IntegerSpinnerValueFactory floorSelections;
    //private Spinner<String> floor;
    //private SpinnerValueFactory.ListSpinnerValueFactory<String> floors;
    private SpinnerValueFactory.IntegerSpinnerValueFactory values;
    private ToggleGroup group = new ToggleGroup();
    private ToggleGroup editorAction = new ToggleGroup();
    private DrawMap editorMap;
    private HospitalMap map;
    private String defaultX = "Select a location on the map.";
    private String defaultY = "Select a location on the map.";
    private String currName;
    private MapNode selectedNode = null;
    private MapEdge selectedEdge = null;
    private Boolean existingNodeSelected = false;
    private Boolean unregisteredNodeClicked = false;
    private MapNode startNode = null;
    private MapNode endNode = null;



/*
    // Mouse Event Handlers and Filters
        EventHandler<MouseEvent> onAddMode = new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent event) {
            while (nodeToggle.isSelected()) {
                GraphicsContext gc = canvas.getGraphicsContext2D();
                Double xcoord = convertXCanvastoBWMap(event.getX());
                Double ycoord = convertYCanvastoBWMap(event.getY());
                //Double xScale = editorMap.getImgWidth() / canvas.getWidth();
                //Double yScale = editorMap.getImgH() / canvas.getHeight();

                xCoord.setText(xcoord.toString());
                yCoord.setText(ycoord.toString());
                System.out.println("Width: " + canvas.getHeight() + "Height: " + canvas.getWidth());

                //xcoord *= xScale;
                //ycoord *= yScale;
                System.out.println("Node x: " + xcoord + " Node y: " + ycoord);
                /// get node name
                String defaultText = "Please enter a name for the node.";
                while (!(name.getText().equals(defaultText))) {
                    name.setText(defaultText);
                }
                String nodeName = name.getText();
                MapNode newNode = HospitalMap.getInstance().createNode(xcoord, ycoord, nodeName, editorMap.getCurFloor());
                editorMap.drawNode(newNode, 3, Color.RED);
                xCoord.clear();
                yCoord.clear();
                name.clear();
            }
            while (edgeToggle.isSelected()) {
                GraphicsContext gc = canvas.getGraphicsContext2D();
                String startDefault = "First select a starting node.";
                xCoord.setText(startDefault);
                ;



            }
        }
        // TODO: Incorp. confirm, make drawn thing black, incorporate dropdown and actually add the node to the database
    };
    */

    EventHandler<MouseEvent> onMouseClick = new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent event) {

            if (ctrlDown) {
                editorMap.toggleZoom(event.getX(), event.getY());
            }

            Double xCanvas = event.getX();
            Double yCanvas = event.getY();
            Integer xBWcoord = convertCanvastoBWMap(xCanvas, editorMap.getImgWidth(), canvas.getWidth());
            Integer yBWcoord = convertCanvastoBWMap(yCanvas, editorMap.getImgH(), canvas.getHeight());
            xCoord.setText(xBWcoord.toString());
            yCoord.setText(yBWcoord.toString());

            if ((editorAction.getSelectedToggle() != null) || (editToggle.isSelected() && (startNode == null))) { // clear the canvas
                reDrawMap();
            }

            MapNode m = findNodeAt(xCanvas.intValue(), yCanvas.intValue()); // check if node exists in database
            if (m == null) {// unregistered location

                if (editorAction.getSelectedToggle() != null) {
                    Paint color;
                    if (addToggle.isSelected()) {
                        String defaultText = "Please enter a name for the new location.";
                        name.setText(defaultText);
                        color = Color.GREEN;
                        MapNode newNode = new MapNode("", new Location(xBWcoord, yBWcoord, editorMap.getCurFloor(), "BTM"), null, "", "", "A", null);
                        editorMap.drawNode(newNode, 3, color);
                        selectedNode = newNode;
                    } else if (deleteToggle.isSelected() || editToggle.isSelected()) {
                        String defaultText = "Please select an existing node.";
                        name.setText(defaultText);
                    }
                }
            } else { // found a node
                name.setText(m.getLongDescription());
                selectedNode = m;
                if (editorAction.getSelectedToggle() != null) {
                    Paint color = Color.BLACK;
                    if (editToggle.isSelected()) {
                        if (startNode == null) {
                            startNode = selectedNode;
                            xCoord.clear();
                            yCoord.clear();
                            name.setText("Now select an ending location.");
                        } else {
                            endNode = selectedNode;
                        }
                        color = Color.AZURE;
                    } else if (deleteToggle.isSelected()) {
                        color = Color.RED;
                    }
                    editorMap.drawNode(m, 3, color);
                }

            }
        }

    };

    private Integer convertCanvastoBWMap(double coordinate, double bwDim, double canvasDim) {
        double scale = bwDim/canvasDim;
        Double conv =  (coordinate *scale);
        return conv.intValue();
    }


    public void initialize() {
        editorMap = new DrawMap(mapPane, canvas, -3, -5, 5000, 3400);
        //editorMap.drawAllEdges(canvas);
        map = HospitalMap.getInstance();
        xCoord.setText(defaultX);
        yCoord.setText(defaultY);

        ArrayList<MenuItem> items = new ArrayList<MenuItem>();
        //nodeType.getItems().addAll(NodeType.values());

        for (NodeType type: NodeType.values()) {
            MenuItem item = new MenuItem(type.toString());
            nodeType.getItems().add(item);

        }


        // set up the Toggles

        nodeToggle.setToggleGroup(group);
        edgeToggle.setToggleGroup(group);
        nodeToggle.setSelected(true); //default to node mode
        nodeToggle.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue != newValue && newValue == true) {
                reDrawMap();
            }
        });
        edgeToggle.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue != newValue && newValue == true) {
                reDrawMap();
            }
        });

        addToggle.setToggleGroup(editorAction);
        editToggle.setToggleGroup(editorAction);
        deleteToggle.setToggleGroup(editorAction);

        addToggle.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue != newValue && newValue == true) {
                refreshMap();
                if (nodeToggle.isSelected()) {
                    xCoord.setText("Select a new map location.");
                    yCoord.setText("Select a new map location.");
                }
                else if (edgeToggle.isSelected()) {
                    name.setText("Select a starting node");
                }
            }
        });
        editToggle.selectedProperty().addListener((observable, oldValue, newValue)-> {
            if (oldValue != newValue && newValue == true) {
                refreshMap();
                if (nodeToggle.isSelected()) {
                    name.setText("Select the node to edit.");
                }
                else if (edgeToggle.isSelected()) {
                    name.setText("Select the start node of the edge to edit.");
                }
                //ineditmode()
            }
        });
        deleteToggle.selectedProperty().addListener((observable, oldValue, newValue)-> {
            if (oldValue != newValue && newValue == true) {
                refreshMap();
                if (nodeToggle.isSelected()) {
                    name.setText("Select the existing node to delete.");
                }
                else if (edgeToggle.isSelected()) {
                    name.setText("Select the start node of the edge to delete.");
                }
                //indeletemode();
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
        editorMap.switchFloor("G");
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
                            editorMap.drawEdge(canvas, e, Color.GRAY);
                        }
                    }

                });
        // set up default Mouse Tracking behavior
        canvas.addEventHandler(MouseEvent.MOUSE_CLICKED, onMouseClick);

    }

    private void reDrawMap() {
        editorMap.switchFloor(editorMap.getCurFloor());
        for(MapNode n : map.getFloorNodes(editorMap.getCurFloor()).values()) {
            System.out.print(n.getId() + " " + n.getShortDescription() + " ");
            for (MapEdge e : n.getEdges()) {
                editorMap.drawEdge(canvas, e, Color.GRAY);
            }
        }
    }


@FXML
    private void refreshMap() {
        selectedNode = null;
        selectedEdge = null;
        reDrawMap();
        xCoord.clear();
        yCoord.clear();
        name.clear();

    }
    // TODO: Incorp. confirm, make drawn thing black, incorporate dropdown and actually add the node to the database
    private void inAddEdgeMode() {
        reDrawMap();
        xCoord.clear();
        yCoord.clear();
        name.clear();

    }

    // takes UNscaled x and y coordinate from user click and searches through existing nodes on floor to see if a matching node is found
    private MapNode findNodeAt(Integer xcoord, Integer ycoord) {
        Map<String, MapNode> allNodes = HospitalMap.getInstance().getFloorNodes(editorMap.getCurFloor());
        for (String id: allNodes.keySet()) {
            MapNode m = allNodes.get(id);
            if (editorMap.isNodeWithinRegion(canvas, m, xcoord, ycoord)) {
                return m;
            }
        }
        return null;
    }

    private MapEdge findEdgeAt(MapNode start, MapNode end) {
        ArrayList<MapEdge> edges = start.getEdges();
        for (MapEdge e: edges) {
            if (e.getEnd().getId().equals(end.getId())) {
                return e;
            }
        }
        xCoord.clear();
        yCoord.clear();
        name.setText("No such edge found.");
        return null;

    }
    @FXML
    private void onConfirm(ActionEvent e) {

        if (addToggle.isSelected()){


        }
        else if (editToggle.isSelected()) {

        }
        else if (deleteToggle.isSelected()) {

        }

    }



    @Override
    public String getFXMLFileName() {
        return "MapEditorV3.fxml";
    }



    @FXML

    private void onBackClick(ActionEvent e){
        SceneEngine.display(MainScreenController.class, null);

    }


    private boolean ctrlDown = false;

    /**
     * Resizes the map when needed
     * @param stage
     */
    @Override
    public void setStage(Stage stage) {
        // On resize of the stage
        ChangeListener<Number> stageSizeListener = (observable, oldValue, newValue) -> {
            editorMap.updateSize();
        };
        // If the stage is resized make the canvas fill
        stage.widthProperty().addListener(stageSizeListener);
        stage.heightProperty().addListener(stageSizeListener);
        // If the pane is resized make the canvas fill
        mapPane.heightProperty().addListener(stageSizeListener);
        mapPane.widthProperty().addListener(stageSizeListener);
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

}


