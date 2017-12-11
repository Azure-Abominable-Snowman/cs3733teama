package com.teama.controllers_refactor2;

import com.jfoenix.controls.*;
import com.teama.controllers.EdgeEditorController;
import com.teama.mapdrawingsubsystem.ClickedListener;
import com.teama.mapdrawingsubsystem.MapDrawingSubsystem;
import com.teama.mapsubsystem.MapSubsystem;
import com.teama.mapsubsystem.data.*;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

import java.io.IOException;
import java.util.*;

public class MapEditorController  {
    @FXML Pane parentPane;
    public Pane getParentPane(){
        return parentPane;
    }
    private MapDrawingSubsystem mapDraw;
    @FXML
    private Text mapTools;
    @FXML
    private JFXToggleButton viewEdges, viewNodes, editNodes, editEdges;
    @FXML
    private JFXTextField nodeID, nodeCoord, longName, shortName;

    @FXML
    private JFXComboBox<NodeType> nodeType;
    @FXML
    private JFXComboBox<Floor> floorSelect;
    @FXML
    private JFXComboBox<String> alignmentOptions;
    @FXML
    private JFXButton confirmBtn, cancelBtn, alignBtn, editNode, addNode, deleteNode, clearBtn;
    @FXML
    private VBox editorInfo, masterBox;
    @FXML
    private GridPane nodeDetails, actionButtons, toolToggles;
    @FXML
    private JFXTabPane masterTabNodes;


    // private Parent currentPopOut;
    private ToggleGroup editorGroup;

    private Map<Long, EventHandler<MouseEvent>> mouseEvents = new HashMap<>();
    private Map<Long, ChangeListener<Boolean>> floorEvents = new HashMap<>();
    private Set<MapNode> alignmentNodes = new HashSet<>();
    private Set<MapNode> selectedNodes = new HashSet<>();

    BooleanProperty edgeEditor = new SimpleBooleanProperty(false);
    BooleanProperty nodeEditor = new SimpleBooleanProperty(false);

    // NEW
    private BooleanProperty unknownLoc = new SimpleBooleanProperty();
    private BooleanProperty knownLoc = new SimpleBooleanProperty();
    private BooleanProperty updateFields = new SimpleBooleanProperty(true);
    private BooleanProperty isAligning = new SimpleBooleanProperty(false);

    private ObjectProperty<Location> selectedLocation = new SimpleObjectProperty<Location>();
    private ObjectProperty<MapNode> selectedNode = new SimpleObjectProperty<MapNode>();
    private ObjectProperty<MapNode> alignmentNode = new SimpleObjectProperty<MapNode>();
    //




    private MapDrawingSubsystem masterMap;
    private MapSubsystem mapData;


