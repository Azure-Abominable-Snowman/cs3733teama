package entities;

import controllers.SceneEngine;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.util.ArrayList;


import static entities.AccessType.ADMIN;
import static entities.AccessType.STAFF;



import java.io.IOException;

public class Main extends Application{

    public static void main(String[] args) {
        // Create a JavaDB data source and populate with the values from the CSV files (CSV data source)
        String dir = System.getProperty("user.dir");
        MapDataSource dbSource = new JavaDatabaseSource("jdbc:derby://localhost:1527/testdb;create=true", "TEST_NODES", "TEST_EDGES");
        MapDataSource csvDataSource =  new CSVDatabaseSource(dir + "\\csvdata\\MapAnodes.csv",
                                                             dir + "\\csvdata\\MapAedges.csv");
        // add all nodes to the database
        ArrayList<String> nodeIds = csvDataSource.getNodeIds();
        for(String id : nodeIds) {
            dbSource.addNode(csvDataSource.getNode(id));
        }
        // add all edges to the database
        for(String id : csvDataSource.getEdgeIds()) {
            dbSource.addEdge(csvDataSource.getEdge(id));
        }


        // At this point the database is completely loaded with edges and nodes

        // Connect to the staff info table


        /*for(String id : dbSource.getNodeIds()) {
            for(MapEdge e : dbSource.getNode(id).getEdges()) {
                System.out.print(e.toCSV() + " ");
                System.out.print(e.getWeight() + " ");
            }
            System.out.println("");
            for(MapEdge e : csvDataSource.getNode(id).getEdges()) {
                System.out.print(e.toCSV() + " ");
            }
            System.out.println("");
            System.out.println("");
        }*/



        dbSource.close();
        csvDataSource.close();
        launch(args);
    }
    @Override
    public void start(Stage primaryStage) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("../MainScreen.fxml"));
            Parent root = loader.load();
            primaryStage.setTitle("Brigham Women's Hospital Kiosk");
            primaryStage.setScene(new Scene(root));
            primaryStage.show();
            SceneEngine.setStages(primaryStage);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
