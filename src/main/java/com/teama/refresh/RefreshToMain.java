//package com.teama.refresh;
//
//
//import java.awt.event.InputEvent;
//import java.awt.event.MouseListener;
//import java.util.List;
//import java.util.ArrayList;
//
//import com.teama.controllers.MainScreenController;
//import javafx.fxml.FXMLLoader;
//import javafx.stage.Stage;
//import org.w3c.dom.events.MouseEvent;
//
//
//public class RefreshToMain implements MouseListener {
//    private List<MementoDesign.Memento> savedStates = new ArrayList<>();
//    public void addMemento(MementoDesign.Memento m){
//        savedStates.add(m);
//    }
//    private Stage primaryStage;
//    private Stage currStage;
//
//    public Stage setPrimary(){
//        FXMLLoader loader = new FXMLLoader();
//        loader.setLocation(getClass().getResource("/MainScreen.fxml"));
//        return null;
//    }
//
//    public MementoDesign.Memento Refresh() {
//        this.primaryStage = setPrimary();
//
//        MementoDesign mementoDesign = new MementoDesign();
//        mementoDesign.setState(primaryStage);
//        savedStates.add(mementoDesign.saveState());
//        mementoDesign.setState(currStage);
//        savedStates.add(mementoDesign.saveState());
//
//
//        while (MouseEvent == null) {
//            try {
//                interaction.wait(1000);
//            } catch (InterruptedException ie) {
//                ie.printStackTrace();
//            }
//        }
//        return null;
//       // return MementoDesign.restore();
//    }
//}
//
