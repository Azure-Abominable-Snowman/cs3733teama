package com.teama.controllers;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXRadioButton;
import com.jfoenix.controls.JFXTreeTableView;
import com.teama.drawing.MapDisplay;
import com.teama.mapsubsystem.MapSubsystem;
import com.teama.mapsubsystem.data.*;
import com.teama.mapsubsystem.pathfinding.AStar.AStar;
import com.teama.mapsubsystem.pathfinding.AStar.BeamSearch;
import com.teama.mapsubsystem.pathfinding.BreathFrist.BreathFirst;
import com.teama.mapsubsystem.pathfinding.Dijkstras.Dijkstras;
import com.teama.mapsubsystem.pathfinding.PathAlgorithm;
import com.teama.messages.ContactInfo;
import com.teama.messages.ContactInfoTypes;
import com.teama.messages.Provider;
import com.teama.requestsubsystem.GenericStaffInfo;
import com.teama.requestsubsystem.interpreterfeature.*;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;


public class MainScreenSidebarController {
    @FXML
    private JFXListView directions;
    @FXML
    private TitledPane algorithmSelect;
    @FXML
    private JFXRadioButton aStar;

    @FXML
    private JFXRadioButton breadthFirst;

    @FXML
    private JFXRadioButton dijkstra;

    @FXML
    private JFXRadioButton beamSearch;

    @FXML
    private Text selectedNode;

    @FXML
    private JFXButton login;

    @FXML
    private TableView<InterpreterTableAdapter> InterpInfoTable;
    @FXML
    private TableColumn<InterpreterTableAdapter,String> firstCol, lastCol, langCol;
    @FXML
    private TableColumn<InterpreterTableAdapter,String> certCol, phoneCol, emailCol;
    private ToggleGroup algoToggleGroup;

    private MapSubsystem mapSubsystem;

    private MapDisplay map;

    public void initialize() {
        mapSubsystem = MapSubsystem.getInstance();

        // Add all of the radio buttons to a toggle group
        algoToggleGroup = new ToggleGroup();
        aStar.setToggleGroup(algoToggleGroup);
        breadthFirst.setToggleGroup(algoToggleGroup);
        dijkstra.setToggleGroup(algoToggleGroup);
        beamSearch.setToggleGroup(algoToggleGroup);

        aStar.setUserData(new AStar());
        breadthFirst.setUserData(new BreathFirst());
        dijkstra.setUserData(new Dijkstras());
        beamSearch.setUserData(new BeamSearch(20)); // TODO: make queue size editable

        // Select the default algorithm
        mapSubsystem.setPathGeneratorStrategy((PathAlgorithm)algoToggleGroup.getSelectedToggle().getUserData());

        // When the toggle group changes, make the algorithm reflect that
        algoToggleGroup.selectedToggleProperty().addListener((Observable obs) -> {
            System.out.println("Changed to "+algoToggleGroup.getSelectedToggle().getUserData());
            mapSubsystem.setPathGeneratorStrategy((PathAlgorithm)algoToggleGroup.getSelectedToggle().getUserData());
        });
        initInterpColumns();
    }

    /**
     * Sets the map display in this controller, must be ran before anything else is to be done regarding the map
     * @param map
     */
    public void setMapDisplay(MapDisplay map) {
        this.map = map;
    }

    @FXML
    void onAddEdge(ActionEvent event) {
        // TODO: After the user clicks this button the user should be able to click on another node to add an edge from
        // TODO: the selected node to that one
    }

    @FXML
    void onAddNode(ActionEvent event) {
        // TODO: After the user clicks this button the user should be able to click a point on the screen
        // TODO: and specify attributes for the nodes creation
    }

    @FXML
    void onSelectNode(ActionEvent event) {
        // TODO: After the button is clicked the user should be able to select a node with the mouse cursor
        // TODO: and it should display in the selectedNode text
    }

    private boolean shownHiddenNodesAndEdges = false;

    private ArrayList<String> shownNodes;
    private ArrayList<String> shownEdges;
    private Floor hiddenDispFloor;

