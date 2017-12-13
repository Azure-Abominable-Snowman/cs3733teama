package com.teama.controllers_refactor2;

import com.jfoenix.controls.*;
import com.teama.controllers.InterpReqController;
import com.teama.controllers.MaintenanceReqController;
import com.teama.controllers.SpiritualCareController;
import com.teama.controllers.ViewStaffController;
import com.teama.mapsubsystem.data.Floor;
import com.teama.mapsubsystem.data.MapNode;
import com.teama.messages.EmailMessage;
import com.teama.messages.Message;
import com.teama.messages.SMSMessage;
import com.teama.requestsubsystem.*;
import com.teama.requestsubsystem.elevatorfeature.ElevatorRequest;
import com.teama.requestsubsystem.elevatorfeature.ElevatorSubsystem;
import com.teama.requestsubsystem.elevatorfeature.MaintenanceType;
import com.teama.requestsubsystem.interpreterfeature.InterpreterRequest;
import com.teama.requestsubsystem.interpreterfeature.InterpreterStaff;
import com.teama.requestsubsystem.interpreterfeature.InterpreterSubsystem;
import com.teama.requestsubsystem.interpreterfeature.Language;
import com.teama.requestsubsystem.spiritualcarefeature.Religion;
import com.teama.requestsubsystem.spiritualcarefeature.SpiritualCareRequest;
import com.teama.requestsubsystem.spiritualcarefeature.SpiritualCareSubsystem;
import com.teama.requestsubsystem.spiritualcarefeature.SpiritualService;
import com.teama.translator.Translator;
import foodRequest.FoodRequest;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;

public class RequestsController extends StaffToolController {
    @FXML
    private Pane parentPane;

    @FXML
    private Tab interpreterTab;

    @FXML
    private VBox intVBox;

    @FXML
    private Tab spiritualTab;

    @FXML
    private VBox SPVBox;

    @FXML
    private Tab elevatorTab;

    @FXML
    private VBox ELEVBox;

    @FXML
    private Tab foodTab;

    @FXML
    private JFXButton btnFood;

    @FXML
    private VBox vbxWrapper;

    @FXML
    private JFXButton clearButton;

    @FXML
    private JFXButton viewStaffButton;

    @FXML
    private JFXButton submitButton;



    private Tab curTab;

    Image check = new Image("/check.png");

    //private global variables for all requests
    private String buildingName;
    private Floor floorName;
    private MapNode mapNodeName;
    private RequestType requestType;
    private String additionalInfoMessage;
    private ServiceStaff staffToFulfill;
    private Message message;

    private int SCALING = 450;


    final BooleanProperty nodeSelected = new SimpleBooleanProperty();


    private AnchorPane curReqPane;
    private ArrayList<InterpreterRequest> requests;
    private Request curRequest;

    private FXMLLoader loader;

    private GenericRequestController intGenController;
    private GenericRequestController spGenController;
    private GenericRequestController eleGenController;

    private InterpReqController interpReqController;
    private SpiritualCareController spiritualCareController;
    private MaintenanceReqController maintenanceReqController;

