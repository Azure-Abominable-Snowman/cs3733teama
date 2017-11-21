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
import javafx.scene.canvas.Canvas;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;

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

    private MapDisplay map;

    private boolean drawerExtended = false;

    @FXML
    void onHamburgerButtonClick(MouseEvent event) throws IOException {
        hamOpnsTran.setRate(animRate*=-1);
        hamOpnsTran.play();
        HBox box = FXMLLoader.load(getClass().getResource("/FeatureAccdn.fxml"));
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

        PathfindingController pathfinding = new PathfindingController(MapSubsystem.getInstance(), map, areaPane, searchBar, searchButton);

        // When the hamburger retracts, make it disappear, otherwise appear
        hamOpnsTran.onFinishedProperty().set((ActionEvent e) -> {
            drawer.setVisible(drawerExtended);
        });
    }
}
