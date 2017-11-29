package com.teama.controllers;

import com.jfoenix.controls.*;
import com.teama.drawing.MapDisplay;
import com.teama.mapsubsystem.MapSubsystem;
import com.teama.mapsubsystem.data.*;
import com.teama.mapsubsystem.pathfinding.AStar.AStar;
import com.teama.mapsubsystem.pathfinding.AStar.BeamSearch;
import com.teama.mapsubsystem.pathfinding.BreathFrist.BreathFirst;
import com.teama.mapsubsystem.pathfinding.Dijkstras.Dijkstras;
import com.teama.mapsubsystem.pathfinding.PathAlgorithm;
import javafx.beans.Observable;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.*;

public class MainScreenSidebarController {
    @FXML
    private JFXListView directions;
    @FXML
    private TitledPane algorithmSelect;
    @FXML
    private JFXRadioButton aStar;

    @FXML
    private JFXRadioButton breadthFirst;

    @FXML
    private JFXRadioButton dijkstra;

    @FXML
    private JFXRadioButton beamSearch;



    @FXML
    private JFXButton login;

    // MAP EDITOR TOOLS
    @FXML
    private JFXToggleButton viewNodes, viewEdges, editNodes, editEdges;
    @FXML
    private JFXButton add, edit;

    @FXML
    private Text nodePrompt;
    @FXML
    private TextField nodeName;
    @FXML
    private Text nodeCoord, curFloor;
    @FXML
    private Text nodeType;


    @FXML
    JFXComboBox nodeTypes;
    @FXML
    private ScrollPane infoPane;
    private ToggleGroup editorToggles;
    private Canvas c;

    @FXML
    private JFXSlider beamSearchQueue;

    private ToggleGroup algoToggleGroup;
    private SimpleBooleanProperty floorChange = new SimpleBooleanProperty(false);

    private MapSubsystem mapSubsystem;

    private MapDisplay map;
    private Map<String, MapNode> floorNodes;
    private Map<String, MapEdge> floorEdges;
    private FXMLLoader loader;
    private JFXButton curFloorButton;
    private MapNode selectedNode;
    private MapEdge selectedEdge;


    //
    private ArrayList<String> shownNodes;
    private ArrayList<String> shownEdges;
    private VBox floorButtonBox;

    private EventHandler oldNodeEditorHandler;