    @FXML
    public void initialize() {
        mapDraw = MapDrawingSubsystem.getInstance();
        //alignPane(xProperty, xOffset, yProperty, yOffset);
        masterMap = MapDrawingSubsystem.getInstance();
        mapData = MapSubsystem.getInstance();
        nodeType.getItems().clear();
        nodeType.getItems().addAll(NodeType.values());

        //alignmentOptions.getItems().clear();
        //alignmentOptions.getItems().addAll("X", "Y");


/*
        alignmentOptions.getSelectionModel().selectedItemProperty()
                .addListener(new ChangeListener<String>() {
                    @Override
                    public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                        if (!(alignmentOptions.disabledProperty().getValue()) && alignmentOptions.getSelectionModel().getSelectedItem() != null) {
                            isAligning.setValue(true);
                            updateFields.setValue(false);
                            System.out.println("User would like to align nodes.");
                            //cancelBtn.disableProperty().setValue(true);
                            //confirmBtn.disableProperty().setValue(true);
                            //alignBtn.setDisable(true);
                        } else {
                            //cancelBtn.disableProperty().setValue(false);
                            //confirmBtn.disableProperty().setValue(false);

                            alignNode.setValue(false);
                        }
                    }
                });
*/

        //scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        floorEvents.put(masterMap.attachFloorChangeListener(onFloorChange), onFloorChange);

        long id = masterMap.attachClickedListener(onEdgeClicked, ClickedListener.EDGECLICKED);
        mouseEvents.put(id, onEdgeClicked);

        editorGroup = new ToggleGroup();
        editNodes.setToggleGroup(editorGroup);
        editEdges.setToggleGroup(editorGroup);
        addNode.disableProperty().bind(knownLoc);
        editNode.disableProperty().bind(unknownLoc);
        deleteNode.disableProperty().bind(unknownLoc);
//        alignmentOptions.disableProperty().bind(unknownLoc);
        alignBtn.disableProperty().bind(unknownLoc);

        viewNodes.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if (newValue) {
                    System.out.println("View Nodes");
                    drawNodes();
                }
                if (!newValue) {
                    System.out.println("Hide Nodes");
                    hideFloorNodes();
                }
            }
        });
        viewEdges.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if (newValue) {
                    drawEdges();
                    System.out.println("View Edges");
                } else if (!newValue) {
                    System.out.println("Hide Edges");
                    hideEdges();
                }
            }
        });

        editorGroup.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
            @Override
            public void changed(ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) {
                if (newValue != null) {
                    if (editNodes.isSelected()) {
                        nodeEditor.setValue(true);
                        edgeEditor.setValue(false);
                        masterBox.getChildren().clear();
                        masterBox.getChildren().addAll(mapTools, toolToggles, editorInfo);
                        //alignmentOptions.disableProperty().setValue(false);
                        //alignBtn.disableProperty().setValue(false);
                        //nodeEditorListenerID = masterMap.attachClickedListener(onClickNodeEditor, ClickedListener.LOCCLICKED);
                        //mouseEvents.put(nodeEditorListenerID, onClickNodeEditor);
                        System.out.println("Selecting nodes for ediitng and such.");

                    } else if (editEdges.isSelected()) {
                        nodeEditor.setValue(false);
                        edgeEditor.setValue(true);
                        masterBox.getChildren().clear();
                        masterBox.getChildren().addAll(mapTools, toolToggles);

                        FXMLLoader loader = new FXMLLoader();
                        loader.setLocation(getClass().getResource("/EdgeMapEditorNew.fxml"));
                        EdgeEditorController e = new EdgeEditorController();
                        loader.setController(e);

                        try {
                            VBox edgeContent = loader.load();
                            masterBox.getChildren().addAll(edgeContent);

                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }


                        //alignmentOptions.disableProperty().setValue(true);
                        //alignBtn.disableProperty().setValue(true);
                        //mapDraw.detachListener(nodeEditorListenerID);
                        //mouseEvents.remove(nodeEditorListenerID);
                        System.out.println("No longer selecting nodes.");
                    }

                }
                else {

                    //updateFields.set(false);
                }
            }
        });
        editNode.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                editNode.visibleProperty().setValue(false);
                deleteNode.visibleProperty().setValue(false);
            }
        });
        addNode.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                editNode.visibleProperty().setValue(false);
                deleteNode.visibleProperty().set(false);
            }
        });
        mouseEvents.put(masterMap.attachClickedListener(onClickNodeEditor, ClickedListener.LOCCLICKED), onClickNodeEditor);
        /*
        confirmBtn.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                editNode.visibleProperty().setValue(true);
                deleteNode.visibleProperty().set(true);

            }
        });
        cancelBtn.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                editNode.visibleProperty().setValue(true);
                deleteNode.visibleProperty().set(true);
            }
        });
        /*
        editEdges.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if (newValue) {

                }
            }
        });
        editNodes.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if (newValue) {

            }
        };
        */
    }
    //  }//

