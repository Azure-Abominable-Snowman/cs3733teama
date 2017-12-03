package com.teama.mapdrawingsubsystem;

import com.teama.mapsubsystem.MapSubsystem;
import com.teama.mapsubsystem.data.Floor;
import com.teama.mapsubsystem.data.Location;
import com.teama.mapsubsystem.data.MapEdge;
import com.teama.mapsubsystem.data.MapNode;
import com.teama.mapsubsystem.pathfinding.Path;
import javafx.beans.value.ChangeListener;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
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
    public void initialize(Canvas mapCanvas, ScrollPane mapScroll, Text floorNumberDisplay, Pane floorButtonBox) {
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

        // Attach a listener that changes the current floor when a button is pressed
        floorNumberDisplay.setText(getCurrentFloor().toString());
        for(Node button : floorButtonBox.getChildren()) {
            button.pressedProperty().addListener((a, b, c) -> {
                map.setCurrentFloor(Floor.getFloor(button.getId()));
                floorNumberDisplay.setText(getCurrentFloor().toString());
            });
        }
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

    public void drawPath(Path path) {
        // TODO: implement path drawing in the underlying implementation
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
        map.deletePoint(edge.getId());
    }

    public void unDrawPath(Path path) {
        // TODO: implement path drawing in the underlying implementation
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

    public void setViewportCenter(Location center) {
        map.setDisplayedLocation(center);
    }

    public void clearMap() {
        map.clear();
    }
}
