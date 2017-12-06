package com.teama.controllers_refactor;
import com.jfoenix.controls.*;
import com.teama.controllers.InterpReqController;
import com.teama.controllers.ViewStaffController;
import com.teama.mapsubsystem.MapSubsystem;
import com.teama.mapsubsystem.data.Floor;
import com.teama.mapsubsystem.data.MapNode;
import com.teama.mapsubsystem.data.NodeType;
import com.teama.messages.EmailMessage;
import com.teama.messages.Message;
import com.teama.messages.SMSMessage;
import com.teama.requestsubsystem.GenericRequest;
import com.teama.requestsubsystem.RequestStatus;
import com.teama.requestsubsystem.RequestType;
import com.teama.requestsubsystem.interpreterfeature.InterpreterRequest;
import com.teama.requestsubsystem.interpreterfeature.InterpreterStaff;
import com.teama.requestsubsystem.interpreterfeature.InterpreterSubsystem;
import com.teama.requestsubsystem.interpreterfeature.Language;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import static com.teama.requestsubsystem.RequestType.*;

public class RequestPopOut extends PopOutController {
    private int xOffset, yOffset;
    private ReadOnlyDoubleProperty xProperty, yProperty;
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
    private ArrayList<InterpreterRequest> requests;
    private InterpreterRequest curRequest;
    private FXMLLoader loader = new FXMLLoader();

    public void initialize() {
        alignPane(xProperty, xOffset, yProperty, yOffset);
        System.out.println(building);
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
    @Override
    public void onOpen(ReadOnlyDoubleProperty xProperty, int xOffset, ReadOnlyDoubleProperty yProperty, int yOffset) {
        this.xOffset = xOffset;
        this.yOffset = yOffset;
        this.xProperty = xProperty;
        this.yProperty = yProperty;
    }

    @Override
    public void onClose() {
        System.out.println("CLOSE REQUEST");
    }

    @Override
    public String getFXMLPath() {
        return "/RequestPopOut.fxml";
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
                curRequest = new InterpreterRequest(new GenericRequest(mapNodeName.getCoordinate(), staffToFulfill.getStaffID(), requestType, RequestStatus.ASSIGNED, additionalInfoMessage), lang);
                InterpreterSubsystem.getInstance().addRequest(curRequest);
                System.out.println("It was successful");
                SMSMessage message1 = new SMSMessage(staffToFulfill.getProvider(), staffToFulfill.getPhoneNumber());
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
        requestView.getItems().clear();
        requestView.getItems().addAll(InterpreterSubsystem.getInstance().getAllRequests(RequestStatus.ASSIGNED));

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
                System.out.println(getClass().getResource("/InterpreterReq.fxml"));
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
          //  if(controller.getLanguage()!=null) {
                staffPopUp.setTitle("View B&W Staff");
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/ViewStaffPopUp.fxml"));

                Scene staffPopUpScene = new Scene(loader.load());
                ViewStaffController viewStaffController = loader.getController();
                System.out.println(viewStaffController);
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
           // }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

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
}
