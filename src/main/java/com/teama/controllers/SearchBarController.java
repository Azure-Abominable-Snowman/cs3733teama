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

import java.lang.reflect.Array;
import java.text.Normalizer;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static java.awt.SystemColor.text;

public class SearchBarController {

    private ComboBox<String> inputField;
    private Button searchButton;
    private MapSubsystem mapSubsystem;
    String filter = "";
    private ObservableList<String> originalItems;
    Set<String> possibleWordSet = new HashSet<>();
    private AutoCompletionBinding<String> autoCompletionBinding;
    private AutoCompletionBinding<String> autoCompletionBinding2;


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

//        Collection<MapNode> floorNodes = mapSubsystem.getVisibleFloorNodes(Floor.GROUND).values();
//        possibleWordSet.clear();
//        for(MapNode n : floorNodes){
//            possibleWordSet.add(n.getLongDescription());
//        }

        //Collections.addAll(possibleWordSet, words);

//        if(doubleMetaphone.doubleMetaphone(input, true).equals(doubleMetaphone.doubleMetaphone(words[1], true))) {
//            autoCompletionBinding = TextFields.bindAutoCompletion(inputField.getEditor(), possibleWordSet);
//        }

        //if(!DoubleMetaphoneMatched(text))
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

    private ArrayList<String> originalComboValues = new ArrayList<>();

    /**
     * Function that uses the current inputField text to generate and display a list of
     * approximately matched strings below the text field
     */
    public void matchFuzzySearchValues() {
        String toMatch = inputField.getEditor().getText();
        if(toMatch.equals("")) {
            inputField.getItems().clear();
            inputField.getItems().addAll(originalComboValues);
            inputField.hide();
        } else {
            inputField.getItems().clear();
            inputField.show();
            System.out.println("MATCHING:");
            for(String des : originalComboValues) {
                System.out.println(doubleMetaphone.doubleMetaphone(des)+" "+doubleMetaphone.doubleMetaphone(toMatch));
                if(doubleMetaphone.isDoubleMetaphoneEqual(des, toMatch)) {
                    inputField.getItems().add(des);
                }
            }
            autoCompletionBinding = TextFields.bindAutoCompletion(inputField.getEditor(), possibleWordSet);
            /*int autoCount = autoCompletionBinding.getVisibleRowCount();
            if(inputField.getItems().size() < autoCount){
                inputField.getItems().clear();

            }*/
        }

        /*String selected = inputField.getSelectionModel().getSelectedItem();

        String input = inputField.getEditor().getText();

        Set<String> possibleWordSet2 = new HashSet<>();


        for(String n : possibleWordSet) {
            possibleWordSet2.clear();
            possibleWordSet2.add(n);
            if (doubleMetaphone.isDoubleMetaphoneEqual(input, n)) {
                System.out.println("aaa");
                //autoCompletionBinding2 = TextFields.bindAutoCompletion(inputField.getEditor(), possibleWordSet2);
            }
        }*/
        //if(doubleMetaphone.doubleMetaphone(input).equals(doubleMetaphone.doubleMetaphone(words[1]))) {
//            Collections.addAll(possibleWordSet, words);
//            autoCompletionBinding = TextFields.bindAutoCompletion(inputField.getEditor(), possibleWordSet);

        //System.out.println("TRIGGERED FUZZY VALUES ON: " + inputField.getSelectionModel().getSelectedItem());
        //System.out.println("DOUBLE METAPHONE ENCODED: " + doubleMetaphone.doubleMetaphone(selected, false) + " " +
               // doubleMetaphone.doubleMetaphone(selected, true));

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
        //String selected = inputField.getSelectionModel().getSelectedItem();

        // Initially populate the list with all of the values (long descriptions)
        inputField.getItems().clear();
        possibleWordSet.clear();
        originalComboValues.clear();

        Collection<MapNode> floorNodes = mapSubsystem.getVisibleFloorNodes(currentFloor).values();
        for(MapNode n : floorNodes) {
            originalComboValues.add(n.getLongDescription());
            possibleWordSet.add(n.getLongDescription());
            //autoCompletionBinding = TextFields.bindAutoCompletion(inputField.getEditor(), possibleWordSet);

        }
        if(inputField.getEditor().getText().equals("")) {
            inputField.getItems().addAll(originalComboValues);
        } else {
            matchFuzzySearchValues();
        }

    }

}
