package boundaries;

import controllers.SceneEngine;
import entities.MapNode;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import entities.db.RequestTable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import entities.servicerequests.*;
import entities.HospitalMap;
import java.util.Map;
import java.util.ArrayList;

public class RequestScreenController implements Controller {
    RequestTable requestTable = RequestTable.getInstance();

    int reqNum = 0;
    @Override
    public String getFXMLFileName() {
        return "RequestScreen.fxml";
    }

    @FXML
    private ComboBox building;

    @FXML
    private ComboBox floor;
    private String floorName;

    @FXML
    private ComboBox longName;
    private String nodeName;

    @FXML
    private ComboBox reqType;

    @FXML
    private TextArea noteField;

    @FXML
    private Button submit;
    @FXML
    private Button confirm;
    @FXML
    private Button cancel;

    @FXML
    private Button backButt;
    @FXML
    Button fulfillRequest;

    @FXML
    public void submitRequest() {

        Request request;
        MapNode mapNode;
        mapNode = findNode();
        RequestType requestType = null;
        PriorityLevel priorityLevel = null;

        //set Request Type
        if (reqType.getSelectionModel().isEmpty()) {
        }
        else {
            String rType = reqType.getItems().toString();
            switch (rType) {
                case "Food":
                    requestType = RequestType.FOOD;
                    priorityLevel = PriorityLevel.LOW;
                    break;
                case "Security":
                    requestType = RequestType.SEC;
                    priorityLevel = PriorityLevel.HIGH;
                    break;
                case "Transportation":
                    requestType = RequestType.TRANS;
                    priorityLevel = PriorityLevel.MEDIUM;
                    break;
                case "INTR":
                    requestType = RequestType.INTR;
                    priorityLevel = PriorityLevel.MEDIUM;
                    break;
            }
        }
        String Note = noteField.getText();

        request = new Request(Integer.toString(reqNum + 1), mapNode, requestType, priorityLevel, Note);

        //Ensure the request is confirmed to submit or canceled
        if(confirm.isPressed()){
            requestTable.getInstance().submitRequest(request);
        }
        else if(cancel.isPressed()){
            request = null;
        }
    }

    //find node from the floor input
    private MapNode findNode(){
        MapNode mNode = null;
        floorName = floor.getItems().toString();
        nodeName = longName.getItems().toString();

        //get the map node from the floor input
        Map<String, MapNode> floorMap = HospitalMap.getInstance().getFloorNodes(floorName);
        ArrayList<MapNode> floorNode = new ArrayList<>(floorMap.values());
        for(int i = 0; i < floorNode.size(); i++){
            if(floorNode.get(i).getLongDescription() == nodeName)
            {
                mNode = floorNode.get(i);
                break;
            }
        }
        return mNode;
    }

    private void confirmRequest(){};
    private void cancelRequest(){};

    @FXML
    private void backClick(ActionEvent event){
        SceneEngine.display(MainScreenController.class, null);
    }
    @FXML
    //this handler is only connected to the first fulfill button
    //TODO create the prefabs for the fulfill sections
    private void fulfillClick(ActionEvent event){
        SceneEngine.display(FulfillReqController.class, SceneEngine.getPopOutStage(), null);
    }

}