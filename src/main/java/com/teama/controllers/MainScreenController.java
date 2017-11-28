package com.teama.controllers;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXDrawer;
import com.jfoenix.controls.JFXHamburger;
import com.jfoenix.transitions.hamburger.HamburgerBackArrowBasicTransition;
import com.sun.javafx.scene.control.skin.TextFieldSkin;
import com.teama.drawing.HospitalMap;
import com.teama.drawing.HospitalMapDisplay;
import com.teama.drawing.MapDisplay;
import com.teama.drawing.ProxyMap;
import com.teama.mapsubsystem.MapSubsystem;
import com.teama.mapsubsystem.data.Floor;
import com.teama.mapsubsystem.data.Location;
import com.teama.mapsubsystem.data.MapNode;
import javafx.beans.Observable;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.controlsfx.control.textfield.AutoCompletionBinding;
import org.controlsfx.control.textfield.TextFields;

import java.io.IOException;
import java.net.URL;
import java.util.*;

/**
 * Delegates tasks to the other controllers on the main screen
 *
 * PathfindingController is for pathfinding and navigation between floors
 * MainScreenSidebarController controls the sidebar hamburger on the main screen
 * NodeInfoPopUpController controls the popup when you ctrl+click on a node
 * SearchBarController controls how the search bar parses and displays its intermediate results for pathfinding
 *
 * (Not yet implemented)
 * MapEditorController handles editing of the map edges/nodes
 */
public class MainScreenController implements Initializable {
    private int animRate=-1;
    private HamburgerBackArrowBasicTransition hamOpnsTran;
    private SimpleBooleanProperty isLoggedIn = new SimpleBooleanProperty();
    public BooleanProperty getLoggedInProperty() {
        return isLoggedIn;
    }

    public final boolean isLoggedIn() {
        return isLoggedIn.get();
    }

    public final void setLoggedIn(Boolean update) {
        isLoggedIn.set(update);
    }

    @FXML
    private ScrollPane mapScroll;
    @FXML
    private JFXButton login;

    @FXML
    private Canvas mapCanvas;

    @FXML
    private JFXDrawer drawer;

    @FXML
    private JFXHamburger hamburgerButton;

    @FXML
    private JFXComboBox<String> searchBar;

    @FXML
    private JFXButton searchButton;

    @FXML
    private AnchorPane areaPane;

    @FXML
    private VBox floorButtonBox;

    @FXML
    private GridPane displayedMaps;

    private MapDisplay map;

    private boolean drawerExtended = false;

    private double maxZoom = 3.0;
    private double minZoom = 1.1;

    private MapSubsystem mapSubsystem;

//    String[] words = {"Fenwood Road",
//            "Schlagler Stairs",
//            "Information Desk",
//            "Information Desk",
//            "Elevator S G",
//            "BTM Security Desk",
//            "Neuro Testing Waiting Area",
//            "Infusion Waiting Area",
//            "Schlagler Innovation Lobby",
//            "Test Lobby",
//            "Test Area1",
//            "Test Area2",
//            "Test Area3",
//            "Test Desk",
//            "Test Front Desk",
//            "Test Info Desk",
//            "Test Desk2",
//            "Test Desk3",
//            "Test Desk4",
//            "Test Desk5",
//            "Test Stairs2",
//    };
//    Set<String> possibleWordSet = new HashSet<>();
//    private AutoCompletionBinding<String> autoCompletionBinding;


