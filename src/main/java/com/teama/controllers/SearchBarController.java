package com.teama.controllers;

import com.teama.mapsubsystem.MapSubsystem;
import com.teama.mapsubsystem.data.Floor;
import com.teama.mapsubsystem.data.MapNode;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import org.apache.commons.codec.language.DoubleMetaphone;

import java.util.Collection;

public class SearchBarController {

    private ComboBox<String> inputField;
    private Button searchButton;
    private MapSubsystem mapSubsystem;

    // Double metaphone fuzzy search algorithm
    private DoubleMetaphone doubleMetaphone;

    public SearchBarController(ComboBox<String> inputField, Button searchButton, MapSubsystem mapSubsystem) {
        this.inputField = inputField;
        this.searchButton = searchButton;
        this.mapSubsystem = mapSubsystem;
        doubleMetaphone = new DoubleMetaphone();
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

        // TODO: Use built in DoubleMetaphone in order to do string matching with the typed values
        // TODO: Display these matched values below where the user is typing
        // TODO: Allow the user to select from this menu
    }

    /**
     * Updates the node listing for the current floor.
     *
     * This might also need to be called when fuzzy search matching ends
     *
     * @param currentFloor
     */
    public void updateNodeListing(Floor currentFloor) {
        // Initially populate the list with all of the values (long descriptions)
        inputField.getItems().clear();
        Collection<MapNode> floorNodes = mapSubsystem.getVisibleFloorNodes(currentFloor).values();
        for(MapNode n : floorNodes) {
            inputField.getItems().add(n.getLongDescription());
        }
    }

}
