package com.teama.controllers;

import com.jfoenix.controls.*;
import com.teama.controllers_refactor.PopOutController;
import com.teama.mapdrawingsubsystem.MapDisplay;
import com.teama.mapsubsystem.MapSubsystem;
import com.teama.mapsubsystem.data.*;
import com.teama.mapsubsystem.pathfinding.AStar.AStar;
import com.teama.mapsubsystem.pathfinding.AStar.BeamSearch;
import com.teama.mapsubsystem.pathfinding.BreathFrist.BreathFirst;
import com.teama.mapsubsystem.pathfinding.Dijkstras.Dijkstras;
import com.teama.mapsubsystem.pathfinding.PathAlgorithm;
import com.teama.mapsubsystem.pathfinding.TextualDirection.Direction;
import com.teama.mapsubsystem.pathfinding.TextualDirection.TextDirections;
import com.teama.messages.EmailMessage;
import com.teama.messages.Message;
import com.teama.messages.SMSMessage;
import com.teama.requestsubsystem.RequestStatus;
import com.teama.messages.SMSMessage;
import com.teama.requestsubsystem.GenericRequest;
import com.teama.requestsubsystem.RequestStatus;
import com.teama.requestsubsystem.RequestType;
import com.teama.requestsubsystem.interpreterfeature.*;
import javafx.beans.Observable;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import static com.teama.requestsubsystem.RequestType.*;
import java.util.HashSet;
import java.util.Set;

import java.util.*;

import static com.teama.requestsubsystem.RequestType.*;

public class MainScreenSidebarController extends PopOutController {
    @FXML
    private HBox hbxRoot;

    @FXML
    private JFXTextArea directions;
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
//START OF REQUEST STUFF
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


    private AnchorPane curReqPane;
//END OF REQUEST STUFF
    // MAP EDITOR TOOLS
    @FXML
    private JFXToggleButton viewNodes, viewEdges, editNodes, editEdges;
    @FXML
    private JFXButton add, edit;

    @FXML
    private TitledPane selectAlg, mapTools, serviceReqs, staff, viewReqs;

    @FXML
    private Text nodePrompt;
    @FXML
    private TextField nodeName;
    @FXML
    private Text nodeCoord, curFloor;
    @FXML
    private Text nodeType;


    @FXML
    JFXComboBox nodeTypes;
    @FXML
    private Pane infoPane;
    private ToggleGroup editorToggles;
    private Canvas c;

    @FXML
    private JFXSlider beamSearchQueue;

    @FXML
    private JFXButton btnAdd;

    @FXML
    private TableView<InterpreterTableAdapter> InterpInfoTable;
    @FXML
    private TableColumn<InterpreterTableAdapter,String> firstCol, lastCol, langCol;
    @FXML
    private TableColumn<InterpreterTableAdapter,String> certCol, phoneCol, emailCol;
    private ToggleGroup algoToggleGroup;
    private SimpleBooleanProperty floorChange = new SimpleBooleanProperty(false);
    private SimpleBooleanProperty isLoggedInProperty;
    private SimpleBooleanProperty showLoginButton;


    private MapSubsystem mapSubsystem;

    private MapDisplay map;
    private Map<String, MapNode> floorNodes;
    private Map<String, MapEdge> floorEdges;
    private FXMLLoader loader;
    private JFXButton curFloorButton;
    private MapNode selectedNode;
    private MapEdge selectedEdge;

    private ArrayList<InterpreterRequest> requests;
    private InterpreterRequest curRequest;


    //
    private ArrayList<String> shownNodes;
    private ArrayList<String> shownEdges;
    private VBox floorButtonBox;

    private EventHandler oldNodeEditorHandler;

