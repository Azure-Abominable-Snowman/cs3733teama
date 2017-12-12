package com.teama.controllers_refactor2;

import com.teama.controllers.InterpReqController;
import com.teama.controllers.MaintenanceReqController;
import com.teama.controllers.SpiritualCareController;
import com.teama.controllers.ViewStaffController;
import com.teama.controllers_refactor.FulfillRequestController;
import com.teama.mapdrawingsubsystem.MapDrawingSubsystem;
import com.teama.mapsubsystem.MapSubsystem;
import com.teama.mapsubsystem.data.Floor;
import com.teama.mapsubsystem.data.MapNode;
import com.teama.messages.EmailMessage;
import com.teama.messages.Message;
import com.teama.messages.SMSMessage;
import com.teama.requestsubsystem.*;
import com.teama.requestsubsystem.elevatorfeature.ElevatorRequest;
import com.teama.requestsubsystem.elevatorfeature.MaintenanceType;
import com.teama.requestsubsystem.interpreterfeature.InterpreterRequest;
import com.teama.requestsubsystem.interpreterfeature.InterpreterStaff;
import com.teama.requestsubsystem.interpreterfeature.InterpreterSubsystem;
import com.teama.requestsubsystem.interpreterfeature.Language;
import com.teama.requestsubsystem.spiritualcarefeature.Religion;
import com.teama.requestsubsystem.spiritualcarefeature.SpiritualCareRequest;
import com.teama.requestsubsystem.spiritualcarefeature.SpiritualService;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;

import java.io.IOException;
import java.util.Date;
import java.util.Map;

