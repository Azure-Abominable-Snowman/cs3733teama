package com.teama.mapdrawingsubsystem;

import com.teama.mapsubsystem.data.Floor;
import com.teama.mapsubsystem.data.Location;
import javafx.beans.value.ChangeListener;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

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

    // Map of all the lines, and all the points
    private Map<String, Line> lineMap = new HashMap<>();
    private Map<String, Point> pointMap = new HashMap<>();

    // Map of all of the images
    private Map<String, Img> imgMap = new HashMap<>();

    // Map of all the text on the screen
    private Map<String, Text> textMap = new HashMap<>();

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

    @Override
    public void drawPoint(String id, Location loc, double size, Color color, boolean clickable, boolean screenCoords) {
        if(screenCoords) {
            loc = convToImageCoords(loc);
        }

        Point p = new Point(id, loc, size, color, clickable);
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
    public void deletePoint(String id) {
        pointMap.remove(id);
        render();
    }

    @Override
    public void deleteLine(String id) {
        lineMap.remove(id);
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
     * finds if a point is on a line, //TODO doesn't work yet...
     * @param loc
     * @param line
     * @return
     */
    private boolean isPointOnLine(Location loc, Line line) {

        //System.out.println("SX: "+line.getStart().getxCoord()+" SY: "+line.getStart().getyCoord()+" EX: "+line.getEnd().getxCoord()+" EY: "+line.getEnd().getyCoord());
        //System.out.println("Weight: " + line.getWeight());

        double inputX = loc.getxCoord();
        double inputY = loc.getyCoord();

        double x1 = line.getStart().getxCoord();
        double y1 = line.getStart().getyCoord();
        double weight = 8;

        double x2 = line.getEnd().getxCoord();
        double y2 = line.getEnd().getyCoord();

        if (line.getStart().getxCoord() > line.getEnd().getxCoord()) {
            x1 = line.getEnd().getxCoord();
            y1 = line.getEnd().getyCoord();
            x2 = line.getStart().getxCoord();
            y2 = line.getStart().getyCoord();
        }
/*
        if (y2>=y1 && (!(inputY<=y2 && inputY >=y1))) {
            return false;
        }
        else if (y1>=y2&& (!(inputY>=y2 && inputY <= y1))) {
            return false;
        }
        */
        // central line:
        //TODO: KINDA works. make this work for nearly vertical or vertical lines...
        double slope = (y2-y1)/(x2-x1);
        double b = y1-slope*x1;
        double alpha = Math.atan(slope);

        double yBounds = weight/(2*Math.sin((Math.PI/2)-alpha));




        double originY = slope*inputX + b;
        System.out.print("Origin Y: "+ originY);
        //boolean isOnLine = false;
        double maxY = originY + yBounds;
        double minY = originY - yBounds;
        System.out.println("LOC: " +  inputX + " " + inputY +  " " + "maxY: " + maxY + " Min Y: " + minY);
        if (inputY <= maxY && inputY >= minY) {
            System.out.println("In y bounds.");
        }
        if (inputX<= x2 && inputX >= x1) {
            System.out.println("In x bounds.");
        }
        return(inputY <= maxY && inputY >= minY) && (inputX<= x2 && inputX >= x1);
        /*
        if (x2>=x1) {
            isOnLine =  (inputY <= maxY && inputY >= minY) && (inputX<= x2 && inputX >= x1);
        }
        else {
            isOnLine = (inputY <= maxY && inputY >= minY) && (inputX<= x1 && inputX >= x2);

        }

        return isOnLine;
*/

        /*
        Vector edge = new Vector(line.start,line.end);
        Vector mouse = new Vector(loc,line.end);
        Vector proj = edge.projection(mouse);
        Vector perb = new Vector();
        perb.x=edge.x-mouse.x;
        perb.y=edge.y-mouse.y;

        if(perb.getNorm()>line.weight) return false; // the point is out side of the thickness TODO check if this weight need to be doubled?
        if(proj.getNorm()>edge.getNorm()) return false; // the point is at least out side of end of line.
        if(edge.x>=0  ) {
            if( proj.x>=0) return  true;
            else return false;
        }
        else {
            if( proj.x<=0) return  true;
            else return false;
        }
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
                drawLine(id, l.getStart(), l.getEnd(), l.getWeight(), Color.RED, false);
                //TODO: make this work
                //return null;
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
}
