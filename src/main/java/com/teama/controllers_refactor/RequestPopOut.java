package com.teama.controllers_refactor;
import com.jfoenix.controls.*;
import com.teama.controllers.InterpReqController;
import com.teama.controllers.ViewStaffController;
import com.teama.mapdrawingsubsystem.MapDrawingSubsystem;
import com.teama.mapsubsystem.MapSubsystem;
import com.teama.mapsubsystem.data.Floor;
import com.teama.mapsubsystem.data.Location;
import com.teama.mapsubsystem.data.MapNode;
import com.teama.mapsubsystem.data.NodeType;
import com.teama.messages.EmailMessage;
import com.teama.messages.Message;
import com.teama.messages.SMSMessage;
import com.teama.requestsubsystem.GenericRequest;
import com.teama.requestsubsystem.Request;
import com.teama.requestsubsystem.RequestStatus;
import com.teama.requestsubsystem.RequestType;
import com.teama.requestsubsystem.interpreterfeature.InterpreterRequest;
import com.teama.requestsubsystem.interpreterfeature.InterpreterStaff;
import com.teama.requestsubsystem.interpreterfeature.InterpreterSubsystem;
import com.teama.requestsubsystem.interpreterfeature.Language;
import javafx.beans.property.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import static com.teama.requestsubsystem.RequestType.*;
import static javafx.scene.paint.Color.color;

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

    @FXML
    private JFXListView<InterpreterRequest> requestView;

    @FXML
    private JFXButton login;

    @FXML
    private JFXButton deleteButton;

    @FXML
    private JFXButton completeButton;

    private String buildingName;
    private Floor floorName;
    private MapNode mapNodeName;
    private RequestType requestType;
    private String additionalInfoMessage;
    private InterpreterStaff staffToFulfill;
    private Message message;

    private int SCALING = 450;

    private InterpReqController controller;

    final BooleanProperty nodeSelected = new SimpleBooleanProperty();


    private AnchorPane curReqPane;
    private ArrayList<InterpreterRequest> requests;
    private InterpreterRequest curRequest = null;
    //private FXMLLoader loader = new FXMLLoader();

    public void initialize() {
        alignPane(xProperty, xOffset, yProperty, yOffset);
        System.out.println(building);
        building.getItems().clear();
        building.getItems().addAll("BTM","Tower","Shapiro", "15 Francis", "45 Francis");

        floor.getItems().clear();
        floor.getItems().addAll(
                Floor.SUBBASEMENT, Floor.BASEMENT, Floor.GROUND, Floor.ONE, Floor.TWO, Floor.THREE);

        typeOfRequest.getItems().clear();
        typeOfRequest.getItems().addAll(
                FOOD, INTR, MAIN, SEC, TRANS);

        //set up requestViewList
        requestView.getItems().clear();
        requestView.getItems().addAll(InterpreterSubsystem.getInstance().getAllRequests(RequestStatus.ASSIGNED));
        nodeSelected.set(false);
        longName.valueProperty().addListener((observable, oldValue, newValue) -> {
            System.out.println(mapNodeName);
            System.out.println(mapNodeName);
            mapNodeName=newValue;
            if(newValue != null){
                System.out.println("This is freaking working!");
                MapDrawingSubsystem.getInstance().setZoomFactor(2);
                Location toMove = new Location((newValue.getCoordinate().getxCoord()+SCALING), newValue.getCoordinate().getyCoord(),
                        newValue.getCoordinate().getLevel(), newValue.getCoordinate().getBuilding());
                MapDrawingSubsystem.getInstance().setViewportCenter(toMove);
                MapDrawingSubsystem.getInstance().drawNode(newValue, 20, color(1, 0, 0));

            }
        });
        nodeSelected.addListener((obs, before, mapNode) ->{
            System.out.println(mapNode);
            System.out.println(mapNodeName);
            if(mapNode!=null){
                System.out.println("This is freaking working!");
                MapDrawingSubsystem.getInstance().setViewportCenter(mapNodeName.getCoordinate());
            }
        });
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
        if(mapNodeName!=null) {
            MapDrawingSubsystem.getInstance().drawNode(mapNodeName, 0, null);
        }

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
        //nodeSelected.set(null);
    }

    @FXML
    public void submitRequest(ActionEvent e) {

        //for Food Request
        String foodDesired = "";
        //for Interpreter Request
        Language lang = null;
        String familySize = null;

        //for Maintenance Request

        //for Transportation Request

        //for Security Request

        if(staffToFulfill == null){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error!");
            alert.setHeaderText("No one assigned to this request.");
            alert.setContentText("Please assign a staff to this request.");
            alert.showAndWait();
        }


        buildingName = building.getSelectionModel().getSelectedItem();
        floorName = floor.getSelectionModel().getSelectedItem();
        mapNodeName = longName.getSelectionModel().getSelectedItem();
        requestType = typeOfRequest.getSelectionModel().getSelectedItem();
        additionalInfoMessage = additionalInfo.getText();

        switch (requestType) {
            case FOOD:
                //foodDesired = controller.getFoodDesired();
                if(buildingName.equals("") || floorName == null || mapNodeName == null || requestType == null || additionalInfoMessage.equals("") || foodDesired.equals("")){
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error1!");
                    alert.setHeaderText("Error with Submitting Your Request.");
                    alert.setContentText("At least one of the fields is empty.  Please fill in the empty field or fields.");
                    alert.showAndWait();
                }
                break;
            case INTR:
                lang = controller.getLanguage();
                familySize = controller.getFamilySize();

                if(buildingName.equals("") || floorName == null || mapNodeName == null || requestType == null || additionalInfoMessage.equals("") || lang == null || familySize.equals("")){
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error2!");
                    alert.setHeaderText("Error with Submitting Your Request.");
                    alert.setContentText("At least one of the fields is empty.  Please fill in the empty field or fields please.");
                    alert.showAndWait();
                }
                curRequest = new InterpreterRequest(new GenericRequest(mapNodeName.getCoordinate(), staffToFulfill.getStaffID(), requestType, RequestStatus.ASSIGNED, additionalInfoMessage), lang);
                InterpreterSubsystem.getInstance().addRequest(curRequest);


                System.out.println("It was successful");
                //TODO fix this
               // if(!InterpreterSubsystem.getInstance().getAllRequests(RequestStatus.ASSIGNED).contains(curRequest)){
                    System.out.println("I o[i2jej]qoi[2 you so much");
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Confirmation");
                    alert.setHeaderText("Success!");
                    alert.setContentText("Your interpreter request has been sent! ");
                    alert.showAndWait();
                //}

                class MyThread implements Runnable {

                    public void run(){
                        SMSMessage message1 = new SMSMessage(staffToFulfill.getProvider(), staffToFulfill.getPhoneNumber());
                        if (!message1.sendMessage(staffToFulfill.getContactInfo(), createTextMessage())) {
                            EmailMessage message2 = new EmailMessage();
                            message2.sendMessage(staffToFulfill.getContactInfo(), createEmailMessage());
                        }
                    }
                }
                Thread t = new Thread(new MyThread());
                System.out.println("Message has been sent!");
                t.start();

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
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(getClass().getResource("/InterpreterReq.fxml"));
                AnchorPane interpParent = loader.load();
                if (curReqPane != interpParent) {
                    System.out.println("Hello Jake");
                    addToThis.getChildren().remove(curReqPane);
                    addToThis.getChildren().add(interpParent);
                    curReqPane = interpParent;
                    InterpReqController temp = loader.getController();
                    controller = temp;
                }
            }
            else if(typeOfRequest.getSelectionModel().getSelectedItem().equals(RequestType.TRANS)){
                System.out.println("this is working");
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(getClass().getResource("/Transportation.fxml"));
                AnchorPane interpParent = loader.load();
                if (curReqPane != interpParent) {
                    System.out.println("Hello Jake");
                    addToThis.getChildren().remove(curReqPane);
                    addToThis.getChildren().add(interpParent);
                    curReqPane = interpParent;
                    InterpReqController temp = loader.getController();
                    controller = temp;
                }
            }

            else if(typeOfRequest.getSelectionModel().getSelectedItem().equals(RequestType.FOOD)){
                System.out.println("this is working");
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(getClass().getResource("/FoodDropDown.fxml"));
                AnchorPane interpParent = loader.load();
                if (curReqPane != interpParent) {
                    System.out.println("Hello Jake");
                    addToThis.getChildren().remove(curReqPane);
                    addToThis.getChildren().add(interpParent);
                    curReqPane = interpParent;
                    InterpReqController temp = loader.getController();
                    controller = temp;
                }
            }
            else if(typeOfRequest.getSelectionModel().getSelectedItem().equals(RequestType.MAIN)){
                System.out.println("this is working");
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(getClass().getResource("/MaintenanceDropDown.fxml"));
                AnchorPane interpParent = loader.load();
                if (curReqPane != interpParent) {
                    System.out.println("Hello Jake");
                    addToThis.getChildren().remove(curReqPane);
                    addToThis.getChildren().add(interpParent);
                    curReqPane = interpParent;
                    InterpReqController temp = loader.getController();
                    controller = temp;
                }
            }

            else if(typeOfRequest.getSelectionModel().getSelectedItem().equals(RequestType.SEC)){
                System.out.println("this is working");
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(getClass().getResource("/SecurityDropDown.fxml"));
                AnchorPane interpParent = loader.load();
                if (curReqPane != interpParent) {
                    System.out.println("Hello Jake");
                    addToThis.getChildren().remove(curReqPane);
                    addToThis.getChildren().add(interpParent);
                    curReqPane = interpParent;
                    InterpReqController temp = loader.getController();
                    controller = temp;
                }
            }
            } catch (IOException e1) {
            e1.printStackTrace();
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
        return message = new Message(requestType.toString()+" Help", additionalInfoMessage);
    }

    @FXML
    public void showStaffPopUp(ActionEvent event) {

        Stage staffPopUp = new Stage();
        try {
            if(typeOfRequest.getSelectionModel().getSelectedItem() == null){
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error!");
                alert.setHeaderText("No Request Type Selected.");
                alert.setContentText("Please choose a request type.");
                alert.showAndWait();
            }

            if(typeOfRequest.getSelectionModel().getSelectedItem().equals(RequestType.INTR)){
                if( controller.getLanguage()== null){
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error4!");
                    alert.setHeaderText("No language selected.");
                    alert.setContentText("Please choose a language.");
                    alert.showAndWait();
                }
                    staffPopUp.setTitle("View B&W Staff");
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/ViewStaffPopUp.fxml"));

                    Scene staffPopUpScene = new Scene(loader.load());
                    ViewStaffController viewStaffController = loader.getController();
                    viewStaffController.setLanguage(controller.getLanguage());
                    System.out.println("controller " + controller);
                    //viewStaffController.setRequestViewList(controller.getLanguage());
                    viewStaffController.setIsComplete(false);
                    viewStaffController.getIsComplete().addListener((obs, before, isComplete) -> {
                        staffToFulfill=viewStaffController.getStaffToFulfill();
                        System.out.println(staffToFulfill);
                        if (isComplete&&staffToFulfill!=null){
                            System.out.println(staffToFulfill);
                            viewStaffButton.setText(staffToFulfill.getFirstName() + " " + staffToFulfill.getLastName());
                            staffPopUp.close();
                            System.out.println("done");
                        }
                    });
                    staffPopUp.setScene(staffPopUpScene);
                    staffPopUp.show();
            }
            System.out.println(staffToFulfill.getFirstName()+staffToFulfill.getLastName());
            String toWrite=staffToFulfill.getFirstName()+staffToFulfill.getLastName();
        } catch (IOException e) {
            e.printStackTrace();
        }
       // viewStaffButton.setText("work");
    }

    @FXML
    public void setNodeData() {
        buildingName = building.getSelectionModel().getSelectedItem();
        floorName = floor.getSelectionModel().getSelectedItem();
        longName.getItems().clear();
        Map<String, MapNode> nodes = MapSubsystem.getInstance().getFloorNodes(floorName);
        System.out.println(nodes.keySet());
        for (MapNode n : nodes.values()) {
            if (!n.getNodeType().equals(NodeType.HALL) && n.getCoordinate().getBuilding().equals(buildingName)) {
                longName.getItems().add(n);
            }
        }
        mapNodeName = longName.getSelectionModel().getSelectedItem();
        nodeSelected.set(true);
        System.out.println(nodeSelected);


    }

    @FXML
    public void deleteRequest(ActionEvent event) {
        InterpreterSubsystem.getInstance().deleteRequest(requestView.getSelectionModel().getSelectedItem().getRequestID());
        requestView.getItems().clear();
        requestView.getItems().addAll(InterpreterSubsystem.getInstance().getAllRequests(RequestStatus.ASSIGNED));
        System.out.println("It was deleted");
    }

    @FXML
    public void fulfillRequest(ActionEvent e){
        Stage fulfillStage = new Stage();
        //TODO change name of that plz
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/FulfillIntReq.fxml"));
        Scene fulfillScene;
        try {
            fulfillScene = new Scene(loader.load());
            FulfillRequestController fillReqController = loader.getController();
            fillReqController.setReqToFulfill(requestView.getSelectionModel().getSelectedItem());
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
}
