package com.teama.controllers;

import com.teama.mapsubsystem.data.MapNodeData;
import com.teama.requestsubsystem.Request;
import com.teama.requestsubsystem.RequestSubsystem;
import com.teama.requestsubsystem.RequestType;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;

public class RequestScreenController implements Controller {
    RequestSubsystem requestTable = RequestSubsystem.getInstance();

    @Override
    public String getFXMLFileName() {
        return "RequestScreen.fxml";
    }

    @FXML
    private ComboBox building;
    @FXML
    private ListView<Request> reqList;
    @FXML
    private ComboBox<String> floor;
    private String floorName;

    @FXML
    private ComboBox<MapNodeData> longName;
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




/*
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

        //fills
        reqList.getItems().clear();
        ArrayList<Request> requests = requestTable.getReqTable().getRequest();
        reqList.getItems().clear();
        for(Request r: requests){
            if(!r.isFulfilled()){
                reqList.getItems().add(r);
            }
        }

    }

    @FXML
    private void onSelect(MouseEvent e){
        System.out.println(reqList.getSelectionModel().getSelectedItem());
        Request r=reqList.getSelectionModel().getSelectedItem();
       SceneEngine.display(FillRequestController.class, SceneEngine.getFillReqStage(), new ControllerInfo(r.getId()));
    }
    private int getIdFromString(String reqEntry){
        int index=0;
        while(reqEntry.charAt(index++)!='\n'){;}
        String strId="";
        while(reqEntry.charAt(index++)!='\n'){
            strId.concat(String.valueOf(reqEntry.charAt(index)));
        }
        return Integer.parseInt(strId);
    }
    private ArrayList<Request> getDummyRequest(){
        ArrayList<Request> requests = new ArrayList<>();
        for(int i=0; i< 10; i++){
            Request r = new Request(Integer.toString(i), new Location(0,0,"BTM", "G"), RequestType.FOOD, PriorityLevel.HIGH, "test", false);
            requests.add(r);
        }
        return requests;
    }

    private void getRequestTable(){
        requestTable.getInstance().getReqTable();
    }

    @FXML
    public void setNodeData(){
        floorName = floor.getSelectionModel().getSelectedItem();
        longName.getItems().clear();

        System.out.println(floorName);
        Map<String, MapNodeData> nodes = MapSubsystem.getInstance().getFloorNodes(floorName);
        System.out.println(nodes.keySet());
        for(MapNodeData n : nodes.values()) {
            if(!n.getNodeType().equals(NodeType.HALL)) {
                longName.getItems().add(n);
            }
        }



    }

    public void submitRequest() {

        Request request;
        MapNodeData mapNodeData;
        mapNodeData = findNode();
        Enum<RequestType> requestType = null;
        Enum<PriorityLevel> priorityLevel = null;

        //set Request Type
        if (reqType.getSelectionModel().isEmpty()) {
            return;
        } else {
            RequestType rType = (RequestType)reqType.getSelectionModel().getSelectedItem();
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

        request = new Request(Integer.toString(requestTable.getInstance().getReqTable().getNextId()), mapNodeData.getCoordinate(), requestType, priorityLevel, Note);

        requestTable.getInstance().submitRequest(request);

        reqList.getItems().add(request);
       /* ArrayList<Request> requests = requestTable.getReqTable().getRequest();
        for(Request r: requests){
            reqList.getItems().add(r.getReqType()+"\n"+r.getId()+"\n"+r.getLocation().toString()+"\n"+
                    r.getNote());
        }*/
        //Ensure the request is confirmed to submit or canceled

       /*if(confirm.isPressed()){
            requestTable.getInstance().submitRequest(request);
        }
        else if(cancel.isPressed()){
            request = null;
        }*/
/*
    }

    //find node from the floor input
    private MapNodeData findNode(){
        MapNodeData mNode = null;
        floorName = floor.getSelectionModel().getSelectedItem();
        nodeName = longName.getSelectionModel().getSelectedItem().toString();

        //get the map node from the floor input
        Map<String, MapNodeData> floorMap = MapSubsystem.getInstance().getFloorNodes(floorName);
        ArrayList<MapNodeData> floorNode = new ArrayList<>(floorMap.values());
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

*/
}