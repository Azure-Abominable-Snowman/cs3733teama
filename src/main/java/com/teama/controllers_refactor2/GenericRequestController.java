package com.teama.controllers_refactor2;

import com.teama.controllers.InterpReqController;
import com.teama.controllers.MaintenanceReqController;
import com.teama.controllers.SpiritualCareController;
import com.teama.controllers_refactor.FulfillRequestController;
import com.teama.mapdrawingsubsystem.MapDrawingSubsystem;
import com.teama.mapsubsystem.MapSubsystem;
import com.teama.mapsubsystem.data.Floor;
import com.teama.mapsubsystem.data.MapNode;
import com.teama.messages.Message;
import com.teama.requestsubsystem.*;
import com.teama.requestsubsystem.interpreterfeature.InterpreterRequest;
import com.teama.requestsubsystem.interpreterfeature.InterpreterSubsystem;
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
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Map;

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

    private FXMLLoader loader;

    public void initialize() throws IOException {
        building.getItems().clear();
        building.getItems().addAll("BTM","Tower","Shapiro", "15 Francis", "45 Francis");

        floor.getItems().clear();
        floor.getItems().addAll(Floor.values());

        typeOfRequest.getItems().clear();
        typeOfRequest.getItems().addAll(RequestType.values());

    }

    @FXML
    public void deleteRequest(ActionEvent event) {
        InterpreterSubsystem.getInstance().deleteRequest(requestView.getSelectionModel().getSelectedItem().getRequestID());
        requestView.getItems().clear();
        requestView.getItems().addAll(InterpreterSubsystem.getInstance().getAllRequests(RequestStatus.ASSIGNED));
        System.out.println("It was deleted");
    }

    public void fulfillRequest(ActionEvent e){

        Stage fulfillStage = new Stage();
        //TODO change name of that plz
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/FulfillIntReq.fxml"));
        Scene fulfillScene;
        try {
            fulfillScene = new Scene(loader.load());
            FulfillRequestController fillReqController = loader.getController();
            fillReqController.setReqToFulfill((InterpreterRequest) requestView.getSelectionModel().getSelectedItem());
            fillReqController.getSubmitted().addListener(((observable, oldValue, submitted) -> {
                if(submitted){
                    requestView.getItems().clear();
                    requestView.getItems().addAll(InterpreterSubsystem.getInstance().getAllRequests(RequestStatus.ASSIGNED));
                }
            }));

            fulfillStage.setScene(fulfillScene);
            fulfillStage.show();
        }
        catch(IOException exception){
            exception.printStackTrace();
            System.out.println("check file name");
        }
    }

    @FXML
    void setNodeData(MouseEvent event) {
        buildingName = building.getSelectionModel().getSelectedItem();
        floorName = floor.getSelectionModel().getSelectedItem();
        longName.getItems().clear();
        Map<String, MapNode> nodes = MapSubsystem.getInstance().getVisibleFloorNodes(floorName);
        System.out.println(nodes.keySet());
        for (MapNode n : nodes.values()) {
            longName.getItems().add(n);
        }
        mapNodeName = longName.getSelectionModel().getSelectedItem();
        nodeSelected.set(true);
        System.out.println(nodeSelected);
    }

    @FXML
    void showStaffPopUp(ActionEvent event) {
    }



    @FXML
    void clearRequest(ActionEvent event) {
        building.getSelectionModel().clearSelection();
        floor.getSelectionModel().clearSelection();
        longName.getSelectionModel().clearSelection();
        additionalInfo.clear();
        //addToThis.getChildren().remove(curReqPane);
        MapDrawingSubsystem.getInstance().drawNode(mapNodeName, 0, null);
        viewStaffButton.setText("View Staff");
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
            MapDrawingSubsystem.getInstance().drawNode(mapNodeName, 0, null);
        }
    }

    public RequestType getRequestType(){
        return requestType;
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


}
