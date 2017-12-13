package com.teama.controllers_refactor2;


import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXDrawer;
import com.jfoenix.controls.JFXHamburger;
import com.jfoenix.controls.JFXSlider;
import com.teama.ProgramSettings;
import com.teama.controllers.NodeInfoPopUpController;
import com.teama.controllers.PathfindingController;
import com.teama.controllers.SearchBarController;
import com.teama.controllers_refactor.PopOutFactory;
import com.teama.controllers_refactor.PopOutType;
import com.teama.mapdrawingsubsystem.ClickedListener;
import com.teama.mapdrawingsubsystem.MapDisplay;
import com.teama.mapdrawingsubsystem.MapDrawingSubsystem;
import com.teama.mapsubsystem.MapSubsystem;
import com.teama.mapsubsystem.data.Floor;
import com.teama.mapsubsystem.data.Location;
import com.teama.mapsubsystem.data.MapNode;
import com.teama.translator.Translator;
import foodRequest.FoodRequest;
import javafx.beans.Observable;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class MainMapScreen implements Initializable {

    @FXML
    private AnchorPane areaPane;

    @FXML
    private ScrollPane mapScroll;

    @FXML
    private Canvas mapCanvas;

    @FXML
    private JFXDrawer drawer;

    @FXML private Pane searchPane;

    @FXML
    private ImageView lanBtn;



    @FXML
    private JFXComboBox<String> searchBar;

    @FXML
    private JFXHamburger hmbDrawerOpener;

    @FXML
    private ImageView searchButton;

    @FXML private ImageView loginButton;

    @FXML private ImageView translateButton;

    @FXML
    private JFXSlider zoomSlider;

    @FXML
    private GridPane floorButtonBox;

    @FXML
    private ImageView directionsButton;

    @FXML
    HBox hbxDrawerBox;

    private MapDisplay map;
    private HamburgerController curController;
    private boolean drawerExtended = false;

    private double maxZoom = 3.0;
    private double minZoom = 1.1;

    private MapSubsystem mapSubsystem;

    private MapDrawingSubsystem mapDrawing;

    private PathfindingController pathfinding;
    private SimpleBooleanProperty isLoggedIn = new SimpleBooleanProperty(false);
    private SimpleBooleanProperty showLoginButton = new SimpleBooleanProperty(true);
    private EventHandler<MouseEvent> originalMouseClick;

    private PopOutFactory popOutFactory = new PopOutFactory();

    private long popUpID;

    // Contains all of the event handlers for the buttons on the sidebar
    // Useful for when we need to open something on the sidebar based on another event
    //get rid of this
    private Map<PopOutType, EventHandler<MouseEvent>> mainSidebarMap = new HashMap<>();
    private final ImageView imgLogIn = new ImageView(new Image(getClass().getResourceAsStream("/icons_i4/user-3-1.png")));
    private final ImageView imgLogOut = new ImageView(new Image(getClass().getResourceAsStream("/icons_i4/LogOut.png")));
    final int NOTIFICATION_SIZE=40;
    final int hmbDRAWEROPENER_WIDTH=30;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        mapSubsystem = MapSubsystem.getInstance();
        pathfinding = new PathfindingController(mainSidebarMap);
        //set up buttonBox IDs
        for(int i=0; i<Floor.values().length; i++) {
            floorButtonBox.getChildren().get(i).setId(Floor.values()[floorButtonBox.getChildren().size()-1-i].toString());
        }
        mapDrawing = MapDrawingSubsystem.getInstance();
        //have the button box be the gridPane here
        System.out.println(mapScroll);
        mapDrawing.initialize(mapCanvas, mapScroll, floorButtonBox, areaPane);

        mapDrawing.setGrow(true);
        mapDrawing.setZoomFactor(1.7); // Initial zoom

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
        // Make a pop up on the user's mouse cursor every time a node is clicked
        popUpID = mapDrawing.attachClickedListener(event -> generateNodePopUp(event), ClickedListener.LOCCLICKED);
        //will need to change this to create the child in the drawer and adjust the drawer

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
            switch (event.getCharacter()) {
                case "=":
                    // zoom in
                    if (mapDrawing.getZoomFactor() < maxZoom) {
                        mapDrawing.setZoomFactor(mapDrawing.getZoomFactor() + 0.1);
                    }
                    break;
                case "-":
                    // zoom out
                    if (mapDrawing.getZoomFactor() > minZoom) {
                        mapDrawing.setZoomFactor(mapDrawing.getZoomFactor() - 0.1);
                    }
                    break;
            }
            // Remove the pop up
            removeCurrentPopUp();

            // make the zoom slider reflect the current zoom level
            zoomSlider.setValue(mapDrawing.getZoomFactor());
        });

        // Set the zoom slider max and min to the zoom max and min
        zoomSlider.setMin(minZoom);
        zoomSlider.setMax(maxZoom);
        // Set to default
        zoomSlider.setValue(mapDrawing.getZoomFactor());
        // When the zoom slider is moved, change the zoom factor on the screen
        zoomSlider.valueProperty().addListener((a, b, after) -> {
            mapDrawing.setZoomFactor(after.doubleValue());
            removeCurrentPopUp();
        });

        // Populate and create the search bar
        SearchBarController mainSearch = new SearchBarController(searchBar, false);

        // When the search button is pressed then generate a new path with that as the destination
        searchButton.pressedProperty().addListener((Observable a) -> {
            mapDrawing.setViewportCenter(mainSearch.getSelectedNode().getCoordinate());
            pathfinding.genPath(mainSearch.getSelectedNode());
        });

        // Have the search bar listen to the beginning of the path and update accordingly
        ProgramSettings.getInstance().getPathEndNodeProp().addListener((a) -> {
            searchBar.getEditor().setText(ProgramSettings.getInstance().getPathEndNodeProp().getValue().getLongDescription());
        });
        // Hide stuff until someone is logged in
        ProgramSettings.getInstance().getIsLoggedInProp().addListener((a) -> {
            System.out.println("logging in");
            setLoginVisibility();
        });
        setLoginVisibility();
    }

    private void setLoginVisibility() {
        if (ProgramSettings.getInstance().getIsLoggedInProp().get()) {
            System.out.println("here");
            //inserting animation here
            Image logOut = new Image(getClass().getResourceAsStream("/materialicons/mainscreenicons/LogOut.png"));
            loginButton.setImage(logOut);
            adjustSearchPane(false);
            System.out.println(imgLogIn);
            imgLogIn.setFitHeight(NOTIFICATION_SIZE);
            imgLogIn.setFitWidth(NOTIFICATION_SIZE);
            Notifications notifications = Notifications.create()
                    .title("Log In Complete")
                    .text("Welcome!")
                    .graphic(imgLogIn)
                    .hideAfter(Duration.seconds(2))
                    .position(Pos.BOTTOM_CENTER)
                    .onAction(new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent event) {
                            System.out.println("Hi Kent");
                        }
                    });
            notifications.owner(areaPane.getScene().getWindow());
            notifications.show();
            System.out.println(notifications);
        } else {
            System.out.println("I guess not");
            // mapEditorButton.setY(startPoint);
            Image logIn = new Image(getClass().getResourceAsStream("/materialicons/mainscreenicons/LogIn.png"));
            loginButton.setImage(logIn);
            adjustSearchPane(true);
        }
    }

    private void adjustSearchPane(boolean removeHmb){
        if(removeHmb){
            hmbDrawerOpener.setVisible(false);
            searchBar.setLayoutX(searchBar.getLayoutX()-hmbDRAWEROPENER_WIDTH);
            searchBar.setPrefWidth(searchBar.getPrefWidth()+hmbDRAWEROPENER_WIDTH);
        }
        else{
            hmbDrawerOpener.setVisible(true);
            searchBar.setLayoutX(searchBar.getLayoutX()+hmbDRAWEROPENER_WIDTH);
            searchBar.setPrefWidth(searchBar.getPrefWidth()-hmbDRAWEROPENER_WIDTH);
        }
    }

    private Parent nodeInfo;
    //TODO update this stuff to create and contain the search info
    @FXML public void onSearchClick(MouseEvent e){

    }
    //TODO update this to create and contain the pathfinding stuff
    @FXML public void onDirectionsClick(MouseEvent e){
        if(drawerExtended){
            //do nothing
        }
        else{
            try {
                disableSearchPane();
                drawer.setVisible(true);
                FXMLLoader openerLoader = new FXMLLoader();
                curController = new DirectionController();
                openerLoader.setResources(Translator.getInstance().getNewBundle());
                openerLoader.setLocation(getClass().getResource(curController.getFXMLPath()));
                openerLoader.setController(curController);
                openerLoader.load();
                curController.getParentPane().prefHeightProperty().bind(drawer.heightProperty());
                curController.onOpen();
                ((DirectionController) curController).setFinder(pathfinding);

                drawer.setDefaultDrawerSize(curController.getParentPane().getPrefWidth());
                drawer.setSidePane(curController.getParentPane());
                drawer.open();
                curController.getClosing().addListener((a, oldVal, newVal) -> {
                    if (newVal) {
                        curController.onClose();
                        drawer.close();
                        drawer.setVisible(false);
                        enableSearchPane();
                    }
                });

            }catch (IOException e1) {
                e1.printStackTrace();
            } {


            }




        }
    }
    @FXML public void onOpenerClick(MouseEvent e){
        //TODO fix double click breaking this guy.. this dang guy though, no ree
        try {
            System.out.println("opening");
            disableSearchPane();
            drawer.setVisible(true);
            FXMLLoader openerLoader = new FXMLLoader();
            curController = new AdminPaneController();
            System.out.println(getClass().getResource(curController.getFXMLPath()));
            openerLoader.setLocation(getClass().getResource(curController.getFXMLPath()));
            openerLoader.setController(curController);
            openerLoader.load();
            //this ties the size of the parentPane of the controller to the size of the drawer
            curController.getParentPane().prefHeightProperty().bind(drawer.heightProperty());
            curController.onOpen();
            System.out.println("in the main "+curController);
            System.out.println(curController.getParentPane().getPrefWidth());
            drawer.setDefaultDrawerSize(curController.getParentPane().getPrefWidth());
            drawer.setSidePane(curController.getParentPane());
            drawer.open();
            curController.getClosing().addListener((a, oldVal, newVal) -> {
                if (newVal) {
                    curController.onClose();
                    drawer.close();
                    drawer.setVisible(false);
                   enableSearchPane();
                }
            });
        }
        catch(IOException error){
            error.printStackTrace();
        }
    }




    @FXML private void onLoginClick(MouseEvent e){
        try {
            if(!ProgramSettings.getInstance().getIsLoggedInProp().get()) {
                Stage logInStage = new Stage();
                FXMLLoader loader = new FXMLLoader();
                loader.setResources(Translator.getInstance().getNewBundle());
                loader.setLocation(getClass().getResource("/PopUps/Login.fxml"));
                logInStage.setScene(new Scene(loader.load()));
                logInStage.resizableProperty().set(false);
                logInStage.resizableProperty().set(false);
                logInStage.initModality(Modality.APPLICATION_MODAL);
                logInStage.showAndWait();
                if(ProgramSettings.getInstance().getIsLoggedInProp().get()) // launch it right after login, leave no chance for double click
                   onOpenerClick(null); // TODO stupid fix, decide on if this work.

            }
            else{
                ProgramSettings.getInstance().getIsLoggedInProp().set(false);
                imgLogOut.setFitHeight(NOTIFICATION_SIZE);
                imgLogOut.setFitWidth(NOTIFICATION_SIZE);
                Notifications notifications = Notifications.create()
                        .title("Log Out Complete")
                        .text("Good Bye!")
                        .graphic(imgLogOut)
                        .hideAfter(Duration.seconds(2))
                        .position(Pos.BOTTOM_CENTER)
                        .onAction(new EventHandler<ActionEvent>() {
                            @Override
                            public void handle(ActionEvent event) {
                                System.out.println("Hi Kent");
                            }
                        });
                notifications.owner(areaPane.getScene().getWindow());
                notifications.show();
                if(drawer.isShown()){
                    if(curController!=null) {
                        curController.onClose();
                    }
                    drawer.close();
                    drawer.setVisible(false);
                    enableSearchPane();
                }
            }

        }
        catch(IOException exception){
            exception.printStackTrace();
        }

    }
    private void enableSearchPane(){
      // hmbDrawerOpener.setDisable(false);
        hbxDrawerBox.getChildren().clear();
        hbxDrawerBox.getChildren().addAll(areaPane);
        searchPane.setVisible(true);
        searchPane.getStyleClass().clear();
        searchPane.getStyleClass().add("searchPane");
    }
    private void disableSearchPane() {
       // hmbDrawerOpener.setDisable(true);
        //this checks to see if the drawer is already there
        hbxDrawerBox.getChildren().clear();
        hbxDrawerBox.getChildren().addAll(drawer, areaPane);
        searchPane.setVisible(false);
        searchPane.getStyleClass().clear();
        searchPane.getStyleClass().add("searchPane-disabled");
    }

    /**
     * Generates a node pop up if able from the given mouse event
     *
     * @param event
     */
    private void generateNodePopUp(MouseEvent event) {
        removeCurrentPopUp(); // only pop up allowed at a time
        System.out.println("CLICK ON NODE BUTTON");
        // Get the node clicked on (if any)
        if(curController!=null) {
            System.out.println(curController.getParentPane().getPrefWidth());
        }
        //TODO ajdust where the node gets drawn based on the current controller
        MapNode nodeAt = mapDrawing.nodeAt(new Location(event, mapDrawing.getCurrentFloor()));

        if (nodeAt != null) {
            System.out.println("CLICKED ON " + nodeAt.getId());

            // Load the screen in and display it on the cursor
            FXMLLoader loader = new FXMLLoader();
            loader.setResources(Translator.getInstance().getNewBundle());
            loader.setLocation(getClass().getResource("/NodeInfoPopUp.fxml"));
            NodeInfoPopUpController nodePopUp = new NodeInfoPopUpController();
            loader.setController(nodePopUp);
            try {
                nodeInfo = loader.load();
            } catch (IOException e) {
                e.printStackTrace();
            }
            areaPane.getChildren().add(nodeInfo);
            System.out.println(areaPane.getChildren().getClass());
            nodePopUp.setInfo(nodeAt, pathfinding, event);
        } else {
            System.out.println("Clicked on a random location.");
            removeCurrentPopUp();
        }
    }

    //this needs to be edited to close the drawer instead
    private void removeCurrentPopUp() {
        if (nodeInfo != null && areaPane.getChildren().contains(nodeInfo)) {
            areaPane.getChildren().remove(nodeInfo);
            nodeInfo = null;
        }

    }//add methods for login click and translate click


    @FXML private void onEmergencyClick(MouseEvent e){
        pathfinding.genExitPath();

            //TODO double check this.
    }

    //CREATES THE ABOUT PAGE POP UP
    //TODO attach this method to the about button
    @FXML
    private void onAboutClick(MouseEvent e) {
        Stage aboutPopUp = new Stage();
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/AboutPage.fxml"));
        loader.setResources(Translator.getInstance().getNewBundle());
        try {
            Parent root = (Parent) loader.load();
            Scene aboutScene = new Scene(root);
            aboutPopUp.setScene(aboutScene);
            aboutPopUp.resizableProperty().set(false);
            aboutPopUp.initModality(Modality.WINDOW_MODAL);
            aboutPopUp.showAndWait();

        } catch (IOException exception) {
            exception.printStackTrace();
        }

    }
    //END OF ABOUT PAGE POP UP

    //create the help page pop up
    @FXML
    private void onHelpClick(MouseEvent e) {
        Stage helpPopUp = new Stage();
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/HelpPage.fxml"));
        loader.setResources(Translator.getInstance().getNewBundle());

        try {
            Parent root = (Parent) loader.load();
            Scene helpScene = new Scene(root);
            helpPopUp.setScene(helpScene);
            helpPopUp.resizableProperty().set(false);
            helpPopUp.initModality(Modality.WINDOW_MODAL);
            helpPopUp.showAndWait();
        } catch (IOException exception) {
            exception.printStackTrace();
        }

    }

    @FXML
    void onLanBtnClicked(MouseEvent event) {
        Stage languages = new Stage();
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/LanguagesPopOut.fxml"));
        Translator.getInstance().setLang("all");
        loader.setResources(Translator.getInstance().getNewBundle());
        try {
            Parent root = (Parent) loader.load();
            Scene helpScene = new Scene(root);
            languages.setScene(helpScene);
            languages.resizableProperty().set(false);
            languages.initModality(Modality.WINDOW_MODAL);
            languages.showAndWait();
        } catch (IOException exception) {
            exception.printStackTrace();
        }

    }

}

