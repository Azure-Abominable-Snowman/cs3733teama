package com.teama.controllers;

import com.teama.mapsubsystem.MapSubsystem;
import com.teama.mapsubsystem.data.Floor;
import com.teama.mapsubsystem.data.MapNode;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Window;
import org.apache.commons.codec.language.DoubleMetaphone;
import org.controlsfx.control.textfield.AutoCompletionBinding;
import org.controlsfx.control.textfield.TextFields;

import java.text.Normalizer;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static java.awt.SystemColor.text;

public class SearchBarController {

    private ComboBox<String> inputField;
    private Button searchButton;
    private MapSubsystem mapSubsystem;
    String filter = "";
    private ObservableList<String> originalItems;
    String[] words = {"Fenwood Road",
            "Schlagler Stairs",
            "Information Desk",
            "Information Desk",
            "Elevator S G",
            "BTM Security Desk",
            "Neuro Testing Waiting Area",
            "Infusion Waiting Area",
            "Schlagler Innovation Lobby",
            "Test Lobby",
            "Test Area1",
            "Test Area2",
            "Test Area3",
            "Test Desk",
            "Test Front Desk",
            "Test Info Desk",
            "Test Desk2",
            "Test Desk3",
            "Test Desk4",
            "Test Desk5",
            "Test Stairs2",
    };
    Set<String> possibleWordSet = new HashSet<>();
    private AutoCompletionBinding<String> autoCompletionBinding;

    // Double metaphone fuzzy search algorithm
    private DoubleMetaphone doubleMetaphone;


//    private Boolean DoubleMetaphoneMatched(String mata){
//        //System.out.println("DOUBLE METAPHONE ENCODED: "+doubleMetaphone.doubleMetaphone(inputField.getSelectionModel().getSelectedItem(), false));
//        return doubleMetaphone.doubleMetaphone(inputField.getSelectionModel().getSelectedItem(), true).equals(doubleMetaphone.doubleMetaphone(mata, true));
//    }

    public SearchBarController(ComboBox<String> inputField, Button searchButton, MapSubsystem mapSubsystem) {
        this.inputField = inputField;
        this.searchButton = searchButton;
        this.mapSubsystem = mapSubsystem;
        doubleMetaphone = new DoubleMetaphone();
        inputField.setEditable(true);

        Collections.addAll(possibleWordSet, words);
        autoCompletionBinding = TextFields.bindAutoCompletion(inputField.getEditor(), possibleWordSet);

//        if(doubleMetaphone.doubleMetaphone(input, true).equals(doubleMetaphone.doubleMetaphone(words[1], true))) {
//            autoCompletionBinding = TextFields.bindAutoCompletion(inputField.getEditor(), possibleWordSet);
//        }

        //if(!DoubleMetaphoneMatched(text))

        if(doubleMetaphone.isDoubleMetaphoneEqual(inputField.getEditor().getText(), words[1],true)){
                autoCompletionBinding = TextFields.bindAutoCompletion(inputField.getEditor(), possibleWordSet);
        }
        inputField.getEditor().setOnKeyPressed((KeyEvent e) -> {
            switch(e.getCode()){
                case ENTER:
                    learnWord(inputField.getEditor().getText());
                    break;
                default:
                    break;
            }
        });
    }
    private void learnWord(String text) {
            possibleWordSet.add(text);

            if (autoCompletionBinding != null)
                autoCompletionBinding.dispose();
            autoCompletionBinding = TextFields.bindAutoCompletion(inputField.getEditor(), possibleWordSet);

    }
    /**
     * Function that uses the current inputField text to generate and display a list of
     * approximately matched strings below the text field
     */
    public void matchFuzzySearchValues() {
        String selected = inputField.getSelectionModel().getSelectedItem();

        String input = inputField.getEditor().getText();



        //if(doubleMetaphone.doubleMetaphone(input).equals(doubleMetaphone.doubleMetaphone(words[1]))) {
            System.out.println("aaaafdsfdsfdsafdsafdsfs");
//            Collections.addAll(possibleWordSet, words);
//            autoCompletionBinding = TextFields.bindAutoCompletion(inputField.getEditor(), possibleWordSet);

            System.out.println("TRIGGERED FUZZY VALUES ON: " + inputField.getSelectionModel().getSelectedItem());
            System.out.println("DOUBLE METAPHONE ENCODED: " + doubleMetaphone.doubleMetaphone(selected, false) + " " +
                    doubleMetaphone.doubleMetaphone(selected, true));

        //System.out.println("Test value is:" + DoubleMetaphoneMatched(words[1]));

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
