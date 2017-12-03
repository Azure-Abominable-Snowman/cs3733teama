package com.teama.controllers;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXDrawer;
import com.jfoenix.controls.JFXSlider;
import com.teama.controllers_refactor.PopOutController;
import com.teama.controllers_refactor.PopOutFactory;
import com.teama.controllers_refactor.PopOutType;
import com.teama.mapdrawingsubsystem.ClickedListener;
import com.teama.mapdrawingsubsystem.MapDisplay;
import com.teama.mapdrawingsubsystem.MapDrawingSubsystem;
import com.teama.mapsubsystem.MapSubsystem;
import com.teama.mapsubsystem.data.Floor;
import com.teama.mapsubsystem.data.Location;
import com.teama.mapsubsystem.data.MapNode;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * Delegates tasks to the other controllers on the main screen
 */
public class MainScreenController implements Initializable {

    @FXML
    private AnchorPane areaPane;

    @FXML
    private GridPane displayedMaps;

    @FXML
    private ScrollPane mapScroll;

    @FXML
    private Canvas mapCanvas;

    @FXML
    private JFXDrawer drawer;

    @FXML
    private JFXComboBox<String> searchBar;

    @FXML
    private JFXButton searchButton;

    @FXML
    private Text floorNumberDisplay;

    @FXML
    private ImageView loginButton;

    @FXML
    private ImageView directoryButton;

    @FXML
    private ImageView mapEditorButton;

    @FXML
    private ImageView serviceRequestButton;

    @FXML
    private JFXSlider zoomSlider;

    @FXML
    private VBox floorButtonBox;

    @FXML
    private ImageView directionsButton;

    @FXML
    private GridPane mainSidebarGrid;


    private MapDisplay map;

    private boolean drawerExtended = false;

    private double maxZoom = 3.0;
    private double minZoom = 1.1;

    private MapSubsystem mapSubsystem;

    private MapDrawingSubsystem mapDrawing;

    private PathfindingController pathfinding;
    private SimpleBooleanProperty isLoggedIn = new SimpleBooleanProperty(false);
    private SimpleBooleanProperty showLoginButton = new SimpleBooleanProperty(true);
    private EventHandler<MouseEvent> originalMouseClick;

    private MainScreenSidebarController sidebar;
    private PopOutFactory popOutFactory = new PopOutFactory();

    // Contains all of the event handlers for the buttons on the sidebar
    // Useful for when we need to open something on the sidebar based on another event
    private Map<PopOutType, EventHandler<MouseEvent>> mainSidebarMap = new HashMap<>();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        mapSubsystem = MapSubsystem.getInstance();
        pathfinding = new PathfindingController(mainSidebarMap);

        // Populate the floor button box
        for(Floor floor : Floor.values()) {
            JFXButton curFloorButton = new JFXButton();
            curFloorButton.setText(floor.toString());
            curFloorButton.getStylesheets().add("css/MainScreenStyle.css");
            curFloorButton.getStyleClass().add("floorbutton");
            curFloorButton.setId(floor.toString());
            curFloorButton.setPrefWidth(35);

            floorButtonBox.getChildren().add(curFloorButton);
        }

        mapDrawing = MapDrawingSubsystem.getInstance();
        mapDrawing.initialize(mapCanvas, mapScroll, floorNumberDisplay, floorButtonBox);

        mapDrawing.setGrow(true);
        mapDrawing.setZoomFactor(minZoom);

        // Add a listener that displays the correct nodes for the floor
        mapDrawing.clearMap();
        for (MapNode n : mapSubsystem.getVisibleFloorNodes(mapDrawing.getCurrentFloor()).values()) {
            mapDrawing.drawNode(n, 0, null);
        }
        mapDrawing.attachFloorChangeListener((a, b, c) -> {
            mapDrawing.clearMap();
            for (MapNode n : mapSubsystem.getVisibleFloorNodes(mapDrawing.getCurrentFloor()).values()) {
                mapDrawing.drawNode(n, 0, null);
            }
        });

        // Attach listeners to all the sidebar pop outs to open their respective pane on click
        for(Node button : mainSidebarGrid.getChildren()) {
            EventHandler<MouseEvent> buttonEvent = event -> {
                // Gets the pop out type from the id defined in scenebuilder
                openInMainSidebar(PopOutType.valueOf(button.getId()), areaPane.widthProperty(), -(int)button.prefWidth(80),
                        button.layoutYProperty(), 0);
            };
            button.onMouseClickedProperty().set(buttonEvent);
            mainSidebarMap.put(PopOutType.valueOf(button.getId()), buttonEvent);
        }

