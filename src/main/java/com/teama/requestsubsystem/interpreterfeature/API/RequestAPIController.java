package com.teama.requestsubsystem.interpreterfeature.API;

import com.jfoenix.controls.*;
import com.teama.mapsubsystem.data.MapNode;
import com.teama.requestsubsystem.RequestStatus;
import com.teama.requestsubsystem.ServiceStaff;
import com.teama.requestsubsystem.interpreterfeature.InterpreterRequest;
import com.teama.requestsubsystem.interpreterfeature.Language;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.VBox;

/**
 * Created by aliss on 12/4/2017.
 */
public class RequestAPIController {
    @FXML
    private VBox formBox;
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
    private TableView<InterpreterRequest> requestTable;
    @FXML
    private TableColumn statusCol, locCol, langCol, staffCol;

    private MapNode destination;
    private Language requiredLanguage;
    private ToggleGroup languageSelectionGroup;

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

        //languageSelectionGroup.selectedToggleProperty().addListener();
        reqLocation.setText(destination.getId());

    }



}
