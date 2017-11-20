package com.teama.drawing;

import com.teama.mapsubsystem.data.Floor;
import com.teama.mapsubsystem.data.Location;
import javafx.beans.value.ChangeListener;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ScrollPane;
import javafx.scene.paint.Color;

import java.util.HashMap;
import java.util.Map;

public class HospitalMapDisplay implements MapDisplay {

    private ScrollPane pane;
    private Canvas canvas;
    private GraphicsContext gc;

    // The map of the current floor of the hospital
    private HospitalMap curMap;
    // HashMap of all of the floors
    private Map<Floor, HospitalMap> maps;
    // Current floor of the hospital
    private Floor curFloor;

    // Location in the center of the screen
    private Location displayedLocation;

    // List of all the lines, and all the points
    private Map<String, Line> lineMap = new HashMap<>();
    private Map<String, Point> pointMap = new HashMap<>();

    public HospitalMapDisplay(ScrollPane pane, Canvas canvas, Map<Floor, HospitalMap> maps) {
        this.pane = pane;
        this.canvas = canvas;
        this.maps = maps;
        gc = canvas.getGraphicsContext2D();
        setCurrentFloor(Floor.GROUND); // Start off on the ground floor


        // Set the canvas to the same size as the pane initially
        canvas.setWidth(pane.getWidth());
        canvas.setHeight(pane.getHeight());

        ChangeListener<Number> stageSizeListener = (observable, oldValue, newValue) -> {
            updateSize();
        };
        // If the pane is resized make the canvas fill, taking into
        // account the aspect ratio of the image
        pane.heightProperty().addListener(stageSizeListener);
        pane.widthProperty().addListener(stageSizeListener);
    }

    private double oldWidth = 0;
    private double oldHeight = 0;

    private double curZoom = 1.0;

    public void setZoom(double zoom) {
        curZoom = zoom;
        updateSize();
    }

    /**
     * Zooms into the given location, location is in terms of the canvas
     * @param loc
     * @param zoom
     */
    public void zoomInto(Location loc, double zoom) {
        setZoom(zoom);
        setDisplayedLocation(loc);
    }

    /**
     * Gets the current zoom level
     * @return
     */
    public double getZoom() {
        return curZoom;
    }

    /**
     * Update the canvas' and scrollpane size as well as render
     */
    private void updateSize() {
        double width = pane.getWidth();
        double height = pane.getHeight();

        if(oldWidth == 0) {
            oldWidth = width;
        }
        if(oldHeight == 0) {
            oldHeight = height;
        }

        double oldHvalue = pane.getHvalue();
        double oldVvalue = pane.getVvalue();
        if(Double.isNaN(oldVvalue)) {
            oldVvalue = 0;
        }
        if(Double.isNaN(oldHvalue)) {
            oldHvalue = 0;
        }

        pane.setVmax(height);
        pane.setHmax(width);

        renderMap(width, height, curZoom);

        pane.setVvalue((height/oldHeight)*oldVvalue);
        pane.setHvalue((width/oldWidth)*oldHvalue);

        oldWidth = width;
        oldHeight = height;
    }

    /** Renders the map while keeping the aspect ratio of the image the same
     * Zoom should range from 1.0 to infinity.
     * @param width
     * @param height
     * @param curZoom
     */
    private void renderMap(double width, double height, double curZoom) {
        if(curMap != null) { // if it can be obtained, set the width and height using the aspect ratio
            double aspectRatio = curMap.getMap().getWidth()/curMap.getMap().getHeight();
            double scaledW, scaledH;
            // keep the aspect ratio
            scaledH = (1 / ((1 / (width)) * aspectRatio));
            scaledW = ((height)*aspectRatio);
            if(scaledH > height) {
                canvas.setHeight(height*curZoom);
                canvas.setWidth(scaledW*curZoom);
                if(curZoom <= 1) { // If we aren't zoomed, translate the image to the center of the screen
                    canvas.setTranslateX(((width - scaledW) / 2));
                    canvas.setTranslateY(0);
                } else {
                    canvas.setTranslateX(0);
                }
            } else {
                canvas.setHeight(scaledH*curZoom);
                canvas.setWidth(width*curZoom);
                if(curZoom <= 1) { // If we aren't zoomed, translate the image to the center of the screen
                    canvas.setTranslateY(((height - scaledH) / 2));
                    canvas.setTranslateX(0);
                } else {
                    canvas.setTranslateY(0);
                }
            }
        } else {
            canvas.setHeight(height*curZoom);
            canvas.setWidth(width*curZoom);
        }
        render();
    }