    private ObservableList<InterpreterTableAdapter> tableVals;
    public void initialize() {
        mapSubsystem = MapSubsystem.getInstance();


        //NAVIGATION STUFF
        // Add all of the radio buttons to a toggle group
        algoToggleGroup = new ToggleGroup();
        aStar.setToggleGroup(algoToggleGroup);
        breadthFirst.setToggleGroup(algoToggleGroup);
        dijkstra.setToggleGroup(algoToggleGroup);
        beamSearch.setToggleGroup(algoToggleGroup);

        aStar.setUserData(new AStar());
        breadthFirst.setUserData(new BreathFirst());
        dijkstra.setUserData(new Dijkstras());
        beamSearch.setUserData(new BeamSearch((int)beamSearchQueue.getValue()));

        // Select the default algorithm
        mapSubsystem.setPathGeneratorStrategy((PathAlgorithm) algoToggleGroup.getSelectedToggle().getUserData());

        // When the toggle group changes, make the algorithm reflect that
        algoToggleGroup.selectedToggleProperty().addListener((Observable obs) -> {
            System.out.println("Changed to "+algoToggleGroup.getSelectedToggle().getUserData());
            mapSubsystem.setPathGeneratorStrategy((PathAlgorithm)algoToggleGroup.getSelectedToggle().getUserData());
        });
        //SERVICE REQUEST STUFF
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


        //STAFF STUFF
        btnAdd.setVisible(false);
        initInterpColumns();

        //MAP EDITOR
        /*
        EventHandler<MouseEvent> onMapClicked = (MouseEvent e) -> {
            if (editNodes.isSelected()) {
                String nodeID = map.pointAt(new Location((int) e.getX(), (int) e.getY(), map.getCurrentFloor(), "Selected Location"));
                MapNode found = mapSubsystem.getNode(nodeID);
            }
        c = map.getUnderlyingCanvas();
        c.onMouseClickedProperty().set(onMapClicked);
        */
        loader = new FXMLLoader();
        //loader.setController(this);
        editorToggles = new ToggleGroup();
        editNodes.setToggleGroup(editorToggles);
        editEdges.setToggleGroup(editorToggles);

        editorToggles.selectedToggleProperty().addListener((Observable obs) -> {
            add.setVisible(true);
            edit.setVisible(true);
            if (editNodes.isSelected()) {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/NodeEditor.fxml"));
                    //loader.setLocation(getClass().getResource("/NodeEditor.fxml"));
                    Parent root = (Parent) loader.load(); // load in fxml
                    NodeEditorController nodeEditor = loader.getController();
                    edit.setText("Edit Existing");
                    nodeEditor.setButtons(add, edit);
                    nodeEditor.setMap(map);
                    infoPane.getChildren().clear();
                    infoPane.getChildren().add(root);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else if (editEdges.isSelected()) {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/EdgeEditor.fxml"));
                    Parent root = (Parent) loader.load();
                    EdgeEditorController edgeEditor = loader.getController();
                    edgeEditor.setMap(map);
                    edit.setText("Delete");
                    edgeEditor.setButtons(add, edit);
                    infoPane.getChildren().clear();
                    infoPane.getChildren().add(root);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else {
                // Delete the cursor
                map.deletePoint(NodeEditorController.selectedLocID);


                // Restore the old node editor handler so the pop ups keep appearing
                map.getUnderlyingCanvas().setOnMouseClicked(oldNodeEditorHandler);

                if(!editNodes.isSelected()) {
                    System.out.println("STOP EDITING NODES");
                }

                // Remove whatever is in the infopane
                infoPane.getChildren().clear();
            }
        });


        viewNodes.setOnAction((ActionEvent e) -> {
            updateCurrentNodesEdges();
            if (viewNodes.isSelected()) {
                drawAllNodes();
                System.out.println("User selected View Nodes");


            }
            else { //viewNodes turned off, delete
                removeHiddenNodes();
            }
        }

        );


        viewEdges.setOnAction((ActionEvent e) -> {
            if (viewEdges.isSelected()) {
                System.out.println("User selected View Edges");
                drawAllEdges();
            } else {
                deleteAllEdges();
            }
        });

        add.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {

            }
        });



    }

    //MAP DISPLAY CODE?
    /**
     * Sets the map display in this controller, must be ran before anything else is to be done regarding the map
     * @param map
     */
    public void setMapDisplay(MapDisplay map) {
        this.map = map;
        oldNodeEditorHandler = map.getUnderlyingCanvas().getOnMouseClicked();
    }

    public EventHandler<MouseEvent> getOldNodeEditorHanlder() {
        return this.oldNodeEditorHandler;
    }

    /**
     * Sets the floor button vbox, must be ran before anything else
     * @param floorButtonBox
     */
    public void setFloorButtonVBox(VBox floorButtonBox) {
        this.floorButtonBox = floorButtonBox;

        // Listen for floor button changes to switch what hidden nodes
        // are displayed if the toggle buttons are selected
        for(Node button : floorButtonBox.getChildren()) {
            button.pressedProperty().addListener((Observable obs) -> {
                updateHiddenNodesEdges();
            });
        }
    }

    public void setDirections(TextDirections textDir) {
        directions.clear();
        for(Direction dir : textDir.getDirections()) {
            directions.appendText(dir.getDescription()+"\n");
        }
    }

