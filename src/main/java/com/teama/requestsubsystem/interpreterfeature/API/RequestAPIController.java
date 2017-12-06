package com.teama.requestsubsystem.interpreterfeature.API;

import com.jfoenix.controls.*;
import com.teama.mapsubsystem.data.MapNode;
import com.teama.requestsubsystem.*;
import com.teama.requestsubsystem.interpreterfeature.InterpreterRequest;
import com.teama.requestsubsystem.interpreterfeature.InterpreterStaff;
import com.teama.requestsubsystem.interpreterfeature.InterpreterSubsystem;
import com.teama.requestsubsystem.interpreterfeature.Language;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.ArrayList;
import java.util.function.Predicate;

/**
 * Created by aliss on 12/4/2017.
 */
public class RequestAPIController {

    @FXML
    private JFXRadioButton span, french, german, rus, cantonese, ukranian, lux, mold, asl;
    @FXML
    private JFXTextField reqLocation;
    @FXML
    private JFXTextArea additionalInfo;
    @FXML
    private JFXComboBox<ServiceStaff> qualStaff;
    @FXML
    private JFXComboBox<RequestStatus> reqStatus;
    @FXML
    private JFXButton cancelBtn, submitBtn, filterBtn, deleteBtn, fulfillBtn;
    @FXML
    private TableView<RequestTableData> requestTable;
    @FXML
    private TableColumn<RequestTableData, RequestStatus> statusCol;
    @FXML
    private TableColumn<RequestTableData, String> locCol;
    @FXML
    private TableColumn<RequestTableData, Language> langCol;
    @FXML
    private TableColumn<RequestTableData, Integer> staffCol;

    @FXML
    private VBox formBox;
    @FXML
    private GridPane newForm, newFormBtns;

    private MapNode destination;
    private Language requiredLanguage;
    private ToggleGroup languageSelectionGroup;
    private BooleanProperty isIncomplete = new SimpleBooleanProperty(true);
    private BooleanProperty isNotSelected = new SimpleBooleanProperty(true);
    private BooleanProperty isFulfilling = new SimpleBooleanProperty(false);
    private RequestTableData selectedData = null;

    public RequestAPIController(MapNode destination) {
        this.destination = destination;
    }

    public void initialize() {
        languageSelectionGroup = new ToggleGroup();
        span.setToggleGroup(languageSelectionGroup);
        french.setToggleGroup(languageSelectionGroup);
        german.setToggleGroup(languageSelectionGroup);
        rus.setToggleGroup(languageSelectionGroup);
        cantonese.setToggleGroup(languageSelectionGroup);
        ukranian.setToggleGroup(languageSelectionGroup);
        lux.setToggleGroup(languageSelectionGroup);
        mold.setToggleGroup(languageSelectionGroup);
        asl.setToggleGroup(languageSelectionGroup);

        span.setUserData(Language.Spanish);
        french.setUserData(Language.French);
        german.setUserData(Language.German);
        rus.setUserData(Language.Russian);
        cantonese.setUserData(Language.Cantonese);
        ukranian.setUserData(Language.Ukranian);
        lux.setUserData(Language.Luxembourgish);
        mold.setUserData(Language.Moldovan);
        asl.setUserData(Language.ASL);

        //languageSelectionGroup.selectedToggleProperty().addListener();
        reqLocation.setText(destination.getId());
        languageSelectionGroup.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
            @Override
            public void changed(ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) {
                if (newValue != null) {
                    Language chosen = (Language)newValue.getUserData();
                    qualStaff.getItems().clear();
                    ArrayList<InterpreterStaff> qualified = InterpreterSubsystem.getInstance().findQualified(chosen);
                    qualStaff.getItems().addAll(qualified);
                    isNotSelected.setValue(false);
                }
                else {
                    isNotSelected.setValue(true);
                }
            }
        });

        //submitBtn.disableProperty().bind(isIncomplete);

        //deleteBtn.disableProperty().bind(isNotSelected);

        //reqStatus.getItems().clear();
        //reqStatus.getItems().addAll(RequestStatus.values());

        requestTable.onMouseClickedProperty().addListener(new ChangeListener<EventHandler<? super MouseEvent>>() {
            @Override
            public void changed(ObservableValue<? extends EventHandler<? super MouseEvent>> observable, EventHandler<? super MouseEvent> oldValue, EventHandler<? super MouseEvent> newValue) {
                if (requestTable.getSelectionModel().getSelectedItem() != null) {
                    isNotSelected.setValue(false);
                }
                else {
                    isNotSelected.setValue(true);
                }
            }
        });

        isFulfilling.addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                formBox.getChildren().clear();

                if (!newValue) {
                    formBox.getChildren().addAll(newForm, newFormBtns);
                    clearAllNewRequestFields();
                }
            }
        });
        //fulfillBtn.disableProperty().bind(isNotSelected);

        //    private TableColumn statusCol, locCol, langCol, staffCol;

        statusCol.setCellValueFactory(cellData -> cellData.getValue().statusProperty());
        locCol.setCellValueFactory(cellData -> cellData.getValue().locationProperty());
        langCol.setCellValueFactory(cellData -> cellData.getValue().requiredLanguageProperty());
        staffCol.setCellValueFactory(cellData -> cellData.getValue().staffIDProperty().asObject());


    }


    @FXML
    void onCancel(ActionEvent event) {
        clearAllNewRequestFields();
    }

    @FXML
    void onSubmit(ActionEvent event) {
        ServiceStaff selected = qualStaff.getSelectionModel().getSelectedItem();
        Toggle requiredLangToggle = languageSelectionGroup.getSelectedToggle();
        if (selected != null && requiredLangToggle!= null) {
            Request newReq = new GenericRequest(destination.getCoordinate(), selected.getStaffID(), RequestType.INTR, RequestStatus.ASSIGNED, additionalInfo.getText());
            InterpreterRequest newInterpReq = new InterpreterRequest(newReq, (Language) requiredLangToggle.getUserData());
            InterpreterRequest added = InterpreterSubsystem.getInstance().addRequest(newInterpReq);
            if (added != null) {
                clearAllNewRequestFields();

                // populate request table
                RequestTableData newData = new RequestTableData(added, destination);
                requestTable.getItems().addAll(newData);
            }
        }
        else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Submission Error");
            alert.setHeaderText("Form Submission Failure");
            alert.setContentText("All fields must be filled in for the request to be submitted.");
            alert.showAndWait();
        }

    }
    public boolean isIsFulfilling() {
        return isFulfilling.get();
    }

    public BooleanProperty isFulfillingProperty() {
        return isFulfilling;
    }

    public void setIsFulfilling(boolean isFulfilling) {
        this.isFulfilling.set(isFulfilling);
    }