        // Make a pop up on the user's mouse cursor every time a node is clicked
        mapDrawing.attachClickedListener(event -> generateNodePopUp(event), ClickedListener.NODECLICKED);

        // Pop up goes away on a floor switch
        mapDrawing.attachFloorChangeListener((a, b, c) -> removeCurrentPopUp());

        // Make the node pop up disappear every time the window is resized
        ChangeListener<Number> removePopUpWhenResized = (ObservableValue<? extends Number> obsVal, Number oldVal, Number newVal) -> removeCurrentPopUp();
        areaPane.heightProperty().addListener(removePopUpWhenResized);
        areaPane.widthProperty().addListener(removePopUpWhenResized);

        // Remove pop up on pan
        mapScroll.onDragDetectedProperty().set((event -> removeCurrentPopUp()));

        // Zoom in and out using plus and minus keys
        mapScroll.onKeyTypedProperty().set((KeyEvent event) -> {
            switch(event.getCharacter()) {
                case "=":
                    // zoom in
                    if(mapDrawing.getZoomFactor() < maxZoom) {
                        mapDrawing.setZoomFactor(mapDrawing.getZoomFactor() + 0.1);
                    }
                    break;
                case "-":
                    // zoom out
                    if(mapDrawing.getZoomFactor() > minZoom) {
                        mapDrawing.setZoomFactor(mapDrawing.getZoomFactor() - 0.1);
                    }
                    break;
            }
            // Remove the pop up
            removeCurrentPopUp();
        });
    }

    private Node currentPopOut;
    private PopOutController currentPopOutController;
    private PopOutType currentPopOutType = null;

    private void openInMainSidebar(PopOutType popOutType, ReadOnlyDoubleProperty xProperty, int xOffset, ReadOnlyDoubleProperty yProperty, int yOffset) {
        try {
            // If the previous pop out type is the same as the current and there is a controller, remove it from the screen
            if(popOutType.equals(currentPopOutType) && currentPopOut != null) {
                currentPopOutController.onClose();
                areaPane.getChildren().remove(currentPopOut);
                currentPopOut = null;
                return;
            }
            // Remove the last pop out if present
            if(currentPopOut != null) {
                currentPopOutController.onClose();
                areaPane.getChildren().remove(currentPopOut);
            }
            PopOutController controller = popOutFactory.makePopOut(popOutType);
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource(controller.getFXMLPath()));
            loader.setController(controller);
            controller.onOpen(xProperty, xOffset, yProperty, yOffset);
            currentPopOut = loader.load();
            currentPopOutController = controller;

            // Add it to the area pane in the correct spot next to the button
            areaPane.getChildren().add(currentPopOut);

            currentPopOutType = popOutType;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Parent nodeInfo;

    /**
     * Generates a node pop up if able from the given mouse event
     * @param event
     */
    private void generateNodePopUp(MouseEvent event) {
        removeCurrentPopUp(); // only pop up allowed at a time
        System.out.println("CLICK ON NODE BUTTON");
        // Get the node clicked on (if any)
        MapNode nodeAt = mapDrawing.nodeAt(new Location(event, mapDrawing.getCurrentFloor()));

        if(nodeAt != null) {
            System.out.println("CLICKED ON "+nodeAt.getId());

            // Load the screen in and display it on the cursor
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/NodeInfoPopUp.fxml"));
            try {
                nodeInfo = loader.load();
            } catch (IOException e) {
                e.printStackTrace();
            }
            NodeInfoPopUpController ni = loader.getController();
            ni.setInfo(nodeAt, pathfinding);

            // Create pane to load nodeInfo root node into
            nodeInfo.toFront(); // bring to front of screen
            areaPane.getChildren().add(nodeInfo);

            // Display the pane next to the mouse cursor
            double windowW = 140;
            double windowH = nodeInfo.prefHeight(windowW)+35;
            System.out.println("WINDOWH "+windowH);

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

    private void removeCurrentPopUp() {
        if(nodeInfo != null && areaPane.getChildren().contains(nodeInfo)) {
            areaPane.getChildren().remove(nodeInfo);
            nodeInfo = null;
        }
    }
}
