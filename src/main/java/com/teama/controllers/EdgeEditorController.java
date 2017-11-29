package com.teama.controllers;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.teama.drawing.MapDisplay;
import com.teama.mapsubsystem.MapSubsystem;
import com.teama.mapsubsystem.data.*;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;


/**
 * Created by aliss on 11/28/2017.
 */
public class EdgeEditorController {
    @FXML
    private Text startNodePrompt, endNodePrompt;
    @FXML
    private TextField startNodeName, endNodeName;
    @FXML
    private Text startCoord, startFloorText, endCoord, endFloorText;
    @FXML
    private Text startNodeType, endNodeType;
    @FXML
    private JFXButton delete, editConfirm, cancel;
    @FXML
    private GridPane startNodeLocInfo, endNodeLocInfo,buttonGrid;
    @FXML
    private JFXButton setStart, setEnd;

    private JFXComboBox<Floor> startFloor, endFloor;
    private JFXButton addMode, deleteMode;

    private MapDisplay map;
    private MapSubsystem masterMap;
    private Canvas c;
    private boolean inAddMode = false;
    private boolean inEditMode = false;
    private boolean isDeleting = false;
    private boolean isEditing = false;

    private MapNode startNode = null; // populated when user clicks set
    private MapNode endNode = null; // populated when user clicks set
    private MapNode selectedNode = null;

    private Location selectedLocation = null;
    private Location startLoc = null;
    private Location endLoc = null;



    private String selectedLocID = "selected";
    private String startLocID = "startLoc";
    private String endLocID = "endLoc";
    private String selectedEdgeID = "selectedEdge";

    private String defaultPrompt = "Select Add New to add an edge or Edit Existing to edit or delete an edge.";