    /**
     * Render the canvas without any resize operations
     */
    private void render() {

        // Clear the whole screen
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        // Draw the background image
        gc.drawImage(curMap.getMap(), 0,0, canvas.getWidth(), canvas.getHeight());

        // Draw all of the lines and edges

        // Lines
        for(Line l : lineMap.values()) {
            l.draw(gc);
        }

        // Points
        for(Point p : pointMap.values()) {
            p.draw(gc);
        }
    }

    /**
     * Converts input units to output, useful for converting canvas coordinates to screen coordinates and vice versa
     * @param val
     * @param origMaxVal
     * @param curMaxVal
     * @return
     */
    private double convUnits(double val, double origMaxVal, double curMaxVal) {
        return (val*(curMaxVal/origMaxVal));
    }

    /**
     * Converts coordinates from the canvas to coordinates to the image
     * @param loc
     * @return
     */
    private Location convToImageCoords(Location loc) {
        return new Location((int)convUnits(loc.getxCoord(), canvas.getWidth(), getMaxX()),
                (int)convUnits(loc.getyCoord(), canvas.getHeight(), getMaxY()),
                loc.getLevel(), loc.getBuilding());
    }

    /**
     * Draws a point on the screen, choice between using coordinates based on the canvas or based
     * on the original map image
     * @param id
     * @param loc
     * @param size
     * @param color
     * @param screenCoords
     */
    @Override
    public void drawPoint(String id, Location loc, double size, Color color, boolean screenCoords) {
        if(screenCoords) {
            loc = convToImageCoords(loc);
        }

        Point p = new Point(loc, size, color);
        pointMap.put(id, p);
        render();
    }

    /**
     * Draws a line on the screen, choice between using coordinates based on the canvas or based on the
     * original map image
     * @param id
     * @param start
     * @param end
     * @param weight
     * @param color
     * @param screenCoords
     */
    @Override
    public void drawLine(String id, Location start, Location end, double weight, Color color, boolean screenCoords) {
        if(screenCoords) {
            start = convToImageCoords(start);
            end = convToImageCoords(end);
        }
        Line l = new Line(start, end, weight, color);
        lineMap.put(id, l);
        render();
    }

    @Override
    public Floor getCurrentFloor() {
        return curFloor;
    }

    @Override
    public void setCurrentFloor(Floor floor) {
        curFloor = floor;
        curMap = maps.get(floor);
    }

    /**
     * Sets the displayed location for the map
     * @param loc
     */
    @Override
    public void setDisplayedLocation(Location loc) {
        pane.setVvalue(loc.getyCoord());
        pane.setHvalue(loc.getxCoord());
        setCurrentFloor(loc.getLevel());
        displayedLocation = getDisplayedLocation();
    }

    /**
     * Updates and returns the location at the middle of the screen
     * @return
     */
    @Override
    public Location getDisplayedLocation() {
        this.displayedLocation  = new Location((int)pane.getHvalue(), (int)pane.getVvalue(), curFloor, "Unknown");
        return  displayedLocation;
    }

    /**
     * Gets the width of the current map
     * @return
     */
    @Override
    public double getMaxX() {
        return curMap.getMap().getWidth();
    }

    /**
     * Gets the height of the current map
     * @return
     */
    @Override
    public double getMaxY() {
        return curMap.getMap().getHeight();
    }

    @Override
    public boolean isPointAt(Location loc) {
        return false;
    }

    @Override
    public boolean isLineAt(Location loc) {
        return false;
    }

    // Nested classes for the point and line so we can redraw them later
    private class Line {
        private Location start, end;
        private double weight;
        private Color color;
        public Line(Location start, Location end, double weight, Color color) {
            this.start = start;
            this.end = end;
            this.weight = weight;
            this.color = color;
        }

        public void draw(GraphicsContext gc) {
            double width = canvas.getWidth();
            double height = canvas.getHeight();
            gc.setLineWidth(weight);
            gc.setStroke(color);
            gc.strokeLine(
                    convUnits(start.getxCoord(), getMaxX(), width),
                    convUnits(start.getyCoord(), getMaxY(), height),
                    convUnits(end.getxCoord(), getMaxX(), width),
                    convUnits(end.getyCoord(), getMaxY(), height));
        }
    }

    private class Point {
        private Location loc;
        private double weight;
        private Color color;
        public Point(Location loc, double weight, Color color) {
            this.loc = loc;
            this.weight = weight;
            this.color = color;
        }

        public void draw(GraphicsContext gc) {
            gc.setFill(color);
            double width = canvas.getWidth();
            double height = canvas.getHeight();
            double nodeX = convUnits(loc.getxCoord(), getMaxX(), width);
            double nodeY = convUnits(loc.getyCoord(), getMaxY(), height);
            gc.fillOval(nodeX, nodeY, weight, weight);
        }
    }
}
