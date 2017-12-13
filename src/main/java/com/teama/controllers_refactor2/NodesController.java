package com.teama.controllers_refactor2;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTabPane;
import com.jfoenix.controls.JFXTextField;
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
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Tab;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

import java.util.HashMap;
import java.util.Optional;

/**
 * Created by aliss on 12/11/2017.
 */
public class NodesController {
    @FXML
    private JFXTextField nodeID, nodeCoord, longName, shortName;

    @FXML
    private JFXComboBox<NodeType> nodeType;
    @FXML
    private JFXTextField floor;
    @FXML
    private JFXButton editNode;
    @FXML
    private JFXButton clearBtn, closeTab;
    @FXML
    private JFXButton confirmBtn;

    private JFXTabPane parent;
    private BooleanProperty editingNodes, editingEdges;

    private BooleanProperty editNodeClicked = new SimpleBooleanProperty(false);


    private MapSubsystem mapData;
    private MapDrawingSubsystem mapDraw;
    private Tab parentTab;
    private EventHandler<MouseEvent> originalLocClick, originalNodeClick;
    private long originalLocClickID, originalNodeClickID;
    private long newLocClickID = -1;
    private long nodeClickID = -1;




    // for existing nodes
    private ObjectProperty<MapNode> selectedNode = new SimpleObjectProperty<MapNode>();

    private ObjectProperty<Location> newLocation = new SimpleObjectProperty(null); // STORES THE CONVERTED, CHANGED LOCATION
    private BooleanProperty detachParentListeners;

    // FOR NODE EDITOR:
    // for unknown locations
    private ObjectProperty<Location> selectedLocation = new SimpleObjectProperty<Location>(); // STORES THE CONVERTED RANDOMLY-CLICKED LOCATION. only set for random locs.
    private ObjectProperty<MapNode> newNode = new SimpleObjectProperty<MapNode>();


    private HashMap<Tab, String> selectedLocations = new HashMap<>();
    private HashMap<Tab, MapNode> selectedNodes = new HashMap<>();
    private HashMap<Tab, NodesController> controllers;


    // FOR EDGE EDITOR:
    private HashMap<Tab, MapEdge> selectedEdges = new HashMap<>();





    public void initialize() {
        nodeType.getItems().clear();
        nodeType.getItems().addAll(NodeType.values());

        //floorSelect.getItems().clear();
        //floorSelect.getItems().addAll(Floor.values());

        mapData = MapSubsystem.getInstance();
        mapDraw = MapDrawingSubsystem.getInstance();
        floor.disableProperty().setValue(true);

        confirmBtn.visibleProperty().bind(editingNodes);

        editNode.visibleProperty().bind(editingNodes);
        clearBtn.visibleProperty().bind(editingNodes);
        closeTab.visibleProperty().bind(editingNodes);
        confirmBtn.setDisable(true);
        selectedLocation.setValue(null);
        selectedNode.setValue(null);
        newNode.setValue(null);
        startNode.setValue(null);
        endNode.setValue(null);
        nodeID.setEditable(false);
        nodeCoord.editableProperty().setValue(false); // will only be updated through map clicks
        System.out.println(confirmBtn);
        System.out.println(editNode);




        setUnEditable();


    }

    public NodesController(JFXTabPane parentPane, BooleanProperty nodeEditing, BooleanProperty edgeEditing, BooleanProperty detachParentListeners, HashMap<Tab, String> selectedLocs, HashMap<Tab, MapNode> selectedNodes) {
        parent = parentPane;
        this.editingNodes = nodeEditing;
        this.editingEdges = edgeEditing;
        this.selectedLocations = selectedLocs;
        this.selectedNodes = selectedNodes;
        this.detachParentListeners = detachParentListeners;

    }

