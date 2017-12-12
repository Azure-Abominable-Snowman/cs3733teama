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
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Tab;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

import java.util.HashMap;

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
    private long newLocClickID;



    public ObjectProperty<MapNode> selectedNodeProperty() {
        return selectedNode;
    }
    // for existing nodes
    private ObjectProperty<MapNode> selectedNode = new SimpleObjectProperty<MapNode>();


    // FOR NODE EDITOR:
    // for unknown locations
    private ObjectProperty<Location> selectedLocation = new SimpleObjectProperty<Location>();
    private ObjectProperty<MapNode> newNode = new SimpleObjectProperty<MapNode>();


    private HashMap<Tab, String> selectedLocations = new HashMap<>();
    private HashMap<Tab, MapNode> selectedNodes = new HashMap<>();


    // FOR EDGE EDITOR:
    private HashMap<Tab, MapEdge> selectedEdges = new HashMap<>();
    private ObjectProperty<MapNode> startNode = new SimpleObjectProperty<MapNode>();
    private ObjectProperty<MapNode> endNode = new SimpleObjectProperty<>();




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
        confirmBtn.setDisable(true);
        selectedLocation.setValue(null);
        selectedNode.setValue(null);
        newNode.setValue(null);
        startNode.setValue(null);
        endNode.setValue(null);
        nodeID.setEditable(false);
        nodeCoord.editableProperty().setValue(false); // will only be updated through map clicks


        setUnEditable();


    }

    public NodesController(JFXTabPane parentPane, BooleanProperty nodeEditing, BooleanProperty edgeEditing, HashMap<Tab, String> selectedLocs, HashMap<Tab, MapNode> selectedNodes) {
        parent = parentPane;
        this.editingNodes = nodeEditing;
        this.editingEdges = edgeEditing;
        this.selectedLocations = selectedLocs;
        this.selectedNodes = selectedNodes;

    }

    public void setParentTab(Tab parentTab) {
        this.parentTab = parentTab;

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
        mapDraw.unDrawNode(clickedNode);
        mapDraw.drawNode(clickedNode, 5, Color.GREEN);
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
        nodeCoord.setText("(" + selected.getxCoord() + ", " +selected.getyCoord() + ")");
        floor.setText(mapDraw.getCurrentFloor().toString());
        selectedLocation.setValue(selected);
        String drawnID = generateDrawnIDForNode();
        if (selectedLocations == null) {
            selectedLocations = new HashMap<Tab, String>();
        }
        selectedLocations.put(parentTab, drawnID);
        mapDraw.drawNewLocation(selected, 5, Color.GREEN, drawnID);

    }
    private String generateDrawnIDForNode() {
        int length = selectedLocations.size();
        String id = "Selected" + length;
        System.out.println(id);
        return id;
    }

    public MapNode getSelectedNode() {
        return selectedNode.get();
    }

    public MapNode getNewNode() {
        return newNode.getValue();
    }

    private MapNode createNode() {
        MapNode created = null;
        MapNode toAdd = newNode.getValue();
        Location loc = selectedLocation.getValue();
        Location converted = mapDraw.convertLocationToImgCoords(loc);
        if (toAdd == null && loc != null)
            if (!(nodeID.getText().equals("") || longName.getText().equals("") || nodeType.getSelectionModel().getSelectedItem() == null || shortName.getText().equals(""))) {
                created = new MapNodeData(nodeID.getText(), new Location(converted.getxCoord(), converted.getyCoord(), Floor.getFloor(floor.getText()), ""),
                        nodeType.getSelectionModel().getSelectedItem(), longName.getText(), shortName.getText(), "A");
            }
        newNode.setValue(created);
        return created;

    }

    private void setUnEditable() {
        nodeType.disableProperty().set(true);
        confirmBtn.setDisable(true);
        clearBtn.setDisable(true);
        nodeID.setEditable(false);
        longName.setEditable(false);
        shortName.setEditable(false);

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
            int newX = converted.getxCoord();
            int newY = converted.getyCoord();
            nodeCoord.setText("(" + newX + ", " + newY + ")");
            if (selectedNode.getValue() != null) {
                System.out.println("Editing the location of an existing node.");
                MapNode toUpdate = selectedNodes.get(parentTab);
                System.out.println(toUpdate.getId());
                mapDraw.unDrawNode(toUpdate);
                toUpdate.getCoordinate().setxCoord(newX);
                toUpdate.getCoordinate().setyCoord(newY);
                selectedNodes.put(parentTab, toUpdate);
                mapDraw.drawNode(toUpdate, 5, Color.GREENYELLOW);
            }
            if (selectedLocation.getValue() != null) {
                System.out.println("Editing the location of a new node");
                selectedLocation.setValue(selected);

                String locID = selectedLocations.get(parentTab);
                mapDraw.unDrawNewLocation(locID);
                mapDraw.drawNewLocation(selected, 5, Color.GREEN, locID);
            }
        }
    };

    @FXML
    public void onEditNode(ActionEvent e) {
        setEditable();
        // TODO: update changed location coord  -- user clicks on map to change the coordinate
        mapDraw.detachListener(originalLocClickID);
        mapDraw.detachListener(originalNodeClickID);
        newLocClickID = mapDraw.attachClickedListener(onLocationChange, ClickedListener.LOCCLICKED);


    }

    private MapNode updateExistingNode() {
        if (selectedNodes != null && selectedNode.getValue() != null) {
            MapNode updated = null;
            if (!(longName.getText().equals("") || nodeType.getSelectionModel().getSelectedItem() == null || shortName.getText().equals(""))) {
                updated = new MapNodeData(selectedNode.getValue().getId(), selectedNode.getValue().getCoordinate(),
                        nodeType.getSelectionModel().getSelectedItem(), longName.getText(), shortName.getText(), selectedNode.getValue().getTeamAssignment());
            }
            if (updated != null) {
                MapNode updatedLocation = selectedNodes.get(parentTab);
                updated.getCoordinate().setxCoord(updatedLocation.getCoordinate().getxCoord());
                updated.getCoordinate().setyCoord(updatedLocation.getCoordinate().getyCoord());
                selectedNode.setValue(updated);
                }
                return updated;
            }
        return null; // shouldn't happen..
    }

    @FXML
    public void onConfirmClick(ActionEvent e) { // for node editing only
        setUnEditable();
        editNode.setDisable(false);
        parentTab.setText(nodeID.getText());
        if (selectedNode.getValue() != null) { // updating an existing node
            MapNode updated = updateExistingNode(); // gets all the updated text and updates coordinate
            if (updated == null) {

            }
        }
        else {
            if (selectedLocation.getValue() != null) { // updating a new node
                MapNode newNode = createNode();
                if (newNode == null) {
                    System.out.println("ERROR, couldn't make a new node from the input given.");
                }

            }
        }
        mapDraw.detachListener(newLocClickID);
        originalLocClickID = mapDraw.attachClickedListener(originalLocClick, ClickedListener.LOCCLICKED);
        originalNodeClickID = mapDraw.attachClickedListener(originalNodeClick, ClickedListener.NODECLICKED);
        confirmBtn.setDisable(true);
        clearBtn.setDisable(true);
    }

    private void reset() {
        if (selectedLocation.getValue()!= null) {
            nodeID.clear();
            longName.clear();
            shortName.clear();
            nodeType.getSelectionModel().clearSelection();

        }
        if (selectedNode.getValue() != null) {
            MapNode selected = selectedNode.getValue();
            nodeID.setText(selected.getId());
            longName.setText(selected.getLongDescription());
            shortName.setText(selected.getShortDescription());
            nodeType.setValue(selected.getNodeType());
        }
        mapDraw.detachListener(newLocClickID);
        originalLocClickID = mapDraw.attachClickedListener(originalLocClick, ClickedListener.LOCCLICKED);
        originalNodeClickID = mapDraw.attachClickedListener(originalNodeClick, ClickedListener.NODECLICKED);

        confirmBtn.setDisable(true);
        clearBtn.setDisable(true);
    }

    @FXML
    public void onResetClick(ActionEvent e) {
        editNode.setDisable(false);
        setUnEditable();
        reset();


    }



}
