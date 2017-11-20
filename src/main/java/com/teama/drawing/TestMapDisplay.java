package com.teama.drawing;

import com.teama.mapsubsystem.data.Floor;
import com.teama.mapsubsystem.data.Location;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class TestMapDisplay extends Application {
    public static void main(String args[]) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/TestMapDisplay.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            primaryStage.setTitle("Map Display Test");
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @FXML
    private ScrollPane mapScroll;

    @FXML
    private Canvas mapCanvas;

    private MapDisplay map;

    private double prevX, prevY;
    private int idCounter = 0;

    @FXML
    void doSomething(MouseEvent event) {
        System.out.println("MOUSE PRESSED ON CANVAS");

        if(event.isShiftDown()) {
            System.out.println("KEY PRESSED");
            if(map.getZoom() == 1) {
                map.zoomInto(new Location((int)event.getX(), (int)event.getY(), map.getCurrentFloor(), "Unknown"), 5);
            } else {
                map.setZoom(1);
            }
            return;
        }

        Location start = new Location((int)event.getX(), (int)event.getY(), Floor.GROUND, "");
        Location end = new Location((int)prevX, (int)prevY, Floor.GROUND, "");

        if(prevX == 0 && prevY == 0) {
            end = start;
        }

        map.drawLine(Integer.toString(idCounter), start, end, 2, Color.RED, true);

        prevX = event.getX();
        prevY =  event.getY();

        map.drawPoint(Integer.toString(idCounter), start, 5, Color.BLUE, true);

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