    @FXML
    void onToggleHiddenNodesAndEdges() {
        if(!shownHiddenNodesAndEdges || !map.getCurrentFloor().equals(hiddenDispFloor)) {
            // Show all hidden nodes and edges
            shownNodes = new ArrayList<>();
            shownEdges = new ArrayList<>();
            for (MapNode n : mapSubsystem.getInvisibleFloorNodes(map.getCurrentFloor()).values()) {
                new DrawNodeInstantly(n).displayOnScreen(map);
                shownNodes.add(n.getId());
                for (MapEdge e : n.getEdges()) {
                    new DrawEdgeInstantly(e).displayOnScreen(map);
                    shownEdges.add(e.getId());
                }
            }
            shownHiddenNodesAndEdges = true;
            hiddenDispFloor = map.getCurrentFloor();
        } else {
            // Hide all hidden nodes and edges
            for(String id : shownNodes) {
                map.deletePoint(id);
            }
            for(String id : shownEdges) {
                map.deleteLine(id);
            }
            shownHiddenNodesAndEdges = false;
        }
    }


    @FXML
    void onLogIn(ActionEvent event) {
        ///Dialog d = new Dialog();
        try {
            /*
            d.getDialogPane().setContent(FXMLLoader.load(getClass().getResource("/StaffLogIn.fxml")));
            d.show();
            */
            Stage loginPopup = new Stage();

            loginPopup.setTitle("B&W Login");
            FXMLLoader loader = new FXMLLoader();
            //StaffLoginController loginController = loader.getController();


            Scene loginScene = new Scene(loader.load(getClass().getResource("/LogInScreen.fxml")));
            //loginPopup.setScene((AnchorPane)));
            StaffLoginController loginController = new StaffLoginController();

            loginController.setLoggedIn(false);

            loginController.getLoggedInProperty().addListener((obs, before, nowLoggedIn) -> {
                if (nowLoggedIn) {
                    loginPopup.hide();
                    login.setVisible(false);
                }
            });


            loginPopup.setScene(loginScene);
            loginPopup.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void initInterpColumns(){
        firstCol.setCellValueFactory(
                new PropertyValueFactory<>("firstName"));
        lastCol.setCellValueFactory(
                new PropertyValueFactory<>("lastName"));
        langCol.setCellValueFactory(
                new PropertyValueFactory<>("languages"));
        certCol.setCellValueFactory(
                new PropertyValueFactory<>("certification"));
        phoneCol.setCellValueFactory(
                new PropertyValueFactory<>("phone"));
        emailCol.setCellValueFactory(
                new PropertyValueFactory<>("email"));
        ObservableList<InterpreterTableAdapter> tableVals =  FXCollections.observableArrayList();
        for(InterpreterStaff interp: getInterpreterStaff()){
            tableVals.add(new InterpreterTableAdapter(interp));
        }
        InterpInfoTable.setItems(tableVals);
        //TODO: get the table to have all of the values of the interpreters
    }
    private ArrayList<InterpreterStaff> getInterpreterStaff(){
        Set<ContactInfoTypes> avail = new HashSet<ContactInfoTypes>();
        avail.add(ContactInfoTypes.EMAIL);
        avail.add(ContactInfoTypes.TEXT);
        avail.add(ContactInfoTypes.PHONE);
        ContactInfo c = new ContactInfo(avail, "4444441134", "wwong2@wpi.edu", Provider.ATT);

        Set<Language> langs = new HashSet<>();
        langs.add(Language.ASL);
        langs.add(Language.French);
        langs.add(Language.Moldovan);
        langs.add(Language.JAVA);

        Set<Language> langs2 = new HashSet<>();
        langs2.add(Language.Moldovan);
        langs2.add(Language.German);
        InterpreterInfo i2 = new InterpreterInfo(langs2, CertificationType.CCHI);


        GenericStaffInfo g = new GenericStaffInfo("William", "Wong", c);
        InterpreterInfo i = new InterpreterInfo(langs, CertificationType.CCHI);
        InterpreterStaff wilson = new InterpreterStaff(g, i);
        GenericStaffInfo g2 = new GenericStaffInfo("Joe", "J", c);
        InterpreterStaff joe = new InterpreterStaff(g2, i2);
        ArrayList<InterpreterStaff> interpreters = new ArrayList<>();
        interpreters.add(wilson);
        interpreters.add(joe);
        return interpreters;
        //TODO: Add the dummy method for getting the interpreters

    }
    public void hideLoginButton() {
        login.setVisible(false);
    }
}