import static com.teama.requestsubsystem.RequestType.*;
import static com.teama.requestsubsystem.RequestType.TRANS;

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
    private InterpreterStaff staffToFulfill;
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

        if(requestType == RequestType.INTR){
            System.out.println("hail satan1");
            typeOfRequest.getSelectionModel().select(RequestType.INTR);
            loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/requestdropdowns/InterpreterReqDropDown.fxml"));
            AnchorPane interpParent = loader.load();
            addToThis.getChildren().add(interpParent);
            iController = loader.getController();
        }

        else if(requestType == RequestType.SPIRITUAL){
            System.out.println("hail satan2");
            typeOfRequest.getSelectionModel().select(RequestType.SPIRITUAL);
            loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/requestdropdowns/SpiritualServiceDropDown.fxml"));
            AnchorPane interpParent = loader.load();
            addToThis.getChildren().add(interpParent);
            sController = loader.getController();

        }

        else if(requestType == RequestType.MAIN){
            System.out.println("hail satan3");
            typeOfRequest.getSelectionModel().select(RequestType.MAIN);
            loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/requestdropdowns/MaintenanceDropDown.fxml"));
            AnchorPane interpParent2 = loader.load();
            addToThis.getChildren().add(interpParent2);
            mController = loader.getController();
        }
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
        Stage staffPopUp = new Stage();
        try {
            if(requestType == null){
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error!");
                alert.setHeaderText("No Request Type Selected.");
                alert.setContentText("Please choose a request type.");
                alert.showAndWait();
            }

            if(requestType.equals(RequestType.INTR)){


                if(iController.getLanguage()== null){
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error!");
                    alert.setHeaderText("No language selected.");
                    alert.setContentText("Please choose a language.");
                    alert.showAndWait();
                }


                staffPopUp.setTitle("View B&W Staff");
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/ViewStaffPopUp.fxml"));

                Scene staffPopUpScene = new Scene(loader.load());
                ViewStaffController viewStaffController = loader.getController();
                //viewStaffController.setLanguage(cmbLang.getSelectionModel().getSelectedItem());
                //System.out.println("controller " + controller);
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



            if(requestType.equals(RequestType.MAIN)){

                if(mController.getMaintenanceType()== null){
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error!");
                    alert.setHeaderText("No type of maintenance selected.");
                    alert.setContentText("Please choose a type of maintenance.");
                    alert.showAndWait();
                }

                staffPopUp.setTitle("View B&W Staff");
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/ViewStaffPopUp.fxml"));

                Scene staffPopUpScene = new Scene(loader.load());
                ViewStaffController viewStaffController = loader.getController();
                //TODO set this up
                //viewStaffController.setMaintenanceType(controller2.getMaintenanceType());
                //System.out.println("controller " + controller);
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
            //System.out.println(staffToFulfill.getFirstName()+staffToFulfill.getLastName());
            //String toWrite=staffToFulfill.getFirstName()+staffToFulfill.getLastName();
        } catch (IOException e) {
            e.printStackTrace();
        }
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

        //interpreter request
        Language lang = null;
        String familySize = null;

        //elevator request
        //populate w/ stuff

        //spiritual request
        Religion rel = null;
        SpiritualService service = null;
        Date d = null;


        if(staffToFulfill == null){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error!");
            alert.setHeaderText("No one assigned to this request.");
            alert.setContentText("Please assign a staff to this request.");
            alert.showAndWait();
        }

        class MyThread implements Runnable {

            public void run() {
                SMSMessage message1 = new SMSMessage(staffToFulfill.getProvider(), staffToFulfill.getPhoneNumber());
                if (!message1.sendMessage(staffToFulfill.getContactInfo(), createTextMessage())) {
                    EmailMessage message2 = new EmailMessage();
                    message2.sendMessage(staffToFulfill.getContactInfo(), createEmailMessage());
                }
            }
        }
        Thread t = new Thread(new MyThread());

        buildingName = building.getSelectionModel().getSelectedItem();
        floorName = floor.getSelectionModel().getSelectedItem();
        mapNodeName = longName.getSelectionModel().getSelectedItem();
        requestType = typeOfRequest.getSelectionModel().getSelectedItem();
        additionalInfoMessage = additionalInfo.getText();

        switch (requestType) {
            case FOOD:
                //foodDesired = controller.getFoodDesired();
                if(buildingName.equals("") || floorName == null || mapNodeName == null || requestType == null || additionalInfoMessage.equals("") /*|| foodDesired.equals("")*/){
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error1!");
                    alert.setHeaderText("Error with Submitting Your Request.");
                    alert.setContentText("At least one of the fields is empty.  Please fill in the empty field or fields.");
                    alert.showAndWait();
                    break;
                }


            case INTR:
                lang = iController.getLanguage();

                if(buildingName.equals("") || floorName == null || mapNodeName == null || requestType == null ||  lang == null ){
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error!");
                    alert.setHeaderText("Error with Submitting Your Request.");
                    alert.setContentText("At least one of the fields is empty.  Please fill in the empty field or fields please.");
                    alert.showAndWait();
                    break;
                }
                viewStaffButton.setText("View Staff");
                curRequest = new InterpreterRequest(new GenericRequest(mapNodeName.getCoordinate(), staffToFulfill.getStaffID(), requestType, RequestStatus.ASSIGNED, additionalInfoMessage), lang);
                InterpreterSubsystem.getInstance().addRequest((InterpreterRequest) curRequest);

                System.out.println(InterpreterSubsystem.getInstance().getAllRequests(RequestStatus.ASSIGNED).contains(curRequest));

                System.out.println("It was successful");
                Notifications notifications = Notifications.create()
                        .title("Success!")
                        .text("Your interpreter request has been added.")
                        .graphic(new ImageView(check))
                        .hideAfter(Duration.seconds(4))
                        .position(Pos.CENTER)
                        .onAction(new EventHandler<ActionEvent>() {
                            @Override
                            public void handle(ActionEvent event) {
                                System.out.println("Hi Kent");
                            }
                        });
                notifications.show();

                t.start();
                break;

            case MAIN:
                PriorityLevel p = null; //controller2.getPriority();
                MaintenanceType m = null; //controller2.getMaintenanceType();

                if(buildingName.equals("") || floorName == null || mapNodeName == null || requestType == null ||  p == null || m == null){
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error!");
                    alert.setHeaderText("Error with Submitting Your Request.");
                    alert.setContentText("At least one of the fields is empty.  Please fill in the empty field or fields please.");
                    alert.showAndWait();
                    break;
                }

                viewStaffButton.setText("View Staff");


                curRequest = new ElevatorRequest(new GenericRequest(mapNodeName.getCoordinate(), staffToFulfill.getStaffID(), requestType, RequestStatus.ASSIGNED, additionalInfoMessage), p,m, "");
                //add request to the database here

                Notifications notifications1 = Notifications.create()
                        .title("Success!")
                        .text("Your elevator maintenance request has been added.")
                        .graphic(new ImageView(check))
                        .hideAfter(Duration.seconds(3))
                        .position(Pos.CENTER)
                        .onAction(new EventHandler<ActionEvent>() {
                            @Override
                            public void handle(ActionEvent event) {
                                System.out.println("Hi Kent");
                            }
                        });
                t.start();
                break;

            case SEC:
                break;
            case TRANS:
                break;
            case SPIRITUAL:
                rel = sController.getReligion();
                service = sController.getSpiritualService();
                if(buildingName.equals("") || floorName == null || mapNodeName == null || requestType == null ||  rel == null || service == null){
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error!");
                    alert.setHeaderText("Error with Submitting Your Request.");
                    alert.setContentText("At least one of the fields is empty.  Please fill in the empty field or fields please.");
                    alert.showAndWait();
                    break;
                }
                viewStaffButton.setText("View Staff");
                curRequest = new SpiritualCareRequest(new GenericRequest(mapNodeName.getCoordinate(), staffToFulfill.getStaffID(), requestType, RequestStatus.ASSIGNED, additionalInfoMessage),rel, service,d);
               //add request to database

                System.out.println(InterpreterSubsystem.getInstance().getAllRequests(RequestStatus.ASSIGNED).contains(curRequest));

                System.out.println("It was successful");
                Notifications notifications2 = Notifications.create()
                        .title("Success!")
                        .text("Your spiritual service request has been added.")
                        .graphic(new ImageView(check))
                        .hideAfter(Duration.seconds(3))
                        .position(Pos.CENTER)
                        .onAction(new EventHandler<ActionEvent>() {
                            @Override
                            public void handle(ActionEvent event) {
                                System.out.println("Hi Kent");
                            }
                        });
                notifications2.show();

                t.start();

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


    public Message createTextMessage() {

        if (requestType.equals(RequestType.INTR)) {
            message = new Message("Needed: " + requestType.toString() + "\n" +
                    "Where: " + buildingName + ", " + floorName.toString() + ", " + mapNodeName.getLongDescription() + "\n"
                    + "Language: " + iController.getLanguage() + "\n" +
                    "Additional Info: " + additionalInfoMessage);


        }
        else if(requestType.equals(RequestType.MAIN)){

            message = new Message("Needed: " + requestType.toString() + "\n" +
                    "Where: " + buildingName + ", " + floorName.toString() + ", " + mapNodeName.getLongDescription() + "\n"
                    + "Type of Request" + mController.getMaintenanceType() + "\n" +
                    "Priority: "+ mController.getPriority()+ "\n"+"Additional Info: " + additionalInfoMessage);
        }

        else if(requestType.equals(RequestType.SPIRITUAL)){

            message = new Message("Needed: " + requestType.toString() + "\n" +
                    "Where: " + buildingName + ", " + floorName.toString() + ", " + mapNodeName.getLongDescription() + "\n"
                    + "Religion: " + sController.getReligion() + "\n" + "Type of Service: "+ sController.getSpiritualService() + "\n"+
                    "Additional Info: " + additionalInfoMessage);
        }
        return message;
    }

    public Message createEmailMessage(){
        if(requestType.equals(RequestType.INTR)){
            message = new Message(requestType.toString()+" Help", "Where: " + buildingName + ", " + floorName.toString() + ", " + mapNodeName.getLongDescription() + "\n"
                    + "Language: " + iController.getLanguage() + "\n" +
                    "Additional Info: " + additionalInfoMessage);
        }
        else if(requestType.equals(RequestType.MAIN)){

            message = new Message(requestType.toString()+" Help", "Where: " + buildingName + ", " + floorName.toString() + ", " + mapNodeName.getLongDescription() + "\n"
                    + "Type of Request" + mController.getMaintenanceType() + "\n" +
                    "Priority: "+ mController.getPriority()+ "\n"+"Additional Info: " + additionalInfoMessage);
        }

        else if(requestType.equals(RequestType.SPIRITUAL)){

            message = new Message(requestType.toString()+" Help", "Where: " + buildingName + ", " + floorName.toString() + ", " + mapNodeName.getLongDescription() + "\n"
                    + "Religion: " + sController.getReligion() + "\n" + "Type of Service: "+ sController.getSpiritualService() + "\n"+
                    "Additional Info: " + additionalInfoMessage);
        }

        return message;
    }




    public void onClose() {
        System.out.println("CLOSE REQUEST");
        if(mapNodeName!=null) {
            MapDrawingSubsystem.getInstance().drawNode(mapNodeName, 0, null);
        }
    }

    public void setRequestType(RequestType t) {
        this.requestType = t;
    }
    public VBox getAddToThis(){
        return this.addToThis;
    }


}
