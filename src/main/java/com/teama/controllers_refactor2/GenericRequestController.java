package com.teama.controllers_refactor2;

import com.teama.controllers.InterpReqController;
import com.teama.controllers.MaintenanceReqController;
import com.teama.controllers.SpiritualCareController;
import com.teama.controllers_refactor.FInterpreterRequestController;
import com.teama.mapdrawingsubsystem.MapDrawingSubsystem;
import com.teama.mapsubsystem.MapSubsystem;
import com.teama.mapsubsystem.data.Floor;
import com.teama.mapsubsystem.data.Location;
import com.teama.mapsubsystem.data.MapNode;
import com.teama.mapsubsystem.data.NodeType;
import com.teama.messages.Message;
import com.teama.requestsubsystem.*;
import com.teama.requestsubsystem.elevatorfeature.ElevatorRequest;
import com.teama.requestsubsystem.elevatorfeature.ElevatorSubsystem;
import com.teama.requestsubsystem.interpreterfeature.InterpreterRequest;
import com.teama.requestsubsystem.interpreterfeature.InterpreterSubsystem;
import com.teama.requestsubsystem.spiritualcarefeature.SpiritualCareRequest;
import com.teama.requestsubsystem.spiritualcarefeature.SpiritualCareSubsystem;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Map;

import static javafx.scene.paint.Color.color;

/**
 * Created by jakepardue on 12/11/17.
 */
public class GenericRequestController {

    @FXML
    private ScrollPane intReq;

    @FXML
    private JFXTextField openRequestlabel;

    @FXML
    private JFXListView<Request> requestView;

    @FXML
    private JFXButton deleteButton;

    @FXML
    private JFXButton completeButton;

    @FXML
    private VBox addToThis;

    @FXML
    private JFXTextField createRequestLabel;

    @FXML
    private JFXTextField locationLabel;

    @FXML
    private JFXComboBox<String> building;

    @FXML
    private JFXComboBox<Floor> floor;

    @FXML
    private JFXComboBox<MapNode> longName;

    @FXML
    private JFXTextField typeLabel;

    @FXML
    private JFXComboBox<RequestType> typeOfRequest;

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

    private String buildingName;
    private Floor floorName;
    private MapNode mapNodeName;
    private RequestType requestType;
    private String additionalInfoMessage;
    private GenericStaff staffToFulfill;
    private Message message;

    final BooleanProperty nodeSelected = new SimpleBooleanProperty();

    private Request curRequest;

    private InterpReqController iController;
    private SpiritualCareController sController;
    private MaintenanceReqController mController;

    Image check = new Image("/check.png");

    private int SCALING = 450;

    private FXMLLoader loader;

    public void initialize() throws IOException {
        building.getItems().clear();
        building.getItems().addAll("BTM","Tower","Shapiro", "15 Francis", "45 Francis");

        floor.getItems().clear();
        floor.getItems().addAll(Floor.values());

        typeOfRequest.getItems().clear();
        typeOfRequest.getItems().addAll(RequestType.values());

        nodeSelected.set(false);
        longName.valueProperty().addListener((observable, oldValue, newValue) -> {
            System.out.println(mapNodeName);
            mapNodeName=newValue;
            if(newValue != null){
                System.out.println("This is freaking working!");
                MapDrawingSubsystem.getInstance().setZoomFactor(2);
                Location toMove = new Location((newValue.getCoordinate().getxCoord()+SCALING), newValue.getCoordinate().getyCoord(),
                        newValue.getCoordinate().getLevel(), newValue.getCoordinate().getBuilding());
                MapDrawingSubsystem.getInstance().setViewportCenter(toMove);
                //MapDrawingSubsystem.getInstance().drawNode(newValue, 20, color(1, 0, 0));
                MapDrawingSubsystem.getInstance().drawImage("locationMarker", new Image("/ic_place_white_24dp_2x.png"),
                        new Location(mapNodeName.getCoordinate().getxCoord() -52, mapNodeName.getCoordinate().getyCoord() -90,mapNodeName.getCoordinate().getLevel()
                        , mapNodeName.getCoordinate().getBuilding()), false);

            }
        });

    }

    @FXML
    public void deleteRequest(ActionEvent event) {
        if (typeOfRequest.getSelectionModel().getSelectedItem() == RequestType.INTR){
            InterpreterSubsystem.getInstance().deleteRequest(requestView.getSelectionModel().getSelectedItem().getRequestID());
            requestView.getItems().clear();
            requestView.getItems().addAll(InterpreterSubsystem.getInstance().getAllRequests(RequestStatus.ASSIGNED));
            System.out.println("It was deleted");
        }
        else if(typeOfRequest.getSelectionModel().getSelectedItem() == RequestType.MAIN){
            ElevatorSubsystem.getInstance().deleteRequest(requestView.getSelectionModel().getSelectedItem().getRequestID());
            requestView.getItems().clear();
            requestView.getItems().addAll(ElevatorSubsystem.getInstance().getAllRequests(RequestStatus.ASSIGNED));

        }
        else if(typeOfRequest.getSelectionModel().getSelectedItem() == RequestType.SPIRITUAL){
            SpiritualCareSubsystem.getInstance().deleteRequest(requestView.getSelectionModel().getSelectedItem().getRequestID());
            requestView.getItems().clear();
            requestView.getItems().addAll(SpiritualCareSubsystem.getInstance().getAllRequests(RequestStatus.ASSIGNED));
        }

    }

