package com.teama.controllers_refactor;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXToggleButton;
import com.teama.controllers.NodeEditorController;
import com.teama.mapdrawingsubsystem.ClickedListener;
import com.teama.mapdrawingsubsystem.MapDrawingSubsystem;
import com.teama.mapsubsystem.MapSubsystem;
import com.teama.mapsubsystem.data.Location;
import com.teama.mapsubsystem.data.MapEdge;
import com.teama.mapsubsystem.data.MapNode;
import com.teama.mapsubsystem.data.NodeType;
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
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.MouseEvent;
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
    private JFXComboBox<NodeType> nodeType;
    @FXML
    private JFXComboBox<String> alignmentOptions;
    @FXML
    private JFXButton confirmBtn, cancelBtn, alignBtn, editNode, addNode, deleteNode;
    @FXML
    private VBox editorInfo;



    private Parent currentPopOut;
    private ToggleGroup editorGroup;

    private Map<Long, EventHandler<MouseEvent>> mouseEvents = new HashMap<>();
    private Map<Long, ChangeListener<Boolean>> floorEvents = new HashMap<>();
    BooleanProperty updateCurrentNode = new SimpleBooleanProperty(true);

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
        //alignmentOptions.setDisable(true);
        alignmentOptions.setVisible(false);
        alignBtn.setVisible(false);


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
                }
                else if (!newValue) {
                    System.out.println("Hide Edges");
                    hideEdges();
                }
            }
        });
        mouseEvents.put(masterMap.attachClickedListener(onLocClick, ClickedListener.LOCCLICKED), onLocClick);
        mouseEvents.put(masterMap.attachClickedListener(onNodeClick, ClickedListener.NODECLICKED), onNodeClick);
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

    EventHandler<MouseEvent> onLocClick = new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent event) {
            closePopUp();
            double xCanv = event.getX();
            double yCanv = event.getY();
            Location selected = new Location(event, masterMap.getCurrentFloor());

            MapNode clickedNode = masterMap.nodeAt(selected);
            if (clickedNode != null) {
                System.out.println(clickedNode.getCoordinate().getxCoord());
                nodeID.setText(clickedNode.getId());
                nodeCoord.setText("(" + clickedNode.getCoordinate().getxCoord() + ", " + clickedNode.getCoordinate().getyCoord());
                longName.setText(clickedNode.getLongDescription());
                shortName.setText(clickedNode.getShortDescription());
                nodeType.setValue(clickedNode.getNodeType());
                nodeType.setDisable(true);


            }

            else {
                Location converted = masterMap.convertEventToImg(event, masterMap.getCurrentFloor());
                clearTextFields();
                nodeCoord.setText("(" + Integer.toString(converted.getxCoord()) + ", " +  Integer.toString(converted.getyCoord()) + ")");

            }
            // IF EDITING NODES, GENERATE + POPUP
        }
    };

    private void clearTextFields() {
        nodeID.setText(nodeID.getPromptText());
        longName.setText(longName.getPromptText());
        shortName.setText(shortName.getPromptText());
        nodeType.setDisable(false);
        nodeType.getSelectionModel().clearSelection();
        nodeType.setDisable(true);
    }

    EventHandler<MouseEvent> onNodeClick = new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent event) {
            //TODO: GENERATE EDIT/ADD POPUP
            closePopUp();
            Parent editPopout;
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/EditDeleteNode.fxml"));
            NodeEditorController node = new NodeEditorController();
            loader.setController(node);
            System.out.println("Node is clicked, make a popup.");
            try {
                editPopout = loader.load();
                currentPopOut = editPopout;
                mapDraw.getAreaPane().getChildren().addAll(editPopout);


            } catch (IOException e) {
                e.printStackTrace();
            }
            node.setInfo(event);


        }
    };

    EventHandler<MouseEvent> onEdgeClick = new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent event) {
            closePopUp();
            //TODO
        }
    };

    private void displayNodePopUp(MouseEvent e) {

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
    private void closePopUp() {
        if(currentPopOut != null && masterMap.getAreaPane().getChildren().contains(currentPopOut)) {
            masterMap.getAreaPane().getChildren().remove(currentPopOut);
            currentPopOut = null;
        }
    }

    @Override
    public void onClose() {
        this.isOpenProperty.setValue(false);
        for (long id: floorEvents.keySet()) {
            masterMap.detachListener(id);
        }
        for (long id: mouseEvents.keySet()) {
            masterMap.detachListener(id);
        }
    }

    @FXML
    void onAlignNode(ActionEvent e) {
        if (alignmentOptions.getSelectionModel().getSelectedItem() != null) {
        }
    }

    @Override
    public String getFXMLPath() {
        return "/EditorPopOut.fxml";
    }
}
