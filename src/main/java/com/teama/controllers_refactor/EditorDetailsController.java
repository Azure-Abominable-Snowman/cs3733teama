package com.teama.controllers_refactor;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXToggleButton;
import com.teama.mapdrawingsubsystem.ClickedListener;
import com.teama.mapdrawingsubsystem.MapDrawingSubsystem;
import com.teama.mapsubsystem.MapSubsystem;
import com.teama.mapsubsystem.data.Location;
import com.teama.mapsubsystem.data.MapEdge;
import com.teama.mapsubsystem.data.MapEdgeData;
import com.teama.mapsubsystem.data.MapNode;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by aliss on 12/5/2017.
 */
public class EditorDetailsController {
    @FXML
    private JFXTextField nodeID1, nodeCoord1, longName1, nodeID2, nodeCoord2, longName2;
    @FXML
    private JFXButton deleteEdge;

    @FXML
    private JFXButton addEdge;

    @FXML
    private JFXButton confirmBtn;

    @FXML
    private JFXButton cancelBtn;

    private MapDrawingSubsystem mapDraw;
    private MapSubsystem mapData;
    private MapNode startNodeEdge= null;
    private MapNode endNodeEdge  = null;
    private JFXToggleButton viewEdges, viewNodes;
    private long listenerID;

    Map<Long, EventHandler<MouseEvent>> mapClickedListeners = new HashMap<>();
    public EditorDetailsController(JFXToggleButton viewEdges, JFXToggleButton viewNodes) {
        this.viewEdges = viewEdges;
        this.viewNodes = viewNodes;
    }

    public void initialize() {
        mapDraw = MapDrawingSubsystem.getInstance();
        mapData = MapSubsystem.getInstance();
        ToggleGroup editorGroup = viewEdges.getToggleGroup();


    }
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
    @FXML
    void onDeleteEdge(ActionEvent e) {
        mapClickedListeners.put(mapDraw.attachClickedListener(onNodeClickEdges, ClickedListener.NODECLICKED), onNodeClickEdges);

        deleteEdge.setDisable(true);
        addEdge.setDisable(true);
        startNodeEdge = null;
        endNodeEdge = null;
        confirmBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (startNodeEdge != null && endNodeEdge != null) {
                    String id1 = startNodeEdge.getId() + "_" + endNodeEdge.getId();
                    String id2 = endNodeEdge.getId() + "_" + startNodeEdge.getId();
                    MapEdge found = mapData.getEdge(id1);
                    if (found == null) {
                        found = mapData.getEdge(id2);
                        if (found != null) {
                            mapData.deleteEdge(id2);
                            mapDraw.unDrawEdge(found);
                        } else {
                            Alert alert = new Alert(Alert.AlertType.ERROR);
                            alert.setTitle("Deletion Failure");
                            alert.setHeaderText("Failed to Delete Edge.");
                            alert.setContentText("Make sure the start and end nodes are valid.");
                            alert.showAndWait();
                        }
                    } else {
                        mapData.deleteEdge(id1);

                        mapDraw.unDrawEdge(found);
                    }
                }
                addEdge.setDisable(false);
                deleteEdge.setDisable(false);
            }
        });

    }

    @FXML
    void onAddEdge(ActionEvent e) {
        listenerID = mapDraw.attachClickedListener(onNodeClickEdges, ClickedListener.NODECLICKED);


        deleteEdge.setDisable(true);
        addEdge.setDisable(true);
        startNodeEdge = null;
        endNodeEdge = null;
        System.out.println("User would like to add an edge.");
        confirmBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (startNodeEdge != null && endNodeEdge != null) {
                    MapEdge newEdge = new MapEdgeData("", startNodeEdge, endNodeEdge);
                    mapData.addEdge(newEdge);
                    if (viewEdges.isSelected()) {
                        System.out.println("User wants to view edges.");
                        mapDraw.drawEdge(newEdge, 5, Color.LIGHTBLUE, false);
                    }
                    mapDraw.detachListener(listenerID);
            }
        }});

    }

    @FXML
    void onCancel(ActionEvent e) {
        nodeID1.setText("");
        nodeCoord1.setText("");
        longName1.setText("");
        nodeID2.setText("");
        nodeCoord2.setText("");
        longName2.setText("");
        startNodeEdge = null;
        endNodeEdge = null;
        addEdge.setDisable(false);
        deleteEdge.setDisable(false);
    }
    @FXML
    void onConfirm(ActionEvent e) {

    }

}
