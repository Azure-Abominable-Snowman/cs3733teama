package com.teama.controllers;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import com.teama.drawing.MapDisplay;
import com.teama.mapsubsystem.MapSubsystem;
import com.teama.mapsubsystem.data.*;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

import java.util.Map;

/**
 * Created by aliss on 11/28/2017.
 */
public class NodeEditorController {

    @FXML
    private Text nodePrompt;
    @FXML
    private JFXTextField nodeName, nodeLongName;
    @FXML
    private Text nodeCoord, curFloor;
    @FXML
    private Text nodeType;
    @FXML
    private JFXButton removeNode, editNode;
    @FXML
    private GridPane nodeInfo, buttonGrid;

    private JFXComboBox<NodeType> nodeTypeSelector;
    private ToggleGroup editorToggles;
    private Canvas c;
    private MapSubsystem masterMap;
    private MapNode selectedNode = null;
    private Location selectedLocation = null;
    private MapDisplay map;
    private JFXButton addMode, editMode;
    private JFXButton confirm, cancel;


    private Map<String, MapNode> visibleNodes;
    private Map<String, MapNode> hiddenNodes;
    //private Map<String, MapEdge> visibleEdges;



    private BooleanProperty addingNew = new SimpleBooleanProperty(false);
    private BooleanProperty editing = new SimpleBooleanProperty(false);

    private boolean isEditEditing = false;
    private boolean isDeleting = false;

    private String defaultPrompt = "Select an existing node to Edit or Delete it. Click 'Add New' to add a new node.";
    public final static String selectedLocID = "selected";

