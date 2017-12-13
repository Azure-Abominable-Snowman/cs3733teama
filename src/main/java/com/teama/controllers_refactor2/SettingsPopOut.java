package com.teama.controllers_refactor2;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXRadioButton;
import com.jfoenix.controls.JFXSlider;
import com.jfoenix.controls.JFXTextField;
import com.teama.controllers_refactor.PopOutController;
import com.teama.mapsubsystem.ExportFormat;
import com.teama.mapsubsystem.MapSubsystem;
import com.teama.mapsubsystem.pathfinding.BreathFrist.BreathFirst;
import com.teama.mapsubsystem.pathfinding.DijkstrasFamily.AStar.BeamSearch;
import com.teama.mapsubsystem.pathfinding.DijkstrasFamily.Dijkstras.Dijkstras;
import com.teama.mapsubsystem.pathfinding.DijkstrasFamily.reverseAStar.ReverseAstar;
import com.teama.mapsubsystem.pathfinding.PathAlgorithm;
import com.teama.translator.Translator;
import javafx.beans.Observable;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class SettingsPopOut extends StaffToolController {
    private int xOffset, yOffset;
    private ReadOnlyDoubleProperty xProperty, yProperty;
    MapSubsystem mapSubsystem = MapSubsystem.getInstance();

    private FXMLLoader loader;

    private ToggleGroup algoToggleGroup;

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Pane parentPane;

    @FXML
    private JFXRadioButton aStar;

    @FXML
    private JFXRadioButton breadthFirst;

    @FXML
    private JFXRadioButton dijkstra;

    @FXML
    private JFXRadioButton beamSearch;

    @FXML
    private JFXSlider beamSearchQueue;

    @FXML
    private JFXButton exportCSV;

    @FXML
    private JFXTextField timeOutField;

    @FXML
    private void exportCSV(ActionEvent event){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Export To CSV");
        Stage s = new Stage();
        File selectedFile = fileChooser.showSaveDialog(s);
        mapSubsystem.export(ExportFormat.CSV, selectedFile.getAbsolutePath()+"_nodes.csv", selectedFile.getAbsolutePath()+"_edges.csv");
    }


    public void initialize() {
        //alignPane(xProperty, xOffset, yProperty, yOffset);
//        FXMLLoader openerLoader = new FXMLLoader();
//        openerLoader.setResources(Translator.getInstance().getNewBundle());
        algoToggleGroup = new ToggleGroup();
        aStar.setToggleGroup(algoToggleGroup);
        breadthFirst.setToggleGroup(algoToggleGroup);
        dijkstra.setToggleGroup(algoToggleGroup);
        beamSearch.setToggleGroup(algoToggleGroup);

        aStar.setUserData(new ReverseAstar()); //TODO booooooooooooom u got me. this is not AStar
        breadthFirst.setUserData(new BreathFirst());
        dijkstra.setUserData(new Dijkstras());
        beamSearch.setUserData(new BeamSearch((int)beamSearchQueue.getValue()));

        // Select the default algorithm
     //   mapSubsystem.getPathGenerator().
        mapSubsystem.setPathGeneratorStrategy((PathAlgorithm) algoToggleGroup.getSelectedToggle().getUserData());

        // When the toggle group changes, make the algorithm reflect that
        algoToggleGroup.selectedToggleProperty().addListener((Observable obs) -> {
            System.out.println("Changed to "+algoToggleGroup.getSelectedToggle().getUserData());
            mapSubsystem.setPathGeneratorStrategy((PathAlgorithm)algoToggleGroup.getSelectedToggle().getUserData());
        });
    }

//    @Override
//    public void onOpen(ReadOnlyDoubleProperty xProperty, int xOffset, ReadOnlyDoubleProperty yProperty, int yOffset) {
//        this.xOffset = xOffset;
//        this.yOffset = yOffset;
//        this.xProperty = xProperty;
//        this.yProperty = yProperty;
//    }




//    @Override
//    public void onClose() {
//        System.out.println("CLOSE SETTING");
//    }





    @Override
    public Pane getParentPane() {
        return parentPane;
    }

    @Override
    public String getFXMLPath(){
        return "/SettingPopOut_new.fxml";
    }




}
