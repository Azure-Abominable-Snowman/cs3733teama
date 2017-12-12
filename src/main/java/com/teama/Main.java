package com.teama;

<<<<<<< HEAD
import com.teama.translator.Translator;
=======
import com.teama.mapdrawingsubsystem.MapDrawingSubsystem;
import com.teama.mapsubsystem.MapSubsystem;
import com.teama.mapsubsystem.data.MapNode;
import com.teama.mapsubsystem.pathfinding.Path;
import com.teama.messages.Message;
>>>>>>> 487e4bd6a821ea92c823b99c2418373539de0780
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ResourceBundle;

public class Main extends Application{

    public static void main(String[] args) {
        launch(args);
    }
//    private MapDrawingSubsystem mapDrawing = MapDrawingSubsystem.getInstance();
//    private MapSubsystem mapSubsystem = MapSubsystem.getInstance();
//    private MapNode tempMapNodeStart = MapSubsystem.getInstance().getNode("AHALL00901");
//    private MapNode tempMapNodeEnd = MapSubsystem.getInstance().getNode("AHALL00401");
//    private Path tempPath = mapSubsystem.getPath(tempMapNodeStart,tempMapNodeEnd);

    @Override
    public void start(Stage primaryStage) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/MainScreen.fxml"));
            loader.setResources(Translator.getInstance().getNewBundle());
            Parent root = loader.load();
            Scene scene = new Scene(root);
            primaryStage.setTitle("Brigham and Women's Hospital Kiosk");
            primaryStage.setScene(scene);
            primaryStage.show();
//            ProgramSettings.getInstance().setPathOriginNodeProp(tempMapNodeStart);
//            ProgramSettings.getInstance().setPathEndNodeProp(tempMapNodeEnd);
//            ProgramSettings.getInstance().setCurrentDisplayedPathProp(tempPath);
//            mapSubsystem.getPathGenerator().generatePath(tempMapNodeStart,tempMapNodeEnd);
            //System.out.println(ProgramSettings.getInstance().getCurrentDisplayedPathProp());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
