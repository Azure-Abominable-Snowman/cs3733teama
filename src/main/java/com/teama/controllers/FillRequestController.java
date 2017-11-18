package com.teama.controllers;

import com.teama.messages.Provider;
import com.teama.messages.SMSMessage;
import com.teama.requestsubsystem.*;
import com.teama.requestsubsystem.StaffInfo;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;

import javax.mail.internet.AddressException;
import java.util.ArrayList;

public class FillRequestController implements Controller {
    private Request request;
    private ArrayList<ServiceStaff> staff;
    private String idRequest="";
    private ServiceStaff staffToFulfill;
    @FXML
    Button fill;
    @FXML
    Button close;
    @FXML
    TextArea reqInfo;
    @FXML
    ListView<ServiceStaff> serviceList;
    @FXML
    Label loc, reqType, priorityLevel, note, id;
    public void initialize() {
        
    }

    @FXML
    private void onSelect(MouseEvent e) {
        System.out.println(serviceList.getSelectionModel().getSelectedItem());
        staffToFulfill = serviceList.getSelectionModel().getSelectedItem();
    }
    @FXML
    private void onBack(){
        SceneEngine.closeFillReq();
    }

    @FXML
    private void fulfill(ActionEvent e) throws AddressException {
        System.out.println(staffToFulfill);
        SMSMessage message = new SMSMessage(staffToFulfill.getPhoneNumber(), (Provider)staffToFulfill.getProvider(),
                request.getNote());
        message.sendSMSMessage();



    }
    @Override
    public void setControllerInfo(ControllerInfo c){
        idRequest=c.getRequest();
        request = RequestTable.getInstance().getReqTable().getRequest(idRequest);
        staff = StaffInfo.getInstance().getStaffInfoDB().getIntrStaff();
        for(ServiceStaff s: staff){
            serviceList.getItems().addAll(s);
        }
        loc.setText("Location: "+request.getLocation().toString());
        reqType.setText("Type: "+request.getReqType());
        priorityLevel.setText("Priority: "+request.getPriority());
        note.setText("Note: "+request.getNote());
        id.setText("ID: "+request.getId());


    }



    private ArrayList<ServiceStaff> generateStaff(){
        ArrayList<ServiceStaff> staff = new ArrayList<>();
        ServiceStaff staff1 = new SecurityStaff("1234", "Jon","Smith", "6034893939", StaffType.SECURITY, null, Provider.VERIZON, true);
        ServiceStaff staff2 = new InterpreterStaff("5678", "Johnny","BeGood", "123456789", StaffType.JANITOR, null, Provider.ATT, true);
        staff.add(staff1);
        staff.add(staff2);
        return staff;
    }

    @Override
    public String getFXMLFileName() {
        return "FulfillRequest.fxml";
    }
    
}
