package com.teama.controllers_refactor2;

import com.jfoenix.controls.*;
import com.teama.controllers.InterpReqController;
import com.teama.controllers.MaintenanceReqController;
import com.teama.controllers.SpiritualCareController;
import com.teama.controllers.ViewStaffController;
import com.teama.controllers_refactor.FulfillRequestController;
import com.teama.mapdrawingsubsystem.MapDrawingSubsystem;
import com.teama.mapsubsystem.MapSubsystem;
import com.teama.mapsubsystem.data.Floor;
import com.teama.mapsubsystem.data.MapNode;
import com.teama.mapsubsystem.data.NodeType;
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
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

import static com.teama.requestsubsystem.RequestType.*;
import static com.teama.requestsubsystem.RequestType.TRANS;

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
    private JFXButton deleteButton;

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
    private InterpreterStaff staffToFulfill;
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
        AnchorPane spSpecific = loader.load();
        spiritualCareController = loader.getController();

        loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/GenericRequest.fxml"));
        ScrollPane spvPane = loader.load();
        spGenController = loader.getController();
        spGenController.getAddToThis().getChildren().add(spSpecific);
        SPVBox.getChildren().add(spvPane);
        spGenController.getTypeOfRequest().getSelectionModel().select(RequestType.SPIRITUAL);

        /*
        spGenController.getListView().getItems().clear();
        spGenController.getListView().getItems().addAll(SpiritualCareSubsystem.getInstance().getAllRequests(RequestStatus.ASSIGNED));
         */


        //for elevator stuff
        loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/requestdropdowns/MaintenanceDropDown.fxml"));
        AnchorPane eleSpecific = loader.load();
        maintenanceReqController = loader.getController();

        loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/GenericRequest.fxml"));
        ScrollPane elevBox = loader.load();
        eleGenController = loader.getController();
        eleGenController.getAddToThis().getChildren().add(eleSpecific);
        ELEVBox.getChildren().add(elevBox);
        eleGenController.getListView().getItems().clear();
        //eleGenController.getListView().getItems().addAll(ElevatorSubsystem.getInstance().getAllRequests(RequestStatus.ASSIGNED));
        eleGenController.getTypeOfRequest().getSelectionModel().select(RequestType.MAIN);
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
        Date d = null;


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

        buildingName = intGenController.getBuildingName();
        floorName = intGenController.getFloorName();
        mapNodeName = intGenController.getMapNodeName();
        requestType = RequestType.INTR;
        additionalInfoMessage = intGenController.getAdditionalInfoMessage();

        switch (requestType) {
            /*
            case FOOD:
                //foodDesired = controller.getFoodDesired();
                if(buildingName.equals("") || floorName == null || mapNodeName == null || requestType == null || additionalInfoMessage.equals("") || foodDesired.equals("")){
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error1!");
                    alert.setHeaderText("Error with Submitting Your Request.");
                    alert.setContentText("At least one of the fields is empty.  Please fill in the empty field or fields.");
                    alert.showAndWait();
                    break;
                }
                */


            case INTR:
                lang = interpReqController.getLanguage();

                if(buildingName.equals("") || floorName == null || mapNodeName == null || requestType == null ||  lang == null ){
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error!");
                    alert.setHeaderText("Error with Submitting Your Request.");
                    alert.setContentText("At least one of the fields is empty.  Please fill in the empty field or fields please.");
                    alert.showAndWait();
                    break;
                }
                //viewStaffButton.setText("View Staff");
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
                break;

            /*
            case MAIN:
                PriorityLevel p = maintenanceReqController.getPriority()
                MaintenanceType m = maintenanceReqController.getMaintenanceType();

                if(buildingName.equals("") || floorName == null || mapNodeName == null || requestType == null ||  p == null || m == null){
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error!");
                    alert.setHeaderText("Error with Submitting Your Request.");
                    alert.setContentText("At least one of the fields is empty.  Please fill in the empty field or fields please.");
                    alert.showAndWait();
                    break;
                }

                //viewStaffButton.setText("View Staff");


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
        */

        }
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
    public void deleteRequest(ActionEvent event) {
        /*
        InterpreterSubsystem.getInstance().deleteRequest(requestView.getSelectionModel().getSelectedItem().getRequestID());
        requestView.getItems().clear();
        requestView.getItems().addAll(InterpreterSubsystem.getInstance().getAllRequests(RequestStatus.ASSIGNED));
        System.out.println("It was deleted");
        */
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
                }
                staffPopUp.setTitle("View B&W Staff");
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/ViewStaffPopUp.fxml"));

                Scene staffPopUpScene = new Scene(loader.load());
                ViewStaffController viewStaffController = loader.getController();
                viewStaffController.setLanguage(interpReqController.getLanguage());
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
            if(elevatorTab.isSelected()){
                if(maintenanceReqController.getMaintenanceType()== null){
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