    public void initialize() throws IOException {
        System.out.println("PLz");

        //for interpreter requests
        loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/requestdropdowns/InterpreterReqDropDown.fxml"));
        loader.setResources(Translator.getInstance().getNewBundle());
        AnchorPane intSpecific = loader.load();
        interpReqController = loader.getController();


        loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/GenericRequest.fxml"));
        loader.setResources(Translator.getInstance().getNewBundle());
        ScrollPane intPane = loader.load();
        intGenController = loader.getController();
        intGenController.getAddToThis().getChildren().add(intSpecific);
        intVBox.getChildren().add(intPane);
        intGenController.getListView().getItems().clear();
        intGenController.getListView().getItems().addAll(InterpreterSubsystem.getInstance().getAllRequests(RequestStatus.ASSIGNED));
        intGenController.getTypeOfRequest().getSelectionModel().select(RequestType.INTR);



        //for spiritual care request stuff
        loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/requestdropdowns/SpiritualServiceDropDown.fxml"));
        loader.setResources(Translator.getInstance().getNewBundle());
        AnchorPane spSpecific = loader.load();
        spiritualCareController = loader.getController();

        loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/GenericRequest.fxml"));
        loader.setResources(Translator.getInstance().getNewBundle());
        ScrollPane spvPane = loader.load();
        spGenController = loader.getController();
        spGenController.getAddToThis().getChildren().add(spSpecific);
        SPVBox.getChildren().add(spvPane);
        spGenController.getListView().getItems().clear();
        spGenController.getTypeOfRequest().getSelectionModel().select(RequestType.SPIRITUAL);


        spGenController.getListView().getItems().clear();
        spGenController.getListView().getItems().addAll(SpiritualCareSubsystem.getInstance().getAllRequests(RequestStatus.ASSIGNED));



        //for elevator stuff
        loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/requestdropdowns/MaintenanceDropDown.fxml"));
        loader.setResources(Translator.getInstance().getNewBundle());
        AnchorPane eleSpecific = loader.load();
        maintenanceReqController = loader.getController();

        loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/GenericRequest.fxml"));
        loader.setResources(Translator.getInstance().getNewBundle());
        ScrollPane elevBox = loader.load();
        eleGenController = loader.getController();
        eleGenController.getAddToThis().getChildren().add(eleSpecific);
        ELEVBox.getChildren().add(elevBox);
        eleGenController.getTypeOfRequest().getSelectionModel().select(RequestType.MAIN);

        eleGenController.getListView().getItems().clear();
        eleGenController.getListView().getItems().addAll(ElevatorSubsystem.getInstance().getAllRequests(RequestStatus.ASSIGNED));
    }

    public void changeType() throws IOException {
    }

    public Pane getParentPane() {
        return parentPane;
    }



    @FXML
    public void callFoodReq(ActionEvent event) {
        run_Food();
    }




    @FXML
    public void submitRequest() {

        //interpreter request
        Language lang = null;
        String familySize = null;

        //elevator request
        //populate w/ stuff

        //spiritual request
        Religion rel = null;
        SpiritualService service = null;
        LocalDate d = null;


        if (staffToFulfill == null) {
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

        if(interpreterTab.isSelected()) {
            buildingName = intGenController.getBuildingName();
            floorName = intGenController.getFloorName();
            mapNodeName = intGenController.getMapNodeName();
            requestType = intGenController.getRequestType();
            additionalInfoMessage = intGenController.getAdditionalInfoMessage();
            lang = interpReqController.getLanguage();

            if (buildingName.equals("") || floorName == null || mapNodeName == null || requestType == null || lang == null) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error!");
                alert.setHeaderText("Error with Submitting Your Request.");
                alert.setContentText("At least one of the fields is empty.  Please fill in the empty field or fields please.");
                alert.showAndWait();
                throw new NullPointerException("Please check ur fields");
            }
            viewStaffButton.setText("Assign Staff");
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
            intGenController.getListView().getItems().clear();
            intGenController.getListView().getItems().addAll(InterpreterSubsystem.getInstance().getAllRequests(RequestStatus.ASSIGNED));
        }

        else if(elevatorTab.isSelected()) {
            buildingName = eleGenController.getBuildingName();
            floorName = eleGenController.getFloorName();
            mapNodeName = eleGenController.getMapNodeName();
            requestType = eleGenController.getRequestType();
            additionalInfoMessage = eleGenController.getAdditionalInfoMessage();
            PriorityLevel p = maintenanceReqController.getPriority();
            MaintenanceType m = maintenanceReqController.getMaintenanceType();

            if (buildingName.equals("") || floorName == null || mapNodeName == null || requestType == null || p == null || m == null) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error!");
                alert.setHeaderText("Error with Submitting Your Request.");
                alert.setContentText("At least one of the fields is empty.  Please fill in the empty field or fields please.");
                alert.showAndWait();
                throw new NullPointerException("Check ur fields bitch");
            }
            viewStaffButton.setText("Assign Staff");
            curRequest = new ElevatorRequest(new GenericRequest(mapNodeName.getCoordinate(), staffToFulfill.getStaffID(), requestType, RequestStatus.ASSIGNED, additionalInfoMessage), p, m, mapNodeName.getId());
            ElevatorSubsystem.getInstance().addRequest((ElevatorRequest) curRequest);

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
            notifications1.show();
            t.start();
            eleGenController.getListView().getItems().clear();
            eleGenController.getListView().getItems().addAll(ElevatorSubsystem.getInstance().getAllRequests(RequestStatus.ASSIGNED));
        }
        else if(spiritualTab.isSelected()) {
            buildingName = spGenController.getBuildingName();
            floorName = spGenController.getFloorName();
            mapNodeName = spGenController.getMapNodeName();
            requestType = spGenController.getRequestType();
            additionalInfoMessage = spGenController.getAdditionalInfoMessage();
            rel = spiritualCareController.getReligion();
            service = spiritualCareController.getSpiritualService();
            d = spiritualCareController.getDate();
            if (buildingName.equals("") || floorName == null || mapNodeName == null || requestType == null || rel == null || service == null) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error!");
                alert.setHeaderText("Error with Submitting Your Request.");
                alert.setContentText("At least one of the fields is empty.  Please fill in the empty field or fields please.");
                alert.showAndWait();
            }

