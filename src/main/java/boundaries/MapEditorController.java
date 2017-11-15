package boundaries;

import controllers.SceneEngine;
import entities.*;
import entities.db.DatabaseUUID;
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
    private ComboBox<NodeType> nodeType;
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
    private Location selectedLocation = null;
    private MapNode selectedNode = null;
    private MapEdge selectedEdge = null;
    private Boolean existingNodeSelected = false;
    private Boolean unregisteredNodeClicked = false;
    private MapNode startNode = null;
    private MapNode endNode = null;

    private String currentFloor = "G";



    EventHandler<MouseEvent> onMouseClick = new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent event) {

            if (ctrlDown) {
                editorMap.toggleZoom(event.getX(), event.getY());
            }
            name.clear();
            Double xCanvas = event.getX();
            Double yCanvas = event.getY();
            Integer xBWcoord = convertCanvastoBWMap(xCanvas, editorMap.getImgWidth(), canvas.getWidth());
            Integer yBWcoord = convertCanvastoBWMap(yCanvas, editorMap.getImgH(), canvas.getHeight());
            refreshMap();

            xCoord.setText(xBWcoord.toString());
            yCoord.setText(yBWcoord.toString());
            /*
            if (edgeToggle.isSelected() && startNode != null) { // redraw the saved start node
                Paint color = Color.AZURE;
                if (addToggle.isSelected()) {
                    color = Color.GREEN;
                }
                else if (deleteToggle.isSelected()) {
                    color = Color.RED;
                }
                editorMap.drawNode(startNode, 4, color);
            }
*/
            MapNode m = findNodeAt(xCanvas.intValue(), yCanvas.intValue()); // check if node exists in database
            if (nodeToggle.isSelected()) {
                if (m == null) {
                    if (addToggle.isSelected()) {
                        String defaultText = "Please enter a name for the new location.";
                        name.setText(defaultText);
                        //color = Color.GREEN;
                        MapNode newNode = new MapNode("", new Location(xBWcoord, yBWcoord, editorMap.getCurFloor(), "BTM"), null, "", "", "Team A", null);
                        editorMap.drawNode(newNode, 4, Color.GREEN);
                        selectedNode = newNode;
                    }
                    else if (deleteToggle.isSelected() || editToggle.isSelected()) {
                            String defaultText = "Please select an existing node.";
                            name.setText(defaultText);
                    }
                }
                else {
                    selectedNode = m;
                    name.setText(m.getLongDescription());
                    if (deleteToggle.isSelected()) {
                        editorMap.drawNode(m, 4, Color.RED);
                    }
                }
            }

            else if (edgeToggle.isSelected() && editorAction.getSelectedToggle()!=null) {
                    Paint color = Color.BLACK;
                    if (addToggle.isSelected()) {
                        color = Color.GREEN;
                    } else if (deleteToggle.isSelected()) {
                        color = Color.RED;
                    }
                    if (startNode != null) {
                        editorMap.drawNode(startNode, 4, color);
                    }
                    if (m == null) {// unregistered location
                        String defaultText = "Please select an existing node.";
                        name.setText(defaultText);

                    } else { // found a node
                        name.setText(m.getLongDescription());
                        nodeType.setValue((NodeType) m.getNodeType());
                        selectedNode = m;

                        editorMap.drawNode(m, 4, color);

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
        editorMap.setRenderHidden(true);
        reDrawMap();
        //editorMap.drawAllEdges(canvas);
        map = HospitalMap.getInstance();
        xCoord.setText(defaultX);
        yCoord.setText(defaultY);

        for(NodeType n : NodeType.values()) {
            if(!n.equalsName("Elevator")) {
                nodeType.getItems().add(n);
            }
        }


        // set up the Toggles

        nodeToggle.setToggleGroup(group);
        edgeToggle.setToggleGroup(group);
        nodeToggle.setSelected(true); //default to node mode


        addToggle.setToggleGroup(editorAction);
        editToggle.setToggleGroup(editorAction);
        deleteToggle.setToggleGroup(editorAction);

        addToggle.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue != newValue && addToggle.isSelected()) {
                refreshMap();
                if (nodeToggle.isSelected()) {
                    xCoord.setText("Select a new map location.");
                    yCoord.setText("Select a new map location.");
                }
                else if (edgeToggle.isSelected()) {
                    startNode = null;
                    endNode = null;
                    name.setText("Select the start node of the edge to add, then Confirm..");
                }
            }
        });
        editToggle.selectedProperty().addListener((observable, oldValue, newValue)-> {
            if (oldValue != newValue && editToggle.isSelected()) {
                refreshMap();
                if (nodeToggle.isSelected()) {
                    name.setText("Select the node to edit.");
                }

                //ineditmode()
            }
        });
        deleteToggle.selectedProperty().addListener((observable, oldValue, newValue)-> {
            if (oldValue != newValue && deleteToggle.isSelected()) {
                refreshMap();
                if (nodeToggle.isSelected()) {
                    name.setText("Select the existing node to delete.");
                }
                else if (edgeToggle.isSelected()) {
                    startNode = null;
                    endNode = null;
                    name.setText("Select the start node of the edge to delete, then Confirm.");
                }
                //indeletemode();
            }
        });
        nodeToggle.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue != newValue && nodeToggle.isSelected()) {
                refreshMap();
                editToggle.setVisible(true);
                if (editorAction.getSelectedToggle()!=null) {
                    if (editToggle.isSelected()) {
                        name.setText("Select the node to edit.");
                    }else if (deleteToggle.isSelected()) {
                        name.setText("Select the existing node to delete, then Confirm.");
                    }
                }
                else {
                    xCoord.setText("Select a new map location.");
                    yCoord.setText("Select a new map location.");
                }
            }
        });
        edgeToggle.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue != newValue && edgeToggle.isSelected()) {
                refreshMap();
                startNode = null;
                endNode = null;
                editToggle.setVisible(false);
                name.setText("Select Mode (Add or Delete)");
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
        //editorMap.switchFloor("G");
        floor.valueProperty().addListener((observable, oldValue, newValue) -> {
                    String currFloor = floor.getValueFactory().getConverter().toString(oldValue);
                    String newFloor = floor.getValueFactory().getConverter().toString(newValue);
                    if (!currFloor.equals(newFloor)) {
                        currFloor = newFloor;
                    }
                    //System.out.println(currFloor);
                    //editorMap.updateCurrFloor(currFloor);
                    //System.out.println(editorMap.getCurFloor());
                    currentFloor = currFloor;
                    refreshMap();

                });
        // set up default Mouse Tracking behavior
        canvas.addEventHandler(MouseEvent.MOUSE_CLICKED, onMouseClick);

    }

    private void reDrawMap() {
        editorMap.switchFloor(currentFloor);
    }


    private void refreshMap() {
        selectedNode = null;
        selectedEdge = null;

        reDrawMap();
        xCoord.clear();
        yCoord.clear();
        name.clear();
        if (edgeToggle.isSelected()) {
            for (MapNode n : map.getFloorNodes(editorMap.getCurFloor()).values()) {
                for (MapEdge e : n.getEdges()) {
                    editorMap.drawEdge(canvas, e, Color.GREY);

                }
            }
        }

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
    //fills in user-inputted values to the node to be added/edited
    private MapNode makeNewNode() {
        MapNode newNode = selectedNode;
        String nodeName = name.getText();
        newNode.setLongDescription(nodeName);
        newNode.setShortDescription(nodeName);
        newNode.setType(nodeType.getValue());
        newNode.setID(DatabaseUUID.generateID(newNode.getNodeType(), newNode.getCoordinate().getLevel()));
        return newNode;

    }
    @FXML
    private void onConfirm(ActionEvent e) {
        if (edgeToggle.isSelected()) {
            if (startNode == null) {
                startNode = selectedNode;
                System.out.println("The start node is "+ startNode.getLongDescription());
                name.clear();
                xCoord.clear();
                yCoord.clear();
                name.setText("Now select an existing ending node, and Confirm.");
            } else if (endNode == null) {
                System.out.println("The start node is "+ startNode.getLongDescription());

                endNode = selectedNode;
                System.out.println("The end node is "+ endNode.getLongDescription());

                String edgeID = startNode.getId() + "_" + endNode.getId();
                MapEdge newEdge = new MapEdge(edgeID, startNode, endNode);
                selectedEdge = newEdge;
                editorMap.drawEdge(canvas, newEdge, Color.YELLOWGREEN);
                name.clear();
                xCoord.clear();
                yCoord.clear();
                if (addToggle.isSelected()) {
                    name.setText("Click Confirm to add edge.");
                }
                else if (deleteToggle.isSelected()) {
                    name.setText("Click Confirm to delete edge.");
                }
            }
            else if (startNode != null && endNode != null) { // got the start and end nodes, so edge is selected
                //String edgeID = startNode.getId() + "_" + endNode.getId();
                //MapEdge edgeSelected = new MapEdge(edgeID, startNode, endNode);
                //edgeSelected.setID(edgeID);
                if (addToggle.isSelected()) {
                    String edgeID = startNode.getId() + "_" + endNode.getId();
                    MapEdge edgeSelected = new MapEdge(edgeID, startNode, endNode);
                    edgeSelected.setID(edgeID);

                    HospitalMap.getInstance().getMap().addEdge(edgeSelected);
                    reDrawMap();
                    editorMap.drawEdge(canvas, edgeSelected, Color.GRAY);
                    System.out.println("The ID of the new Edge is " + edgeSelected.getId());
                    if (addToggle.isSelected()) {
                        addToggle.setSelected(false);
                    }
                    refreshMap();
                }
                else if (deleteToggle.isSelected()) {
                    MapEdge edgeSelected = null;
                    for(MapEdge startEdge : startNode.getEdges()) {
                        for(MapEdge endEdge : endNode.getEdges()) {
                            if(startEdge.getId().equals(endEdge.getId())) {
                                edgeSelected = endEdge;
                                break;
                            }
                        }
                    }
                    HospitalMap.getInstance().getMap().removeEdge(edgeSelected.getId());
                    refreshMap();
                    deleteToggle.setSelected(false);
                }
            }
        }
        if (nodeToggle.isSelected()) {
            if (addToggle.isSelected() || editToggle.isSelected()) {
                MapNode newNode = makeNewNode();
                //reDrawMap();
                //editorMap.drawNode(newNode, 3, Color.BLACK);
                HospitalMap.getInstance().getMap().addNode(newNode);
                refreshMap();
                if (addToggle.isSelected()) {
                    addToggle.setSelected(false);
                } else if (editToggle.isSelected()) {
                    editToggle.setSelected(false);
                }
            } else if (deleteToggle.isSelected()) {
                MapNode toDelete = selectedNode;
                HospitalMap.getInstance().getMap().removeNode(toDelete.getId());
                refreshMap();
                deleteToggle.setSelected(false);
            }
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


