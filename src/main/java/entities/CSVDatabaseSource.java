package entities;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class CSVDatabaseSource implements MapDataSource {

    private HashMap<String, MapNode> nodeMap = new HashMap<>();
    private HashMap<String, MapEdge> edgeMap = new HashMap<>();

    public CSVDatabaseSource(String nodeFilename, String edgeFilename) {
        BufferedReader nodeReader = openFile(nodeFilename);
        try {
            parseCSV(nodeReader.readLine());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private List<String> parseCSV(String csvLine) {
        return Arrays.asList(csvLine.split(",", -1));
    }

    private BufferedReader openFile(String filename) {
        try {
            // FileReader reads text files in the default encoding.
            FileReader fileReader = new FileReader(filename);

            // Always wrap FileReader in BufferedReader.
            return new BufferedReader(fileReader);

        }
        catch(FileNotFoundException ex) {
            System.out.println(
                    "Unable to open file '" +
                            filename + "'");
        }

        return null;
    }

    @Override
    public MapNode getNode(String id) {
        return null;
    }

    @Override
    public MapEdge getAdjacentEdges(String id) {
        return null;
    }

    @Override
    public MapEdge getAdjacentNodes(String id) {
        return null;
    }

    @Override
    public void addNode(MapNode node) {

    }

    @Override
    public void removeNode(String id) {

    }

    @Override
    public void addEdge(MapEdge edge) {

    }
}
