package com.teama.drawing;

import com.teama.controllers.NodeInfoPopUpController;
import com.teama.mapsubsystem.data.*;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class TestMapDisplay extends Application {
    public static void main(String args[]) {
        launch(args);
    }

    private Scene primaryScene;

    @Override
    public void start(Stage primaryStage) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/TestMapDisplay.fxml"));
            Parent root = loader.load();
            primaryScene = new Scene(root);
            primaryStage.setTitle("Map Display Test");
            primaryStage.setScene(primaryScene);
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @FXML
    private ScrollPane mapScroll;

    @FXML
    private Canvas mapCanvas;

    @FXML
    private AnchorPane outerPane;

    private MapDisplay map;

    private double prevX, prevY;
    private int idCounter = 0;

    private Parent nodeInfo;

    @FXML
    protected void doSomething(MouseEvent event) throws IOException {
        System.out.println("MOUSE PRESSED ON CANVAS");

        if(event.isShiftDown()) {
            System.out.println("ZOOM");
            if(map.getZoom() == 1) {
                map.zoomInto(new Location((int)event.getX(), (int)event.getY(), map.getCurrentFloor(), "Unknown"), 5);
            } else {
                map.setZoom(1);
            }
            return;
        }

        if(event.isControlDown()) {
            System.out.println("LOOK FOR POINT OR LINE");
            if(map.pointAt(new Location((int)event.getX(), (int)event.getY(), map.getCurrentFloor(), "Unknown")) != null) {
                System.out.println("Point!");
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(getClass().getResource("/NodeInfoPopUp.fxml"));
                nodeInfo = loader.load();
                NodeInfoPopUpController ni = loader.getController();
                MapNode n = new MapNodeData("test", null, NodeType.HALL, "Really really long description", "Short Des", "Team A");
                ni.setNode(n);

                // Create pane to load nodeInfo root node into
                outerPane.getChildren().add(nodeInfo);

                double newX = event.getSceneX()-nodeInfo.getBoundsInParent().getWidth()/2;
                double newY = event.getSceneY()-nodeInfo.getBoundsInParent().getHeight()*4.5;

                if(newX <= outerPane.getWidth()-nodeInfo.getBoundsInParent().getWidth() && newX >= 0) {
                    nodeInfo.setTranslateX(newX);
                } else if (newX > 0) {
                    nodeInfo.setTranslateX(outerPane.getWidth()-nodeInfo.getBoundsInParent().getWidth()*1.2);
                }

                if(newY <= outerPane.getHeight() && newY >= 0) {
                    System.out.println("TRANSLATE Y");
                    nodeInfo.setTranslateY(newY);
                }
            }
            //System.out.println(map.lineAt(new Location((int)event.getX(), (int)event.getY(), map.getCurrentFloor(), "Unknown")));
            return;
        }

        if(nodeInfo != null) {
            outerPane.getChildren().remove(nodeInfo);
        }

        Location start = new Location((int)event.getX(), (int)event.getY(), Floor.GROUND, "");
        Location end = new Location((int)prevX, (int)prevY, Floor.GROUND, "");

        if(prevX == 0 && prevY == 0) {
            end = start;
        }

        map.drawLine(Integer.toString(idCounter), start, end, 2, Color.RED, true);

        prevX = event.getX();
        prevY =  event.getY();

        map.drawPoint(Integer.toString(idCounter), start, 10, Color.BLUE, true);

        idCounter++;

/*
        Location one = new Location(0, 100, Floor.GROUND, "");
        Location two = new Location(100, 100, Floor.GROUND, "");
        Location three = new Location(100, 200, Floor.GROUND, "");
        Location four = new Location(0, 200, Floor.GROUND, "");

        map.drawLine(Integer.toString(idCounter), one, two, 2, Color.GREEN);
        idCounter++;
        map.drawLine(Integer.toString(idCounter), two, three, 2, Color.GREEN);
        idCounter++;
        map.drawLine(Integer.toString(idCounter), three, four, 2, Color.GREEN);
        idCounter++;
        map.drawLine(Integer.toString(idCounter), four, one, 2, Color.GREEN);*/
    }

    public void initialize() {
        Map<Floor, HospitalMap> imgs = new HashMap<>();
        imgs.put(Floor.SUBBASEMENT, new ProxyMap("/maps/L2.png"));
        imgs.put(Floor.BASEMENT, new ProxyMap("/maps/L1.png"));
        imgs.put(Floor.GROUND, new ProxyMap("/maps/G.png"));
        imgs.put(Floor.ONE, new ProxyMap("/maps/1.png"));
        imgs.put(Floor.TWO, new ProxyMap("/maps/2.png"));
        imgs.put(Floor.THREE, new ProxyMap("/maps/3.png"));
        map = new HospitalMapDisplay(mapScroll, mapCanvas, imgs);

        System.out.println("INITIALIZED");
    }

}
