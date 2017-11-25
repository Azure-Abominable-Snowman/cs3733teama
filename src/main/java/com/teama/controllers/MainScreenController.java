package com.teama.controllers;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDrawer;
import com.jfoenix.controls.JFXHamburger;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.transitions.hamburger.HamburgerBackArrowBasicTransition;
import com.teama.drawing.HospitalMap;
import com.teama.drawing.HospitalMapDisplay;
import com.teama.drawing.MapDisplay;
import com.teama.drawing.ProxyMap;
import com.teama.mapsubsystem.MapSubsystem;
import com.teama.mapsubsystem.data.Floor;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class MainScreenController implements Initializable {
    private int animRate=-1;
    private HamburgerBackArrowBasicTransition hamOpnsTran;

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
    private JFXTextField searchBar;

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

    @FXML
    void onHamburgerButtonClick(MouseEvent event) throws IOException {
        hamOpnsTran.setRate(animRate*=-1);
        hamOpnsTran.play();
        HBox box = FXMLLoader.load(getClass().getResource("/PathfindingAccordion.fxml"));
        box.toFront();
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

        drawer.setSidePane();
        hamOpnsTran = new HamburgerBackArrowBasicTransition(hamburgerButton);

        Map<Floor, HospitalMap> imgs = new HashMap<>();
        imgs.put(Floor.SUBBASEMENT, new ProxyMap("/maps/L2.png"));
        imgs.put(Floor.BASEMENT, new ProxyMap("/maps/L1.png"));
        imgs.put(Floor.GROUND, new ProxyMap("/maps/G.png"));
        imgs.put(Floor.ONE, new ProxyMap("/maps/1.png"));
        imgs.put(Floor.TWO, new ProxyMap("/maps/2.png"));
        imgs.put(Floor.THREE, new ProxyMap("/maps/3.png"));

        map = new HospitalMapDisplay(mapScroll, mapCanvas, imgs);
        map.setGrow(true);
        map.setZoom(1.5);

        PathfindingController pathfinding = new PathfindingController(MapSubsystem.getInstance(), map, areaPane, searchBar, searchButton, floorButtonBox);

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
            FXMLLoader loader = new FXMLLoader();
            //StaffLoginController loginController = loader.getController();


            Scene loginScene = new Scene(loader.load(getClass().getResource("/LogIn2.fxml")));
            //loginPopup.setScene((AnchorPane)));
            StaffLoginController loginController = new StaffLoginController();

            loginController.setLoggedIn(false);

            loginController.getLoggedInProperty().addListener((obs, before, nowLoggedIn) -> {
                if (nowLoggedIn) {
                    loginPopup.hide();
                    login.setVisible(false);
                }
            });


            loginPopup.setScene(loginScene);
            loginPopup.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void hideLoginButton() {
        login.setVisible(false);
    }


}
