package com.teama.drawing;

import com.teama.mapsubsystem.data.Floor;
import com.teama.mapsubsystem.data.Location;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.ScrollPane;
import javafx.scene.paint.Color;

public class HospitalMapDisplay implements MapDisplay {

    private ScrollPane pane;
    private Canvas canvas;

    public HospitalMapDisplay(ScrollPane pane, Canvas canvas) {
        this.pane = pane;
        this.canvas = canvas;
    }

    @Override
    public void drawPoint(Location loc, double size, Color color) {

    }

    @Override
    public void drawLine(double weight, Color color) {

    }

    @Override
    public Floor getCurrentFloor() {
        return null;
    }

    @Override
    public void setCurrentFloor(Floor floor) {

    }

    @Override
    public void setDisplayedLocation(Location loc) {

    }

    @Override
    public Location getDisplayedLocation() {
        return null;
    }

    @Override
    public int getMaxX() {
        return 0;
    }

    @Override
    public int getMaxY() {
        return 0;
    }

    @Override
    public boolean isPointAt(Location loc) {
        return false;
    }

    @Override
    public boolean isLineAt(Location loc) {
        return false;
    }
}
