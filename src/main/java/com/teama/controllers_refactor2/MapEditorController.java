package com.teama.controllers_refactor2;

import com.jfoenix.controls.*;
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
import javafx.event.Event;
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
    private JFXButton alignBtn, addNode, deleteNode, addEdge, deleteEdge;
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
    private HashMap<Tab, MapNode> selectedNodes = new HashMap<>();
    private HashMap<Tab, String> selectedLocations = new HashMap<>();

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

    //



    private MapDrawingSubsystem masterMap;
    private MapSubsystem mapData;


    @FXML
    public void initialize() {
        mapDraw = MapDrawingSubsystem.getInstance();
        //alignPane(xProperty, xOffset, yProperty, yOffset);
        masterMap = MapDrawingSubsystem.getInstance();
        mapData = MapSubsystem.getInstance();


        addEdge = new JFXButton();
        deleteEdge = new JFXButton();
        addEdge.setText("Add Edge");
        deleteEdge.setText("Delete Edge");
        actionButtons.setVisible(false);

        //scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        floorEvents.put(masterMap.attachFloorChangeListener(onFloorChange), onFloorChange);

        masterTabNodes.setTabClosingPolicy(TabPane.TabClosingPolicy.ALL_TABS);

        editorGroup = new ToggleGroup();
        editNodes.setToggleGroup(editorGroup);
        editEdges.setToggleGroup(editorGroup);
       // addNode.disableProperty().bind(knownLoc);
        //editNode.disableProperty().bind(unknownLoc);
        //deleteNode.disableProperty().bind(unknownLoc);
//        alignmentOptions.disableProperty().bind(unknownLoc);
       // alignBtn.disableProperty().bind(unknownLoc);

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
                if (mouseEvents != null) {
                    for (long id : mouseEvents.keySet()) {
                        masterMap.detachListener(id);
                    }
                    mouseEvents = null;
                }
                if (newValue != null) {
                    if (mouseEvents == null) {
                        mouseEvents = new HashMap<Long, EventHandler<MouseEvent>>();
                    }
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
                        long id = masterMap.attachClickedListener(onEdgeClicked, ClickedListener.EDGECLICKED);
                        mouseEvents.put(id, onEdgeClicked);
                        onLocClickID = masterMap.attachClickedListener(onRandomLocClick, ClickedListener.LOCCLICKED);
                        mouseEvents.put(onLocClickID, onRandomLocClick);
                        onNodeClickID = masterMap.attachClickedListener(onNodeClick, ClickedListener.NODECLICKED);
                        mouseEvents.put(onNodeClickID, onNodeClick);
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
        });}




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
    private void setUpButtonsForNodes() {
        //actionButtons.getChildren().remove(addEdge);
        //actionButtons.getChildren().remove(deleteEdge);
        actionButtons.add(addNode, 0, 0);
        actionButtons.add(alignBtn, 1, 0);
        actionButtons.add(deleteNode, 2, 0);
        actionButtons.setHalignment(addNode, HPos.CENTER);
        actionButtons.setHalignment(alignBtn, HPos.CENTER);
        actionButtons.setHalignment(deleteNode, HPos.CENTER);
        addNode.setAlignment(Pos.CENTER);
        alignBtn.setAlignment(Pos.CENTER);
        deleteNode.setAlignment(Pos.CENTER);
        addNode.setDisable(true);
        alignBtn.setDisable(true);
        deleteNode.setDisable(true);

    }

    private void setUpButtonsForEdges() {
        //actionButtons.getChildren().remove(addNode);
        //actionButtons.getChildren().remove(deleteNode);
        //actionButtons.getChildren().remove(alignBtn);
        actionButtons.add(deleteEdge, 0, 0);
        actionButtons.add(addEdge, 2, 0);
        System.out.println(actionButtons.getRowConstraints().size());
        //actionButtons.getChildren().add(2, addEdge);
        actionButtons.setHalignment(deleteEdge, HPos.CENTER);
        actionButtons.setHalignment(addEdge, HPos.CENTER);
        deleteEdge.setAlignment(Pos.CENTER);
        addEdge.setAlignment(Pos.CENTER);
        deleteEdge.setDisable(true);
        addEdge.setDisable(true);



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

    private void clearTextFieldsNodes() {

        nodeID.clear();
        longName.clear();
        shortName.clear();
        //nodeType.setDisable(false);
        nodeType.getSelectionModel().clearSelection();
        nodeType.setDisable(true);
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
            // TODO
            Location previouslySelected = selectedLocation.getValue();
            Location selected = mouseClickToLocation(event);

            if (previouslySelected == null ||
                    (previouslySelected.getxCoord() != selected.getxCoord() && previouslySelected.getyCoord() != selected.getyCoord()))
            {
                NodesController node = new NodesController(masterTabNodes, nodeEditor, edgeEditor, selectedLocations, selectedNodes);
                Tab newTab = generateTab(node);
                node.setParentTab(newTab);

                selectedLocation.set(selected);
                newTab.setText("New Node");
                node.setLocation(selected);
                node.setOriginalLocClick(onRandomLocClick, onLocClickID);
                node.setOriginalNodeClick(onNodeClick, onNodeClickID);


            }
            /*
            String drawnID = generateDrawnIDForNode();
            if (selectedLocations == null) {
                selectedLocations = new HashMap<Tab, String>();
            }
            selectedLocations.put(newTab, drawnID);
            */
            //masterMap.drawNewLocation(selected, 5, Color.GREEN, drawnID);
        }
    };

    EventHandler<MouseEvent> onNodeClick = new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent event) {
            Location selected = mouseClickToLocation(event);
            MapNode clickedNode = masterMap.nodeAt(selected);
            MapNode previouslyClicked = selectedNode.getValue();
            if (previouslyClicked == null ||
                    (!previouslyClicked.getId().equals(clickedNode.getId()))) {
                NodesController node = new NodesController(masterTabNodes, nodeEditor, edgeEditor, selectedLocations, selectedNodes);
                Tab newTab = generateTab(node);
                node.setParentTab(newTab);


                selectedNode.set(clickedNode);
                newTab.setText(clickedNode.getId());
                node.setNodeInfo(clickedNode);
                node.setOriginalLocClick(onRandomLocClick, onLocClickID);
                node.setOriginalNodeClick(onNodeClick, onNodeClickID);

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
                System.out.println("Found an edge!");
                Location mouseLoc = new Location(event, masterMap.getCurrentFloor());
                MapEdge foundEdge = masterMap.edgeAt(mouseLoc);
                masterMap.drawEdge(foundEdge, 5, Color.RED, false);

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
            // TODO

        }

        ;




        @FXML
        void onEditNode(ActionEvent e) {
            updateFields.setValue(false);
            if (selectedNode.getValue() != null) {
                //addNode.setVisible(false);
                //editNode.setVisible(false);
                //deleteNode.setVisible(false);
                nodeType.setDisable(false);
                //confirmBtn.setDisable(false);
                //cancelBtn.setDisable(false);
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
                for (Tab t : selectedNodes.keySet()) {
                    MapNode m = selectedNodes.get(t);
                    for (MapEdge edge : m.getEdges()) { // clean up edges
                        mapData.deleteEdge(edge.getId());
                        mapDraw.unDrawEdge(edge);
                    }
                    mapData.deleteNode(m.getId());
                    mapDraw.unDrawNode(m);

                }
            }
            selectedNodes = null;

            clearTextFieldsNodes();

        }


        public String getFXMLPath() {
            return "/MainScreenDrawers/MapEditorNoTab.fxml";
        }
    }
