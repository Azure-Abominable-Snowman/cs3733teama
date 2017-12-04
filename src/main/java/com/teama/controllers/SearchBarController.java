package com.teama.controllers;

import com.teama.mapdrawingsubsystem.MapDrawingSubsystem;
import com.teama.mapsubsystem.MapSubsystem;
import com.teama.mapsubsystem.data.Floor;
import com.teama.mapsubsystem.data.MapNode;
import javafx.scene.control.ComboBox;
import org.apache.commons.codec.language.DoubleMetaphone;

import java.util.ArrayList;
import java.util.Comparator;

public class SearchBarController {

    private ComboBox<String> inputField;
    private MapSubsystem mapSubsystem;

    // Double metaphone fuzzy search algorithm
    private DoubleMetaphone doubleMetaphone;

    public SearchBarController(ComboBox<String> inputField, boolean floorNodes) {
        this.inputField = inputField;
        this.mapSubsystem = MapSubsystem.getInstance();
        doubleMetaphone = new DoubleMetaphone();
        // Editable (fuzzy searching)
        inputField.getEditor().setEditable(true);
        MapDrawingSubsystem mapDrawing = MapDrawingSubsystem.getInstance();

        // Tie updating the node listing to floor change events if we are looking at floor nodes
        if(floorNodes) {
            updateNodeListing(mapDrawing.getCurrentFloor());
            mapDrawing.attachFloorChangeListener((a, b, c) -> {
                updateNodeListing(mapDrawing.getCurrentFloor());
            });
        } else {
            updateNodeListing();
            // still attach to a floor change in case nodes are updated
            mapDrawing.attachFloorChangeListener((a, b, c) -> {
                updateNodeListing();
            });
        }

        // On typing into the combo box, update the fuzzy search values
        inputField.getEditor().onKeyTypedProperty().set((event) -> {
            matchFuzzySearchValues();
        });
    }

    /**
     * Function that uses the current inputField text to generate and display a list of
     * approximately matched strings below the text field
     */
    public void matchFuzzySearchValues() {
        String selected = inputField.getSelectionModel().getSelectedItem();
        System.out.println("TRIGGERED FUZZY VALUES ON: "+inputField.getSelectionModel().getSelectedItem());
        System.out.println("DOUBLE METAPHONE ENCODED: "+doubleMetaphone.doubleMetaphone(selected, false)+" "+
                doubleMetaphone.doubleMetaphone(selected, true));

        System.out.println("DMETA EQUAL: "+doubleMetaphone.isDoubleMetaphoneEqual("testval", selected));

        // TODO: Use built in DoubleMetaphone in order to do string matching with the typed values
        // TODO: Display these matched values below where the user is typing
        // TODO: Allow the user to select from this menu
    }

    public void updateNodeListing() {
        inputField.getItems().clear();
        ArrayList<MapNode> floorNodes = new ArrayList<>();
        for (Floor f : Floor.values()) {
            floorNodes.addAll(mapSubsystem.getVisibleFloorNodes(f).values());
        }
        finishUpdateNodes(floorNodes);
    }

    /**
     * Updates the node listing for the building
     *
     * This might also need to be called when fuzzy search matching ends
     *
     */
    public void updateNodeListing(Floor floor) {
        // Initially populate the list with all of the values (long descriptions)
        inputField.getItems().clear();
        ArrayList<MapNode> floorNodes = new ArrayList<>();
        floorNodes.addAll(mapSubsystem.getVisibleFloorNodes(floor).values());
        finishUpdateNodes(floorNodes);
    }

    private void finishUpdateNodes(ArrayList<MapNode> floorNodes) {
        // Alphabetize by long description
        floorNodes.sort(new Comparator<MapNode>() {
            @Override
            public int compare(MapNode o1, MapNode o2) {
                return o1.getLongDescription().compareTo(o2.getLongDescription());
            }
        });

        for(MapNode n : floorNodes) {
            inputField.getItems().add(n.getLongDescription());
        }
    }

    public MapNode getSelectedNode() {
        return mapSubsystem.getNodeByDescription(inputField.getSelectionModel().getSelectedItem(), true);
    }

}
