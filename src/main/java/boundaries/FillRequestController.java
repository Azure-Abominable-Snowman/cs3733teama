package boundaries;

import entities.staff.*;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;

import java.util.ArrayList;

public class FillRequestController implements Controller {
    @FXML
    Button fill;
    @FXML
    TextArea reqInfo;
    @FXML
    ListView serviceList;

    public void initialize() {
        
    }

    @FXML
    private void onSelect(MouseEvent e){
        for(ServiceStaff s: generateStaff()){
            serviceList.getItems().addAll(s);
        }
        System.out.println(serviceList.getSelectionModel().getSelectedItem());
    }
    @Override
    public void setControllerInfo(ControllerInfo c){

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
