package com.teama.controllers_refactor2;

import com.jfoenix.controls.*;
import com.teama.controllers.InterpReqController;
import com.teama.controllers.MaintenanceReqController;
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
import com.teama.requestsubsystem.spiritualcarefeature.SpiritualService;
import javafx.beans.property.BooleanProperty;
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
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import static com.teama.requestsubsystem.RequestType.*;
import static com.teama.requestsubsystem.RequestType.TRANS;

public class RequestsController extends StaffToolController {
    @FXML
    private Pane parentPane;

    @FXML
    private Tab interpreterTab;

    @FXML
    private AnchorPane example;

    @FXML
    private VBox intVBox;

    @FXML
    private Tab spiritualTab;

    @FXML
    private AnchorPane example1;

    @FXML
    private VBox SPVBox;

    @FXML
    private Tab elevatorTab;

    @FXML
    private AnchorPane example3;

    @FXML
    private VBox ELEVBox;


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

    private FXMLLoader loader = new FXMLLoader();

    private GenericRequestController controller;

    public void initialize() throws IOException {
        System.out.println("PLz");

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/GenericRequest.fxml"));

        FXMLLoader loader1 = new FXMLLoader();
        loader1.setLocation(getClass().getResource("/GenericRequest.fxml"));

        FXMLLoader loader2 = new FXMLLoader();
        loader2.setLocation(getClass().getResource("/GenericRequest.fxml"));

        ScrollPane example = loader.load();
        ScrollPane example1 = loader1.load();
        ScrollPane example2 = loader2.load();

        intVBox.getChildren().clear();
        intVBox.getChildren().add(example);

        SPVBox.getChildren().clear();
        SPVBox.getChildren().add(example1);

        ELEVBox.getChildren().clear();
        ELEVBox.getChildren().add(example2);

        controller = loader.getController();

        }

    public void changeType(){
        if(interpreterTab.isSelected()){
            //curTab = interpreterTab;
            //requestType = RequestType.INTR;
            System.out.println("hi there jake");
            controller.setRequestType(RequestType.INTR);
        }
        else if(spiritualTab.isSelected()){
            curTab = spiritualTab;
            requestType = RequestType.SPIRITUAL;
            System.out.println("how are u");
            controller.setRequestType(RequestType.SPIRITUAL);

        }
        else if(elevatorTab.isSelected()){
            curTab = elevatorTab;
            requestType = RequestType.MAIN;
            System.out.println("fuck soft eng");
            controller.setRequestType(RequestType.MAIN);
        }


    }

    @Override
    public Pane getParentPane() {
        return parentPane;
    }

    @Override
    public String getFXMLPath() {
        return "/MainScreenDrawers/RequestPane_refactor.fxml";
    }
}
