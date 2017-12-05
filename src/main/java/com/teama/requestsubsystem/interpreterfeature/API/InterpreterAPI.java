package com.teama.requestsubsystem.interpreterfeature.API;

import com.jfoenix.controls.JFXTabPane;
import com.teama.requestsubsystem.interpreterfeature.ServiceException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class InterpreterAPI extends Application{
    public void run(int xcoord, int ycoord, int windowWidth, int windowLength, String cssPath,
             String destNodeID, String originNodeID) throws ServiceException {
        // First, make the window and put it on the screen at the specified x and y coordinates
        FXMLLoader loader = new FXMLLoader();
        MainController mainController = new MainController(cssPath, destNodeID);
        loader.setController(mainController);
        loader.setLocation(getClass().getResource("/InterpreterAPI.fxml"));
        JFXTabPane pane;
        try {
            pane = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
            throw new ServiceException();
        }
        Stage stage = new Stage();
        stage.setTitle("Team A Interpreter Request");
        // Make sure the CSS stylesheet is set
        pane.getStylesheets().add(cssPath);
        Scene scene = new Scene(pane);
        stage.setScene(scene);
        stage.show();
        stage.setX(xcoord);
        stage.setY(ycoord);
        stage.setWidth(windowWidth);
        stage.setHeight(windowLength);

        stage.setMinWidth(850);
        stage.setMinHeight(670);

    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        InterpreterAPI api = new InterpreterAPI();
        try {
            api.run(100, 150, 500, 500,
                    "/css/DefaultStyle.css", "AINFO0010G", "");
        } catch (ServiceException e) {
            System.out.println("Caught service exception");
            e.printStackTrace();
        }

    }
}
