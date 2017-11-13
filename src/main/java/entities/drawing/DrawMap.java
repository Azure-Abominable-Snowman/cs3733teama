package entities.drawing;

import controllers.SceneEngine;
import entities.HospitalMap;
import entities.Location;
import entities.MapEdge;
import entities.MapNode;
import entities.PathRelated.Path;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

import java.util.ArrayList;
import java.util.Map;

public class DrawMap {
    private Canvas c;
    private GraphicsContext gc;
    private int xOffset, yOffset, imgW, imgH;
    private ImageStash stash;
    private Map<String, String> bwImgs = SceneEngine.getHospitalImageMap();
    private Image bwImg;
    private int nodeDim = 3;
    private double zoomUnit = 3;
    private boolean zoomed = false;
    private double curZoom = 1;
    private String curFloor = "G";
    private ArrayList<MapNode> nodes;
    private Map<String, MapNode> nodesOnFloor;
    private ScrollPane mapPane;
    private HospitalMap map;

    public DrawMap(ScrollPane mapPane, Canvas c, int xOffset, int yOffset, int imgW, int imgH) {
        this.c = c;
        this.gc = c.getGraphicsContext2D();
        this.xOffset = xOffset;
        this.yOffset = yOffset;
        this.imgH = imgH;
        this.imgW = imgW;
        this.mapPane = mapPane;
        String filename = bwImgs.get("G");
        stash = new ImageStash(filename);
        bwImg = stash.getImage();
        map = HospitalMap.getInstance("csvdata/MapAedges.csv", "csvdata/MapAnodes.csv");

        ArrayList<String> nodeIds = map.getMap().getNodeIds();
        nodes = new ArrayList<>();
        for(String id : nodeIds) {
            nodes.add(map.getMap().getNode(id));
        }
    }

    /**
     * Converts input coordinate system to output coordinate system
     */
    public double convUnits(double x, double origMaxX, double curMaxX) {
        return (x*(curMaxX/origMaxX));
    }

    /**
     * Creates new location with the X and Y coordinates suitable for drawing on the map
     * @param loc
     * @return
     */
    private Location convNodeCoords(Location loc) {
        Location newLoc = new Location(loc);
        newLoc.setxCoord(loc.getxCoord()+xOffset);
        newLoc.setyCoord(loc.getyCoord()+yOffset);
        return newLoc;
    }

    public void drawNode(MapNode n, int size, Paint pointColor) {
        double width = c.getWidth(); // make sure the width and height are updated
        double height = c.getHeight();
        Location drawLoc = convNodeCoords(n.getCoordinate());
        double nodeX = convUnits(drawLoc.getxCoord(), imgW, width);
        double nodeY = convUnits(drawLoc.getyCoord(), imgH, height);
        gc.setFill(pointColor);
        gc.fillOval(nodeX, nodeY, size, size);
    }

    public void drawEdge(Canvas c, MapEdge edge) {
        double width = c.getWidth(); // make sure the width and height are updated
        double height = c.getHeight();
        Location start = convNodeCoords(edge.getStart().getCoordinate());
        Location end = convNodeCoords(edge.getEnd().getCoordinate());
        gc.strokeLine(
                convUnits(start.getxCoord(), imgW, width),
                convUnits(start.getyCoord(), imgH, height),
                convUnits(end.getxCoord(), imgW, width),
                convUnits(end.getyCoord(), imgH, height));
    }

    public void switchFloor(String newFloor) {
        renderMap(mapPane.getWidth() * curZoom, mapPane.getHeight() * curZoom, newFloor);
    }

    public void renderMap(double width, double height) {
        renderMap(width, height, curFloor);
    }

    public void renderMap(double width, double height, String floor) {
        if(!floor.equals(curFloor)) { //switching to a new image
            curFloor = floor;
            stash = null;
            String filename = bwImgs.get(floor);
            stash = new ImageStash(filename);
            bwImg = stash.getImage();
        }
        c.setWidth(width);
        c.setHeight(height);
        gc.clearRect(0, 0, width, height);
        gc.drawImage(bwImg, 0,0, width, height);
        for(MapNode n : map.getFloorNodes(floor).values()) {
            drawNode(n, nodeDim, Color.BLACK);
        }
    }

    public void updateSize() {
        double width = mapPane.getWidth();
        double height = mapPane.getHeight();
        mapPane.setVmax(height);
        mapPane.setHmax(width);
        renderMap(width, height);
    }

    public void toggleZoom(double x, double y) {
        if(!zoomed) {
            renderMap(mapPane.getWidth() * zoomUnit, mapPane.getHeight() * zoomUnit);
            mapPane.setHvalue(x); // set so it's in the center of the screen
            mapPane.setVvalue(y);
            zoomed = true;
            curZoom = 3;
        } else {
            renderMap(mapPane.getWidth(), mapPane.getHeight());
            zoomed = false;
            curZoom = 1;
        }
    }

    public void setNodeDim(int nodeDim) {
        this.nodeDim = nodeDim;
    }

    public int getNodeDim() {
        return nodeDim;
    }

    public String getCurFloor() {
        return curFloor;
    }

    public void drawPath(Path path) {
        System.out.println(path.getConnectors().size());
        for(MapEdge e : path.getConnectors()) {
            drawEdge(c, e);
        }
    }
}
