package com.teama.controllers_refactor;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXToggleButton;
import com.teama.mapdrawingsubsystem.ClickedListener;
import com.teama.mapdrawingsubsystem.MapDrawingSubsystem;
import com.teama.mapsubsystem.MapSubsystem;
import com.teama.mapsubsystem.data.*;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class EditorPopOut extends PopOutController {

    private int xOffset, yOffset;
    private ReadOnlyDoubleProperty xProperty, yProperty;
    private MapDrawingSubsystem mapDraw;

    @FXML
    private JFXToggleButton viewEdges, viewNodes, editNodes, editEdges;
    @FXML
    private JFXTextField nodeID, nodeCoord, longName, shortName;
    @FXML
    private JFXTextField nodeID1, nodeCoord1, longName1, nodeID2, nodeCoord2, longName2;
    @FXML
    private JFXComboBox<NodeType> nodeType;
    @FXML
    private JFXComboBox<String> alignmentOptions;
    @FXML
    private JFXButton confirmBtn, cancelBtn, alignBtn, editNode, addNode, deleteNode;
    @FXML
    private VBox editorInfo;
    @FXML
    private GridPane nodeDetails, alignNodes, actionButtons, finishButtons;
    @FXML
    private ScrollPane scrollPane;


    private Parent currentPopOut;
    private ToggleGroup editorGroup;

    private Map<Long, EventHandler<MouseEvent>> mouseEvents = new HashMap<>();
    private Map<Long, ChangeListener<Boolean>> floorEvents = new HashMap<>();
    BooleanProperty updateCurrentNode = new SimpleBooleanProperty(true);
    BooleanProperty edgeEditor = new SimpleBooleanProperty(false);
    BooleanProperty nodeEditor = new SimpleBooleanProperty(false);

    BooleanProperty alignNode = new SimpleBooleanProperty(false);


    MapNode selectedNode = null;
    Location selectedLocation = null;
    MapNode alignmentNode = null;
    MapNode startNode = null;
    MapNode endNode = null;

    private long nodeEditorListenerID, edgeEditorListenerID;

    @FXML
    public void initialize() {
        mapDraw = MapDrawingSubsystem.getInstance();
        alignPane(xProperty, xOffset, yProperty, yOffset);
        // viewEdges = new JFXToggleButton();
        //viewNodes = new JFXToggleButton();
        viewEdges.setText("View Edges");
        viewNodes.setText("View Hall Nodes");
        nodeType.getItems().clear();
        nodeType.getItems().addAll(NodeType.values());
        alignmentOptions.getItems().clear();
        alignmentOptions.getItems().addAll("X", "Y");
        alignmentOptions.setDisable(true);
        alignBtn.setDisable(true);

        alignmentOptions.getSelectionModel().selectedItemProperty()
                .addListener(new ChangeListener<String>() {
                    @Override
                    public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                        if (!(alignmentOptions.disabledProperty().getValue()) && alignmentOptions.getSelectionModel().getSelectedItem() != null) {
                            updateCurrentNode.setValue(false);
                            alignNode.setValue(true);
                            System.out.println("User would like to align nodes.");
                            cancelBtn.disableProperty().setValue(true);
                            confirmBtn.disableProperty().setValue(true);
                            //alignBtn.setDisable(true);
                        } else {
                            updateCurrentNode.setValue(true);
                            cancelBtn.disableProperty().setValue(false);
                            confirmBtn.disableProperty().setValue(false);

                            alignNode.setValue(false);
                        }
                    }
                });

        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        editorGroup = new ToggleGroup();
        editNodes.setToggleGroup(editorGroup);
        editEdges.setToggleGroup(editorGroup);


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

        editEdges.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if (newValue) {
                    editorInfo.getChildren().clear();
                    edgeEditor.setValue(true);
                    FXMLLoader loader = new FXMLLoader();
                    loader.setLocation(getClass().getResource("/EdgeMapEditorNew.fxml"));
                    loader.setController(this);
                    try {
                        Pane node = loader.load();
                        editorInfo.getChildren().add(node);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    edgeEditorListenerID = masterMap.attachClickedListener(onNodeClickEdges, ClickedListener.NODECLICKED);
                    mouseEvents.put(edgeEditorListenerID, onNodeClickEdges);
                } else {
                    edgeEditor.setValue(false);
                    editorInfo.getChildren().clear();
                    editorInfo.getChildren().addAll(nodeDetails, alignNodes, actionButtons, finishButtons);
                    mapDraw.detachListener(edgeEditorListenerID);
                    mouseEvents.remove(edgeEditorListenerID);
                }
            }
        });

        editNodes.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if (newValue && newValue != oldValue) {
                    nodeEditor.setValue(true);
                    editorInfo.getChildren().clear();
                    editorInfo.getChildren().addAll(nodeDetails, alignNodes, actionButtons, finishButtons);
                    alignmentOptions.disableProperty().setValue(false);
                    //alignBtn.disableProperty().setValue(false);
                    nodeEditorListenerID = masterMap.attachClickedListener(onLocClickEditNodes, ClickedListener.LOCCLICKED);
                    mouseEvents.put(nodeEditorListenerID, onLocClickEditNodes);
                } else {
                    nodeEditor.setValue(false);
                    alignmentOptions.disableProperty().setValue(true);
                    alignBtn.disableProperty().setValue(true);
                    mapDraw.detachListener(nodeEditorListenerID);
                    mouseEvents.remove(nodeEditorListenerID);
                }
            }
        });
        //mouseEvents.put(masterMap.attachClickedListener(onNodeClick, ClickedListener.NODECLICKED), onNodeClick);
    }

    private MapDrawingSubsystem masterMap;
    private MapSubsystem mapData;

    @Override
    public void onOpen(ReadOnlyDoubleProperty xProperty, int xOffset, ReadOnlyDoubleProperty yProperty, int yOffset) {


        this.xOffset = xOffset;
        this.yOffset = yOffset;
        this.xProperty = xProperty;
        this.yProperty = yProperty;

        masterMap = MapDrawingSubsystem.getInstance();
        mapData = MapSubsystem.getInstance();

        floorEvents.put(masterMap.attachFloorChangeListener(onFloorChange), onFloorChange);

    }

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


    private void clearTextFieldsNodes() {

        nodeID.setText(nodeID.getPromptText());
        longName.setText(longName.getPromptText());
        shortName.setText(shortName.getPromptText());
        nodeType.setDisable(false);
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


    EventHandler<MouseEvent> onLocClickEditNodes = new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent event) {
            //TODO: GENERATE EDIT/ADD POPUP
            //double xCanv = event.getX();
            //double yCanv = event.getY();
            Location selected = mouseClickToLocation(event);
            MapNode clickedNode = masterMap.nodeAt(selected);
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

    private MapNode startNodeEdge = null;
    private MapNode endNodeEdge = null;

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

    private void closePopUp() {
        if (currentPopOut != null && masterMap.getAreaPane().getChildren().contains(currentPopOut)) {
            masterMap.getAreaPane().getChildren().remove(currentPopOut);
            currentPopOut = null;
        }
    }

    @Override
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
        System.out.println(alignmentNode.getId());
        if (alignmentOptions.getSelectionModel().getSelectedItem() != null && alignmentNode != null && selectedNode != null) {
            String direction = alignmentOptions.getSelectionModel().getSelectedItem();

            if (direction.equals("X")) {
                MapNode toUpdate = new MapNodeData(selectedNode.getId(), new Location(selectedNode.getCoordinate().getxCoord(),
                        alignmentNode.getCoordinate().getyCoord(), selectedNode.getCoordinate().getLevel(), selectedNode.getCoordinate().getBuilding()),
                        selectedNode.getNodeType(), selectedNode.getLongDescription(), selectedNode.getShortDescription(), selectedNode.getTeamAssignment(), selectedNode.getEdges());
                mapDraw.unDrawNode(selectedNode);
                mapDraw.drawNode(toUpdate, 5, Color.DARKBLUE);
                mapData.addNode(toUpdate);
                //mapData.deleteNode(selectedNode.getId());


                //mapData.addNode(selectedNode);
            }
            else if (direction.equals("Y")) {
                MapNode toUpdate = new MapNodeData(selectedNode.getId(), new Location(alignmentNode.getCoordinate().getxCoord(),
                        selectedNode.getCoordinate().getyCoord(), selectedNode.getCoordinate().getLevel(), selectedNode.getCoordinate().getBuilding()),
                        selectedNode.getNodeType(), selectedNode.getLongDescription(), selectedNode.getShortDescription(), selectedNode.getTeamAssignment(), selectedNode.getEdges());
                mapDraw.unDrawNode(selectedNode);
                mapDraw.drawNode(toUpdate, 5, Color.DARKBLUE);
                mapData.addNode(toUpdate);
                //mapData.deleteNode(selectedNode.getId());


            }

        }
        clearTextFieldsNodes();
        alignmentNode = null;
        alignBtn.disableProperty().setValue(true);
        alignmentOptions.getSelectionModel().clearSelection();
        updateCurrentNode.setValue(true);
    }


    @FXML
    void onConfirm(ActionEvent e) {
        updateCurrentNode.setValue(true);

    }

    @FXML
    void onCancel(ActionEvent e) {
        updateCurrentNode.setValue(true);

    }

    @FXML
    void onAddNode(ActionEvent e) {
        updateCurrentNode.setValue(false);
        addNode.setVisible(false);
        editNode.setVisible(false);
        deleteNode.setVisible(false);
        nodeType.setDisable(false);
        confirmBtn.setDisable(false);
        cancelBtn.setDisable(false);
        confirmBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (selectedLocation != null) {
                        /*
                        MapNode toAdd = new MapNodeData(nodeID.getText(), new Location(selectedLocation.getxCoord(), selectedLocation.getyCoord(), masterMap.getCurrentFloor(), ""),
                                nodeType.getSelectionModel().getSelectedItem(), longName.getText(), shortName.getText(), "A");
                                */
                    MapNode toAdd = nodeFieldsToNode();
                    if (mapData.addNode(toAdd) != null) {
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
        cancelBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                clearTextFieldsNodes();
            }
        });
    }

    private MapNode nodeFieldsToNode() {
        MapNode created = new MapNodeData(nodeID.getText(), new Location(selectedLocation.getxCoord(), selectedLocation.getyCoord(), masterMap.getCurrentFloor(), ""),
                nodeType.getSelectionModel().getSelectedItem(), longName.getText(), shortName.getText(), "A");
        if (created != null) {
            System.out.println("jawieor");
        }
        return created;
    }

    @FXML
    void onEditNode(ActionEvent e) {
        updateCurrentNode.setValue(false);
        addNode.setVisible(false);
        editNode.setVisible(false);
        deleteNode.setVisible(false);
        nodeType.setDisable(false);
        confirmBtn.setDisable(false);
        cancelBtn.setDisable(false);
        confirmBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (selectedNode != null) {
                    MapNode update = nodeFieldsToNode();
                    MapSubsystem.getInstance().addNode(update);
                }
            }
        });
        cancelBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                clearTextFieldsNodes();

            }
        });
    }

    @FXML
    void onDeleteNode(ActionEvent e) {
        updateCurrentNode.setValue(false);
        updateCurrentNode.setValue(false);
        addNode.setVisible(false);
        editNode.setVisible(false);
        deleteNode.setVisible(false);
        nodeType.setDisable(false);
        confirmBtn.setDisable(false);
        cancelBtn.setDisable(false);

        confirmBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (selectedNode != null) {
                    for (MapEdge e : selectedNode.getEdges()) {
                        mapData.deleteEdge(e.getId());
                        mapDraw.unDrawEdge(e);
                    }
                    mapData.deleteNode(selectedNode.getId());
                    mapDraw.unDrawNode(selectedNode);
                }
            }
        });
        cancelBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                clearTextFieldsNodes();
            }
        });
    }


    @Override
    public String getFXMLPath() {
        return "/EditorPopOut.fxml";
    }

}
