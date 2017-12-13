package com.teama.controllers_refactor;

import com.jfoenix.controls.JFXComboBox;
import com.teama.ProgramSettings;
import com.teama.controllers.SearchBarController;
import com.teama.mapdrawingsubsystem.MapDrawingSubsystem;
import com.teama.mapsubsystem.MapSubsystem;
import com.teama.mapsubsystem.data.Floor;
import com.teama.mapsubsystem.data.MapNode;
import com.teama.mapsubsystem.data.NodeType;
import com.teama.mapsubsystem.pathfinding.DirectionAdapter;
import com.teama.mapsubsystem.pathfinding.DirectionsGenerator;
import com.teama.mapsubsystem.pathfinding.Path;
import com.teama.mapsubsystem.pathfinding.Direction;
import com.teama.mapsubsystem.pathfinding.TextDirections;
import com.teama.mapsubsystem.pathfinding.TextualDirections;
import javafx.beans.Observable;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

public class DirectionsPopOut extends PopOutController {
    private int xOffset, yOffset;
    private ReadOnlyDoubleProperty xProperty, yProperty;

    @FXML
    private TableView<DirectionAdapter> textDirections;

    @FXML
    private TableColumn<String, Text> stepCol;

    @FXML
    private TableColumn<String, Text> descriptionCol;

    @FXML
    private TableColumn<String, Text> distanceCol;

    @FXML
    private JFXComboBox<String> originNodeCombo;

    @FXML
    private TableColumn<String, ImageView> directionCol;

    @FXML
    private JFXComboBox<String> filterBox;

    public void initialize() {
        alignPane(xProperty, xOffset, yProperty, yOffset);

        ReadOnlyObjectProperty<Path> pathObjectProperty = ProgramSettings.getInstance().getCurrentDisplayedPathProp();

        if(pathObjectProperty.getValue() != null) {
            putDirectionsOnScreen(pathObjectProperty.getValue());
        }
        pathObjectProperty.addListener((a, b, currentPath) -> {
            putDirectionsOnScreen(currentPath);
        });

        stepCol.setCellValueFactory(
                new PropertyValueFactory<>("stepNum"));
        descriptionCol.setCellValueFactory(
                new PropertyValueFactory<>("description"));
        distanceCol.setCellValueFactory(
                new PropertyValueFactory<>("distance"));
        directionCol.setCellValueFactory(
                new PropertyValueFactory<>("direction"));


        textDirections.setFixedCellSize(75); // cells need to be bigger than default

        // Factory for each row, set to have the text wrap
        textDirections.setRowFactory(tv -> {
            TableRow<DirectionAdapter> row = new TableRow<>();
            row.setAlignment(Pos.CENTER);
            return row;
        });


        // Populate the combo box and allow fuzzy search by tying it to a search controller
        SearchBarController searchBarController = new SearchBarController(originNodeCombo, true);

        // Initially set the origin node to the current origin node
        MapNode beginOrigin = ProgramSettings.getInstance().getPathOriginNodeProp().getValue();
        if(beginOrigin != null) {
            originNodeCombo.getEditor().setText(beginOrigin.getLongDescription());
        } else {
            originNodeCombo.getEditor().setText(mapSubsystem.getKioskNode().getLongDescription());
        }

        // When the origin combo box changes selected value, set the origin of the path to the new one
        originNodeCombo.getSelectionModel().selectedItemProperty().addListener((Observable a) -> {
            System.out.println("CHANGED SELECTION "+originNodeCombo.getSelectionModel().getSelectedItem());
            MapNode selectedNode = MapSubsystem.getInstance().getNodeByDescription(originNodeCombo.getSelectionModel().getSelectedItem(), true);
            if(selectedNode != null) {
                // Tell path controller to make a new path
                ProgramSettings.getInstance().setPathOriginNodeProp(selectedNode);
            }
        });

        // Populate the filter box
        filterBox.getItems().add("");
        for(NodeType nType : NodeType.values()) {
            if(!nType.equals(NodeType.HALL)) {
                filterBox.getItems().add(nType.toString());
            }
        }

        // Make a listener on the filter box that when it changes the nodes of that type
        // are displayed differently
        filterBox.getSelectionModel().selectedItemProperty().addListener((a1, b1, c1) -> {
            for(long id : filterFloorListeners) {
                mapDrawing.detachListener(id);
            }
            updateFilter();
            drawShortestPathToFilter();
            filterFloorListeners.add(mapDrawing.attachFloorChangeListener((a, b, c) -> {
                System.out.println(filterBox.getSelectionModel().getSelectedItem());
                updateFilter();
            }));
        });


        // Make a listener on the tableview to focus on the node relating to the direction when selected
        textDirections.getSelectionModel().selectedItemProperty().addListener((a) -> {
            mapDrawing.setViewportCenter(textDirections.getSelectionModel().getSelectedItem().getLocToFocus());
        });
    }

    private MapDrawingSubsystem mapDrawing = MapDrawingSubsystem.getInstance();
    private MapSubsystem mapSubsystem = MapSubsystem.getInstance();

    private ArrayList<Long> filterFloorListeners = new ArrayList<>();

    private void updateFilter() {
        mapDrawing.refreshMapNodes();
        Collection<MapNode> floorNodes = mapSubsystem.getVisibleFloorNodes(mapDrawing.getCurrentFloor()).values();
        for (MapNode n : floorNodes) {
            if (n.getNodeType().equals(NodeType.fromValue(filterBox.getSelectionModel().getSelectedItem()))) {
                // Display on floor differently
                mapDrawing.drawNode(n, 15, Color.RED);
            }
        }
    }

    private void drawShortestPathToFilter() {
        // Find the shortest path to the given nodetype
        Path shortest = mapSubsystem.findClosest(NodeType.fromValue(filterBox.getSelectionModel().getSelectedItem()));
        System.out.println("SHORTEST FOUND TO "+shortest.getEndNode().getId());
        ProgramSettings.getInstance().setPathOriginNodeProp(shortest.getStartNode());
        ProgramSettings.getInstance().setPathEndNodeProp(shortest.getEndNode());
    }

    private DirectionsGenerator directionsGenerator = new TextualDirections();

    private void putDirectionsOnScreen(Path path) {
        TextDirections directions = directionsGenerator.generateDirections(path);
        ObservableList<DirectionAdapter> directionVals = FXCollections.observableArrayList();
        int num = 1;
        for(Direction d : directions.getDirections()) {
            directionVals.add(new DirectionAdapter(num, d));
            num++;
        }
        textDirections.setItems(directionVals);

        // make the combo box automatically change to the start of the path
        originNodeCombo.getEditor().setText(path.getStartNode().getLongDescription());
    }

    @Override
    public void onOpen(ReadOnlyDoubleProperty xProperty, int xOffset, ReadOnlyDoubleProperty yProperty, int yOffset) {
        this.xOffset = xOffset;
        this.yOffset = yOffset;
        this.xProperty = xProperty;
        this.yProperty = yProperty;
    }

    @Override
    public void onClose() {
        //TODO rdraw nodes that are filtered

    }

    @Override
    public String getFXMLPath() {
        return "/DirectionsPopOut.fxml";
    }
}