    public void initialize() {
        masterMap = MapSubsystem.getInstance();
        startFloor = new JFXComboBox<>();
        startFloor.getItems().clear();
        startFloor.getItems().addAll(Floor.values());

        clearAllText();
        startNodePrompt.setText(defaultPrompt);

        startNodeLocInfo.add(startFloor, 2, 0);
        startFloor.setVisible(false);

        endFloor = new JFXComboBox<>();
        endFloor.getItems().clear();
        endFloor.getItems().addAll(Floor.values());
        endNodeLocInfo.add(endFloor, 2, 0);
        endFloor.setVisible(false);

        setStart.setDisable(true);
        setEnd.setDisable(true);

        delete.setVisible(false);
        editConfirm.setVisible(false);
        cancel.setVisible(false);

        delete.getStylesheets().add("css/MainScreenStyle.css");
        delete.getStyleClass().add("normalButton");
        editConfirm.getStylesheets().add("css/MainScreenStyle.css");
        editConfirm.getStyleClass().add("normalButton");
        cancel.getStylesheets().add("css/MainScreenStyle.css");
        cancel.getStyleClass().add("normalButton");

        setStart.setOnMouseClicked((MouseEvent e) -> {

            if (setStart.getText().equals("Edit")) {
                setStart.setText("Set");
                map.deletePoint(selectedLocID);
                map.deleteLine(selectedEdgeID);

                startNode = null;
                startLoc = null;
            }
            else if (setStart.getText().equals("Set")) {
                setStart.setText("Edit");
                if (selectedNode != null) {
                    if (endLoc!= null) {
                        map.deletePoint(startLocID);
                    }
                    startNode = selectedNode;
                    startLoc = startNode.getCoordinate();
                    map.drawPoint(startLocID, startLoc, 8, Color.GREEN, false);
                    selectedNode = null;
                    System.out.println("Set a start node.");
                    if (endLoc != null) {
                        map.drawLine(selectedEdgeID, startLoc, endLoc, 7, Color.GREEN, false);
                    }
                }
            }



        });

        editConfirm.setOnAction((ActionEvent e) -> {
          if (editConfirm.getText().equals("Edit")) {
              isEditing = true;
              isDeleting = false;
              setButtonsForAddMode();

          }
          else if (editConfirm.getText().equals("Confirm")) {
              if (inAddMode) {
                  if (startNode != null) {
                      if (endNode != null) {
                          MapEdge newEdge = masterMap.addEdge(new MapEdgeData("", startNode, endNode));
                          if (newEdge != null) {
                              //new DrawEdgeInstantly(newEdge).displayOnScreen(map);
                          }
                      }
                      else {
                          startNodePrompt.setText("Select valid start and end nodes");
                      }
                  }
                  else {
                      startNodePrompt.setText("Select valid start and end nodes.");
                  }
              }
              restoreToDefault();
          }
        });

        setEnd.setOnMouseClicked((MouseEvent e) -> {
            if (setEnd.getText().equals("Edit")) {
                endNode = null;
                endLoc = null;
                setEnd.setText("Set");
                map.deletePoint(selectedLocID);
                map.deleteLine(selectedEdgeID);
            }
            else if (setEnd.getText().equals("Set")) {
                setEnd.setText("Edit");
                if (selectedNode != null) {
                    if (endLoc != null) {
                        map.deletePoint(endLocID);
                        map.deleteLine(selectedEdgeID);
                    }

                    endNode = selectedNode;
                    endLoc = endNode.getCoordinate();
                    map.drawPoint(endLocID, endLoc, 8, Color.BLUE, false);
                    map.drawPoint(startLocID, startLoc, 8, Color.BLUE, false);

                    selectedNode = null;
                    System.out.println("Set an End node.");

                    map.drawLine(selectedEdgeID, startLoc, endLoc, 7, Color.GREEN, false);
                }
            }
        });

        cancel.setOnAction((ActionEvent e) -> {
            restoreToDefault();
        });

        delete.setOnMouseClicked((MouseEvent e) -> {
            System.out.println("DELETING");
            if(startNode != null && endNode != null) {
                System.out.println("Deleting "+startNode.getId()+" connection to "+endNode.getId());
                // Try deleting both possible id's start_end and end_start
                // From both the screen and the database
                masterMap.deleteEdge(startNode.getId()+"_"+endNode.getId());
                masterMap.deleteEdge(endNode.getId()+"_"+startNode.getId());
                map.deleteLine(startNode.getId()+"_"+endNode.getId());
                map.deleteLine(endNode.getId()+"_"+startNode.getId());

                restoreToDefault();
            }
        });

    }
    private void clearAllText() {
        startNodePrompt.setText("");
        //endNodePrompt.setText("");
        startNodeName.setText("");
        endNodeName.setText("");
        startNodeType.setText("");
        endNodeType.setText("");
        startCoord.setText("");
        endCoord.setText("");
        startFloorText.setText("");
        endFloorText.setText("");
    }

    private void restoreToDefault() {
        clearAllText();
        startNodePrompt.setText(defaultPrompt);


        if (selectedLocation != null) {
            map.deletePoint(selectedLocID);
        }

        startNode = null; // populated when user clicks set
        endNode = null; // populated when user clicks set
        selectedNode = null;

        selectedLocation = null;
        startLoc = null;
        endLoc = null;

        inAddMode = false;
        inEditMode = false;
        isDeleting = false;
        isEditing = false;

        setStart.setText("Set");
        setEnd.setText("Set");
        map.deletePoint(selectedLocID);
        map.deleteLine(selectedEdgeID);
        map.deletePoint(startLocID);
        map.deletePoint(endLocID);

        addMode.setVisible(true);
        deleteMode.setVisible(true);
        addMode.setDisable(false);
        deleteMode.setDisable(false);

        delete.setVisible(false);
        editConfirm.setVisible(false);
        cancel.setVisible(false);

        setStart.setDisable(true);
        setEnd.setDisable(true);
    }
    /*
    private MapNode nodeFromUser(boolean startNode) {
        MapNode newNode = null;
        if (startNode) {
            startNodePrompt.setText("");
            String name = startNodeName.getText();
            String floor = curFloor.getText();
            Location loc = selectedLocation;
            String nodeType = "";
            if (nodeTypeSelector.getValue() != null) {
                nodeType = nodeTypeSelector.getValue().name();
            }
            if (name.equals("") || floor.equals("") || nodeType.equals("")) {
                nodePrompt.setText("Please fill in all fields.");
            } else {
                newNode = new MapNodeData("", selectedLocation, (NodeType) nodeTypeSelector.getValue(), name, "", "A");

            }
        }
        else {
        }

        return newNode;
    }
    */
    public void setButtons(JFXButton add, JFXButton delete) {
        this.addMode = add;
        this.deleteMode = delete;
        this.addMode.setOnAction((ActionEvent e) -> {
            restoreToDefault();
            inAddMode = true;
            inEditMode = false;
            //addMode.setVisible(false);
            //deleteMode.setVisible(false);
            addMode.setDisable(true);
            deleteMode.setDisable(true);
            setButtonsForAddMode();
            startNodePrompt.setText("To add an edge, select Start and End Nodes, then Confirm.");
        });



        this.deleteMode.setOnAction((ActionEvent e) -> {
            restoreToDefault();
            startNodePrompt.setText("Select the start and end nodes of the edge to Edit or Delete.");
            addMode.setDisable(true);
            deleteMode.setDisable(true);
            inAddMode = false;
            inEditMode = true;
            //addMode.setVisible(false);
            //deleteMode.setVisible(false);
            setButtonsForDeleteMode();
            });

    }

