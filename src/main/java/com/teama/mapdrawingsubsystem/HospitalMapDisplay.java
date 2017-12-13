package com.teama.mapdrawingsubsystem;

import com.teama.mapsubsystem.data.*;
import com.teama.mapsubsystem.pathfinding.Path;
import javafx.animation.AnimationTimer;
import javafx.beans.value.ChangeListener;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.*;

import static java.lang.Math.PI;

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

    // Map of all the lines, and all the points
    private Map<String, Line> lineMap = new HashMap<>();
    private Map<String, Point> pointMap = new HashMap<>();

    // Map of all of the images
    private Map<String, Img> imgMap = new HashMap<>();

    // Map of all the text on the screen
    private Map<String, Text> textMap = new HashMap<>();

    // Map of all the paths on the screen
    private Map<String, DrawPath> pathMap = new HashMap<>();

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

        // Draw Paths
        for(DrawPath p : pathMap.values()) {
            p.draw(gc);
        }

        // Draw all of the lines and edges

        // Lines
        for(Line l : lineMap.values()) {
            l.draw(gc);
        }

        // Points
        for(Point p : pointMap.values()) {
            p.draw(gc);
        }

        // Images
        for(Img i : imgMap.values()) {
            i.draw(gc);
        }

        // Text
        for(Text t : textMap.values()) {
            t.draw(gc);
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
    public Location convToImageCoords(Location loc) {
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
        drawPoint(id, loc, size, color, true, screenCoords);
    }

    /**
     *
     * @param id
     * @param newLoc
     */
    public void dragPoint(String id, Location newLoc) {
        Point oldPoint = pointMap.get(id);

        pointMap.remove(id);
        render();
        pointMap.put(id, new Point(id, newLoc, oldPoint.getWeight(), oldPoint.getColor(), oldPoint.getClickable()));
        render();



    }

    @Override
    public void drawPoint(String id, Location loc, double size, Color color, boolean clickable, boolean screenCoords) {
        if(screenCoords) {
            loc = convToImageCoords(loc);
        }

        Point p = new Point(id, loc, 7, color, clickable);

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
        drawLine(id, start, end, weight, color, false, screenCoords);
    }

    // TODO: This is a lot of repeated code, needs to be factored out somehow...
    @Override
    public void drawLine(String id, Location start, Location end, double weight, Color color, boolean arrow, boolean screenCoords) {
        if(screenCoords) {
            start = convToImageCoords(start);
            end = convToImageCoords(end);
        }
        Line l = new Line(id, start, end, weight, color, arrow);
        lineMap.put(id, l);
        render();
    }

    @Override
    public void drawImage(String id, Image img, Location center, boolean screenCoords) {
        if(screenCoords) {
            center = convToImageCoords(center);
        }

        Img i = new Img(id, img, center);
        imgMap.put(id, i);
        render();
    }

    @Override
    public void drawText(String id, String text, Location center, Font f, boolean screenCoords) {
        if(screenCoords) {
            center = convToImageCoords(center);
        }

        Text t = new Text(id, text, f, center);
        textMap.put(id, t);
        render();
    }

    @Override
    public void drawPath(String id, Path path) {
        DrawPath p = new DrawPath(id, path);
        pathMap.put(id, p);
        p.startAnimation();
        render();
    }

    @Override
    public void deletePoint(String id) {
        System.out.println(pointMap.keySet());

        pointMap.remove(id);
        System.out.println(pointMap.keySet());
        render();
    }


    @Override
    public void deleteLine(String id) {
        System.out.println(lineMap.keySet());
        lineMap.remove(id);
        System.out.println(lineMap.keySet());
        render();
    }

    @Override
    public void deleteImage(String id) {
        imgMap.remove(id);
        render();
    }

    @Override
    public void deleteText(String id) {
        textMap.remove(id);
        render();
    }

    @Override
    public void deletePath(String id) {
        pathMap.get(id).remove();
        pathMap.remove(id);
        render();
    }

    /**
     * Clears all the text from the map
     */
    @Override
    public void clearText() {
        textMap.clear();
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
        render();
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

    @Override
    public void setDisplayedLocation(Location loc, boolean screenCoords) {
        System.out.println("OLD X: "+loc.getxCoord()+" OLD Y "+loc.getyCoord());
        if (!screenCoords) {
            double newX = (loc.getxCoord()*canvas.getWidth()) / curMap.getMap().getWidth(); // map it onto canvas
            newX =  (newX - pane.getHmax()/2) * ( pane.getHmax() / (canvas.getWidth()-pane.getHmax())); // map it onto scrollbar

            double  newY = (loc.getyCoord()*canvas.getHeight()) / curMap.getMap().getHeight(); // map it onto canvas
            newY = (newY - pane.getVmax()/2) * (pane.getVmax()) / (canvas.getHeight()-pane.getVmax()); // map it onto scrollbar
            loc = new Location((int)(newX), (int)(newY), loc.getLevel(), loc.getBuilding());
        }
        System.out.println("NEW X: "+loc.getxCoord()+" NEW Y "+loc.getyCoord());
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

        double pointDiameter = point.getWeight()*2;

        return ((loc.getxCoord() <= pointLoc.getxCoord()+(pointDiameter) && loc.getxCoord() >= pointLoc.getxCoord()-(pointDiameter)) &&
                (loc.getyCoord() <= pointLoc.getyCoord()+(pointDiameter) && loc.getyCoord() >= pointLoc.getyCoord()-(pointDiameter)));
    }

    @Override
    public String pointAt(Location loc) {
        for (Point p : pointMap.values()) {
            if (p.getClickable() && isPointOnLoc(loc, p)) {
                return p.getId();
            }
        }
        return null;
    }

    public void clear() {
        lineMap = new HashMap<>();
        pointMap = new HashMap<>();
        render();
    }

    /**
     * finds if a point is on a line,
     * @param loc
     * @param line
     * @return
     */
    private boolean isPointOnLine(Location loc, Line line) {


        //System.out.println("SX: "+line.getStart().getxCoord()+" SY: "+line.getStart().getyCoord()+" EX: "+line.getEnd().getxCoord()+" EY: "+line.getEnd().getyCoord());
        //System.out.println("Weight: " + line.getWeight());
        if (isInBounds(loc, line)) {
            Vector edge = new Vector(line.getStart(), line.getEnd());
            Vector click = new Vector(line.getStart(), loc);

            double weight = line.getWeight();
            if (weight <= 5) {
                weight = 12;
            }

            double scalarProj = click.scalarProjection(edge); // scalar project of click onto edge
            double yCompClick = Math.sqrt(Math.pow(click.magntiude(), 2) - Math.pow(scalarProj, 2)); // get y component of clicked location
            //System.out.println("Click Magnitude: " + click.magntiude() + " Scalar Projection: " + scalarProj + " yComponent of Click: " + yCompClick);

            return (yCompClick <= weight / 2);
        }
        return false;



    }

    private boolean isInBounds(Location loc, Line line) {
        double weight = line.getWeight();
        if (line.getWeight() <= 5) {
            weight = 12;
        }

        double inputX = loc.getxCoord();
        double inputY = loc.getyCoord();

        double x1 = line.getStart().getxCoord();
        double y1 = line.getStart().getyCoord();
        double bound = weight/2;

        double x2 = line.getEnd().getxCoord();
        double y2 = line.getEnd().getyCoord();
        boolean inXBounds = false;
        if (line.getStart().getxCoord() <= line.getEnd().getxCoord()) {
            inXBounds = inputX >= (line.getStart().getxCoord()-bound) && (inputX <=(line.getEnd().getxCoord()+ bound));

        }
        else {
            inXBounds = inputX >= (line.getEnd().getxCoord()-bound) && inputX <= (line.getStart().getxCoord() + bound);
        }

        boolean inYBounds = false;
        if (line.getStart().getyCoord() <= line.getEnd().getyCoord()) {
            inYBounds = inputY >= (line.getStart().getyCoord()-bound) && inputY <= (line.getEnd().getyCoord() + bound);
        }
        else {
            inYBounds = inputY >= (line.getEnd().getyCoord()-bound) && inputY <= (line.getStart().getyCoord()+bound);

        }
        if (inXBounds && inYBounds) {
            System.out.println(" Input x: " + inputX + " Input Y: " + inputY + " Xbound: " + line.getStart().getxCoord() + " " + line.getEnd().getxCoord());
            System.out.println(" YBound: " + line.getStart().getyCoord() + " " + line.getEnd().getyCoord());
        }
        return inXBounds && inYBounds;

            /*
            x1 = line.getEnd().getxCoord();
            y1 = line.getEnd().getyCoord();
            x2 = line.getStart().getxCoord();
            y2 = line.getStart().getyCoord();
            */

        /*
        boolean inBounds = false;
        if ((loc.getxCoord()>=x1 && loc.getxCoord() <= x2 ) && (loc.getyCoord()>= y1 && loc.getyCoord() <= y2)) {
            System.out.println("Line: x1 = " + x1 + " x2 = " + x2 + " y1 = " + y1 + " y2 = " + y2 + " clicked: " + loc.getxCoord() + " y: " + loc.getyCoord());
            inBounds = true;
        }
        System.out.println("Line: x1 = " + x1 + " x2 = " + x2 + " y1 = " + y1 + " y2 = " + y2 + " clicked: " + loc.getxCoord() + " y: " + loc.getyCoord());

        System.out.println("Not in bounds. ");
        return inBounds;
        */

    }

    private class Vector{
        public double x=0  , y=0 ;
        public double norm=-1;
        public Vector(Location start,Location end){
            x = end.getxCoord() - start.getxCoord();
            y = end.getyCoord() - start.getyCoord();
        }
        public Vector(){}

        /**
         * get the projection from b onto this vector
         * @param b the vector that will project onto this vector.
         * @return the this proj b result.
         */

        public Vector projection(Vector b){
            Vector result  = new Vector();
            result.norm= (this.x*b.x+this.y*b.y) / b.getNorm();
            result.x = b.x/b.getNorm();
            result.y = b.y/b.getNorm();
            return result;
        }

        public double magntiude() {
            return Math.sqrt(Math.pow(this.x, 2) + Math.pow(this.y, 2));
        }

        /**
         *
         * @return double, scalar project of 'this' vector onto second vector
         */
        public double scalarProjection(Vector b) {
            double dotProd = this.x*b.x + this.y*b.y;
            double magnitudeB = b.magntiude();
            System.out.println(dotProd/magnitudeB);
            return dotProd/magnitudeB;
        }

        public double  getNorm (){
            if ( norm<0) norm = Math.sqrt(x*x+y*y); // lazy initialize.
            return norm;
        }



    }

    @Override
    public String lineAt(Location loc) {
        System.out.println("Y: "+loc.getyCoord()+" X: "+loc.getxCoord());
        loc = convToImageCoords(loc);
        for(Line l : lineMap.values()) {
            if(isPointOnLine(loc, l)) {
                String id = l.getId();
                return id;
            }
        }
        return null;
    }

    @Override
    public String pathPointAt(Location loc) {
        return null;
    }

    @Override
    public Canvas getUnderlyingCanvas() {
        return canvas;
    }

    @Override
    public ScrollPane getUnderlyingScrollPane() {
        return pane;
    }

    // Nested classes for the point and line so we can redraw them later
    private class Line {
        private Location start, end;
        private double weight;
        private Color color;
        private String id;
        private boolean arrow;

        public Line(String id, Location start, Location end, double weight, Color color, boolean arrow) {
            this.start = start;
            this.end = end;
            this.weight = weight;
            this.color = color;
            this.id = id;
            this.arrow = arrow;
        }

        public void draw(GraphicsContext gc) {
            /*double width = canvas.getWidth();
            double height = canvas.getHeight();
            gc.setLineWidth(weight);
            gc.setStroke(color);
            gc.strokeLine(
                    convUnits(start.getxCoord(), getMaxX(), width),
                    convUnits(start.getyCoord(), getMaxY(), height),
                    convUnits(end.getxCoord(), getMaxX(), width),
                    convUnits(end.getyCoord(), getMaxY(), height));*/
            draw(gc, arrow);
        }



        public void draw(GraphicsContext gc, boolean arrow) {
            double width = canvas.getWidth();
            double height = canvas.getHeight();
            gc.setLineWidth(weight);
            gc.setStroke(color);
            double node1X =  convUnits(start.getxCoord(), getMaxX(), width);
            double node1Y = convUnits(start.getyCoord(), getMaxY(), height);
            double node2X = convUnits(end.getxCoord(), getMaxX(), width);
            double node2Y = convUnits(end.getyCoord(), getMaxY(), height);
            if(arrow) {
                double arrowAngle = Math.toRadians(45.0);
                double arrowLength = 10.0;
                double dx = node1X - node2X;
                double dy = node1Y - node2Y;
                double angle = Math.atan2(dy, dx);
                double x1 = Math.cos(angle + arrowAngle) * arrowLength + node2X;
                double y1 = Math.sin(angle + arrowAngle) * arrowLength + node2Y;

                double x2 = Math.cos(angle - arrowAngle) * arrowLength + node2X;
                double y2 = Math.sin(angle - arrowAngle) * arrowLength + node2Y;
                gc.strokeLine(node2X, node2Y, x1, y1);
                gc.strokeLine(node2X, node2Y, x2, y2);
            }
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
        private boolean clickable;

        public Point(String id, Location loc, double weight, Color color, boolean clickable) {
            this.loc = loc;
            this.weight = weight;
            this.color = color;
            this.id = id;
            this.clickable = clickable;
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
        public Color getColor() {
            return color;
        }

        public double getWeight() {
            return weight;
        }

        public String getId() {
            return id;
        }

        public boolean getClickable() { return clickable; }
    }

    // Stores location data for an image so it can be easily redrawn
    private class Img {
        private Location loc;
        private Image img;
        private String id;

        public Img(String id, Image img, Location loc) {
            this.loc = loc;
            this.img = img;
            this.id = id;
        }

        public void draw(GraphicsContext gc) {
            double x = convUnits(loc.getxCoord(), getMaxX(), canvas.getWidth());
            double y = convUnits(loc.getyCoord(), getMaxY(), canvas.getHeight());
            gc.drawImage(img, x, y);
        }

        public Location getLoc() { return loc; }

        public String getId() { return id; }
    }

    // Text drawn on the screen
    private class Text {
        private Location loc;
        private String text;
        private String id;
        private Font f;

        public Text(String id, String text, Font f, Location loc) {
            this.loc = loc;
            this.text = text;
            this.id = id;
            this.f = f;
        }

        public void draw(GraphicsContext gc) {
            double x = convUnits(loc.getxCoord(), getMaxX(), canvas.getWidth());
            double y = convUnits(loc.getyCoord(), getMaxY(), canvas.getHeight());
            // TODO: Draw a box around the text to make it stand out
            gc.setFont(f);
            gc.setFill(Color.BLACK);
            gc.fillText(text, x, y);
        }

        public Location getLoc() { return loc; }
        public String getId() { return id; }
    }

    private class DrawPath {
        private Path path;
        private String id;
        private boolean correctedPath = false;
        private Map<Floor, ArrayList<Text>> textOnFloor = new HashMap<>();
        private MapNode firstOnFloor;
        private MapNode endOnFloor;
        private Point origFirstOnFloor;
        private Point origEndOnFloor;
        private AnimationTimer timer;
        private int curEdgeIdx = 0;
        private ArrayList<MapEdge> edgesOnFloor;
        private double curLenAcc = 0;

        private DrawPath(String id, Path path) {
            this.path = path;
            this.id = id;

            for(Floor f : Floor.values()) {
                textOnFloor.put(f, new ArrayList<>());
            }
        }

        private void startAnimation() {
            timer = new AnimationTimer() {
                @Override
                public void handle(long now) {
                    if (edgesOnFloor.size() <= curEdgeIdx) {
                        curEdgeIdx = 0;
                        return;
                    }
                    render();
                    MapEdge curEdge = edgesOnFloor.get(curEdgeIdx);
                    Location startLoc = curEdge.getStart().getCoordinate();
                    Location endLoc = curEdge.getEnd().getCoordinate();
                    // Find the length of the edge
                    double length = Math.sqrt(Math.pow(startLoc.getxCoord() - endLoc.getxCoord(), 2) +
                            Math.pow(startLoc.getyCoord() - endLoc.getyCoord(), 2));
                    // Find angle of the vector
                    // Add 90 degrees or pi radians to take into account differences in coordinate systems
                    double angle = Math.atan2(startLoc.getyCoord() - endLoc.getyCoord(), startLoc.getxCoord() - endLoc.getxCoord()) + PI;
                    // Increment from 0 to the length of the vector
                    curLenAcc += 4;
                    // Calculate the X and Y position of the point to be placed
                    double ptX = Math.cos(angle) * curLenAcc + startLoc.getxCoord();
                    double ptY = Math.sin(angle) * curLenAcc + startLoc.getyCoord();
                    // Display animated point on the screen
                    gc.fillOval(convUnits(ptX, getMaxX(), canvas.getWidth()) - 4, convUnits(ptY, getMaxY(), canvas.getHeight()) - 4, 8, 8);
                    if (curLenAcc >= length) {
                        curLenAcc = 0;
                        // Switch to the next node
                        curEdgeIdx = (curEdgeIdx + 1) % (edgesOnFloor.size());
                    }
                    //System.out.println("LENGTH: "+length+" ANGLE: "+angle+" PTX: "+ptX+" PTY: "+ptY);
                }
            };
            timer.start();
        }

        private void stopAnimation() {
            timer.stop();
        }

        private void remove() {
            // Remove edges
            for(MapEdge e : path.getConnectors()) {
                lineMap.remove(e.getId());
            }
            // Remove annotations
            Set<String> s = new HashSet<>(textMap.keySet());
            for(String id : s) {
                textMap.remove(id);
            }
            // Remove the differently colored front and end node
            if(origFirstOnFloor != null && pointMap.containsKey(origFirstOnFloor.getId())) {
                pointMap.remove(origFirstOnFloor.getId());
            }
            if(origEndOnFloor != null && pointMap.containsKey(origEndOnFloor.getId())) {
                pointMap.remove(origEndOnFloor.getId());
            }
            stopAnimation();
        }

        private void draw(GraphicsContext gc) {
            if(!correctedPath) {
                correctedPath = true;
                path = genCorrPath();
            }

            // Remove the old annotations from this floor
            for(Text t : textOnFloor.get(getCurrentFloor())) {
                textMap.remove(t.getId());
            }

            // Draw the annotations for this particular floor
            for(Text t : textOnFloor.get(getCurrentFloor())) {
                textMap.put(t.getId(), t);
            }

            // Draw the first and last node on this floor special
            firstOnFloor = null;
            endOnFloor = null;
            edgesOnFloor = new ArrayList<>();

            // Draw all the edges on this floor
            for(MapEdge e : path.getConnectors()) {
                boolean isCurStartOnFloor = e.getStart().getCoordinate().getLevel().equals(getCurrentFloor());
                boolean isCurEndOnFloor = e.getEnd().getCoordinate().getLevel().equals(getCurrentFloor());
                if(firstOnFloor == null && isCurStartOnFloor) {
                    firstOnFloor = e.getStart();
                    endOnFloor = e.getEnd();
                }
                if(isCurEndOnFloor) {
                    endOnFloor = e.getEnd();
                }
                if(isCurStartOnFloor && isCurEndOnFloor) {
                    lineMap.put(e.getId(), new Line(e.getId(), e.getStart().getCoordinate(), e.getEnd().getCoordinate(), 4, Color.CADETBLUE, false));
                    edgesOnFloor.add(e);
                }
            }

            // Draw the start and end nodes special
            if(firstOnFloor != null) {
                origFirstOnFloor = pointMap.get(firstOnFloor.getId());
                pointMap.put(firstOnFloor.getId(), new Point(firstOnFloor.getId(), firstOnFloor.getCoordinate(), 12, Color.RED, true));
            }
            if(endOnFloor != null) {
                origEndOnFloor = pointMap.get(endOnFloor.getId());
                pointMap.put(endOnFloor.getId(), new Point(endOnFloor.getId(), endOnFloor.getCoordinate(), 12, Color.RED, true));
            }
        }

        private Path genCorrPath() {
            Path corrP = new Path();
            // Turn them the right way and then store the edges the correct orientation
            MapNode startNode = path.getNodes().get(0);
            System.out.println("START NODE: " + startNode.getShortDescription());
            // Node where the first part of the path ends
            MapNode lastEnd = null;
            // Start from the second edge and turn all of the connectors the right way around and store them in
            // the new path object
            for (int i = 0; i < path.getConnectors().size(); i++) {
                MapEdge curEdge = path.getConnectors().get(i);
                if (lastEnd == null) {
                    lastEnd = curEdge.getEnd();
                    // The first one always gets swapped, so make it initially backward
                    if (!lastEnd.getId().equals(startNode.getId())) {
                        // Flip so the start node is first
                        lastEnd = path.getConnectors().get(0).getStart();
                    }
                }
                if (curEdge.getStartID().compareTo(lastEnd.getId()) == 0) {
                    // Doesn't need to be swapped
                    //System.out.println(curEdge.getStartID()+" "+curEdge.getEndID()+" NO SWAP");
                    lastEnd = curEdge.getEnd();
                } else {
                    // Needs to be swapped
                    curEdge = new MapEdgeData(curEdge.getId(), curEdge.getEnd(), curEdge.getStart());
                    //System.out.println(curEdge.getStartID()+" "+curEdge.getEndID()+" SWAP");
                    lastEnd = curEdge.getEnd();
                }
                corrP.addNode(curEdge.getStart());
                corrP.addNode(curEdge.getEnd());
                corrP.addEdge(curEdge);

                // See if the floor changes on this, if it does then store an annotation in the hashmap on which floor it goes to
                Floor endFloor = curEdge.getEnd().getCoordinate().getLevel();
                Floor startFloor = curEdge.getStart().getCoordinate().getLevel();
                String annoText = "";
                for(Floor floorToUse : Floor.values()) {
                    if (!startFloor.equals(endFloor) && (startFloor.equals(floorToUse) || endFloor.equals(floorToUse))) {
                        MapNode chFloorNode = curEdge.getStart();
                        // We now know that the floor is being changed, but we need to follow the path until we arrive at the correct floor
                        // Iterate through the the nodes in the path until we arrive on the next floor or the path ends.
                        if (startFloor.equals(floorToUse)) {
                            // For start floor -> different floor
                            for (int j = i + 1; j < path.getConnectors().size(); j++) {
                                // Check if the start and end nodes are on the same floor
                                // If they are then this is the destination floor
                                MapEdge checkEdge = path.getConnectors().get(j);
                                if (checkEdge.getStart().getCoordinate().getLevel().equals(checkEdge.getEnd().getCoordinate().getLevel())) {
                                    annoText = "To " + checkEdge.getStart().getCoordinate().getLevel().toString();
                                    break;
                                }
                            }
                        } else {
                            // For other floor -> start floor
                            // For loop must be reversed
                            for (int j = i; j < path.getConnectors().size(); j--) {
                                MapEdge checkEdge = path.getConnectors().get(j);
                                if (checkEdge.getStart().getCoordinate().getLevel().equals(checkEdge.getEnd().getCoordinate().getLevel())) {
                                    annoText = "From " + checkEdge.getStart().getCoordinate().getLevel().toString();
                                    break;
                                }
                            }
                        }
                        //System.out.println("DRAW "+annoText+" AS AN ANNOTATION WITH ID "+chFloorNode.getId());
                        // TODO: Migrate to images of arrows, and have those be clickable
                        Text newT = new Text(chFloorNode.getId(), annoText, Font.font("Courier", FontWeight.BOLD, 16), chFloorNode.getCoordinate());
                        ArrayList<Text> arrayOfText = textOnFloor.get(floorToUse);
                        arrayOfText.add(newT);
                    }
                }


            }
            return corrP;
        }
    }
}