/*
    @Override
    public void onOpen(ReadOnlyDoubleProperty xProperty, int xOffset, ReadOnlyDoubleProperty yProperty, int yOffset) {


        this.xOffset = xOffset;
        this.yOffset = yOffset;
        this.xProperty = xProperty;
        this.yProperty = yProperty;




    }
    */

    ChangeListener<Boolean> onFloorChange = new ChangeListener<Boolean>() {
        @Override
        public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
            if (viewNodes.isSelected()) {
                System.out.println("Viewing nodes; floor change detected.");
                drawNodes();
            }
            if (viewEdges.isSelected()) {
                System.out.println("Viewing edges; floor change detected.");

                drawEdges();
            }
        }
    };

    //            nType = nodeTypeSelector.getSelectionModel().getSelectedItem().toString();
/*
if (nodeTypeSelector.getSelectionModel().getSelectedItem() != null) {
            nType = nodeTypeSelector.getSelectionModel().getSelectedItem().toString();
        }
 */
    private void resetToDefault() {
    }

    private void clearTextFieldsNodes() {

        nodeID.clear();
        longName.clear();
        shortName.clear();
        //nodeType.setDisable(false);
        nodeType.getSelectionModel().clearSelection();
        nodeType.setDisable(true);
        //confirmBtn.setDisable(true);
        //cancelBtn.setDisable(true);
        //addNode.setVisible(true);
        //editNode.setVisible(true);
        //deleteNode.setVisible(true);
        //updateCurrentNode.setValue(true);
        //selectedLocation = null;
        //selectedNode = null;
        //alignBtn.setDisable(true);
        //alignmentOptions.setDisable(true);
        //alignNode.setValue(false);
        //alignmentNode = null;
    }

    private Location mouseClickToLocation(MouseEvent e) {
        double xCanv = e.getX();
        double yCanv = e.getY();
        Location selected = new Location(e, masterMap.getCurrentFloor());
        return selected;
    }


    EventHandler<MouseEvent> onClickNodeEditor = new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent event) {
            //double xCanv = event.getX();
            //double yCanv = event.getY();
            Location selected = mouseClickToLocation(event);
            MapNode clickedNode = masterMap.nodeAt(selected);
            selectedLocation.set(selected);

            if (clickedNode != null) {
                System.out.println(clickedNode.getId());
                if (selectedNodes == null) {
                    selectedNodes = new HashSet<>();
                }
                selectedNodes.add(clickedNode);
                masterMap.unDrawNode(clickedNode);
                masterMap.drawNode(clickedNode, 5, Color.GREEN);
            }

            if (updateFields.getValue()) {
                if (clickedNode == null) {

                    unknownLoc.set(true);
                    knownLoc.set(false);
                    clearTextFieldsNodes();
                    nodeCoord.setText("(" + selected.getxCoord() + ", " +selected.getyCoord() + ")");

                } else {
                    System.out.println("Update the selected node and node fields.");
                    unknownLoc.set(false);
                    knownLoc.set(true);
                    selectedNode.setValue(clickedNode);
                    if (updateFields.get()) {
                        nodeID.setText(clickedNode.getId());
                        nodeCoord.setText("(" + clickedNode.getCoordinate().getxCoord() + ", " + clickedNode.getCoordinate().getyCoord() + ")");
                        longName.setText(clickedNode.getLongDescription());
                        shortName.setText(clickedNode.getShortDescription());
                        nodeType.setValue(clickedNode.getNodeType());
                    }
                    //selectedLocation.set(selected);
                }
            }
            else if (isAligning.getValue()) {
                if (clickedNode != null) {
                    System.out.println("The selected Node is: " + selectedNode.getValue().getId());

                    System.out.println("Got an alignment node.");
                    alignmentNode.setValue(clickedNode);
                    System.out.println("The alignment node is: " + alignmentNode.getValue().getId());

                }
            }
            /*
            if (updateCurrentNode.getValue()) {
                clearTextFieldsNodes();
                if (clickedNode != null) {
                    selectedLocation = clickedNode.getCoordinate();
                    nodeID.setText(clickedNode.getId());
                    nodeCoord.setText("(" + clickedNode.getCoordinate().getxCoord() + ", " + clickedNode.getCoordinate().getyCoord() + ")");
                    longName.setText(clickedNode.getLongDescription());
                    shortName.setText(clickedNode.getShortDescription());
                    nodeType.setValue(clickedNode.getNodeType());
                    nodeType.setDisable(true);
                    addNode.setDisable(true);
                    editNode.setDisable(false);
                    deleteNode.setDisable(false);
                    selectedNode = clickedNode;
                } else {
                    Location converted = masterMap.convertEventToImg(event, masterMap.getCurrentFloor());
                    selectedLocation = converted;
                    selectedNode = null;
                    addNode.setDisable(false);
                    editNode.setDisable(true);
                    deleteNode.setDisable(true);
                    clearTextFieldsNodes();
                    nodeCoord.setText("(" + Integer.toString(converted.getxCoord()) + ", " + Integer.toString(converted.getyCoord()) + ")");
                }
            } else if (alignNode.getValue()) {
                System.out.println("Align the node.");
                if (clickedNode != null) {
                    System.out.println("Found a node to align by.");
                    alignmentNode = clickedNode;
                    alignBtn.disableProperty().setValue(false);

                }
            }
            */


            /*
            closePopUp();
            Parent editPopout;
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/EditDeleteNode.fxml"));
            loader.setController(this);
            try {
                editPopout = loader.load();
                currentPopOut = editPopout;
                mapDraw.getAreaPane().getChildren().addAll(editPopout);


            } catch (IOException e) {
                e.printStackTrace();
            }
            //node.setInfo(event);
            */
        }
    };
    EventHandler<MouseEvent> onNodeClickAndDrag = new EventHandler<MouseEvent>() {
        @Override

        public void handle(MouseEvent event) {

        }
    };

    private EventHandler<MouseEvent> onEdgeClicked = new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent event) {
            System.out.println("Found an edge!");
            Location mouseLoc = new Location(event, masterMap.getCurrentFloor());
            MapEdge foundEdge = masterMap.edgeAt(mouseLoc);
            // masterMap.drawLine(foundEdge.getId(), l.getStart(), l.getEnd(), l.getWeight(), Color.RED, false);


        }
    };


    /*
        //public void clearSelect
        private EventHandler<MouseEvent> onNodeClickEdges = new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                MapNode selected = mapDraw.nodeAt(new Location(event, mapDraw.getCurrentFloor()));
                if(startNodeEdge == null) {
                    // Start node selected
                    startNodeEdge = selected;
                    displayNodeOnEdgeEditor(startNodeEdge, true);
                } else {
                    // End node selected
                    endNodeEdge = selected;
                    displayNodeOnEdgeEditor(selected, false);
                    // Display the edge on the screen, and delete the previous one
                    mapDraw.drawEdge(new MapEdgeData("DRAWN_EDGE", startNodeEdge, endNodeEdge), 5, Color.LIGHTBLUE, false);

                }
            }
        };

        private void displayNodeOnEdgeEditor(MapNode n , boolean start) {
            Location coord = n.getCoordinate();
            String coordString = "("+coord.getxCoord()+", "+coord.getyCoord()+")";
            if(start) {
                nodeID1.setText(n.getId());
                nodeCoord1.setText(coordString);
                longName1.setText(n.getLongDescription());
            } else {
                nodeID2.setText(n.getId());
                nodeCoord2.setText(coordString);
                longName2.setText(n.getLongDescription());
            }
        }

    */
    private void drawNodes() {
        for (MapNode m : mapData.getVisibleFloorNodes(masterMap.getCurrentFloor()).values()) {
            masterMap.drawNode(m, 5, Color.DARKBLUE);
        }
        for (MapNode m : mapData.getInvisibleFloorNodes(masterMap.getCurrentFloor()).values()) {
            masterMap.drawNode(m, 5, Color.DARKBLUE);
        }
    }

    private void hideFloorNodes() {
        for (MapNode m : mapData.getInvisibleFloorNodes(masterMap.getCurrentFloor()).values()) {
            masterMap.unDrawNode(m);
        }
    }

    private void drawEdges() {
        for (MapNode m : mapData.getFloorNodes(masterMap.getCurrentFloor()).values()) {
            for (MapEdge e : m.getEdges()) {
                masterMap.drawEdge(e, 5, Color.LIGHTBLUE, false);

            }
        }
    }

    private void hideEdges() {
        for (MapNode m : mapData.getFloorNodes(masterMap.getCurrentFloor()).values()) {
            for (MapEdge e : m.getEdges()) {
                masterMap.unDrawEdge(e);

            }
        }
    }



    public void onClose() {
        //this.isOpenProperty.setValue(false);
        for (long id : floorEvents.keySet()) {
            masterMap.detachListener(id);
        }
        for (long id : mouseEvents.keySet()) {
            masterMap.detachListener(id);
        }
    }

    @FXML
    void onAlignNode(ActionEvent e) {
        /*
        MapNode selected = selectedNode.getValue();
        System.out.println("The selected Node is: " + selectedNode.getValue().getId());
        System.out.println("AlignmentNode: " + alignmentNode.getValue().getId());
        if (alignmentOptions.getSelectionModel().getSelectedItem() != null && alignmentNode.getValue() != null && selectedNode != null) {
            String direction = alignmentOptions.getSelectionModel().getSelectedItem();

            if (direction.equals("X")) {
                MapNode toUpdate = new MapNodeData(selected.getId(), new Location(selected.getCoordinate().getxCoord(),
                        alignmentNode.getValue().getCoordinate().getyCoord(), selected.getCoordinate().getLevel(), selected.getCoordinate().getBuilding()),
                        selected.getNodeType(), selected.getLongDescription(), selected.getShortDescription(), selected.getTeamAssignment(), selected.getEdges());
                mapDraw.unDrawNode(selected);
                mapDraw.drawNode(toUpdate, 5, Color.DARKBLUE);
                mapData.addNode(toUpdate);
                nodeCoord.setText("(" + selected.getCoordinate().getxCoord() + ", " + alignmentNode.getValue().getCoordinate().getyCoord() + ")");
                //mapData.deleteNode(selectedNode.getId());


                //mapData.addNode(selectedNode);
            }
            else if (direction.equals("Y")) {
                MapNode toUpdate = new MapNodeData(selected.getId(), new Location(alignmentNode.getValue().getCoordinate().getxCoord(),
                        selected.getCoordinate().getyCoord(), selected.getCoordinate().getLevel(), selected.getCoordinate().getBuilding()),
                        selected.getNodeType(), selected.getLongDescription(), selected.getShortDescription(), selected.getTeamAssignment(), selected.getEdges());
                mapDraw.unDrawNode(selected);
                mapDraw.drawNode(toUpdate, 5, Color.DARKBLUE);
                mapData.addNode(toUpdate);
                //mapData.deleteNode(selectedNode.getId());


            }
            isAligning.setValue(false);
            alignmentNode.setValue(null);
            alignmentOptions.getSelectionModel().clearSelection();
        }
        else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Alignment Error");
            alert.setHeaderText("Failed to Align Node");
            alert.setContentText("Please select a valid node to align to.");
            alert.showAndWait();

        }
        //clearTextFieldsNodes();
        //alignmentNode = null;
        //alignBtn.disableProperty().setValue(true);
        alignmentOptions.getSelectionModel().clearSelection();
        //updateCurrentNode.setValue(true);
        */
    }


    @FXML
    void onConfirm(ActionEvent e) {
        updateFields.setValue(true);
        clearTextFieldsNodes();

    }

    @FXML
    void onCancel(ActionEvent e) {
        updateFields.setValue(true);
        clearTextFieldsNodes();

    }

    @FXML
    void onAddNode(ActionEvent e) {
        updateFields.setValue(false);
        System.out.println("user wants to add a node.");
        //addNode.setVisible(false);
        //editNode.setVisible(false);
        //deleteNode.setVisible(false);
        //nodeType.setDisable(false);
        //confirmBtn.setDisable(false);
        //cancelBtn.setDisable(false);
        confirmBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (selectedLocation.getValue() != null) {
                        /*
                        MapNode toAdd = new MapNodeData(nodeID.getText(), new Location(selectedLocation.getxCoord(), selectedLocation.getyCoord(), masterMap.getCurrentFloor(), ""),
                                nodeType.getSelectionModel().getSelectedItem(), longName.getText(), shortName.getText(), "A");
                                */
                    MapNode toAdd = nodeFieldsToNode();
                    if (toAdd != null) {
                        mapData.addNode(toAdd);
                        System.out.println("Adding a node");
                        clearTextFieldsNodes();
                        mapDraw.drawNode(toAdd, 5, Color.DARKBLUE);

                    } else {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Invalid Addition");
                        alert.setHeaderText("Failed to Add Node");
                        alert.setContentText("Make sure all fields are filled in.");
                        alert.showAndWait();
                    }
                }
            }
        });

    }

    private MapNode nodeFieldsToNode() {
        MapNode created = null;
        Location chosen = selectedLocation.get();
        if (chosen != null)
            if (!(nodeID.getText().equals("") && longName.getText().equals("") && nodeType.getSelectionModel().getSelectedItem()== null && shortName.getText().equals(""))) {
                created = new MapNodeData(nodeID.getText(), new Location(chosen.getxCoord(), chosen.getyCoord(), masterMap.getCurrentFloor(), ""),
                        nodeType.getSelectionModel().getSelectedItem(), longName.getText(), shortName.getText(), "A");
                if (created != null) {
                    System.out.println("jawieor");
                }}
        return created;
    }

    @FXML
    void onEditNode(ActionEvent e) {
        updateFields.setValue(false);
        if (selectedNode.getValue() != null) {
            //addNode.setVisible(false);
            //editNode.setVisible(false);
            //deleteNode.setVisible(false);
            nodeType.setDisable(false);
            confirmBtn.setDisable(false);
            cancelBtn.setDisable(false);
            confirmBtn.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {

                    MapNode update = nodeFieldsToNode();
                    if (update != null) {
                        MapSubsystem.getInstance().addNode(update);
                        clearTextFieldsNodes();
                        updateFields.setValue(true);
                    }
                    else {
                        clearTextFieldsNodes();
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Invalid Edit");
                        alert.setHeaderText("Failed to Edit Node");
                        alert.setContentText("Select an existing node to edit.");
                        alert.showAndWait();
                    }

                }
            });
        }

    }

    @FXML
    void onDeleteNode(ActionEvent e) {
        //updateFields.setValue(false);
        //todo: make the alert more visually appealing
        Alert confirmDelete = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDelete.setTitle("Delete Selected Nodes");
        confirmDelete.setHeaderText("Are you sure you would like to delete these nodes?");
        confirmDelete.setContentText("Press Confirm to continue");
        
        ButtonType confirm = new ButtonType("Confirm");
        ButtonType cancel = new ButtonType("Cancel");
        confirmDelete.getButtonTypes().setAll(confirm, cancel);
        Optional<ButtonType> selectedChoice = confirmDelete.showAndWait();
        if (selectedChoice.get() == confirm) {
            for (MapNode m: selectedNodes) {
                for (MapEdge edge: m.getEdges()) { // clean up edges
                    mapData.deleteEdge(edge.getId());
                    mapDraw.unDrawEdge(edge);
                }
                mapData.deleteNode(m.getId());
                mapDraw.unDrawNode(m);

            }
        }
        else {
            if (selectedNodes.isEmpty()) {
                System.out.println("No selected nodes.");
            }
            for (MapNode m: selectedNodes) {
                mapDraw.unDrawNode(m);
                mapDraw.drawNode(m, 6, Color.DARKBLUE);
            }
        }
        selectedNodes = null;

        clearTextFieldsNodes();

    }


    public String getFXMLPath(){
        return "/MainScreenDrawers/MapEditorFinal.fxml";
    }
}