    public void fulfillRequest(ActionEvent e){
        try {
            if (typeOfRequest.getSelectionModel().getSelectedItem() == RequestType.INTR) {
                Stage fulfillStage = new Stage();
                //TODO change name of that plz
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/FulfillIntReq.fxml"));
                Scene fulfillScene;
                try {
                    fulfillScene = new Scene(loader.load());
                    FInterpreterRequestController fillReqController = loader.getController();
                    fillReqController.setReqToFulfill((InterpreterRequest) requestView.getSelectionModel().getSelectedItem());
                    fillReqController.getSubmitted().addListener(((observable, oldValue, submitted) -> {
                        if (submitted) {
                            requestView.getItems().clear();
                            requestView.getItems().addAll(InterpreterSubsystem.getInstance().getAllRequests(RequestStatus.ASSIGNED));
                        }
                    }));

                    fulfillStage.setScene(fulfillScene);
                    fulfillStage.show();
                } catch (IOException exception) {
                    exception.printStackTrace();
                    System.out.println("check file name");
                }
            } else if (typeOfRequest.getSelectionModel().getSelectedItem() == RequestType.MAIN) {
                Stage fulfillStage = new Stage();
                loader = new FXMLLoader();
                loader.setLocation(getClass().getResource("/MaintenanceFufilForm.fxml"));
                Scene fulfillScene;
                try {
                    Pane parent = loader.load();
                    fulfillScene = new Scene(parent);
                    FulfillElevatorMaintController controller = loader.getController();
                    controller.setReqToFulfill((ElevatorRequest)requestView.getSelectionModel().getSelectedItem());
                    controller.getSubmitted().addListener(((observable, oldValue, submitted) -> {
                        if (submitted) {
                            requestView.getItems().clear();
                            requestView.getItems().addAll(ElevatorSubsystem.getInstance().getAllRequests(RequestStatus.ASSIGNED));
                        }
                    }));

                    fulfillStage.setScene(fulfillScene);
                    fulfillStage.show();
                }catch(Exception ex){
                    ex.printStackTrace();
                    System.out.println("check file name");
                }
            }
            else if (typeOfRequest.getSelectionModel().getSelectedItem() == RequestType.SPIRITUAL) {
                Stage fulfillStage = new Stage();
                //TODO change name of that plz
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/SpiritualFulfilRequest.fxml"));
                Scene fulfillScene;
                try {
                    fulfillScene = new Scene(loader.load());
                    FulfilSpiritualReqController controller = loader.getController();
                    controller.setReqToFulfill((SpiritualCareRequest) requestView.getSelectionModel().getSelectedItem());
                    controller.getSubmitted().addListener(((observable, oldValue, submitted) -> {
                        if (submitted) {
                            requestView.getItems().clear();
                            requestView.getItems().addAll(SpiritualCareSubsystem.getInstance().getAllRequests(RequestStatus.ASSIGNED));
                        }
                    }));

                    fulfillStage.setScene(fulfillScene);
                    fulfillStage.show();
                }catch(Exception ex){
                    ex.printStackTrace();
                    System.out.println("check file name");
                }
            }
        }catch (Exception exception){
            exception.printStackTrace();
            System.out.println("Please choose one of the chosen requests");
        }
    }

    @FXML
    void setNodeData(MouseEvent event) {
        buildingName = building.getSelectionModel().getSelectedItem();
        floorName = floor.getSelectionModel().getSelectedItem();
        longName.getItems().clear();
        Map<String, MapNode> nodes = MapSubsystem.getInstance().getVisibleFloorNodes(floorName);
        System.out.println(nodes.keySet());
        if(typeOfRequest.getSelectionModel().getSelectedItem() == RequestType.MAIN){
            for(MapNode n: nodes.values()){
                if(n.getNodeType().equals(NodeType.ELEV) && n.getCoordinate().getBuilding().equals(buildingName)){
                    longName.getItems().add(n);
                }
            }
        }
        else{
            for (MapNode n : nodes.values()) {
                if(n.getCoordinate().getBuilding().equals( buildingName)){
                    longName.getItems().add(n);
                }
            }
        }
        mapNodeName = longName.getSelectionModel().getSelectedItem();
        nodeSelected.set(true);
        System.out.println(nodeSelected);
    }

    @FXML
    void showStaffPopUp(ActionEvent event) {
    }




    void clearRequest() {
        building.getSelectionModel().clearSelection();
        floor.getSelectionModel().clearSelection();
        longName.getSelectionModel().clearSelection();
        additionalInfo.clear();
        //addToThis.getChildren().remove(curReqPane);
        //MapDrawingSubsystem.getInstance().drawNode(mapNodeName, 0, null);
    }


    @FXML
    void submitRequest(ActionEvent event) {

    }


    public Message createTextMessage() {
        return null;
    }




    public void onClose() {
        System.out.println("CLOSE REQUEST");
        if(mapNodeName!=null) {
            MapDrawingSubsystem.getInstance().unDrawImage("locationMarker");
        }
    }

    public RequestType getRequestType(){
        return typeOfRequest.getSelectionModel().getSelectedItem();
    }

    public String getBuildingName(){
        return building.getSelectionModel().getSelectedItem();
    }

    public String getAdditionalInfoMessage(){
        return noteLabel.getText();
    }

    public Floor getFloorName() {
        return floor.getSelectionModel().getSelectedItem();
    }

    public MapNode getMapNodeName(){
        return longName.getSelectionModel().getSelectedItem();
    }

    public GenericStaff getStaffToFulfill(){
        return staffToFulfill;
    }

    public JFXListView<Request> getListView(){
        return this.requestView;
    }

    public void setRequestType(RequestType t) {
        this.requestType = t;
    }

    public VBox getAddToThis(){
        return this.addToThis;
    }

    public JFXComboBox<RequestType> getTypeOfRequest() {
        return typeOfRequest;
    }

    public void setButtonText(String s){
        viewStaffButton.setText(s);
    }


}
