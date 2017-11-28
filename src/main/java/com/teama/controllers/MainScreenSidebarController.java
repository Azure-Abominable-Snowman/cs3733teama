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
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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
    private ToggleGroup algoToggleGroup;
    private SimpleBooleanProperty floorChange = new SimpleBooleanProperty(false);

    private MapSubsystem mapSubsystem;

    private MapDisplay map;
    private Map<String, MapNode> allNodes;
    private Map<String, MapEdge> allEdges;
    private FXMLLoader loader;
    private JFXButton curFloorButton;
    private MapNode selectedNode;
    private MapEdge selectedEdge;


    //
    private ArrayList<String> shownNodes;
    private ArrayList<String> shownEdges;

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
        editorToggles = new ToggleGroup();
        editNodes.setToggleGroup(editorToggles);
        editEdges.setToggleGroup(editorToggles);



        editorToggles.selectedToggleProperty().addListener((Observable obs) -> {
            Parent root;
            if (editNodes.isSelected()) {
                try {
                    loader.setLocation(getClass().getResource("/NodeEditor.fxml"));
                    root = (Parent) loader.load();
                    infoPane.setContent(root);
                    nodePrompt.setText("");
                    nodeName.setText("");
                    nodeCoord.setText("");
                    curFloor.setText("");
                    nodeType.setText("");
                    


                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else if (editEdges.isSelected()) {
                try {
                    loader.setLocation(getClass().getResource("/EdgeEditor.fxml"));
                    root = (Parent) loader.load();
                    infoPane.setContent(root);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else {
                //infoPane.setContent(defaultInfo);
            }
        });

        viewNodes.setOnAction((ActionEvent e) -> {
            updateCurrentNodesEdges();
            if (viewNodes.isSelected()) {
                drawAllNodes();
                System.out.println("User selected View Nodes");


            }
            else { //viewNodes turned off, delete
                deleteAllNodes();
            }
                }
        );

        viewEdges.setOnAction((ActionEvent e) -> {
            if (viewEdges.isSelected()) {
                System.out.println("User selected View Edges");
                drawAllEdges();
            }else {
                deleteAllEdges();
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

    // sets the curFloorButton to listen to floor switches and to draw appropriate nodes/edges
    // TODO: doesn't work
    public void setCurFloorButton(JFXButton curFloor) {
        this.curFloorButton = curFloor;
        // DISPLAYING NODES AND EDGES
        curFloorButton.onMouseClickedProperty().addListener((Observable obs) -> {
            System.out.println("Floor is changed to" + (map.getCurrentFloor().toString()));
            deleteAllEdges();
            deleteAllNodes();
            updateCurrentNodesEdges();
            if (viewEdges.isSelected()) {
                drawAllEdges();
            }
            if (viewNodes.isSelected()) {
                drawAllNodes();
            }
        });

    }
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


    @FXML
    void onAddEdge(ActionEvent event) {
        // TODO: After the user clicks this button the user should be able to click on another node to add an edge from
        // TODO: the selected node to that one
    }

    @FXML
    void onAddNew(ActionEvent event) {
        if (editNodes.isSelected()) {

        }
    }

    @FXML
    void onSelectNode(ActionEvent event) {
        // TODO: After the button is clicked the user should be able to select a node with the mouse cursor
        // TODO: and it should display in the selectedNode text
    }


    void onToggleNodesEdges() { // controls what is shown on the map based on the toggle currently selected by user
        //allNodes = mapSubsystem.getFloorNodes(map.getCurrentFloor());
        //allEdges = getAllEdges(allNodes);
        updateCurrentNodesEdges();
        if (viewNodes.isSelected()) {
            for (MapNode m: allNodes.values()) {
                new DrawNodeInstantly(m).displayOnScreen(map);
            }
        } else if (!viewNodes.isSelected()) {
            for (String id: mapSubsystem.getInvisibleFloorNodes(map.getCurrentFloor()).keySet()) {
                map.deletePoint(id);
            }
        }
        if (viewEdges.isSelected()) {
            for (MapEdge e: allEdges.values()) {
                new DrawEdgeInstantly(e).displayOnScreen(map);
            }
        } else if (!viewEdges.isSelected()) {
            for (MapEdge e: allEdges.values()) {
                map.deleteLine(e.getId());
            }
        }
    }



    // when floor is changed, update the current Maps of Nodes and Edges
    private void updateCurrentNodesEdges() {
        allNodes = mapSubsystem.getFloorNodes(map.getCurrentFloor());
        allEdges = getAllEdges(allNodes);
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
        for (MapNode m: allNodes.values()) {
            new DrawNodeInstantly(m).displayOnScreen(map);
        }
    }

    private void deleteAllNodes() {
        for (MapNode m: allNodes.values()) {
            map.deletePoint(m.getId());
        }
    }

    private void drawAllEdges() {
        updateCurrentNodesEdges();
        for (MapEdge e: allEdges.values()) {
            new DrawEdgeInstantly(e).displayOnScreen(map);
        }
    }

    private void deleteAllEdges() {
        for (MapEdge e: allEdges.values()) {
            map.deleteLine(e.getId());
        }
    }

   private boolean shownHiddenNodesAndEdges = false;

   // private ArrayList<String> shownNodes;
   // private ArrayList<String> shownEdges;
    private Floor hiddenDispFloor;

    @FXML
    void onToggleHiddenNodesAndEdges() {
        if(!shownHiddenNodesAndEdges || !map.getCurrentFloor().equals(hiddenDispFloor)) {
            // Show all hidden nodes and edges
            shownNodes = new ArrayList<>();
            shownEdges = new ArrayList<>();
            for (MapNode n : mapSubsystem.getInvisibleFloorNodes(map.getCurrentFloor()).values()) {
                new DrawNodeInstantly(n).displayOnScreen(map);
                shownNodes.add(n.getId());
                for (MapEdge e : n.getEdges()) {
                    new DrawEdgeInstantly(e).displayOnScreen(map);
                    shownEdges.add(e.getId());
                }
            }
            shownHiddenNodesAndEdges = true;
            hiddenDispFloor = map.getCurrentFloor();
        } else {
            // Hide all hidden nodes and edges
            for(String id : shownNodes) {
                map.deletePoint(id);
            }
            for(String id : shownEdges) {
                map.deleteLine(id);
            }
            shownHiddenNodesAndEdges = false;
        }
    }




    @FXML
    public void onLoginClick() {
        ///Dialog d = new Dialog();
        try {
            /*
            d.getDialogPane().setContent(FXMLLoader.load(getClass().getResource("/StaffLogIn.fxml")));
            d.show();
            */
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

            //Scene loginScene = new Scene(loader.load(getClass().getResource("/LogInScreen.fxml")));

            //loginPopup.setScene((AnchorPane)));
            //StaffLoginController loginController = new StaffLoginController();

            //loginController.setLoggedIn(false);
            //login.visibleProperty().bind(loginController.getLoggedInProperty());
            //setLoggedIn(false);
            //login.visibleProperty().bind(this.isLoggedIn);
/*
            loginController.getLoggedInProperty().addListener((obs, before, now) -> {
                if (now) {
                    loginPopup.hide();
                    login.setVisible(false);
                }
                else {
                    login.setVisible(true);
                }
            });
*/
           // loader.setController(loginController);

            loginPopup.setScene(loginScene);
            loginPopup.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
