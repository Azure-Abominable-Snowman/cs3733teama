package com.teama.controllers;

import com.teama.mapdrawingsubsystem.MapDrawingSubsystem;
import com.teama.mapsubsystem.MapSubsystem;
import com.teama.mapsubsystem.data.Floor;
import com.teama.mapsubsystem.data.MapNode;
import javafx.scene.control.ComboBox;
import javafx.scene.input.KeyEvent;
import org.apache.commons.codec.language.DoubleMetaphone;

import java.util.*;

public class SearchBarController {

    private ComboBox<String> inputField;
    private MapSubsystem mapSubsystem;
    private MapDrawingSubsystem mapDrawing;

    // Double metaphone fuzzy search algorithm
    private DoubleMetaphone doubleMetaphone;

    private boolean floorNodes;

    public SearchBarController(ComboBox<String> inputField, boolean floorNodes) {
        this.inputField = inputField;
        this.mapSubsystem = MapSubsystem.getInstance();
        this.floorNodes = floorNodes;
        doubleMetaphone = new DoubleMetaphone();
        // Editable (fuzzy searching)
        inputField.getEditor().setEditable(true);
        mapDrawing = MapDrawingSubsystem.getInstance();

        // Tie updating the node listing to floor change events if we are looking at floor nodes
        if(floorNodes) {
            updateNodeListing(mapDrawing.getCurrentFloor());
            mapDrawing.attachFloorChangeListener((a, b, c) -> {
                String text = inputField.getEditor().getText();
                updateNodeListing(mapDrawing.getCurrentFloor());
                inputField.getEditor().setText(text);
            });
        } else {
            updateNodeListing();
            // still attach to a floor change in case nodes are updated
            mapDrawing.attachFloorChangeListener((a, b, c) -> {
                String text = inputField.getEditor().getText();
                updateNodeListing();
                inputField.getEditor().setText(text);
            });
        }

        // On typing into the combo box, update the fuzzy search values
        inputField.getEditor().setOnKeyTyped((event) -> {
            matchFuzzySearchValues(event);
        });
    }

    private ArrayList<String> desToMatchAgainst = new ArrayList<>();

    /**
     * Function that uses the current inputField text to generate and display a list of
     * approximately matched strings below the text field
     */
    public void matchFuzzySearchValues(KeyEvent event) {
        String selected = inputField.getEditor().getText()+event.getCharacter();
        System.out.println(selected);
        if(selected != null && !selected.equals("")) {
            Map<String, Integer> sortedByMatch = new HashMap<>();
            for (String des : desToMatchAgainst) {
                sortedByMatch.put(des, 0);
                // Iterate through each substring and match it with the corresponding substring of the input
                String[] subStrings = des.split(" ");
                String[] toMatchSubStrings = selected.split(" ");
                int timesMatched = 0;
                for (int i = 0; i < subStrings.length; i++) {
                    if (toMatchSubStrings.length > i) {
                        if (doubleMetaphone.isDoubleMetaphoneEqual(subStrings[i], toMatchSubStrings[i])) {
                            timesMatched++;
                        }
                    } else {
                        if (doubleMetaphone.isDoubleMetaphoneEqual(subStrings[i], toMatchSubStrings[toMatchSubStrings.length - 1])) {
                            timesMatched++;
                        }
                    }
                }
                sortedByMatch.put(des, timesMatched);
            }

            ArrayList<Map.Entry<String, Integer>> sorted = new ArrayList<>(sortedByMatch.entrySet());
            Collections.sort(sorted, new Comparator<Map.Entry<String, Integer>>() {
                        @Override
                        public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
                            return o2.getValue().compareTo(o1.getValue());
                        }
                    }
            );
            // Sort the set by the values
            inputField.getItems().clear();
            Iterator<Map.Entry<String, Integer>> sortedIterator = sorted.iterator();
            while(sortedIterator.hasNext()) {
                Map.Entry<String, Integer> next = sortedIterator.next();
                //System.out.println(next.getValue());
                inputField.getItems().add(next.getKey());
            }
            inputField.show();
        }
    }

    public void updateNodeListing() {
        inputField.getItems().clear();
        ArrayList<MapNode> floorNodes = new ArrayList<>();
        for (Floor f : Floor.values()) {
            floorNodes.addAll(mapSubsystem.getVisibleFloorNodes(f).values());
        }
        finishUpdateNodes(floorNodes);
    }

    public void updateNodeListing(Floor floor) {
        // Initially populate the list with all of the values (long descriptions)
        inputField.getItems().clear();
        ArrayList<MapNode> floorNodes = new ArrayList<>();
        floorNodes.addAll(mapSubsystem.getVisibleFloorNodes(floor).values());
        finishUpdateNodes(floorNodes);
    }

    private void finishUpdateNodes(ArrayList<MapNode> floorNodes) {
        // Alphabetize by long description
        floorNodes.sort((o1, o2) -> {
            return o1.getLongDescription().compareTo(o2.getLongDescription());
        });

        for(MapNode n : floorNodes) {
            inputField.getItems().add(n.getLongDescription());
        }
        desToMatchAgainst.addAll(inputField.getItems());
    }

    public MapNode getSelectedNode() {
        return mapSubsystem.getNodeByDescription(inputField.getSelectionModel().getSelectedItem(), true);
    }

}
