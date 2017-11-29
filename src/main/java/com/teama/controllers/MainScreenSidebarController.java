package com.teama.controllers;

import com.teama.messages.EmailMessage;
import com.teama.messages.SMSMessage;
import com.teama.requestsubsystem.GenericRequestInfo;
import com.teama.requestsubsystem.RequestStatus;
import com.teama.requestsubsystem.interpreterfeature.InterpreterStaff;
import com.teama.requestsubsystem.interpreterfeature.Language;

import java.util.Map;
import com.jfoenix.controls.*;
import com.teama.drawing.MapDisplay;
import com.teama.mapsubsystem.MapSubsystem;
import com.teama.mapsubsystem.data.*;
import com.teama.mapsubsystem.pathfinding.AStar.AStar;
import com.teama.mapsubsystem.pathfinding.AStar.BeamSearch;
import com.teama.mapsubsystem.pathfinding.BreathFrist.BreathFirst;
import com.teama.mapsubsystem.pathfinding.Dijkstras.Dijkstras;
import com.teama.mapsubsystem.pathfinding.PathAlgorithm;
import com.teama.requestsubsystem.RequestType;
import com.teama.requestsubsystem.interpreterfeature.InterpreterRequest;
import com.teama.requestsubsystem.interpreterfeature.InterpreterSubsystem;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.beans.Observable;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ToggleGroup;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import com.teama.messages.Message;

import java.io.IOException;
import java.util.ArrayList;

import static com.teama.requestsubsystem.RequestType.*;

public class MainScreenSidebarController {
    @FXML
    private JFXListView<?> directions;

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
    private VBox addToThis;

    @FXML
    private JFXTextField createRequestLabel;

    @FXML
    private JFXTextField locationLabel;

    @FXML
    private JFXComboBox<String> building = new JFXComboBox<>();

    @FXML
    private JFXComboBox<Floor> floor = new JFXComboBox<>();

    @FXML
    private JFXComboBox<MapNode> longName = new JFXComboBox<>();

    @FXML
    private JFXTextField typeLabel;

    @FXML
    private JFXComboBox<RequestType> typeOfRequest = new JFXComboBox<>();

    @FXML
    private JFXTextField noteLabel;

    @FXML
    private JFXTextArea additionalInfo;

    @FXML
    private JFXButton viewStaffButton;

    @FXML
    private JFXButton cancelButton;

    @FXML
    private JFXButton submitButton;

    @FXML
    private JFXListView<InterpreterRequest> requestView;

    @FXML
    private JFXButton login;


    private String buildingName;
    private Floor floorName;
    private MapNode mapNodeName;
    private RequestType requestType;
    private String additionalInfoMessage;
    private InterpreterStaff staffToFulfill;
    private Message message;

    private InterpReqController controller;

    private FXMLLoader loader = new FXMLLoader();

    private AnchorPane curReqPane;

    private ToggleGroup algoToggleGroup;

    private MapSubsystem mapSubsystem;

    private MapDisplay map;

    private ArrayList<InterpreterRequest> requests;

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
        mapSubsystem.setPathGeneratorStrategy((PathAlgorithm) algoToggleGroup.getSelectedToggle().getUserData());

        // When the toggle group changes, make the algorithm reflect that
        algoToggleGroup.selectedToggleProperty().addListener((Observable obs) -> {
            System.out.println("Changed to " + algoToggleGroup.getSelectedToggle().getUserData());
            mapSubsystem.setPathGeneratorStrategy((PathAlgorithm) algoToggleGroup.getSelectedToggle().getUserData());
        });

        //set up for Service Request
        building.getItems().clear();
        building.getItems().add("BTM");

        floor.getItems().clear();
        floor.getItems().addAll(
                Floor.SUBBASEMENT, Floor.BASEMENT, Floor.GROUND, Floor.ONE, Floor.TWO, Floor.THREE);

        typeOfRequest.getItems().clear();
        typeOfRequest.getItems().addAll(
                FOOD, INTR, MAIN, SEC, TRANS);