    public void setParentTab(Tab parentTab, HashMap<Tab, NodesController> controllers) {
        this.parentTab = parentTab;
        this.controllers = controllers;
        parentTab.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if (editingNodes.getValue()) {
                    if (!newValue && editNode.disableProperty().getValue()) {
                        detachListener();
                        setUnEditable();
                    }
                }
            }
        });
    }
    public void setNodeInfo(MapNode clickedNode) {
        nodeID.setText(clickedNode.getId());
        nodeCoord.setText("(" + clickedNode.getCoordinate().getxCoord() + ", " + clickedNode.getCoordinate().getyCoord() + ")");
        floor.setText(mapDraw.getCurrentFloor().toString());
        longName.setText(clickedNode.getLongDescription());
        shortName.setText(clickedNode.getShortDescription());
        nodeType.setValue(clickedNode.getNodeType());
        selectedNode.setValue(clickedNode);
        if (selectedNodes == null) {
            selectedNodes = new HashMap<Tab, MapNode>();
        }
        selectedNodes.put(parentTab, clickedNode);
        if (editingNodes.get()) {
            mapDraw.unDrawNode(clickedNode);
            mapDraw.drawNode(clickedNode, 5, Color.GREEN);
        }
    }

    public void setOriginalLocClick(EventHandler<MouseEvent> h, long id) {
        this.originalLocClick = h;
        this.originalLocClickID = id;
    }
    public void setOriginalNodeClick(EventHandler<MouseEvent> h, long id) {
        this.originalNodeClick = h;
        this.originalNodeClickID = id;
    }

    public void setLocation(Location selected) {
        if (selected != null) {
            Location converted = mapDraw.convertLocationToImgCoords(selected);
            nodeCoord.setText("(" + selected.getxCoord() + ", " + selected.getyCoord() + ")");
            floor.setText(mapDraw.getCurrentFloor().toString());
            selectedLocation.setValue(converted); // STORES THE CONVERTED LOCATION.

            String drawnID = generateDrawnIDForNode();
            if (selectedLocations == null) {
                selectedLocations = new HashMap<Tab, String>();
            }
            selectedLocations.put(parentTab, drawnID);
            System.out.println(selectedLocations);
            mapDraw.drawNewLocation(converted, 5, Color.GREEN, drawnID, false);
        }

    }
    private String generateDrawnIDForNode() {
        int length = selectedLocations.size();
        String id = "Selected" + length;
        System.out.println(id);
        return id;
    }


    public MapNode getNodetoAdd() {
        if (selectedNode.getValue() != null) {
            return selectedNode.getValue();
        }
        MapNode toAdd = newNode.getValue();
        if (toAdd == null) {
            toAdd = createNode();
            if (toAdd == null) {
                //TODO
                //generate alert
                System.out.println("Could not create a new node from the input info.");
            }
        }
        return toAdd;
    }

    private MapNode createNode() {
        MapNode created = null;
        //MapNode toAdd = newNode.getValue();
        if (selectedLocation.getValue() != null) {
            Location loc = null;
            if (newLocation.getValue() != null) {
                loc = newLocation.getValue();
            }
            else {
                loc = selectedLocation.getValue(); // get the converted location
            }
            //Location converted = mapDraw.convertLocationToImgCoords(loc);
            if (loc != null) {
                if (!(nodeID.getText().equals("") || longName.getText().equals("") || nodeType.getSelectionModel().getSelectedItem() == null || shortName.getText().equals(""))) {
                    created = new MapNodeData(nodeID.getText(), new Location(loc.getxCoord(), loc.getyCoord(), Floor.getFloor(floor.getText()), ""),
                            nodeType.getSelectionModel().getSelectedItem(), longName.getText(), shortName.getText(), "A");
                    if (created != null) {
                        newNode.setValue(created);
                    } else {
                        System.out.println("Fields valid but can't create the node.");
                    }
                }
            }
            else {
                System.out.println("Invalid fields. Could not create node.");
            }

        }
        return created;

    }

    private void setUnEditable() {
        nodeType.disableProperty().set(true);
        confirmBtn.setDisable(true);
        clearBtn.setDisable(true);
        nodeID.setEditable(false);
        longName.setEditable(false);
        shortName.setEditable(false);

        editNode.setDisable(false);
        //confirmBtn.setVisible(false);
        //clearBtn.setVisible(false);

    }

    private void setEditable() {
        nodeType.disableProperty().set(false);
        editNode.disableProperty().setValue(true);
        if (selectedLocation.getValue()!= null) {
            nodeID.setEditable(true);
        }
        confirmBtn.setDisable(false);
        clearBtn.setDisable(false);
        longName.setEditable(true);
        shortName.setEditable(true);
        //confirmBtn.setVisible(true);
        //clearBtn.setVisible(true);


    }

    EventHandler<MouseEvent> onLocationChange = new EventHandler<MouseEvent>() { // update a node location on editing nodes
        @Override
        public void handle(MouseEvent event) {
            Location selected = new Location(event, mapDraw.getCurrentFloor());
            Location converted = mapDraw.convertLocationToImgCoords(selected);
            newLocation.setValue(converted); // STORE THE CONVERTED LOCATION
            int newX = converted.getxCoord();
            int newY = converted.getyCoord();
            nodeCoord.setText("(" + newX + ", " + newY + ")");
            if (selectedNode.getValue() != null) {
                System.out.println("Editing the location of an existing node.");
                System.out.println(selectedNode.getValue().getCoordinate().getxCoord() + " " + selectedNode.getValue().getCoordinate().getyCoord());

                MapNode toUpdate = selectedNodes.get(parentTab);
                if (toUpdate != null) {
                    System.out.println(toUpdate.getId());
                    mapDraw.unDrawNode(toUpdate);
                }

                //selectedNodes.put(parentTab, toUpdate);
                mapDraw.drawNewLocation(converted, 5, Color.GREEN, toUpdate.getId(), false);
                System.out.println(selectedNode.getValue().getCoordinate().getxCoord() + " " + selectedNode.getValue().getCoordinate().getyCoord());
            }
            if (selectedLocation.getValue() != null) {
                System.out.println("Editing the location of a new node");
                //selectedLocation.setValue(converted);

                String locID = selectedLocations.get(parentTab);
                mapDraw.unDrawNewLocation(locID);
                mapDraw.drawNewLocation(converted, 5, Color.GREEN, locID, false);
            }
        }
    };

    @FXML
    public void onEditNode(ActionEvent e) {
        setEditable();
        // TODO: update changed location coord  -- user clicks on map to change the coordinate
        //mapDraw.detachListener(originalLocClickID);
        //mapDraw.detachListener(originalNodeClickID);
        detachParentListeners.set(true);
        newLocClickID = mapDraw.attachClickedListener(onLocationChange, ClickedListener.LOCCLICKED);
    }

    private MapNode updateExistingNode() {
        if (selectedNode.getValue() != null) {
            MapNode updated = null;
            if (!(longName.getText().equals("") || nodeType.getSelectionModel().getSelectedItem() == null || shortName.getText().equals(""))) {
                updated = new MapNodeData(selectedNode.getValue().getId(), selectedNode.getValue().getCoordinate(),
                        nodeType.getSelectionModel().getSelectedItem(), longName.getText(), shortName.getText(), selectedNode.getValue().getTeamAssignment());
            }
            else {
                System.out.println("Invalid fields. Could not update existing node.");
            }
            if (updated != null) {
                Location updatedLocation = newLocation.getValue();
                if (updatedLocation != null) {
                    //updatedLocation = mapDraw.convertLocationToImgCoords(updatedLocation);
                    updated.getCoordinate().setxCoord(updatedLocation.getxCoord());
                    updated.getCoordinate().setyCoord(updatedLocation.getyCoord());
                    newLocation.setValue(null);
                }
            }

            return updated;
        }
        return null; // shouldn't happen..
    }
    private void completeUpdate(MapNode updated) {
        System.out.println("Completed update");
        selectedNode.setValue(updated);
        parentTab.setText(nodeID.getText());
        setUnEditable();
        editNode.setDisable(false);
    }
    @FXML
    public void onConfirmClick(ActionEvent e) { // for node editing only
        MapNode updated = null;
        if (selectedNode.getValue() != null) { // updating an existing node
            updated = updateExistingNode(); // gets all the updated text and updates coordinate
        }
        else if (selectedLocation.getValue()!= null) {
            updated = createNode();
        }

        if (updated != null) {
            completeUpdate(updated);
        }
        else {
                // generate alert
            Alert error = new Alert(Alert.AlertType.ERROR);
            error.setTitle("Update Failure");
            error.setHeaderText("Illegal fields detected");
            error.setContentText("Fill in all required fields before proceeding. Click Cancel to close the tab.");

            ButtonType ok = new ButtonType("OK");
            ButtonType cancel = new ButtonType("Cancel");
            error.getButtonTypes().setAll(ok, cancel);
            Optional<ButtonType> selectedChoice = error.showAndWait();
            if (selectedChoice.get() == cancel) {
                onCloseTab();
                if (controllers.containsKey(parentTab)) {
                    controllers.remove(parentTab);
                }
                if (selectedLocations.containsKey(parentTab)) {
                    selectedLocations.remove(parentTab);
                }
                if (selectedNodes.containsKey(parentTab)) {
                    selectedNodes.remove(parentTab);
                }
                parent.getTabs().remove(parentTab);
                parentTab = null;
            } else {
                detachListener();
                setUnEditable();
            }

        }



        //confirmBtn.setDisable(true);
        //clearBtn.setDisable(true);
    }

    private void reset() {
        if (selectedLocation.getValue()!= null) {
            nodeID.clear();
            longName.clear();
            shortName.clear();
            nodeType.getSelectionModel().clearSelection();
            Location selected = selectedLocation.getValue();
            String drawnID = selectedLocations.get(parentTab);
            mapDraw.unDrawNewLocation(drawnID);
            mapDraw.drawNewLocation(selected, 5, Color.GREEN, drawnID, false);
            //selected = mapDraw.convertLocationToImgCoords(selected);
            nodeCoord.setText("(" + selected.getxCoord() + ", " + selected.getyCoord() + ")");

        }
        if (selectedNode.getValue() != null) {
            MapNode selected = selectedNode.getValue();
            nodeID.setText(selected.getId());
            longName.setText(selected.getLongDescription());
            nodeCoord.setText("(" + selected.getCoordinate().getxCoord() + ", " + selected.getCoordinate().getyCoord() + ")");
            shortName.setText(selected.getShortDescription());
            nodeType.setValue(selected.getNodeType());
            mapDraw.unDrawNode(selected);

            mapDraw.drawNode(selected, 5, Color.GREEN);
        }
        newLocation.setValue(null);
        detachListener();

        //originalLocClickID = mapDraw.attachClickedListener(originalLocClick, ClickedListener.LOCCLICKED);
        //originalNodeClickID = mapDraw.attachClickedListener(originalNodeClick, ClickedListener.NODECLICKED);

        setUnEditable();
    }

    @FXML
    public void onResetClick(ActionEvent e) {
        editNode.setDisable(false);
        setUnEditable();
        reset();
    }

    public void onCloseTab() {
        if (editingNodes.getValue()) {
            if (selectedLocation.getValue() != null) {
                System.out.println("Was adding a new node, but decided otherwise.");
                System.out.println(parentTab);
                String id = selectedLocations.get(parentTab);
                System.out.println(id);
                mapDraw.unDrawNewLocation(id);

                //selectedLocations.remove(parentTab);
            } else if (selectedNode.getValue() != null) {
                System.out.println("Was editing existing node, but decided otherwise.");
                System.out.println(parentTab);
                MapNode m = selectedNodes.get(parentTab);
                mapDraw.unDrawNode(m);
                //MapNode original = mapData.getNode(m.getId());
                mapDraw.drawNode(m, 5, Color.BLACK);
                //selectedNodes.remove(parentTab);
            }
            detachListener();


            //controllers.remove(parentTab);
            selectedNode.setValue(null);
            selectedLocation.setValue(null);
            newLocation = null;
        }
        else if (editingEdges.getValue()){
            parent.getTabs().remove(parentTab);
        }

    }
    SimpleObjectProperty<MapNode> startNode = new SimpleObjectProperty<>(null);
    SimpleObjectProperty<MapNode> endNode = new SimpleObjectProperty<>(null);

    public void setStartNode(MapNode m) {
        startNode.setValue(m);
    }
    public void setEndNode(MapNode m) {
        endNode.setValue(m);
    }

    @FXML
    public void onCloseTab(ActionEvent e) {
        onCloseTab();
        //parent.getTabs().remove(parentTab);
        if (controllers.containsKey(parentTab)) {
            controllers.remove(parentTab);
        }
        if (selectedLocations.containsKey(parentTab)) {
            selectedLocations.remove(parentTab);
        }
        if (selectedNodes.containsKey(parentTab)) {
            selectedNodes.remove(parentTab);
        }
        parent.getTabs().remove(parentTab);
        parentTab = null;



    }
    private void detachListener() {
        mapDraw.detachListener(newLocClickID);
            originalLocClickID = mapDraw.attachClickedListener(originalLocClick, ClickedListener.LOCCLICKED);
            originalNodeClickID = mapDraw.attachClickedListener(originalNodeClick, ClickedListener.NODECLICKED);
            detachParentListeners.setValue(false);
            System.out.println("Detached the " + parentTab + " listeners");
        }


    private BooleanProperty waitToAdd;

    public void setWaitToAdd(BooleanProperty wait) {
        this.waitToAdd = wait;
    }

    public MapNode onCompleteAdd() {
        MapNode toReturn = null;
        if (selectedLocation.getValue()!= null) {
            toReturn = createNode();
            String id = selectedLocations.get(parentTab);
            if (toReturn != null) {
                mapDraw.unDrawNewLocation(id);
                mapDraw.drawNode(toReturn, 5, Color.DARKBLUE);
            }
        }
        else if (selectedNode.getValue() != null) {
            toReturn = updateExistingNode();
            if (toReturn != null) {
                mapDraw.unDrawNode(toReturn);
                mapDraw.drawNode(toReturn, 5, Color.DARKBLUE);
            }


        }
        if (toReturn != null) {
            mapData.addNode(toReturn);
            return toReturn;
        }
        else {
            return null;
        }

       //detachListener();
        //parent.getTabs().remove(parentTab);
    }





}
