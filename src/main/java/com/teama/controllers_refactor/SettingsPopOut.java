package com.teama.controllers_refactor;

import com.jfoenix.controls.JFXRadioButton;
import com.jfoenix.controls.JFXSlider;
import com.teama.mapsubsystem.MapSubsystem;
import com.teama.mapsubsystem.ExportFormat;
import com.teama.mapsubsystem.pathfinding.BreathFrist.BreathFirst;
import com.teama.mapsubsystem.pathfinding.DijkstrasFamily.AStar.AStar;
import com.teama.mapsubsystem.pathfinding.DijkstrasFamily.AStar.BeamSearch;
import com.teama.mapsubsystem.pathfinding.DijkstrasFamily.Dijkstras.Dijkstras;
import com.teama.mapsubsystem.pathfinding.DijkstrasFamily.reverseAStar.ReverseAstar;
import com.teama.mapsubsystem.pathfinding.PathAlgorithm;
import com.teama.translator.Translator;
import javafx.beans.Observable;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ToggleGroup;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;

public class SettingsPopOut extends PopOutController {
    private int xOffset, yOffset;
    private ReadOnlyDoubleProperty xProperty, yProperty;
    MapSubsystem mapSubsystem = MapSubsystem.getInstance();



    @FXML
    private void exportCSV(ActionEvent event){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Export To CSV");
        Stage s = new Stage();
        File selectedFile = fileChooser.showSaveDialog(s);
        mapSubsystem.export(ExportFormat.CSV, selectedFile.getAbsolutePath()+"_nodes.csv", selectedFile.getAbsolutePath()+"_edges.csv");
    }


    private ToggleGroup algoToggleGroup;

    @FXML
    private Button spanishBtn;

    @FXML
    private Button englishBtn;

    @FXML
    private Button frenchBtn;

    @FXML
    private Button portugueseBtn;

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

    public void initialize() {
        alignPane(xProperty, xOffset, yProperty, yOffset);

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

    @Override
    public void onOpen(ReadOnlyDoubleProperty xProperty, int xOffset, ReadOnlyDoubleProperty yProperty, int yOffset) {
        this.xOffset = xOffset;
        this.yOffset = yOffset;
        this.xProperty = xProperty;
        this.yProperty = yProperty;
    }

    @FXML
    void changToFrench(ActionEvent event) {
      //  Translator.getInstance().setLang("fr");
    }

    @FXML
    void changToSpanish(ActionEvent event) {
        Translator.getInstance().setLang("es");
    }

    @FXML
    void changeToEnghlish(ActionEvent event) {
        Translator.getInstance().setLang("en");
    }

    @FXML
    void changeToPortuguese(ActionEvent event) {
       // Translator.getInstance().setLang("pt");
    }

    @Override
    public void onClose() {
        System.out.println("CLOSE SETTING");
    }

    @Override
    public String getFXMLPath(){return "/SettingsPopOut.fxml";}

}