    @FXML
    void onHamburgerButtonClick(MouseEvent event) throws IOException {
        hamOpnsTran.setRate(animRate*=-1);
        hamOpnsTran.play();
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/MainSidebar.fxml"));
        HBox box = loader.load();
        MainScreenSidebarController sidebar = loader.getController();
        sidebar.setMapDisplay(map);
        drawer.setSidePane(box);
        if(drawer.isShown()){
            drawer.close();
            drawerExtended = false;
        } else {
            drawer.setPrefWidth(box.getPrefWidth());
            drawerExtended = true;
            drawer.open();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        mapSubsystem = MapSubsystem.getInstance();
        drawer.setSidePane();
        hamOpnsTran = new HamburgerBackArrowBasicTransition(hamburgerButton);

//        Collections.addAll(possibleWordSet, words);


        //String[] possibleWords = {"test", "test2"};

        Map<Floor, HospitalMap> imgs = new HashMap<>();
        imgs.put(Floor.SUBBASEMENT, new ProxyMap("/maps/L2.png"));
        imgs.put(Floor.BASEMENT, new ProxyMap("/maps/L1.png"));
        imgs.put(Floor.GROUND, new ProxyMap("/maps/G.png"));
        imgs.put(Floor.ONE, new ProxyMap("/maps/1.png"));
        imgs.put(Floor.TWO, new ProxyMap("/maps/2.png"));
        imgs.put(Floor.THREE, new ProxyMap("/maps/3.png"));

        map = new HospitalMapDisplay(mapScroll, mapCanvas, imgs);
        map.setGrow(true);
        map.setZoom(minZoom);

        PathfindingController pathfinding = new PathfindingController(MapSubsystem.getInstance(), map, areaPane, floorButtonBox);
        SearchBarController searchBarController = new SearchBarController(searchBar, searchButton, mapSubsystem);

//        searchBar.setEditable(true);
//        //autoCompletionBinding = TextFields.bindAutoCompletion(searchBar.getEditor(), possibleWordSet);
//        //TextFields.bindAutoCompletion(searchBar.getEditor(), possibleWordSet);
//
//        searchBar.getEditor().setOnKeyPressed((KeyEvent e)->{
//            switch(e.getCode()){
//                case ENTER:
//                    learnWord(searchBar.getEditor().getText());
//                    break;
//                default:
//                    break;
//            }
//        });

        // Zoom in and out using plus and minus keys
        mapScroll.onKeyTypedProperty().set((KeyEvent event) -> {
            switch(event.getCharacter()) {
                case "=":
                    // zoom in
                    if(map.getZoom() < maxZoom) { // TODO: make this not throw an exception when the image gets too big
                        map.setZoom(map.getZoom() + 0.1);
                    }
                    break;
                case "-":
                    // zoom out
                    if(map.getZoom() > minZoom) {
                        map.setZoom(map.getZoom() - 0.1);
                    }
                    break;
            }
        });

        // Make each node clickable to reveal a detailed menu
        EventHandler<MouseEvent> clickedOnMapHandler = (MouseEvent event) -> {
            //System.out.println("CLICKED ON MAP SCREEN AT "+event.getX()+" "+event.getY());

            if(nodeInfo != null && areaPane.getChildren().contains(nodeInfo)) {
                areaPane.getChildren().remove(nodeInfo);
                nodeInfo = null;
            }

            if(event.isControlDown()) { // check for a node and if there is one display the node info
                generateNodePopUp(event);
            } else {
                pathfinding.genPathWithClicks(event);
            }
        };
        map.getUnderlyingCanvas().onMouseClickedProperty().set(clickedOnMapHandler);

        // When the map is resized the pop up must be taken off the screen
        // TODO: Make the pop up move instead
        ChangeListener<Number> removePopUpWhenResized = (ObservableValue<? extends Number> obsVal, Number oldVal, Number newVal) -> {
            if(nodeInfo != null && areaPane.getChildren().contains(nodeInfo)) {
                areaPane.getChildren().remove(nodeInfo);
                nodeInfo = null;
            }
        };
        areaPane.heightProperty().addListener(removePopUpWhenResized);
        areaPane.widthProperty().addListener(removePopUpWhenResized);


        // When the map is panned, remove the pop up
        EventHandler<MouseEvent> panningMap = (MouseEvent event) -> {
            if(nodeInfo != null && areaPane.getChildren().contains(nodeInfo)) {
                areaPane.getChildren().remove(nodeInfo);
                nodeInfo = null;
            }
        };
        mapScroll.onDragDetectedProperty().set(panningMap);

        /*Canvas newMapCanvas = new Canvas(); Test to add another map as an inset (multi-floor pathfinding)
        ScrollPane newMapPane = new ScrollPane(newMapCanvas);
        MapDisplay newMap = new HospitalMapDisplay(newMapPane, newMapCanvas, imgs);
        newMapPane.setMinWidth(300);
        newMap.setGrow(true);
        displayedMaps.addColumn(1, newMapPane);*/

        // When the hamburger retracts, make it disappear, otherwise appear
        hamOpnsTran.onFinishedProperty().set((ActionEvent e) -> {
            drawer.setVisible(drawerExtended);
        });

        // Handle the search bar, allow for autocomplete and fuzzy searching of nodes
        searchBar.valueProperty().addListener((observable, oldValue, newValue) -> {
            searchBarController.matchFuzzySearchValues();
        });

        searchBarController.updateNodeListing(map.getCurrentFloor());
        // When the floor is switched make it so the floor is changed in the search box
        for(Node node : floorButtonBox.getChildren()) {
            node.pressedProperty().addListener((Observable obs) -> {
                searchBarController.updateNodeListing(map.getCurrentFloor());
            });
        }



        // If the searchbutton is pressed, display a path to the selected/approximately matched node
        searchButton.pressedProperty().addListener((ObservableValue<? extends Boolean> obs, Boolean before, Boolean after) -> {
            if(!before && after) { // When button is pressed
                System.out.println("SEARCH BUTTON PRESSED");
                MapNode dest = mapSubsystem.getNodeByDescription(searchBar.getSelectionModel().getSelectedItem(), true);
                pathfinding.genPath(dest);
            }
        });
    }

//    private void learnWord(String text) {
//        possibleWordSet.add(text);
//
//        if(autoCompletionBinding != null)
//            autoCompletionBinding.dispose();
//
//                autoCompletionBinding = TextFields.bindAutoCompletion(searchBar.getEditor(), possibleWordSet);
//    }


    private Parent nodeInfo;

    /**
     * Generates a node pop up if able from the given mouse event
     * @param event
     */
    private void generateNodePopUp(MouseEvent event) {
        System.out.println("CLICK ON NODE BUTTON");
        // Get the id of the node clicked on (if any)
        String id = map.pointAt(new Location((int)event.getX(), (int)event.getY(), map.getCurrentFloor(), "Unknown"));

        System.out.println("CLICKED ON "+id);

        if(id != null) {
            MapNode clickedNode = mapSubsystem.getNode(id);

            // Load the screen in and display it on the cursor
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/NodeInfoPopUp.fxml"));
            try {
                nodeInfo = loader.load();
            } catch (IOException e) {
                e.printStackTrace();
            }
            NodeInfoPopUpController ni = loader.getController();
            ni.setInfo(clickedNode, map, mapSubsystem);

            // Create pane to load nodeInfo root node into
            nodeInfo.toFront(); // bring to front of screen
            areaPane.getChildren().add(nodeInfo);

            // Display the pane next to the mouse cursor
            double windowW = 140; // TODO: Pull these directly from the parent node itself.
            double windowH = 225;

            double newX = event.getSceneX()-windowW/2;
            double newY = event.getSceneY()-windowH;

            if(newX <= mapScroll.getWidth()-nodeInfo.getBoundsInParent().getWidth() && newX >= 0) {
                nodeInfo.setTranslateX(newX);
            } else if (newX > 0) {
                nodeInfo.setTranslateX(mapScroll.getWidth()-nodeInfo.getBoundsInParent().getWidth()*1.2);
            }

            if(newY <= mapScroll.getHeight() && newY >= 0) {
                System.out.println("MOVE Y");
                nodeInfo.setTranslateY(newY);
            }
        }
    }


    public void hideLoginButton() {
        login.setVisible(false);
    }


}
