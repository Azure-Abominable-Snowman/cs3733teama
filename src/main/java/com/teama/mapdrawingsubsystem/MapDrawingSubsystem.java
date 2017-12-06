package com.teama.mapdrawingsubsystem;

import com.teama.mapsubsystem.MapSubsystem;
import com.teama.mapsubsystem.data.*;
import com.teama.mapsubsystem.pathfinding.Path;
import javafx.beans.value.ChangeListener;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MapDrawingSubsystem {

    private MapDisplay map;
    private Pane floorButtonBox;

    private long listenerIDCounter = 0; // keeps track of the listeners added
    private Map<Long, ChangeListener<? super Boolean>> floorBoxListenerMap;
    private Map<Long, EventHandler<MouseEvent>> clickedListenerMap;
    private Map<ClickedListener, ArrayList<Long>> listenerLists = new HashMap<>();

    private MapSubsystem mapDB =  MapSubsystem.getInstance();
    private Text floorNumberDisplay;

    private AnchorPane areaPane;
    private static class MapDrawingSubsystemGetter {
        private static final MapDrawingSubsystem instance = new MapDrawingSubsystem();
    }

    public static synchronized MapDrawingSubsystem getInstance() {
        return MapDrawingSubsystem.MapDrawingSubsystemGetter.instance;
    }

    private EventHandler<MouseEvent> masterMouseClickedEvent = (MouseEvent event) -> {
        // Look to see if we should fire the on point event or the on line event, or any other clicked events
        // If we should, iterate through and fire the events.
        Location mouseLoc = new Location(event, getCurrentFloor());
        if(listenerLists.containsKey(ClickedListener.NODECLICKED) && map.pointAt(mouseLoc) != null) {
            for(Long id : listenerLists.get(ClickedListener.NODECLICKED)) {
                clickedListenerMap.get(id).handle(event);
            }
        }

        if(listenerLists.containsKey(ClickedListener.EDGECLICKED) && map.lineAt(mouseLoc) != null) {
            for(Long id : listenerLists.get(ClickedListener.EDGECLICKED)) {
                clickedListenerMap.get(id).handle(event);
            }
        }

        if(listenerLists.containsKey(ClickedListener.LOCCLICKED)) {
            for (Long id : listenerLists.get(ClickedListener.LOCCLICKED)) {
                clickedListenerMap.get(id).handle(event);
            }
        }

        // TODO: do the same for images and paths
    };

    /**
     * Initializes the drawing subsystem with a canvas and a scrollpane
     * @param mapCanvas
     * @param mapScroll
     */
    public void initialize(Canvas mapCanvas, ScrollPane mapScroll, Text floorNumberDisplay, Pane floorButtonBox, AnchorPane areaPane) {
        clickedListenerMap = new HashMap<>();
        floorBoxListenerMap = new HashMap<>();
        Map<Floor, HospitalMap> imgs = new HashMap<>();
        imgs.put(Floor.SUBBASEMENT, new ProxyMap("/maps/L2.png"));
        imgs.put(Floor.BASEMENT, new ProxyMap("/maps/L1.png"));
        imgs.put(Floor.GROUND, new ProxyMap("/maps/G.png"));
        imgs.put(Floor.ONE, new ProxyMap("/maps/1.png"));
        imgs.put(Floor.TWO, new ProxyMap("/maps/2.png"));
        imgs.put(Floor.THREE, new ProxyMap("/maps/3.png"));
        map = new HospitalMapDisplay(mapScroll, mapCanvas, imgs);

        mapCanvas.onMouseClickedProperty().set(masterMouseClickedEvent);

        this.floorNumberDisplay = floorNumberDisplay;
        this.floorButtonBox = floorButtonBox;
        this.areaPane = areaPane;

        // Attach a listener that changes the current floor when a button is pressed
        floorNumberDisplay.setText(getCurrentFloor().toString());
        for(Node button : floorButtonBox.getChildren()) {
            button.pressedProperty().addListener((a, b, c) -> {
                map.setCurrentFloor(Floor.getFloor(button.getId()));
                floorNumberDisplay.setText(getCurrentFloor().toString());
            });
        }
    }

    public AnchorPane getAreaPane() {
        return this.areaPane;
    }

    public Floor getCurrentFloor() {
        return map.getCurrentFloor();
    }

    public double getZoomFactor() {
        return map.getZoom();
    }

    public void setZoomFactor(double zoomFactor) {
        map.setZoom(zoomFactor);
    }

    public void setGrow(boolean grow) { map.setGrow(grow); }

    public MapNode nodeAt(Location loc) {
        String id = map.pointAt(loc);
        if(id == null) {
            return null;
        }
        return mapDB.getNode(id);
    }

    public MapEdge edgeAt(Location loc) {
        String id = map.lineAt(loc);
        if(id == null) {
            return null;
        }
        return mapDB.getEdge(id);
    }


    public long attachFloorChangeListener(ChangeListener<? super Boolean> event) {
        if(floorButtonBox == null) {
            System.out.println("Tried to set a floor change listener when no button box is specified");
            return -1;
        }
        for(int i = 0; i < Floor.values().length; i++) {
            floorButtonBox.getChildren().get(i).pressedProperty().addListener(event);
        }
        listenerIDCounter++;
        floorBoxListenerMap.put(listenerIDCounter, event);
        return listenerIDCounter;
    }

    /**
     * Method to attach a new listener to a specific click event
     * @param event
     * @param listener
     * @return
     */
    public long attachClickedListener(EventHandler<MouseEvent> event, ClickedListener listener) {
        listenerIDCounter++;
        clickedListenerMap.put(listenerIDCounter, event);
        if(!listenerLists.containsKey(listener)) {
            listenerLists.put(listener, new ArrayList<>());
        }
        listenerLists.get(listener).add(listenerIDCounter);
        return listenerIDCounter;
    }

    public void detachListener(long id) {
        if(floorBoxListenerMap.containsKey(id)) {
            for(int i = 0; i < Floor.values().length; i++) {
                floorButtonBox.getChildren().get(i).pressedProperty().removeListener(floorBoxListenerMap.get(id));
            }
            floorBoxListenerMap.remove(id);
        }
        for(ArrayList<Long> l : listenerLists.values()) {
            l.remove(id);
        }
    }

    /**
     * Draws a node, if the size is 0 then it picks the default, if color is null it
     * picks the default
     * @param node
     * @param size
     * @param color
     */
    public void drawNode(MapNode node, int size, Color color) {
        if(size == 0) {
            size = 8;
        }
        if(color == null) {
            color = Color.BLACK;
        }
        map.drawPoint(node.getId(), node.getCoordinate(),size, color,true, false);
    }

    public void drawNode(MapNode node, int size, Color color, ArrayList<PointAnimation> animation) {
        // TODO: implement animations
    }

    /**
     * Draws an edge, if the weight is zero it picks the default, if the color is null it picks
     * the default
     * @param edge
     * @param weight
     * @param color
     */
    public void drawEdge(MapEdge edge, int weight, Color color, boolean arrow) {
        if(weight == 0) {
            weight = 5;
        }
        if(color == null) {
            color = Color.CADETBLUE;
        }
        map.drawLine(edge.getId(), edge.getStart().getCoordinate(), edge.getEnd().getCoordinate(), weight, color, arrow,false);
    }

    public void drawEdge(MapEdge edge, int weight, Color color, boolean arrow, ArrayList<LineAnimation> animation) {
        // TODO: implement animations
    }

    private Map<Long, Path> displayedPaths = new HashMap<>();

    /**
     * Draws a path, returns an ID connected to that particular path to use to remove it from the screen
     * @param path
     * @return
     */
    public long drawPath(Path path) {
        drawPathOnScreen(path);
        long pathID = attachFloorChangeListener((a, b, c) -> {
            drawPathOnScreen(path);
        });
        displayedPaths.put(pathID, path);
        return pathID;
    }

    private void drawPathOnScreen(Path path) {
        // Clear all previous annotations from the screen
        map.clearText();
        // boolean to highlight the start of a path on the floor special
        boolean drawnFirst = false;
        // Last node drawn
        MapNode lastDrawn = null;
        // Turn them the right way and then display the edges
        // Node where the path starts
        MapNode startNode = path.getNodes().get(0);
        System.out.println("START NODE: " + startNode.getShortDescription());
        // Node where the first part of the path ends
        MapNode lastEnd = null;
        // Start from the second edge and turn all of the connectors the right way around and then draw them
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
            if (curEdge.getStart().getCoordinate().getLevel().equals(getCurrentFloor()) && curEdge.getEnd().getCoordinate().getLevel().equals(getCurrentFloor())) {
                new DrawEdgeInstantly(curEdge).displayOnScreen(map);
                if (!drawnFirst) {
                    MapDrawingSubsystem.getInstance().drawNode(curEdge.getStart(), 9, Color.RED);
                    drawnFirst = true;
                }
                lastDrawn = lastEnd;
            }

            // See if the floor changes on this, if it does then draw an annotation on the node on the currently displayed floor
            // With the name of the next floor to go to
            Floor endFloor = curEdge.getEnd().getCoordinate().getLevel();
            Floor startFloor = curEdge.getStart().getCoordinate().getLevel();
            String annoText = "";
            if (!startFloor.equals(endFloor) && (startFloor.equals(getCurrentFloor()) || endFloor.equals(getCurrentFloor()))) {
                MapNode chFloorNode = curEdge.getStart();
                // We now know that the floor is being changed, but we need to follow the path until we arrive at the correct floor
                // Iterate through the the nodes in the path until we arrive on the next floor or the path ends.
                if (startFloor.equals(getCurrentFloor())) {
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
                map.drawText(chFloorNode.getId(), annoText, chFloorNode.getCoordinate(), Font.font("Courier", FontWeight.BOLD, 16), false);
            }

        }
        // Draw the end special
        if (lastDrawn != null) {
            MapDrawingSubsystem.getInstance().drawNode(lastDrawn, 9, Color.RED);
        }
    }

    public void drawPath(Path path, ArrayList<PathAnimation> animation) {
        // TODO: implement path drawing in the underlying implementation
    }

    public void drawImage(String id, Image img, Location center, boolean screenCoords) {
        map.drawImage(id, img, center, screenCoords);
    }

    public void drawText(String id, String text, Location center, Font f, boolean screenCoords) {
        map.drawText(id, text, center, f, screenCoords);
    }

    public void unDrawNode(MapNode node) {
        map.deletePoint(node.getId());
    }

    public void unDrawEdge(MapEdge edge) {
        map.deleteLine(edge.getId());
    }

    public void unDrawPath(long pathID) {
        detachListener(pathID);
        Path pathToRemove = displayedPaths.get(pathID);
        // Delete all the edges from the screen
        for(MapEdge e : pathToRemove.getConnectors()) {
            map.deleteLine(e.getId());
        }
        // Redisplay all of the visible nodes on the current floor
        for(MapNode n : pathToRemove.getNodes()) {
            if (n.getCoordinate().getLevel().equals(getCurrentFloor()) && !n.getNodeType().equals(NodeType.HALL)) {
                drawNode(n, 0, null);
            }
            // Delete all possible annotations
            //display.deleteText(n.getId());
        }
        displayedPaths.remove(pathID);
    }

    public void unDrawText(String id) {
        map.deleteText(id);
    }

    public void unDrawImage(String id) {
        map.deleteImage(id);
    }

    public double getWidth() {
        return map.getMaxX();
    }

    public double getHeight() {
        return map.getMaxY();
    }

    public Location convertEventToImg(MouseEvent e, Floor current) {
        Location canvLoc = new Location(e, current);
        return map.convToImageCoords(canvLoc);
    }

    public void setViewportCenter(Location center) {
        map.setDisplayedLocation(center);
    }

    public void clearMap() {
        map.clear();
    }

    public void refreshMapNodes() {
        for(MapNode n : mapDB.getVisibleFloorNodes(getCurrentFloor()).values()) {
            drawNode(n, 0, null);
        }
    }
}