    private void setButtonsForAddMode() {
        editConfirm.setText("Confirm");
        editConfirm.setVisible(true);
        cancel.setVisible(true);
        delete.setVisible(false);

        setStart.setDisable(false);
        setEnd.setDisable(false);
    }

    private void setButtonsForDeleteMode() {
        editConfirm.setVisible(false);
        cancel.setVisible(true);
        delete.setVisible(true);

        setStart.setDisable(false);
        setEnd.setDisable(false);
    }

    private void hideAllButtons() {
        editConfirm.setVisible(false);
        cancel.setVisible(false);
        delete.setVisible(false);
    }

    private void setNodeInfo(boolean startNode) {

        MapNode nodeToSet = selectedNode;
        if (nodeToSet != null) {
            String longDesc = nodeToSet.getLongDescription();
            String coord = "(" + nodeToSet.getCoordinate().getxCoord() + ", " + nodeToSet.getCoordinate().getyCoord() + ")";
            String floorText = map.getCurrentFloor().toString();
            String nodeType = nodeToSet.getNodeType().toString();

            if (nodeToSet != null) {
                if (startNode) {
                    startNodeName.setText(longDesc);
                    startCoord.setText(coord);
                    startFloorText.setText(floorText);
                    startNodeType.setText(nodeType);
                } else if (!startNode) {
                    endNodeName.setText(longDesc);
                    endCoord.setText(coord);
                    endFloorText.setText(floorText);
                    endNodeType.setText(nodeType);
                }
            }
        }
        else {
            System.out.println("No existing node selected.");
        }

    }

    private void getLocationInfo(MouseEvent event) {

        selectedLocation = null;
        selectedNode = null;
        //clearAllText();
        Location clicked = new Location((int)event.getX(), (int)event.getY(), map.getCurrentFloor(), "Unknown");
        String id = map.pointAt(clicked);
        clicked = map.convToImageCoords(clicked);
        selectedLocation = clicked;
        if(id != null) {
            MapNode clickedNode = masterMap.getNode(id);
            selectedNode = clickedNode;
            if (startNode == null) {
                setNodeInfo(true);
                System.out.println("User selected a start node!");
            }
            else if (endNode == null) {
                setNodeInfo(false);

                System.out.println("User selected an end node!");
            }

            }

        map.drawPoint(selectedLocID, selectedLocation, 8, Color.GREEN, false);
    }

    public void setMap(MapDisplay m) {
        this.map = m;
        this.c = map.getUnderlyingCanvas();
        c.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {

                getLocationInfo(event);
                if (startNode == null) {
                    setNodeInfo(true);
                }
                else if (endNode == null) {
                    setNodeInfo(false);
                }
                c.removeEventHandler(MouseEvent.MOUSE_CLICKED, this);
            }
        });
    }

}