    public void setLoggedInProperty(SimpleBooleanProperty b, SimpleBooleanProperty button) {
        this.isLoggedInProperty = b;

        this.showLoginButton = button;
        selectAlg.visibleProperty().bind(isLoggedInProperty);
        mapTools.visibleProperty().bind(isLoggedInProperty);
        serviceReqs.visibleProperty().bind(isLoggedInProperty);
        staff.visibleProperty().bind(isLoggedInProperty);
        login.visibleProperty().bind(showLoginButton);
        viewReqs.visibleProperty().bind(isLoggedInProperty);
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/LogInScreen.fxml"));
            Parent root = (Parent) loader.load();
            //loader.setLocation(getClass().getResource("/LogInScreen.fxml"));
            StaffLoginController loginController = loader.getController();
            loginController.initialize();

            Scene loginScene = new Scene(root);
            loginController.getLoggedInProperty().addListener((obs, before, now) -> {
                if (now) {
                    loginPopup.hide();

                    btnAdd.setVisible(true);
                    isLoggedInProperty.set(true);
                    showLoginButton.set(false);
                }
                else {
                    isLoggedInProperty.set(false);
                    showLoginButton.set(true);
                }
            });

            loader.setController(loginController);

            loginPopup.setScene(loginScene);
            loginPopup.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onAddStaff(ActionEvent event){
        popUpInterpInfo(null);
    }
    private void updateHiddenNodesEdges() { // controls what is shown on the map based on the toggle currently selected by user
        updateCurrentNodesEdges();

        // If viewnodes is selected, view all the invisible nodes
        if (viewNodes.isSelected()) {
            drawAllNodes();
        }

        // If viewedges is selected, view all invisible edges
        if (viewEdges.isSelected()) {
            drawAllEdges();
        }
    }
    // helper to add all the edges to draw
    private Map<String, MapEdge> getAllEdges(Map<String, MapNode> allNodes) {
        Map<String, MapEdge> allEdges = new HashMap<>();
        for (MapNode m: allNodes.values()) {
            ArrayList<MapEdge> edges = m.getEdges();
            for (MapEdge e: edges) {
                allEdges.put(e.getId(),e);
            }
        }
        return allEdges;
    }


    private void drawAllNodes() {
        updateCurrentNodesEdges();
        for (MapNode m: floorNodes.values()) {
            new DrawNodeInstantly(m).displayOnScreen(map);
        }
    }

    private void removeHiddenNodes() {
        for (String id : mapSubsystem.getInvisibleFloorNodes(map.getCurrentFloor()).keySet()) {
            map.deletePoint(id);
        }
    }

    private Set<String> tempEdges = new HashSet<>();

    private void drawAllEdges() {
        updateCurrentNodesEdges();
        for (MapEdge e: floorEdges.values()) {
            // To make sure we don't overwrite real edges that may be displayed on the screen for pathfinding, add _FAKE at the end of the id.
            new DrawEdgeInstantly(new MapEdgeData(e.getId()+"_FAKE", e.getStartID(), e.getEndID())).displayOnScreen(map);
            tempEdges.add(e.getId()+"_FAKE");
        }
    }

    private void deleteAllEdges() {
        for (String id : tempEdges) {
            map.deleteLine(id);
        }
        tempEdges.clear();
    }

    // when floor is changed, update the current Maps of Nodes and Edges
    private void updateCurrentNodesEdges() {
        floorNodes = mapSubsystem.getFloorNodes(map.getCurrentFloor());
        floorEdges = getAllEdges(floorNodes);
    }

    //Staff screen start


    private void popUpInterpInfo(InterpreterStaff staff){
        Stage InterpPopUp = new Stage();
        try {
            InterpPopUp.setTitle("View B&W Interpreters");
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/InterpreterModScreen.fxml"));

            Scene InterpModScene = new Scene(loader.load());
            InterpreterModController interpreterModController = loader.getController();
            interpreterModController.setInterpreter(staff);
            interpreterModController.setEditing(true);
            interpreterModController.setCompleted(false);
            interpreterModController.getCompleted().addListener((obs, before, completed) -> {
                if (completed) {
                    if(interpreterModController.getEditing()){
                        updateInterpList();
                    }
                    InterpPopUp.hide();
                }
            });
            InterpPopUp.setScene(InterpModScene);
            InterpPopUp.show();
        }
        catch (IOException e) {
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

        tableVals =  FXCollections.observableArrayList();
        for(InterpreterStaff interp: getInterpreterStaff()){
            tableVals.add(new InterpreterTableAdapter(interp));
        }
        InterpInfoTable.setItems(tableVals);
        InterpInfoTable.setRowFactory(tv -> {
            TableRow<InterpreterTableAdapter> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (! row.isEmpty()) {

                    InterpreterTableAdapter clickedRow = row.getItem();
                    //System.out.println(clickedRow.getInterpreter());
                    popUpInterpInfo(clickedRow.getInterpreter());
                }
            });
            return row ;
        });
    }
    //This is called by the add/modify popout to allow the list to update based on the DB
    public void updateInterpList(){
        System.out.println("updating list");
        tableVals =  FXCollections.observableArrayList();
        for(InterpreterStaff interp: getInterpreterStaff()){
            tableVals.add(new InterpreterTableAdapter(interp));
        }
        InterpInfoTable.setItems(tableVals);
    }
    //TODO update the method to get all the interpreters from the DB
    private ArrayList<InterpreterStaff> getInterpreterStaff(){
        return InterpreterSubsystem.getInstance().getAllStaff();
    }