        //set up requestViewList
        requestView.getItems().clear();
        requestView.getItems().addAll(InterpreterSubsystem.getInstance().getAllRequests(RequestStatus.ASSIGNED));

    }


    /**
     * Sets the map display in this controller, must be ran before anything else is to be done regarding the map
     *
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
    private InterpreterRequest curRequest;
    @FXML
    void onToggleHiddenNodesAndEdges() {
        if (!shownHiddenNodesAndEdges || !map.getCurrentFloor().equals(hiddenDispFloor)) {
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
            for (String id : shownNodes) {
                map.deletePoint(id);
            }
            for (String id : shownEdges) {
                map.deleteLine(id);
            }
            shownHiddenNodesAndEdges = false;
        }
    }


    @FXML
    public void onLoginClick() {
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
            //login.visibleProperty().bind(loginController.getLoggedInProperty());
            //setLoggedIn(false);
            //login.visibleProperty().bind(this.isLoggedIn);
/*
            loginController.getLoggedInProperty().addListener((obs, before, now) -> {
                if (now) {
                    loginPopup.hide();
                    login.setVisible(false);
                }
                else {
                    login.setVisible(true);
                }
            });
*/
            loader.setController(loginController);

            loginPopup.setScene(loginScene);
            loginPopup.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void hideLoginButton() {
        login.setVisible(false);
    }


    //Methods for Service Request TidlePane


    @FXML
    public void setNodeData() {
        floorName = floor.getSelectionModel().getSelectedItem();
        longName.getItems().clear();
        Map<String, MapNode> nodes = MapSubsystem.getInstance().getFloorNodes(floorName);
        System.out.println(nodes.keySet());
        for (MapNode n : nodes.values()) {
            if (!n.getNodeType().equals(NodeType.HALL)) {
                longName.getItems().add(n);
            }
        }
    }

    @FXML
    public void clearRequest(ActionEvent e) {
        building.getSelectionModel().clearSelection();
        floor.getSelectionModel().clearSelection();
        longName.getSelectionModel().clearSelection();
        typeOfRequest.getSelectionModel().clearSelection();
        additionalInfo.clear();
        //controller.
    }

    @FXML
    public void submitRequest(ActionEvent e) {
        Language lang = null;
        String familySize = null;
        buildingName = building.getSelectionModel().getSelectedItem();
        floorName = floor.getSelectionModel().getSelectedItem();
        mapNodeName = longName.getSelectionModel().getSelectedItem();
        requestType = typeOfRequest.getSelectionModel().getSelectedItem();
        additionalInfoMessage = additionalInfo.getText();

        switch (requestType) {
            case FOOD:
                break;
            case INTR:
                lang = controller.getLanguage();
                familySize = controller.getFamilySize();
                curRequest = new InterpreterRequest(new GenericRequestInfo(mapNodeName.getCoordinate(), staffToFulfill.getStaffID(), additionalInfoMessage),
                        Integer.parseInt(familySize),
                        lang);
                InterpreterSubsystem.getInstance().addRequest(curRequest);
                System.out.println("It was successful");
                SMSMessage message1 = new SMSMessage(staffToFulfill.getProvider(), staffToFulfill.getPhone());
                if (!message1.sendMessage(staffToFulfill.getContactInfo(), createTextMessage())) {
                    EmailMessage message2 = new EmailMessage();
                    message2.sendMessage(staffToFulfill.getContactInfo(), createEmailMessage());
                }
                break;
            case MAIN:
                break;
            case SEC:
                break;
            case TRANS:
                break;
            default:
                break;
        }

        System.out.println(buildingName);
        System.out.println(floorName);
        System.out.println(mapNodeName);
        System.out.println(requestType);
        System.out.println(additionalInfoMessage);
        System.out.println(familySize);
        System.out.println(lang);

    }

    @FXML
    public void onRequestSelected() {
        try {
            System.out.println("Firing");
            if (typeOfRequest.getSelectionModel().getSelectedItem().equals(RequestType.INTR)) {
                System.out.println("Firing");
                loader.setLocation(getClass().getResource("/InterpreterReq.fxml"));
                AnchorPane interpParent = loader.load();
                if (curReqPane != interpParent) {
                    addToThis.getChildren().remove(curReqPane);
                    addToThis.getChildren().add(interpParent);
                    curReqPane = interpParent;
                    InterpReqController temp = loader.getController();
                    controller = temp;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Message createTextMessage(){
        message = new Message("Needed: "+ requestType.toString()+ "\n"+
                "Where: "+ buildingName +", "+ floorName.toString()+", "+mapNodeName.getLongDescription()+ "\n"
                + "Language: " + controller.getLanguage().toString()+"\n"+
                "Size of Family:" + controller.getFamilySize().toString()+"\n"+
                "Additional Info: "+additionalInfoMessage);
        return message;
    }

    public Message createEmailMessage(){
        return message = new Message("Interpreter Help", additionalInfoMessage);
    }

    @FXML
    public void showStaffPopUp(ActionEvent event) {

        Stage staffPopUp = new Stage();
        try {
            if(controller.getLanguage()!=null) {
                staffPopUp.setTitle("View B&W Staff");
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/ViewStaffPopUp.fxml"));

                Scene staffPopUpScene = new Scene(loader.load());
                ViewStaffController viewStaffController = loader.getController();
                viewStaffController.setLanguage(controller.getLanguage());
                viewStaffController.setIsComplete(false);
                viewStaffController.getIsComplete().addListener((obs, before, isComplete) -> {
                    staffToFulfill=viewStaffController.getStaffToFulfill();
                    System.out.println(staffToFulfill);
                    if (isComplete&&staffToFulfill!=null){
                        System.out.println(staffToFulfill);
                        staffPopUp.close();
                        System.out.println("done");
                    }
                });
                staffPopUp.setScene(staffPopUpScene);
                staffPopUp.show();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
