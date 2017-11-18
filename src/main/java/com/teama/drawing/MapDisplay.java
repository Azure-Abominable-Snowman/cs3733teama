package com.teama.drawing;

import com.teama.mapsubsystem.data.Floor;
import com.teama.mapsubsystem.data.Location;
import javafx.scene.paint.Color;

public interface MapDisplay {
    void drawPoint(Location loc, double size, Color color);
    void drawLine(double weight, Color color);
    Floor getCurrentFloor();
    void setCurrentFloor(Floor floor);
    void setDisplayedLocation(Location loc);
    Location getDisplayedLocation();
    int getMaxX();
    int getMaxY();
    boolean isPointAt(Location loc);
    boolean isLineAt(Location loc);
}