    public void initialize() {
        mapSubsystem = MapSubsystem.getInstance();


        // Add all of the radio buttons to a toggle group
        algoToggleGroup = new ToggleGroup();
        aStar.setToggleGroup(algoToggleGroup);
        breadthFirst.setToggleGroup(algoToggleGroup);
        dijkstra.setToggleGroup(algoToggleGroup);
        beamSearch.setToggleGroup(algoToggleGroup);

        aStar.setUserData(new AStar());
        breadthFirst.setUserData(new BreathFirst());
        dijkstra.setUserData(new Dijkstras());
        beamSearch.setUserData(new BeamSearch(20)); // TODO: make queue size editable

        // Select the default algorithm
        mapSubsystem.setPathGeneratorStrategy((PathAlgorithm)algoToggleGroup.getSelectedToggle().getUserData());

        //

        // When the toggle group changes, make the algorithm reflect that
        algoToggleGroup.selectedToggleProperty().addListener((Observable obs) -> {
            System.out.println("Changed to "+algoToggleGroup.getSelectedToggle().getUserData());
            mapSubsystem.setPathGeneratorStrategy((PathAlgorithm)algoToggleGroup.getSelectedToggle().getUserData());
        });


        //Map Editor
        /*
        EventHandler<MouseEvent> onMapClicked = (MouseEvent e) -> {
            if (editNodes.isSelected()) {
                String nodeID = map.pointAt(new Location((int) e.getX(), (int) e.getY(), map.getCurrentFloor(), "Selected Location"));
                MapNode found = mapSubsystem.getNode(nodeID);
            }
        c = map.getUnderlyingCanvas();
        c.onMouseClickedProperty().set(onMapClicked);
        */
        loader = new FXMLLoader();
        //loader.setController(this);
        editorToggles = new ToggleGroup();
        editNodes.setToggleGroup(editorToggles);
        editEdges.setToggleGroup(editorToggles);


        editorToggles.selectedToggleProperty().addListener((Observable obs) -> {
            if (editNodes.isSelected()) {
                try {
                    oldNodeEditorHandler = map.getUnderlyingCanvas().getOnMouseClicked();
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/NodeEditor.fxml"));
                    //loader.setLocation(getClass().getResource("/NodeEditor.fxml"));
                    Parent root = (Parent) loader.load(); // load in fxml
                    NodeEditorController nodeEditor = loader.getController();
                    nodeEditor.setButtons(add, edit);
                    nodeEditor.setMap(map);
                    infoPane.setContent(root);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else if (editEdges.isSelected()) {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/EdgeEditor.fxml"));
                    Parent root = (Parent) loader.load();
                    EdgeEditorController edgeEditor = loader.getController();
                    edgeEditor.setMap(map);
                    edgeEditor.setButtons(add, edit);
                    infoPane.setContent(root);


                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else {
                // Delete the cursor
                map.deletePoint(NodeEditorController.selectedLocID);

                // check to see if editNodes is deselected, and if it is then
                // make it so the mouse cursor thing doesn't appear and the pop ups
                // start appearing again
                if(!editNodes.isSelected()) {
                    System.out.println("STOP EDITING NODES");
                    map.getUnderlyingCanvas().setOnMouseClicked(oldNodeEditorHandler);
                }

                // Remove whatever is in the infopane
                infoPane.setContent(null);
            }
        });

        viewNodes.setOnAction((ActionEvent e) -> {
            updateCurrentNodesEdges();
            if (viewNodes.isSelected()) {
                drawAllNodes();
                System.out.println("User selected View Nodes");


            }
            else { //viewNodes turned off, delete
                removeHiddenNodes();
            }
        }

        );

        viewEdges.setOnAction((ActionEvent e) -> {
            if (viewEdges.isSelected()) {
                System.out.println("User selected View Edges");
                drawAllEdges();
            } else {
                deleteAllEdges();
            }
        });

        add.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {

            }
        });

    }


    /**
     * Sets the map display in this controller, must be ran before anything else is to be done regarding the map
     * @param map
     */
    public void setMapDisplay(MapDisplay map) {
        this.map = map;
    }

    /**
     * Sets the floor button vbox, must be ran before anything else
     * @param floorButtonBox
     */
    public void setFloorButtonVBox(VBox floorButtonBox) {
        this.floorButtonBox = floorButtonBox;

        // Listen for floor button changes to switch what hidden nodes
        // are displayed if the toggle buttons are selected
        for(Node button : floorButtonBox.getChildren()) {
            button.pressedProperty().addListener((Observable obs) -> {
                updateHiddenNodesEdges();
            });
        }
    }

    /*
    private void updateNodeInfo() {
        if (selectedNode != null) {
            nodePrompt.setText("To edit or delete this node, select the Edit button above.");
            nodeName.setText(selectedNode.getLongDescription());
            nodeCoord.setText("(" + selectedNode.getCoordinate().getxCoord() + ", " + selectedNode.getCoordinate().getyCoord() + ")");
            curFloor.setText(map.getCurrentFloor().toString());
            nodeType.setText(selectedNode.getNodeType().toString());
        }
    }

    public void getLocationInfo(MouseEvent event) {
        if (editNodes.isSelected()) {
            String id = map.pointAt(new Location((int)event.getX(), (int)event.getY(), map.getCurrentFloor(), "Unknown"));
            if(id != null) {
                System.out.println("User selected a node!");
                MapNode clickedNode = mapSubsystem.getNode(id);
                selectedNode = clickedNode;
                updateNodeInfo();
        }
        else {
                selectedNode = null;
            }

        }

    }
    */


    private void updateHiddenNodesEdges() { // controls what is shown on the map based on the toggle currently selected by user
        updateCurrentNodesEdges();

        // If viewnodes is selected, view all the invisible nodes
        if (viewNodes.isSelected()) {
            drawAllNodes();
        }

        // If viewedges is selected, view all invisible edges
        if (viewEdges.isSelected()) {
            drawAllEdges();
        }
    }

    // when floor is changed, update the current Maps of Nodes and Edges
    private void updateCurrentNodesEdges() {
        floorNodes = mapSubsystem.getFloorNodes(map.getCurrentFloor());
        floorEdges = getAllEdges(floorNodes);
    }

    // helper to add all the edges to draw
    private Map<String, MapEdge> getAllEdges(Map<String, MapNode> allNodes) {
        Map<String, MapEdge> allEdges = new HashMap<>();
        for (MapNode m: allNodes.values()) {
            ArrayList<MapEdge> edges = m.getEdges();
            for (MapEdge e: edges) {
                allEdges.put(e.getId(),e);
            }
        }
        return allEdges;
    }


    private void drawAllNodes() {
        updateCurrentNodesEdges();
        for (MapNode m: floorNodes.values()) {
            new DrawNodeInstantly(m).displayOnScreen(map);
        }
    }

    private void removeHiddenNodes() {
        for (String id : mapSubsystem.getInvisibleFloorNodes(map.getCurrentFloor()).keySet()) {
            map.deletePoint(id);
        }
    }

    private Set<String> tempEdges = new HashSet<>();

    private void drawAllEdges() {
        updateCurrentNodesEdges();
        for (MapEdge e: floorEdges.values()) {
            // To make sure we don't overwrite real edges that may be displayed on the screen for pathfinding, add _FAKE at the end of the id.
            new DrawEdgeInstantly(new MapEdgeData(e.getId()+"_FAKE", e.getStartID(), e.getEndID())).displayOnScreen(map);
            tempEdges.add(e.getId()+"_FAKE");
        }
    }

    private void deleteAllEdges() {
        for (String id : tempEdges) {
            map.deleteLine(id);
        }
        tempEdges.clear();
    }

    @FXML
    public void onLoginClick() {
        ///Dialog d = new Dialog();
        try {
            Stage loginPopup = new Stage();

            loginPopup.setTitle("B&W Login");
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/LogInScreen.fxml"));
            Parent root = (Parent) loader.load();
            //loader.setLocation(getClass().getResource("/LogInScreen.fxml"));
            StaffLoginController loginController = loader.getController();
            loginController.initialize();

            Scene loginScene = new Scene(root);
            loginController.getLoggedInProperty().addListener((obs, before, now) -> {
                if (now) {
                    loginPopup.hide();
                    login.setVisible(false);
                }
                else {
                    login.setVisible(true);
                }
            });

            loginPopup.setScene(loginScene);
            loginPopup.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