    public void hideLoginButton() {
        login.setVisible(false);
    }


    //Methods for Service Request TitlePane

    /**
     * fills longName JFXComboBox with MapNodes pertaining on the floor selected by the user
     */
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

    /**
     * clears the JFXComboBoxes when the Cancel button is clicked
     * @param e
     */

    //SERVICE REQUEST STUFF
    @FXML
    public void clearRequest(ActionEvent e) {
        building.getSelectionModel().clearSelection();
        floor.getSelectionModel().clearSelection();
        longName.getSelectionModel().clearSelection();
        typeOfRequest.getSelectionModel().clearSelection();
        additionalInfo.clear();
        //controller.
    }

    /**
     * gets the values from all the ComboBoxes,
     * adds the created request to its database
     * sends a message to the staff who is assigned to the request
     * @param e
     */
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

                if(buildingName.equals("") || floorName == null || mapNodeName == null || requestType == null || additionalInfoMessage.equals("") || lang == null || familySize.equals("")){
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error!");
                    alert.setHeaderText("Error with Submitting Your Request.");
                    alert.setContentText("Atleast one of the fields is empty.  Please fill in the empty field or fields.");
                    alert.showAndWait();
                }

                curRequest = new InterpreterRequest(new GenericRequestInfo(mapNodeName.getCoordinate(), staffToFulfill.getStaffID(), additionalInfoMessage),
                        Integer.parseInt(familySize),
                        lang);
                InterpreterSubsystem.getInstance().addRequest(curRequest);
                System.out.println("It was successful");

                class MyThread implements Runnable {

                    public void run(){
                        SMSMessage message1 = new SMSMessage(staffToFulfill.getProvider(), staffToFulfill.getPhone());
                        if (!message1.sendMessage(staffToFulfill.getContactInfo(), createTextMessage())) {
                            EmailMessage message2 = new EmailMessage();
                            message2.sendMessage(staffToFulfill.getContactInfo(), createEmailMessage());
                        }
                    }
                }

            Thread t = new Thread(new MyThread());
            t.start();
                requestView.getItems().clear();
                requestView.getItems().addAll(InterpreterSubsystem.getInstance().getAllRequests(RequestStatus.ASSIGNED));
                System.out.println("It was added");
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

    /**
     * based on the requestType another FXML file is added with additional fields
     */

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
               // viewStaffController.setRequestViewList(controller.getLanguage());
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
    //END REQEUST STUFF
    public HBox getHbxRoot(){return hbxRoot;}

    /**
     * deletes a certain request from the database and then repopulates the requestView
     * @param event
     * @return void
     */

    @FXML
    void deleteRequest(ActionEvent event) {
        InterpreterSubsystem.getInstance().deleteRequest(requestView.getSelectionModel().getSelectedItem().getRequestID());
        requestView.getItems().clear();
        requestView.getItems().addAll(InterpreterSubsystem.getInstance().getAllRequests(RequestStatus.ASSIGNED));
        System.out.println("It was deleted");
    }
    @Override
    public void onOpen(ReadOnlyDoubleProperty xProperty, int xOffset, ReadOnlyDoubleProperty yProperty, int yOffset) {

    }

    /**
     * changes the status of a selected request to complete and then repopulates the requestView
     * @param event
     * @return void
     */

    @FXML
    void changeRequestStatus(ActionEvent event) {
        System.out.println(requestView.getSelectionModel().getSelectedItem());
        InterpreterSubsystem.getInstance().fulfillRequest(requestView.getSelectionModel().getSelectedItem());
        requestView.getItems().clear();
        requestView.getItems().addAll(InterpreterSubsystem.getInstance().getAllRequests(RequestStatus.ASSIGNED));
    }
    @Override
    public void onClose() {

    }

    @Override
    public String getFXMLPath() {
        return null;
    }
}