    public void initialize() {
        System.out.println("Creating a new editor controller.");
        masterMap = MapSubsystem.getInstance();

        clearAllText();
        nodePrompt.setText(defaultPrompt);
        nodeName.setDisable(true);
        nodeLongName.setDisable(true);

        nodeTypeSelector = new JFXComboBox<>();
        nodeTypeSelector.getItems().clear(); // reset any defaults
        nodeTypeSelector.getItems().addAll(NodeType.values());
        nodeTypeSelector.setPromptText("Select a Node Type");


        // Add to the right part of the gridpane
        nodeInfo.add(nodeTypeSelector, 1,3);

        nodeTypeSelector.setVisible(false);

        nodeType.setVisible(true);
        // add some new buttons
        confirm = new JFXButton();
        confirm.setText("Confirm");
        cancel = new JFXButton();
        cancel.setText("Cancel");
        confirm.getStylesheets().add("css/MainScreenStyle.css");
        cancel.getStylesheets().add("css/MainScreenStyle.css");
        confirm.getStyleClass().add("normalButton");
        cancel.getStyleClass().add("normalButton");

        cancel.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                restoreToDefault();
            }
        });

        confirm.setOnMouseClicked((MouseEvent event) ->{
                if (selectedLocation != null) {
                    if (addingNew.get()) { // Adding node
                        MapNode toAdd = nodeFromUser();
                        if (toAdd != null) {
                            MapNode added = masterMap.addNode(toAdd);
                            restoreToDefault();
                            nodeName.setDisable(true);
                            nodeLongName.setDisable(true);
                            if (added != null) {
                                new DrawNodeInstantly(added).displayOnScreen(map);
                            }
                        }
                    }
                    else if (isEditEditing) { // Editing node
                        MapNode toUpdate = nodeFromUser();
                        if (toUpdate != null && selectedNode != null) {
                            System.out.println();
                            MapNode edited = masterMap.addNode(new MapNodeData(selectedNode.getId(), toUpdate.getCoordinate(),
                                    toUpdate.getNodeType(),toUpdate.getLongDescription(), toUpdate.getShortDescription(),
                                    selectedNode.getTeamAssignment(), selectedNode.getEdges()));
                            if (edited != null) {
                                map.deletePoint(selectedNode.getId());
                                new DrawNodeInstantly(edited).displayOnScreen(map);
                            }
                            restoreToDefault();

                        }
                        else {
                            System.out.println("Something is WONG.");
                        }
                    }
                    else if (isDeleting) { // Deleting node
                        if (selectedNode != null) {
                            masterMap.deleteNode(selectedNode.getId());
                            map.deletePoint(selectedNode.getId());
                            restoreToDefault();

                        }
                    }
                }
                else {
                    restoreToDefault();
                    clearAllText();
                    nodePrompt.setText("Select a location at which to add a node.");
                }
            }
        );
        buttonGrid.add(confirm, 0, 0);
        buttonGrid.add(cancel, 2, 0);
        hideAllButtons();



        //nodeTypeSelector.visibleProperty().bin;
        //nodeType.visibleProperty().bind(editing);
        //editNode.visibleProperty().bind(editing);
        //removeNode.visibleProperty().bind(editing);
    }

    private void restoreToDefault() {
        clearAllText();
        nodePrompt.setText(defaultPrompt);
        isNotAdding();
        isNotEditing();
        hideAllButtons();

        if (selectedLocation != null) {
            map.deletePoint(selectedLocID);
        }

        selectedNode = null;
        selectedLocation = null;
        isEditEditing = false;
        isDeleting = false;
        nodeTypeSelector.setVisible(false);
        nodeType.setVisible(true);
    }

    private MapNode nodeFromUser() {
        MapNode newNode = null;
        nodePrompt.setText("");
        String name = nodeName.getText();
        String longDes = nodeLongName.getText();
        String floor = curFloor.getText();
        String nType = nodeType.getText();
        if (nodeTypeSelector.getSelectionModel().getSelectedItem() != null) {
            nType = nodeTypeSelector.getSelectionModel().getSelectedItem().toString();
        }
        if (name.equals("") || floor.equals("") || nType.equals("")) {
            nodePrompt.setText("Please fill in all fields.");
        } else {
            newNode = new MapNodeData("", selectedLocation, NodeType.fromValue(nType), longDes, name, "Team A");
        }

        return newNode;
    }

    private void setButtonsForAddMode() {
        confirm.setVisible(true);
        cancel.setVisible(true);
        editNode.setVisible(false);
        removeNode.setVisible(false);
    }

    private void setButtonsForEditMode() {
        confirm.setVisible(false);
        cancel.setVisible(true);
        editNode.setVisible(true);
        removeNode.setVisible(true);
    }

    private void hideAllButtons() {
        confirm.setVisible(false);
        cancel.setVisible(false);
        editNode.setVisible(false);
        removeNode.setVisible(false);
    }
    public void clearAllText() {
        nodePrompt.setText("");
        nodeName.setText("");
        nodeCoord.setText("");
        curFloor.setText("");
        nodeType.setText("");
        nodeLongName.setText("");
    }

    public void setButtons(JFXButton add, JFXButton edit) {
        this.addMode = add;
        this.editMode = edit;
        this.addMode.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                isAdding();
                clearAllText();
                if (selectedLocation != null) {
                    nodeCoord.setText("(" + selectedLocation.getxCoord() + ", " + selectedLocation.getyCoord() + ")");
                    curFloor.setText(selectedLocation.getLevel().toString());
                    nodePrompt.setText("Add details to this location, then Confirm.");
                }
                else {
                    nodePrompt.setText("Select a new location on the map.");
                }

                //confirm.setVisible(true);
                nodeType.setVisible(false);
                nodeTypeSelector.setPromptText("Select a Node Type");
                nodeTypeSelector.setVisible(true);

                nodeName.setDisable(false);
                nodeLongName.setDisable(false);

                setButtonsForAddMode();

            }
        });
        this.editMode.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {

                isEditing();
                if (selectedLocation == null) {
                    nodePrompt.setText("Select an existing node to edit or delete.");
                } else {
                    nodePrompt.setText("Would you like to Edit or Delete this node?");
                    setButtonsForEditMode();
                    nodeTypeSelector.setVisible(false);
                    nodeType.setVisible(true);
                    nodeName.setDisable(false);
                    nodeLongName.setDisable(false);
                }


            }
        });
    }

    private void updateNodeInfo() {
        if (selectedNode != null) {
            nodeName.setText(selectedNode.getShortDescription());
            nodeLongName.setText(selectedNode.getLongDescription());
            nodeCoord.setText("(" + selectedNode.getCoordinate().getxCoord() + ", " + selectedNode.getCoordinate().getyCoord() + ")");
            curFloor.setText(map.getCurrentFloor().toString());
            nodeType.setText(selectedNode.getNodeType().toString());
        }

    }

    private void getLocationInfo(MouseEvent event) {
        selectedNode = null; // reset
        clearAllText();
        Location clicked = new Location((int)event.getX(), (int)event.getY(), map.getCurrentFloor(), "Unknown");
        String id = map.pointAt(clicked);
        clicked = map.convToImageCoords(clicked);
        selectedLocation = clicked;
        if(id != null) {
            System.out.println("User selected a node!");
            if (addingNew.get()) {
                clearAllText();
                nodePrompt.setText("A node already exists at the selected location.");
            }
            else {
                MapNode clickedNode = masterMap.getNode(id);
                selectedNode = clickedNode;
                if (editing.get()) {
                    nodePrompt.setText("Would you like to edit or delete this node?");
                }
                else if (!editing.get() && !addingNew.get()) {
                    nodePrompt.setText("To edit or delete this node, click on Edit Existing.");
                }

                updateNodeInfo();
            }
        }
        else {
            if (editing.get()) {
                nodePrompt.setText("Please select an existing node to Edit or Delete.");
            }
            else {
                if (!addingNew.get() && !editing.get()) {
                    nodePrompt.setText("To add this node, click on Add New.");
                }
                else if (addingNew.get()) {
                    nodePrompt.setText("Fill in the information for this node, then Confirm.");
                }
                nodeCoord.setText("(" + clicked.getxCoord() + ", " + clicked.getyCoord() + ")");
                curFloor.setText(clicked.getLevel().toString());
            }
        }
        map.drawPoint(selectedLocID, selectedLocation, 8, Color.GREEN, false,false);
    }

    public void setMap(MapDisplay m) {
        this.map = m;
        this.c = map.getUnderlyingCanvas();
        c.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {

                getLocationInfo(event);
                updateNodeInfo();
                c.removeEventHandler(MouseEvent.MOUSE_CLICKED, this);
            }
        });
    }
    @FXML
    public void onDeleteClick(ActionEvent e) {
        showConfirmCancelEditMode();
        nodeName.setDisable(true);
        nodeLongName.setDisable(true);
        isDeleting = true;
        isEditEditing = false;
        if (selectedNode!= null) {
            nodePrompt.setText("Click Confirm to delete this node.");
        }
        else {
            clearAllText();
            nodePrompt.setText("Please select an existing node to delete.");
        }
    }
    @FXML
    public void onEditClick(ActionEvent e) {
        System.out.println("EDIT");
        nodeName.setDisable(true);
        nodeLongName.setDisable(true);
        showConfirmCancelEditMode();
        if (selectedNode != null) {
            nodePrompt.setVisible(false);
            nodeTypeSelector.setVisible(false);
            editNode.setVisible(false);
            removeNode.setVisible(false);
            confirm.setVisible(true);
            isEditEditing = true;
            isDeleting = false;
        }
        else {
            clearAllText();
            nodePrompt.setText("Please select an existing node to edit.");
        }
    }



    private void showConfirmCancelEditMode() {
        removeNode.setVisible(false);
        editNode.setVisible(false);
        confirm.setVisible(true);
        cancel.setVisible(true);
    }


    private void isAdding() {
        addingNew.set(true);
        isNotEditing();
    }

    private void isNotAdding() {
        addingNew.set(false);
    }

    private void isEditing() {
        editing.set(true);
        isNotAdding();
    }

    private void isNotEditing() {
        editing.set(false);
    }
}
