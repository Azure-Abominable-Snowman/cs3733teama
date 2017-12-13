package com.teama.controllers_refactor2;

import com.jfoenix.controls.*;
import com.teama.mapdrawingsubsystem.ClickedListener;
import com.teama.mapdrawingsubsystem.MapDrawingSubsystem;
import com.teama.mapsubsystem.MapSubsystem;
import com.teama.mapsubsystem.data.*;
import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class MapEditorController extends StaffToolController  {
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
    private JFXToggleButton addEdgeOption;

    @FXML
    private JFXComboBox<NodeType> nodeType;
    @FXML
    private JFXComboBox<Floor> floorSelect;
    @FXML
    private JFXComboBox<String> alignmentOptions;
    @FXML
    private JFXButton alignBtn, addNode, deleteNode, addEdge, deleteEdge, cancelEdge;
    @FXML
    private VBox editorInfo, masterBox;
    @FXML
    private GridPane nodeDetails, actionButtons, toolToggles;
    @FXML
    private JFXTabPane masterTabNodes;

    private BooleanProperty detachParentListeners = new SimpleBooleanProperty(false);
    // private Parent currentPopOut;
    private ToggleGroup editorGroup;

    private Map<Long, EventHandler<MouseEvent>> mouseEvents = new HashMap<>();
    private Map<Long, ChangeListener<Boolean>> floorEvents = new HashMap<>();
    private HashMap<Tab, MapNode> selectedNodes = new HashMap<>();
    private HashMap<Tab, String> selectedLocations = new HashMap<>();
    private HashMap<Tab, NodesController> controllers = new HashMap<>();

    BooleanProperty edgeEditor = new SimpleBooleanProperty(false);
    BooleanProperty nodeEditor = new SimpleBooleanProperty(false);

    // NEW
    private BooleanProperty unknownLoc = new SimpleBooleanProperty();
    private BooleanProperty knownLoc = new SimpleBooleanProperty();
    private BooleanProperty updateFields = new SimpleBooleanProperty(true);
    private BooleanProperty isAligning = new SimpleBooleanProperty(false);

    private ObjectProperty<Location> selectedLocation = new SimpleObjectProperty<Location>();
    private ObjectProperty<MapNode> selectedNode = new SimpleObjectProperty<MapNode>();

    private long onLocClickID;
    private long onNodeClickID;
    private long onEdgeClickID;

    // EDGES
    private SimpleObjectProperty<MapNode> startNode = new SimpleObjectProperty<>(null);
    private SimpleObjectProperty<MapNode> endNode = new SimpleObjectProperty<>(null);




    private MapDrawingSubsystem masterMap;
    private MapSubsystem mapData;

    private SimpleObjectProperty<MapEdge> selectedEdge = new SimpleObjectProperty(null);
    private SimpleStringProperty newDrawEdgeID = new SimpleStringProperty("newEdge");





    @FXML
    public void initialize() {
        mapDraw = MapDrawingSubsystem.getInstance();
        //alignPane(xProperty, xOffset, yProperty, yOffset);
        masterMap = MapDrawingSubsystem.getInstance();
        mapData = MapSubsystem.getInstance();


        addEdge = new JFXButton();
        deleteEdge = new JFXButton();
        cancelEdge = new JFXButton();
        addEdge.setText("Add Edge");
        deleteEdge.setText("Delete Edge");
        cancelEdge.setText("Cancel");
        actionButtons.setVisible(false);

        cancelEdge.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                masterTabNodes.getTabs().clear();
                if (selectedEdge.getValue()!= null) {
                    masterMap.unDrawEdge(selectedEdge.get());
                    if (viewEdges.isSelected()) {
                        masterMap.drawEdge(selectedEdge.get(), 5, Color.LIGHTBLUE, false);
                    }

                }
                masterMap.unDrawNewEdge(newDrawEdgeID.getName());
                startNode.set(null);
                endNode.set(null);
                selectedEdge.set(null);

            }
        });

        //scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        floorEvents.put(masterMap.attachFloorChangeListener(onFloorChange), onFloorChange);
        detachParentListeners.addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if (newValue) {
                    masterMap.detachListener(onLocClickID);
                    masterMap.detachListener(onNodeClickID);
                    mouseEvents.remove(onLocClickID);
                    mouseEvents.remove(onNodeClickID);
                }
                else if (!newValue) {
                    onLocClickID = masterMap.attachClickedListener(onRandomLocClick, ClickedListener.LOCCLICKED);

                    onNodeClickID = masterMap.attachClickedListener(onNodeClick, ClickedListener.NODECLICKED);
                    mouseEvents.put(onLocClickID, onRandomLocClick);
                    mouseEvents.put(onNodeClickID, onNodeClick);
                    System.out.println("Detached the parent listeners.");
                }
            }
        });
        addEdgeOption.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if (addEdgeOption.isSelected()) {
                    System.out.println("Adding a new edge.");
                    addEdge.setVisible(true);
                    deleteEdge.setVisible(false);
                    if (onEdgeClickID != -1) {
                        mapDraw.detachListener(onEdgeClickID);
                        onEdgeClickID = -1;
                        mouseEvents.remove(onEdgeClickID);
                    }
                    onNodeClickID = mapDraw.attachClickedListener(onNodeClick, ClickedListener.NODECLICKED);
                    mouseEvents.put(onNodeClickID, onNodeClick);

                }
                else if (!addEdgeOption.isSelected()) { // no longer adding a new edge
                    System.out.println("Deleting old edges.");
                    addEdge.setVisible(false);
                    deleteEdge.setVisible(false);
                    if (onNodeClickID != -1) {
                        mapDraw.detachListener(onNodeClickID);
                        onNodeClickID = -1;
                        mouseEvents.remove(onNodeClickID);
                    }
                    onEdgeClickID = mapDraw.attachClickedListener(onEdgeClicked, ClickedListener.EDGECLICKED);
                    mouseEvents.put(onEdgeClickID, onEdgeClicked);
                }
            }
        });

        addEdgeOption.visibleProperty().bind(editEdges.selectedProperty());

        masterTabNodes.getTabs().addListener(new ListChangeListener<Tab>() {
            @Override
            public void onChanged(Change<? extends Tab> c) {
                if (masterTabNodes.getTabs().isEmpty()) {
                    if (edgeEditor.getValue()) {
                        disableEdgeButtons();

                    }
                    else if (nodeEditor.getValue()) {
                        disableNodeButtons();
                    }
                }
                else {
                    if (edgeEditor.getValue()) {
                        if (masterTabNodes.getTabs().size() == 2) {
                            enableEdgeButtons();

                        }
                        else {
                            disableEdgeButtons();
                        }
                    }
                }
            }
        });


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
        cancelEdge.visibleProperty().bind(editEdges.selectedProperty());

        editorGroup.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
            @Override
            public void changed(ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) {
                if (!mouseEvents.keySet().isEmpty()) {
                    for (long id : mouseEvents.keySet()) {
                        masterMap.detachListener(id);
                    }
                    mouseEvents = new HashMap<Long, EventHandler<MouseEvent>>();
                    onLocClickID = -1;
                    onEdgeClickID = -1;
                    onNodeClickID = -1;
                }
                if (newValue != null) {
                    actionButtons.getChildren().clear();
                    actionButtons.setVisible(true);
                    if (editNodes.isSelected()) {

                        nodeEditor.setValue(true);
                        edgeEditor.setValue(false);
                        masterTabNodes.getTabs().clear();
                        onLocClickID = masterMap.attachClickedListener(onRandomLocClick, ClickedListener.LOCCLICKED);
                        mouseEvents.put(onLocClickID, onRandomLocClick);
                        onNodeClickID = masterMap.attachClickedListener(onNodeClick, ClickedListener.NODECLICKED);
                        mouseEvents.put(onNodeClickID, onNodeClick);

                        setUpButtonsForNodes();
                        System.out.println("Selecting nodes for editing and such.");

                    } else if (editEdges.isSelected()) {

                        nodeEditor.setValue(false);
                        edgeEditor.setValue(true);
                        masterTabNodes.getTabs().clear();
                        if (addEdgeOption.isSelected()) {
                            onNodeClickID = masterMap.attachClickedListener(onNodeClick, ClickedListener.NODECLICKED);
                            mouseEvents.put(onNodeClickID, onNodeClick);
                        }
                        else {
                            onEdgeClickID = masterMap.attachClickedListener(onEdgeClicked, ClickedListener.EDGECLICKED);
                            mouseEvents.put(onEdgeClickID, onEdgeClicked);
                            for (Tab t: controllers.keySet()) {
                                NodesController n = controllers.get(t);
                                n.onCloseTab();
                            }
                        }


                        setUpButtonsForEdges();

                        System.out.println("Editing edges.");
                    }

                }
                else {
                    System.out.println("Not editing anything.");
                    nodeEditor.setValue(false);
                    edgeEditor.setValue(false);
                    actionButtons.setVisible(false);
                }
            }
        });



        deleteEdge.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                //  // TODO: 12/12/2017
                if (masterTabNodes.getTabs().size() == 2) {
                    if (startNode.get() != null && endNode.get() != null && selectedEdge.get() != null) {
                        mapData.deleteEdge(selectedEdge.get().getId());

                        mapDraw.unDrawEdge(selectedEdge.get());

                    }
                    selectedEdge.set(null);
                    startNode.set(null);
                    endNode.set(null);
                    disableEdgeButtons();
                    masterTabNodes.getTabs().clear();

                }
                else {
                    Alert error = new Alert(Alert.AlertType.ERROR);
                    error.setTitle("Edge Deletion Failure");
                    error.setHeaderText("No edge selected");
                    error.setContentText("Select an existing edge to delete it.");

                    ButtonType ok = new ButtonType("OK");
                    enableEdgeButtons();
                }

            }

        });

        addEdge.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if(masterTabNodes.getTabs().size() == 2) {
                    if (startNode.getValue() != null && endNode.getValue() != null) {
                        MapEdge toAdd = new MapEdgeData(startNode.get().getId() + "_" + endNode.get().getId(), startNode.get(), endNode.get());
                        if (toAdd != null) {
                            mapData.addEdge(toAdd);
                            mapDraw.drawEdge(toAdd, 5, Color.LIGHTBLUE, false);
                            mapDraw.unDrawNewEdge(newDrawEdgeID.get());
                            if (!viewEdges.selectedProperty().get()) {
                                mapDraw.unDrawEdge(toAdd);
                            }
                        }
                    }
                    selectedEdge.set(null);
                    startNode.set(null);
                    endNode.set(null);
                    masterTabNodes.getTabs().clear();
                    disableEdgeButtons();
                }
            }
        });

        selectedEdge.addListener((observable, oldValue, newValue) -> {
            if (newValue == null) {
                        deleteEdge.setVisible(false);
                        addEdge.setVisible(true);
                    } else if (newValue != null) {
                        deleteEdge.setVisible(true);
                        addEdge.setVisible(false);
                    }
                });


    }