/*
    @FXML
    void onFilter(ActionEvent e) {
        RequestStatus selected = reqStatus.getSelectionModel().getSelectedItem();
        if (selected != null) {
            ArrayList<InterpreterRequest> filtered = new ArrayList<>();
            requestTable.get
            if (selected == RequestStatus.ASSIGNED || selected == RequestStatus.CLOSED) {
                filtered =  InterpreterSubsystem.getInstance().getAllRequests(selected);
            }
            else {
                filtered = InterpreterSubsystem.getInstance().getAllRequests(RequestStatus.ASSIGNED);
                for (InterpreterRequest r: InterpreterSubsystem.getInstance().getAllRequests(RequestStatus.CLOSED)) {
                    filtered.add(r);
                }
            }
            for (InterpreterRequest req: filtered) {
                RequestTableData updated = new RequestTableData(req, MapSubsystem.getInstance().getNode());
                requestTable.getItems().add(updated);
            }
        }
    }
    */

    private void clearAllNewRequestFields() {
        reqLocation.clear();
        for (Toggle t: languageSelectionGroup.getToggles()) {
            t.setSelected(false);
        }
        qualStaff.getItems().clear();
        additionalInfo.clear();
    }

    @FXML
    void onFulfill(ActionEvent e) {
        if (requestTable.getSelectionModel().getSelectedItem() != null) {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/FullfilRqAPI.fxml"));
            RequestTableData selected = requestTable.getSelectionModel().getSelectedItem();
            isFulfilling.setValue(true);
            FulfillRqAPIController controller = new FulfillRqAPIController(selected, isFulfilling, requestTable);
            loader.setController(controller);
            try {
                Pane node = loader.load();
                formBox.getChildren().addAll(node);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }


    }

    @FXML
    void onDelete(ActionEvent e) {
        if (requestTable.getSelectionModel().getSelectedItem() != null) {
            RequestTableData selected = requestTable.getSelectionModel().getSelectedItem();

            if (InterpreterSubsystem.getInstance().deleteRequest(selected.getRequestID())) {
                requestTable.getItems().removeIf(new Predicate<RequestTableData>() {
                    @Override
                    public boolean test(RequestTableData requestTableData) {
                        return requestTableData.getRequestID() == selected.getRequestID();
                    }
                });
            }
            else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Request Deletion");
                alert.setHeaderText("Failed to Delete Request");
                alert.setContentText("An error occured while attempting to delete this request.");
                alert.showAndWait();
            }
        }

    }




}
