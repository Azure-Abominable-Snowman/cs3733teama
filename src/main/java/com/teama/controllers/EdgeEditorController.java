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
    private JFXButton removeEdge, confirm, cancel;
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
    private boolean inDeleteMode = false;

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



    public void initialize() {
        masterMap = MapSubsystem.getInstance();
        startFloor = new JFXComboBox<>();
        startFloor.getItems().clear();
        startFloor.getItems().addAll(Floor.values());

        startNodeLocInfo.add(startFloor, 0, 2);
        startFloor.setVisible(false);

        endFloor = new JFXComboBox<>();
        endFloor.getItems().clear();
        endFloor.getItems().addAll(Floor.values());
        endNodeLocInfo.add(endFloor, 0, 2);
        endFloor.setVisible(false);

        setStart.setOnMouseClicked((MouseEvent e) -> {
            if (selectedNode != null) {
                    startNode = selectedNode;
                    startLoc = startNode.getCoordinate();
                    map.drawPoint(startLocID, startLoc, 8, Color.BLUE, false);
                    selectedNode = null;
                    System.out.println("Set a start node.");
            }
            if (setStart.getText().equals("Edit")) {
                setStart.setText("Set");
            }
            else if (setStart.getText().equals("Set")) {
                setStart.setText("Edit");
            }

        });

        setEnd.setOnMouseClicked((MouseEvent e) -> {
            if (selectedNode != null) {
                endNode = selectedNode;
                endLoc = endNode.getCoordinate();
                map.drawPoint(endLocID, endLoc, 8, Color.BLUE, false);

                selectedNode = null;
                System.out.println("Set an End node.");

                map.drawLine(selectedEdgeID, startLoc, endLoc, 7, Color.GREEN, false);
            }
            if (setEnd.getText().equals("Edit")) {
                setEnd.setText("Set");
            }
            else if (setEnd.getText().equals("Set")) {
                setEnd.setText("Edit");
            }
        });

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
            inAddMode = true;
            inDeleteMode = false;

        });
        this.deleteMode.setOnAction((ActionEvent e) -> {
            inAddMode = false;
            inDeleteMode = true;
        });

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
