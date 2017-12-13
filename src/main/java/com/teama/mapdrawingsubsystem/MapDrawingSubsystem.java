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
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MapDrawingSubsystem {

    private MapDisplay map;
    private GridPane floorButtonBox;

    private long listenerIDCounter = 0; // keeps track of the listeners added
    private Map<Long, ChangeListener<? super Boolean>> floorBoxListenerMap;
    private Map<Long, EventHandler<MouseEvent>> clickedListenerMap;
    private Map<ClickedListener, ArrayList<Long>> listenerLists = new HashMap<>();

    private MapSubsystem mapDB =  MapSubsystem.getInstance();
   // private Text floorNumberDisplay;

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
                System.out.println("Clicked on a node");
                clickedListenerMap.get(id).handle(event);
            }
        }
        if(listenerLists.containsKey(ClickedListener.LOCCLICKED) && map.pointAt(mouseLoc) == null) {
            for (Long id : listenerLists.get(ClickedListener.LOCCLICKED)) {
                System.out.println("Clicked on a random location ");
                clickedListenerMap.get(id).handle(event);
            }
        }

        if(listenerLists.containsKey(ClickedListener.EDGECLICKED) && map.lineAt(mouseLoc) != null && map.pointAt(mouseLoc) == null) {
            for(Long id : listenerLists.get(ClickedListener.EDGECLICKED)) {
                clickedListenerMap.get(id).handle(event);

            }
        }



        // TODO: do the same for images and paths
    };

    private ScrollPane mapScroll;

    /**
     * Initializes the drawing subsystem with a canvas and a scrollpane
     * @param mapCanvas
     * @param mapScroll
     */
    public void initialize(Canvas mapCanvas, ScrollPane mapScroll, GridPane floorButtonBox, AnchorPane areaPane) {
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

      //  this.floorNumberDisplay = floorNumberDisplay;
        this.floorButtonBox = floorButtonBox;
        this.areaPane = areaPane;
        this.mapScroll = mapScroll;
        //have this attach listeners to all of the buttons where the
        //loops goes through all of the buttons and makes it transparent
        //then set the one that was clicked to be filled in
        // Attach a listener that changes the current floor when a button is pressed
      //  floorNumberDisplay.setText(getCurrentFloor().toString());
        System.out.println(floorButtonBox);
        for(Node button : floorButtonBox.getChildren()) {
            button.pressedProperty().addListener((a, b, c) -> {
                for(Node node: floorButtonBox.getChildren()){
                    node.getStyleClass().clear();
                    node.getStyleClass().add("floorButton");
                }
                button.getStyleClass().clear();
                button.getStyleClass().add("floorButtonSelected");
                map.setCurrentFloor(Floor.getFloor(button.getId()));
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
           // floorButtonBox.getChildren().get(i).pressedProperty().addListener((a, b, c) -> {});
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
            size = 5;
        }
        if(color == null) {
            color = Color.DARKBLUE;
        }
        map.drawPoint(node.getId(), node.getCoordinate(),size, color,true, false);
    }
    public void drawNewLocation(Location loc, int size, Color color, String id, boolean screenCoords) {
        if (size == 0) {
            size = 5;
        }
        size = 7;

        if (color == null) {
            color = Color.DARKBLUE;
        }
        map.drawPoint(id, loc, size, color, true, false);
        System.out.println(id);
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

    public void drawNewEdge(String ID, MapNode start, MapNode end, int weight, Color color, boolean arrow) {
        if (weight == 0) {
            weight= 5;
        }
        if (color == null) {
            color = Color.CADETBLUE;
        }
        map.drawLine(ID, start.getCoordinate(), end.getCoordinate(), weight, color, arrow, false);
    }

    public void drawEdge(MapEdge edge, int weight, Color color, boolean arrow, ArrayList<LineAnimation> animation) {
        // TODO: implement animations
    }

    private Map<String, Path> displayedPaths = new HashMap<>();

    /**
     * Draws a path, returns an ID connected to that particular path to use to remove it from the screen
     * @param path
     * @return
     */
    public String drawPath(Path path) {
        map.drawPath(path.getStartNode().getId()+path.getEndNode().getId(), path);
        return path.getStartNode().getId()+path.getEndNode().getId();
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

    public void unDrawNewEdge(String id) {
        map.deleteLine(id);
    }

    public void unDrawNewLocation(String id) {
        map.deletePoint(id);
    }

    public void unDrawPath(String pathID) {
        //detachListener(pathID);
        /*Path pathToRemove = displayedPaths.get(pathID);
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
        displayedPaths.remove(pathID);*/
        map.deletePath(pathID);
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
        map.setDisplayedLocation(center, false);
        // Notify all the floor changed listeners about the floor change
        for(ChangeListener<? super Boolean> listener : floorBoxListenerMap.values()) {
            listener.changed(null, false, true);
        }

        /*attachClickedListener((MouseEvent a) -> {
            System.out.println("X: "+mapScroll.getHvalue()+" Y: "+mapScroll.getVvalue());
        }, ClickedListener.LOCCLICKED);*/
    }
    public Location convertLocationToImgCoords(Location loc) {
        return map.convToImageCoords(loc);
    }

    public void clearMap() {
        map.clear();
    }

    public void refreshMapNodes() {
        for(MapNode n : mapDB.getVisibleFloorNodes(getCurrentFloor()).values()) {
            drawNode(n, 0, null);
        }
        // Redraw the path
        /*Path curPath = ProgramSettings.getInstance().getCurrentDisplayedPathProp().getValue();
        if(curPath != null) {
            drawPathOnScreen(curPath);
        }*/
    }
}
