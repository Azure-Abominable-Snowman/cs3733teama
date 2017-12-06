package com.teama.mapdrawingsubsystem;

import com.teama.mapsubsystem.data.Floor;
import com.teama.mapsubsystem.data.Location;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public interface MapDisplay {
    void drawPoint(String id, Location loc, double size, Color color, boolean screenCoords);
    void drawPoint(String id, Location loc, double size, Color color, boolean clickable, boolean screenCoords);
    void drawLine(String id, Location start, Location end, double weight, Color color, boolean screenCoords);
    void drawLine(String id, Location start, Location end, double weight, Color color, boolean arrow, boolean screenCoords);
    void drawImage(String id, Image img, Location center, boolean screenCoords);
    void drawText(String id, String text, Location center, Font f, boolean screenCoords);

    void deletePoint(String id);
    void deleteLine(String id);
    void deleteImage(String id);
    void deleteText(String id);

    void clearText();

    String pointAt(Location loc);
    String lineAt(Location loc);

    String pathPointAt(Location loc);

    Floor getCurrentFloor();
    void setCurrentFloor(Floor floor);

    void setDisplayedLocation(Location loc);
    Location getDisplayedLocation();

    double getMaxX();
    double getMaxY();

    void setZoom(double zoom);
    double getZoom();
    void zoomInto(Location loc, double zoom);
    void setGrow(boolean grow);
    boolean getGrow();

    Canvas getUnderlyingCanvas();
    ScrollPane getUnderlyingScrollPane();

    public Location convToImageCoords(Location loc);

    void clear();
}