/*
    @Override
    public void onOpen(ReadOnlyDoubleProperty xProperty, int xOffset, ReadOnlyDoubleProperty yProperty, int yOffset) {


        this.xOffset = xOffset;
        this.yOffset = yOffset;
        this.xProperty = xProperty;
        this.yProperty = yProperty;




    }
    /*
    assume actionButtons gridpane is already cleared
     */
    EventHandler<MouseEvent> onDelete = new EventHandler<MouseEvent>() {
    @Override
    public void handle(MouseEvent event) {
        if (masterTabNodes.getTabs().size() == 2) {
            if (startNode.get() != null && endNode.get() != null && selectedEdge.get() != null) {
                mapData.deleteEdge(selectedEdge.get().getId());
                mapDraw.unDrawEdge(selectedEdge.get());
            }
            selectedEdge.set(null);
            startNode.set(null);
            endNode.set(null);
        }


    }
};
    private void setUpButtonsForNodes() {
        actionButtons.add(addNode, 2, 0);
        actionButtons.add(alignBtn, 1, 0);
        actionButtons.add(deleteNode, 0, 0);
        actionButtons.setHalignment(addNode, HPos.CENTER);
        actionButtons.setHalignment(alignBtn, HPos.CENTER);
        actionButtons.setHalignment(deleteNode, HPos.CENTER);
        addNode.setAlignment(Pos.CENTER);
        alignBtn.setAlignment(Pos.CENTER);
        deleteNode.setAlignment(Pos.CENTER);
        disableNodeButtons();

    }

    private void setUpButtonsForEdges() {
        actionButtons.add(cancelEdge, 0, 0);
        actionButtons.add(deleteEdge, 1, 0);
        actionButtons.add(addEdge, 2, 0);
        System.out.println(actionButtons.getRowConstraints().size());
        actionButtons.setHalignment(cancelEdge, HPos.CENTER);
        actionButtons.setHalignment(deleteEdge, HPos.CENTER);
        actionButtons.setHalignment(addEdge, HPos.CENTER);
        deleteEdge.setAlignment(Pos.CENTER);
        addEdge.setAlignment(Pos.CENTER);
        disableEdgeButtons();
    }

    private void disableNodeButtons() {
        addNode.setDisable(true);
        alignBtn.setDisable(true);
        deleteNode.setDisable(true);
    }
    private void enableNodeButtons() {
        addNode.setDisable(false);
        alignBtn.setDisable(false);
        deleteNode.setDisable(false);
    }

    private void disableEdgeButtons() {
        deleteEdge.setDisable(true);
        addEdge.setDisable(true);
        cancelEdge.setDisable(false);
    }
    private void enableEdgeButtons() {
        deleteEdge.setDisable(false);
        addEdge.setDisable(false);
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
    private void resetToDefault() {
    }


    private Location mouseClickToLocation(MouseEvent e) {
        double xCanv = e.getX();
        double yCanv = e.getY();
        Location selected = new Location(e, masterMap.getCurrentFloor());
        return selected;
    }

    private Tab generateTab(NodesController node) {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/MainScreenDrawers/nodeTab.fxml"));
        Tab nodeTab = new Tab();
        /*
        nodeTab.onClosedProperty().set(new EventHandler<Event>() {
            @Override
            public void handle(Event event) {
                if (selectedNodes != null) {
                    MapNode unselected = selectedNodes.get(nodeTab);
                    mapDraw.unDrawNode(selectedNodes.get(nodeTab));
                    mapDraw.drawNode(unselected, 5, Color.DARKBLUE);
                    System.out.println(selectedNodes.size());
                    selectedNodes.remove(nodeTab);
                    System.out.println(selectedNodes.size());
                }
                if (selectedLocations != null) {
                    String id = selectedLocations.get(nodeTab);
                    masterMap.unDrawNewLocation(id);
                    selectedLocations.remove(nodeTab);
                }
            }
        });
        */
        loader.setController(node);
        try {
            AnchorPane tabContents = loader.load();
            nodeTab.setContent(tabContents);
            masterTabNodes.getTabs().add(nodeTab);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return nodeTab;
    }
    private String generateDrawnIDForNode() {
        int length = selectedLocations.size();
        String id = "Selected" + length;
        System.out.println(id);
        return id;
    }
    EventHandler<MouseEvent> onRandomLocClick = new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent event) {
            // disappears? what's going on here
            Location previouslySelected = selectedLocation.getValue();
            Location selected = mouseClickToLocation(event);

            if (previouslySelected == null ||
                    (previouslySelected.getxCoord() != selected.getxCoord() && previouslySelected.getyCoord() != selected.getyCoord()))
            {
                NodesController node = new NodesController(masterTabNodes, nodeEditor, edgeEditor, detachParentListeners, selectedLocations, selectedNodes);
                Tab newTab = generateTab(node);
                // generate new tab
                controllers.put(newTab,node);

                node.setParentTab(newTab, controllers);
                if (nodeEditor.getValue()) {
                    enableNodeButtons();
                }
                System.out.println(controllers.size());

                selectedLocation.set(selected);
                newTab.setText("New Node");
                System.out.println("Adding a new tab.");
                node.setLocation(selected);
                //node.setOriginalLocClick(onRandomLocClick, onLocClickID);
                //node.setOriginalNodeClick(onNodeClick, onNodeClickID);



            }
        }
    };



    EventHandler<MouseEvent> onNodeClick = new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent event) {
            Location selected = mouseClickToLocation(event);
            selectedEdge.setValue(null);

            MapNode clickedNode = masterMap.nodeAt(selected);
            MapNode previouslyClicked = selectedNode.getValue();
            if (previouslyClicked == null ||
                    (!previouslyClicked.getId().equals(clickedNode.getId()))) {
                NodesController node = new NodesController(masterTabNodes, nodeEditor, edgeEditor, detachParentListeners, selectedLocations, selectedNodes);


                if (nodeEditor.getValue()) {
                    Tab newTab = generateTab(node);
                    node.setParentTab(newTab, controllers);
                    enableNodeButtons();
                    selectedNode.set(clickedNode);
                    newTab.setText(clickedNode.getId());
                    System.out.println("adding a new tab");
                    node.setNodeInfo(clickedNode);
                    controllers.put(newTab,node);

                    node.setParentTab(newTab, controllers);
                    // node.setOriginalLocClick(onRandomLocClick, onLocClickID);
                    //node.setOriginalNodeClick(onNodeClick, onNodeClickID);
                }
                else if (edgeEditor.getValue()) {
                    System.out.println("Editing edges. Adding a new edge");
                    disableEdgeButtons();
                    System.out.println(startNode.get());
                    System.out.println(endNode.get());
                    if (masterTabNodes.getTabs().size() < 2) {
                        Tab newTab = generateTab(node);
                        node.setParentTab(newTab, controllers);
                        System.out.println("Can add another tab.");
                        if (previouslyClicked == null ||
                                (!previouslyClicked.getId().equals(clickedNode.getId()))) {
                            if (previouslyClicked != null) {
                                mapDraw.unDrawNode(previouslyClicked);
                                mapDraw.drawNode(previouslyClicked, 5, Color.DARKBLUE);
                            }
                            if (startNode.getValue() == null) {
                                addEdge.disableProperty().setValue(true);
                                startNode.setValue(clickedNode);
                                newTab.setText("Start Node");
                                System.out.println("User selected a start node.");

                            } else if (endNode.getValue() == null) {
                                endNode.setValue(clickedNode);
                                newTab.setText("End Node");
                                System.out.println("User selected an end node.");
                                addEdge.disableProperty().setValue(false);
                                mapDraw.drawNewEdge(newDrawEdgeID.get(), startNode.get(), endNode.get(), 5, Color.LIGHTGREEN, false);


                            }
                            /*
                            else {
                                mapDraw.unDrawNode(startNode.get());
                                mapDraw.unDrawNode(endNode.get());
                                mapDraw.drawNode(startNode.get(), 5, Color.DARKBLUE);
                                mapDraw.drawNode(endNode.get(), 5, Color.DARKBLUE);
                                startNode.setValue(clickedNode);
                                endNode.setValue(null);
                                mapDraw.unDrawNewEdge(newDrawEdgeID.getName());
                            }
                            */
                            //mapDraw.drawNode(clickedNode, 5, Color.GREEN);
                            node.setNodeInfo(clickedNode);
                            controllers.put(newTab,node);

                            node.setParentTab(newTab, controllers);

                        }
                    }
                }

                System.out.println(controllers.size());



            }


            /*
            if (selectedNodes == null) {
                selectedNodes = new HashMap<Tab, MapNode>();
            }
            selectedNodes.put(newTab, clickedNode);
            masterMap.unDrawNode(clickedNode);
            masterMap.drawNode(clickedNode, 5, Color.GREEN);
            */
        }
    };





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




        EventHandler<MouseEvent> onNodeClickAndDrag = new EventHandler<MouseEvent>() {
            @Override

            public void handle(MouseEvent event) {
//TODO, MAYBE, IF TIME
            }
        };

        private EventHandler<MouseEvent> onEdgeClicked = new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                Location mouseLoc = new Location(event, masterMap.getCurrentFloor());
                MapEdge foundEdge = masterMap.edgeAt(mouseLoc);
                if (startNode.getValue()!= null) {
                    masterMap.unDrawNode(startNode.get());
                    masterMap.drawNode(startNode.get(), 5, Color.DARKBLUE);
                }
                if (endNode.getValue() != null) {
                    masterMap.unDrawNode(endNode.get());
                    masterMap.drawNode(endNode.get(), 5, Color.DARKBLUE);
                }
                MapEdge previouslySelected = selectedEdge.get();
                if (previouslySelected == null || ((previouslySelected.getStart() != foundEdge.getStart() || previouslySelected.getStart() != foundEdge.getEnd()) && (previouslySelected.getEnd() != foundEdge.getEnd() || previouslySelected.getEnd() != foundEdge.getStart())))
                { // found a new edge

                    controllers = new HashMap<Tab, NodesController>();
                    System.out.println("Found an edge!");
                    if (previouslySelected != null) {
                        masterMap.unDrawEdge(previouslySelected);
                        masterMap.drawEdge(previouslySelected, 5, Color.LIGHTBLUE, false);

                        startNode.setValue(null);
                        endNode.setValue(null);
                    }
                    masterMap.drawEdge(foundEdge, 5, Color.RED, false);
                    enableEdgeButtons();
                    masterTabNodes.getTabs().clear();
                    startNode.setValue(foundEdge.getStart());

                    endNode.setValue(foundEdge.getEnd());

                    // make the start tab
                    NodesController nodeStart = new NodesController(masterTabNodes, nodeEditor, edgeEditor, detachParentListeners, selectedLocations, selectedNodes);
                    Tab startTab = generateTab(nodeStart);
                    nodeStart.setParentTab(startTab, controllers);
                    startTab.setText("Start Node");
                    nodeStart.setNodeInfo(startNode.getValue());


                    // make the end tab
                    NodesController nodeEnd = new NodesController(masterTabNodes, nodeEditor, edgeEditor, detachParentListeners, selectedLocations, selectedNodes);
                    Tab endTab = generateTab(nodeEnd);
                    nodeEnd.setParentTab(endTab, controllers);
                    endTab.setText("End Node");
                    nodeEnd.setNodeInfo(endNode.get());
                    selectedEdge.set(foundEdge);
                } else {
                    System.out.println("Clicked on an edge already selected.");
                }


            }
        };

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
            // TODO

        }


        @FXML
        void onAddNode(ActionEvent e) {
            // TODO
            boolean deleteAll = false;
            System.out.println("The controller hashmap is: " + controllers);
            for (Tab newNodeTab: controllers.keySet()) {
                NodesController n =  controllers.get(newNodeTab);
                System.out.println("The Controllers is " + n);

                MapNode added = n.onCompleteAdd();
                if (added == null) {
                    Alert error = new Alert(Alert.AlertType.ERROR);
                    error.setTitle("Node Addition Failure");
                    error.setHeaderText("Illegal fields detected");
                    error.setContentText("Fill in all required fields before proceeding. Click Cancel to close all tabs.");

                    ButtonType ok = new ButtonType("OK");
                    ButtonType cancel = new ButtonType("Cancel");
                    error.getButtonTypes().setAll(ok, cancel);
                    Optional<ButtonType> selectedChoice = error.showAndWait();
                    if (selectedChoice.get() == ok) {
                        addNode.setDisable(false);
                    }
                    else if (selectedChoice.get() == cancel) {
                        deleteAll = true;
                    }
                    break;
                }
                else {
                    deleteAll = true;
                }

            }
            if (deleteAll) {
                for (Tab t: controllers.keySet()) {
                    NodesController node = controllers.get(t);
                    node.onCloseTab();
                }
                masterTabNodes.getTabs().clear();
                if (masterTabNodes.getTabs().isEmpty()) {
                    selectedNodes = new HashMap<Tab, MapNode>();
                    selectedLocations = new HashMap<Tab, String>();
                    controllers = new HashMap<Tab, NodesController>();
                    //deleteNode.disableProperty().setValue(true);
                }
            }



        }

        private void reAttachNodeListeners() {
            onLocClickID = masterMap.attachClickedListener(onRandomLocClick, ClickedListener.LOCCLICKED);

            onNodeClickID = masterMap.attachClickedListener(onNodeClick, ClickedListener.NODECLICKED);
            mouseEvents.put(onLocClickID, onRandomLocClick);
            mouseEvents.put(onNodeClickID, onNodeClick);
        }


        @FXML
        void onDeleteNode(ActionEvent e) { // only deletes existing nodes
            //updateFields.setValue(false);
            //todo: make the alert more visually appealing; make it work
            Alert confirmDelete = new Alert(Alert.AlertType.CONFIRMATION);
            confirmDelete.setTitle("Delete Selected Nodes");
            confirmDelete.setHeaderText("Are you sure you would like to delete the existing nodes?");
            confirmDelete.setContentText("Existing nodes will be permanently deleted from the database. Unsaved data will be discarded.");

            ButtonType confirm = new ButtonType("Confirm");
            ButtonType cancel = new ButtonType("Cancel");
            confirmDelete.getButtonTypes().setAll(confirm, cancel);
            Optional<ButtonType> selectedChoice = confirmDelete.showAndWait();
            if (selectedChoice.get() == confirm) {
                for (Tab t : controllers.keySet()) {
                    if (selectedNodes.containsKey(t)) {
                        MapNode m = selectedNodes.get(t);
                        if (m != null) {
                            for (MapEdge edge : m.getEdges()) { // clean up edges
                                mapData.deleteEdge(edge.getId());
                                mapDraw.unDrawEdge(edge);

                            }
                            NodesController n = controllers.get(t);
                            n.onCloseTab();
                            mapData.deleteNode(m.getId());
                            mapDraw.unDrawNode(m);
                        }
                    }

                    else if (selectedLocations.containsKey(t)) {
                        NodesController n = controllers.get(t);
                        n.onCloseTab(); // just remove the tab from the list
                    }
                }
                masterTabNodes.getTabs().clear();
                if (masterTabNodes.getTabs().isEmpty()) {
                    selectedNodes = new HashMap<Tab, MapNode>();
                    selectedLocations = new HashMap<Tab, String>();
                    controllers = new HashMap<Tab, NodesController>();
                    //deleteNode.disableProperty().setValue(true);
                }
            }
            if (nodeEditor.getValue()) {
                reAttachNodeListeners();
            }
            else if (edgeEditor.getValue()) {
                // TODO
            }


        }




        public String getFXMLPath() {
            return "/MainScreenDrawers/MapEditorNoTab.fxml";
        }
    }
