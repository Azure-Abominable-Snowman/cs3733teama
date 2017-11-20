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

    private boolean grow = false;

    /**
     * Sets the grow variable. Should the map grow to fit the whole screen when resizing or should the
     * map shrink to fit the screen and maintain aspect ratio.
     * @param grow
     */
    public void setGrow(boolean grow) {
        this.grow = grow;
    }

    public boolean getGrow() {
        return grow;
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

        if(grow) {
            renderMapGrow(width, height, curZoom);
        } else {
            renderMap(width, height, curZoom);
        }

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
     * Renders the map but has a preference for growth rather than fitting within the screen
     * @param width
     * @param height
     * @param curZoom
     */
    private void renderMapGrow(double width, double height, double curZoom) {
        if(curMap != null) { // if it can be obtained, set the width and height using the aspect ratio
            double aspectRatio = curMap.getMap().getWidth()/curMap.getMap().getHeight();
            double scaledW, scaledH;
            // keep the aspect ratio
            scaledH = (1 / ((1 / (width)) * aspectRatio));
            scaledW = ((height)*aspectRatio);
            if(scaledH <= pane.getHeight()) {
                canvas.setHeight(height*curZoom);
                canvas.setWidth(scaledW*curZoom);
                if(curZoom <= 1) { // If we aren't zoomed, translate the image to the center of the screen
                    canvas.setTranslateX(((width - scaledW) / 2));
                    canvas.setTranslateY(0);
                } else {
                    canvas.setTranslateX(0);
                }
            }

            if(scaledW <= pane.getWidth()) {
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

        Point p = new Point(id, loc, size, color);
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
        Line l = new Line(id, start, end, weight, color);
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


    private boolean isPointOnLoc(Location loc, Point point) {
        loc = convToImageCoords(loc);
        Location pointLoc = point.getLoc();

        return ((loc.getxCoord() <= pointLoc.getxCoord()+(point.getWeight()) && loc.getxCoord() >= pointLoc.getxCoord()-(point.getWeight())) &&
                (loc.getyCoord() <= pointLoc.getyCoord()+(point.getWeight()) && loc.getyCoord() >= pointLoc.getyCoord()-(point.getWeight())));
    }

    @Override
    public String pointAt(Location loc) {
        for (Point p : pointMap.values()) {
            if (isPointOnLoc(loc, p)) {
                return p.getId();
            }
        }
        return null;
    }

    /**
     * finds if a point is on a line, doesn't work yet...
     * @param loc
     * @param line
     * @return
     */
    private boolean isPointOnLine(Location loc, Line line) {
        System.out.println("SX: "+line.getStart().getxCoord()+" SY: "+line.getStart().getyCoord()+" EX: "+line.getEnd().getxCoord()+" EY: "+line.getEnd().getyCoord());
        try {
            double slope = ((line.getStart().getyCoord() - line.getEnd().getyCoord())/(line.getStart().getxCoord() - line.getEnd().getxCoord()));
            double invSlope = 1 / slope;
            loc = convToImageCoords(loc);
            System.out.println("CONVY: "+loc.getyCoord()+" CONVX: "+loc.getxCoord());

            double yGuess = slope*loc.getxCoord()-slope*line.getStart().getxCoord()+line.getStart().getyCoord();
            double xGuess = loc.getyCoord()*invSlope-line.getStart().getyCoord()*invSlope+line.getStart().getxCoord();
            System.out.println("YGUESS: "+yGuess+" XGUESS: "+xGuess);

            double yDist = yGuess-loc.getyCoord();
            double xDist = xGuess-loc.getxCoord();

            System.out.println("YDIST: "+yDist+" XDIST: "+xDist);

            /*double bound = Math.cos(Math.atan((xDist/yDist)))*yDist;

            System.out.println("BOUND: "+bound);*/

        } catch(ArithmeticException e) {
            return false;
        }

        return false;
    }

    @Override
    public String lineAt(Location loc) {
        System.out.println("Y: "+loc.getyCoord()+" X: "+loc.getxCoord());
        for(Line l : lineMap.values()) {
            if(isPointOnLine(loc, l)) {
                //return l.getId();
                //TODO: make this work
                return null;
            }
        }
        return null;
    }

    // Nested classes for the point and line so we can redraw them later
    private class Line {
        private Location start, end;
        private double weight;
        private Color color;
        private String id;
        public Line(String id, Location start, Location end, double weight, Color color) {
            this.start = start;
            this.end = end;
            this.weight = weight;
            this.color = color;
            this.id = id;
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

        public Location getStart() {
            return start;
        }

        public Location getEnd() {
            return end;
        }

        public double getWeight() {
            return weight;
        }

        public String getId() {
            return id;
        }
    }

    // Stores location data in the format of the image, not the canvas
    private class Point {
        private Location loc;
        private double weight;
        private Color color;
        private String id;
        public Point(String id, Location loc, double weight, Color color) {
            this.loc = loc;
            this.weight = weight;
            this.color = color;
            this.id = id;
        }

        public void draw(GraphicsContext gc) {
            gc.setFill(color);
            double width = canvas.getWidth();
            double height = canvas.getHeight();
            double nodeX = convUnits(loc.getxCoord(), getMaxX(), width);
            double nodeY = convUnits(loc.getyCoord(), getMaxY(), height);
            gc.fillOval(nodeX-(weight/2), nodeY-(weight/2), weight, weight);
        }

        public Location getLoc() {
            return loc;
        }

        public double getWeight() {
            return weight;
        }

        public String getId() {
            return id;
        }
    }
}
