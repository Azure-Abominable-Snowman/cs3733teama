package com.teama.drawing;

import com.teama.mapsubsystem.data.Floor;
import com.teama.mapsubsystem.data.Location;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.ScrollPane;
import javafx.scene.paint.Color;

public interface MapDisplay {
    void drawPoint(String id, Location loc, double size, Color color, boolean screenCoords);
    void drawLine(String id, Location start, Location end, double weight, Color color, boolean screenCoords);
    Floor getCurrentFloor();
    void setCurrentFloor(Floor floor);
    void setDisplayedLocation(Location loc);
    Location getDisplayedLocation();
    double getMaxX();
    double getMaxY();
    String pointAt(Location loc);
    String lineAt(Location loc);
    void setZoom(double zoom);
    double getZoom();
    void zoomInto(Location loc, double zoom);
    void setGrow(boolean grow);
    boolean getGrow();
    Canvas getUnderlyingCanvas();
    ScrollPane getUnderlyingScrollPane();
}
