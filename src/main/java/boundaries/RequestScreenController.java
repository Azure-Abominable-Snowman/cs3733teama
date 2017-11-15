package boundaries;

import controllers.SceneEngine;
import entities.HospitalMap;
import entities.MapNode;
import entities.NodeType;
import entities.db.RequestTable;
import entities.servicerequests.PriorityLevel;
import entities.servicerequests.Request;
import entities.servicerequests.RequestType;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;

import java.util.ArrayList;
import java.util.Map;

public class RequestScreenController implements Controller {
    RequestTable requestTable = RequestTable.getInstance();

    @Override
    public String getFXMLFileName() {
        return "RequestScreen.fxml";
    }

    @FXML
    private ComboBox building;

    @FXML
    private ComboBox<String> floor;
    private String floorName;

    @FXML
    private ComboBox<MapNode> longName;
    private String nodeName;

    @FXML
    private ComboBox<Enum<RequestType>> reqType;

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



    public void initialize(){
        //fills building options drop down menu
        building.getItems().clear();
        building.getItems().addAll("BTM");

        //fills floor options drop down menu
        floor.getItems().clear();
        floor.getItems().addAll("L2", "G", "1", "2", "3");

        //fills Request options drop down menu
        reqType.getItems().clear();
        reqType.getItems().addAll(
                RequestType.FOOD, RequestType.SEC,RequestType.INTR, RequestType.TRANS, RequestType.MAIN);


    }

    @FXML
    public void setNodeData(){
        floorName = floor.getSelectionModel().getSelectedItem();
        longName.getItems().clear();

        System.out.println(floorName);
        Map<String, MapNode> nodes = HospitalMap.getInstance().getFloorNodes(floorName);
        System.out.println(nodes.keySet());
        for(MapNode n : nodes.values()) {
            if(!n.getNodeType().equals(NodeType.HALL)) {
                longName.getItems().add(n);
            }
        }



    }

    public void submitRequest() {

        Request request;
        MapNode mapNode;
        mapNode = findNode();
        Enum<RequestType> requestType = null;
        Enum<PriorityLevel> priorityLevel = null;

        //set Request Type
        if (reqType.getSelectionModel().isEmpty()) {
            return;
        } else {
            RequestType rType = (RequestType)reqType.getSelectionModel().getSelectedItem();
            System.out.println(rType.name());
            switch (rType.name()) {
                case "FOOD":
                    requestType = RequestType.FOOD;
                    priorityLevel = PriorityLevel.LOW;
                    break;
                case "SEC":
                    requestType = RequestType.SEC;
                    priorityLevel = PriorityLevel.HIGH;
                    break;
                case "TRANS":
                    requestType = RequestType.TRANS;
                    priorityLevel = PriorityLevel.MEDIUM;
                    break;
                case "INTR":
                    requestType = RequestType.INTR;
                    priorityLevel = PriorityLevel.MEDIUM;
                    break;
                case "MAIN":
                    requestType = RequestType.MAIN;
                    priorityLevel = PriorityLevel.HIGH;
                    break;
            }
        }
        String Note = noteField.getText();

        request = new Request(Integer.toString(requestTable.getInstance().getReqTable().getNextId()), mapNode.getCoordinate(), requestType, priorityLevel, Note);

        requestTable.getInstance().submitRequest(request);

        //Ensure the request is confirmed to submit or canceled
        /*
        if(confirm.isPressed()){
            requestTable.getInstance().submitRequest(request);
        }
        else if(cancel.isPressed()){
            request = null;
        }
        */
    }

    //find node from the floor input
    private MapNode findNode(){
        MapNode mNode = null;
        floorName = floor.getSelectionModel().getSelectedItem();
        nodeName = longName.getSelectionModel().getSelectedItem().toString();

        //get the map node from the floor input
        Map<String, MapNode> floorMap = HospitalMap.getInstance().getFloorNodes(floorName);
        ArrayList<MapNode> floorNode = new ArrayList<>(floorMap.values());
        for(int i = 0; i < floorNode.size(); i++){
            if(floorNode.get(i).getShortDescription().equals(nodeName))
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