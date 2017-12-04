package com.teama.controllers_refactor;

import com.jfoenix.controls.JFXToggleButton;
import com.teama.mapdrawingsubsystem.MapDrawingSubsystem;
import com.teama.mapsubsystem.MapSubsystem;
import com.teama.mapsubsystem.data.MapEdge;
import com.teama.mapsubsystem.data.MapNode;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.paint.Color;

public class EditorPopOut extends PopOutController {

    private int xOffset, yOffset;
    private ReadOnlyDoubleProperty xProperty, yProperty;

    @FXML
    private JFXToggleButton viewEdges, viewNodes;
    @FXML
    public void initialize() {
        System.out.println("hello");
        alignPane(xProperty, xOffset, yProperty, yOffset);
       // viewEdges = new JFXToggleButton();
        //viewNodes = new JFXToggleButton();
        viewEdges.setText("View Edges");
        viewNodes.setText("View Hall Nodes");

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
                }
                else if (!newValue) {
                    System.out.println("Hide Edges");
                    hideEdges();
                }
            }
        });


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


    }

    private void drawNodes() {
        for (MapNode m: mapData.getVisibleFloorNodes(masterMap.getCurrentFloor()).values()) {
            masterMap.drawNode(m, 3, Color.DARKBLUE);
        }
        for (MapNode m: mapData.getInvisibleFloorNodes(masterMap.getCurrentFloor()).values()) {
            masterMap.drawNode(m, 3, Color.DARKBLUE);
        }
    }

    private void hideFloorNodes() {
        for (MapNode m: mapData.getInvisibleFloorNodes(masterMap.getCurrentFloor()).values()) {
            masterMap.unDrawNode(m);
        }
    }

    private void drawEdges() {
        for (MapNode m: mapData.getFloorNodes(masterMap.getCurrentFloor()).values()) {
            for (MapEdge e: m.getEdges()) {
                masterMap.drawEdge(e, 5, Color.LIGHTBLUE, false);

            }
        }
    }

    private void hideEdges() {
        for (MapNode m: mapData.getFloorNodes(masterMap.getCurrentFloor()).values()) {
            for (MapEdge e: m.getEdges()) {
                masterMap.unDrawEdge(e);

            }
        }
    }
    @Override
    public void onClose() {

    }

    @Override
    public String getFXMLPath() {
        return "/EditorPopOut.fxml";
    }
}