            viewStaffButton.setText("Assign Staff");
            curRequest = new SpiritualCareRequest(new GenericRequest(mapNodeName.getCoordinate(), staffToFulfill.getStaffID(), requestType, RequestStatus.ASSIGNED, additionalInfoMessage), rel, service, d);
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
            spGenController.getListView().getItems().clear();
            spGenController.getListView().getItems().addAll(SpiritualCareSubsystem.getInstance().getAllRequests(RequestStatus.ASSIGNED));
        }

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
                    + "Language: " + interpReqController.getLanguage() + "\n" +
                    "Additional Info: " + additionalInfoMessage);


        }
        else if(requestType.equals(RequestType.MAIN)){

            message = new Message("Needed: " + requestType.toString() + "\n" +
                    "Where: " + buildingName + ", " + floorName.toString() + ", " + mapNodeName.getLongDescription() + "\n"
                    + "Type of Request" + maintenanceReqController.getMaintenanceType() + "\n" +
                    "Priority: "+ maintenanceReqController.getPriority()+ "\n"+"Additional Info: " + additionalInfoMessage);
        }

        else if(requestType.equals(RequestType.SPIRITUAL)){

            message = new Message("Needed: " + requestType.toString() + "\n" +
                    "Where: " + buildingName + ", " + floorName.toString() + ", " + mapNodeName.getLongDescription() + "\n"
                    + "Religion: " + spiritualCareController.getReligion() + "\n" + "Type of Service: "+ spiritualCareController.getSpiritualService() + "\n"+
                    "Additional Info: " + additionalInfoMessage);
        }
        return message;
    }

    public Message createEmailMessage(){
        if(requestType.equals(RequestType.INTR)){
            message = new Message(requestType.toString()+" Help", "Where: " + buildingName + ", " + floorName.toString() + ", " + mapNodeName.getLongDescription() + "\n"
                    + "Language: " + interpReqController.getLanguage() + "\n" +
                    "Additional Info: " + additionalInfoMessage);
        }
        else if(requestType.equals(RequestType.MAIN)){

            message = new Message(requestType.toString()+" Help", "Where: " + buildingName + ", " + floorName.toString() + ", " + mapNodeName.getLongDescription() + "\n"
                    + "Type of Request" + maintenanceReqController.getMaintenanceType() + "\n" +
                    "Priority: "+ maintenanceReqController.getPriority()+ "\n"+"Additional Info: " + additionalInfoMessage);
        }

        else if(requestType.equals(RequestType.SPIRITUAL)){

            message = new Message(requestType.toString()+" Help", "Where: " + buildingName + ", " + floorName.toString() + ", " + mapNodeName.getLongDescription() + "\n"
                    + "Religion: " + spiritualCareController.getReligion() + "\n" + "Type of Service: "+ spiritualCareController.getSpiritualService() + "\n"+
                    "Additional Info: " + additionalInfoMessage);
        }

        return message;
    }


    @FXML
    public void clearRequest(ActionEvent event) {
        if(interpreterTab.isSelected()){
            intGenController.clearRequest();
            interpReqController.clearRequest();
        }
        else if(spiritualTab.isSelected()){
            spGenController.clearRequest();
            spiritualCareController.clearRequest();
        }
        else if(elevatorTab.isSelected()){
            eleGenController.clearRequest();
            maintenanceReqController.clearRequest();

        }
    }

    @FXML
    void showStaffPopUp(ActionEvent event) {

        Stage staffPopUp = new Stage();
        try {
            if(interpreterTab.isSelected()){
                if(interpReqController.getLanguage()== null){
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error!");
                    alert.setHeaderText("No language selected.");
                    alert.setContentText("Please choose a language.");
                    alert.showAndWait();
                    throw new NullPointerException("Please choose a language");
                }
                staffPopUp.setTitle("View B&W Staff");
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/ViewStaffPopUp.fxml"));
                loader.setResources(Translator.getInstance().getNewBundle());
                Scene staffPopUpScene = new Scene(loader.load());
                ViewStaffController viewStaffController = loader.getController();
                viewStaffController.setLanguage(interpReqController.getLanguage());
                //System.out.println("controller " + controller);
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
            if(elevatorTab.isSelected()){
                if(maintenanceReqController.getMaintenanceType()== null){
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error!");
                    alert.setHeaderText("No type of maintenance selected.");
                    alert.setContentText("Please choose a type of elevator maintenance.");
                    alert.showAndWait();
                    throw new NullPointerException("Please choose a type of elevator service");
                }

                staffPopUp.setTitle("View B&W Staff");
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/ViewStaffPopUp.fxml"));
                loader.setResources(Translator.getInstance().getNewBundle());

                Scene staffPopUpScene = new Scene(loader.load());
                ViewStaffController viewStaffController = loader.getController();
                //TODO set this up
                viewStaffController.setMaintenanceType(maintenanceReqController.getMaintenanceType());

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
            else if(spiritualTab.isSelected()){
                if(spiritualCareController.getReligion() == null){
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error!");
                    alert.setHeaderText("No religion was selected.");
                    alert.setContentText("Please choose a type of religion.");
                    alert.showAndWait();
                    throw new NullPointerException("Please choose a religion");
                }

                staffPopUp.setTitle("View B&W Staff");
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/ViewStaffPopUp.fxml"));
                loader.setResources(Translator.getInstance().getNewBundle());

                Scene staffPopUpScene = new Scene(loader.load());
                ViewStaffController viewStaffController = loader.getController();
                //TODO set this up

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
                System.out.println(staffToFulfill);
            }
            //System.out.println(staffToFulfill.getFirstName()+staffToFulfill.getLastName());
            //String toWrite=staffToFulfill.getFirstName()+staffToFulfill.getLastName();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }




    @Override
    public String getFXMLPath() {
        return "/MainScreenDrawers/RequestPane_refactor.fxml";
    }

    public void bind(DoubleProperty doubleProperty) {

    }

    private void run_Food(){
        FoodRequest foodRequest = new FoodRequest();
        try{
            foodRequest.run(0,0,1900,1000,null,null,null);
        }catch (Exception e){
            System.out.println("Failed to run API");
            e.printStackTrace();
        }
    }

}
