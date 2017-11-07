package entities;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class CSVDatabaseSource implements MapDataSource {

    private HashMap<String, MapNode> nodeMap = new HashMap<>();
    private HashMap<String, MapEdge> edgeMap = new HashMap<>();

    public CSVDatabaseSource(String nodeFilename, String edgeFilename) {
        ArrayList<List<String>> nodeData = parseCSVFile(nodeFilename);
        for (List<String> row : nodeData.subList(1, nodeData.size())) {
            // Iterate through each row and make a node object
            // for each one and put it into the hashmap
            MapNode n = new MapNode(row.get(0),
                            new Location(Integer.parseInt(row.get(1)), Integer.parseInt(row.get(2)),
                            row.get(3), row.get(4)), NodeType.valueOf(row.get(5)), row.get(6), row.get(7), row.get(8), null);
            nodeMap.put(row.get(0), n);
        }

        ArrayList<List<String>> edgeData = parseCSVFile(edgeFilename);
        for (List<String> row : edgeData.subList(1, edgeData.size())) {
            // Iterate through each row and make an edge object
            // for each one and put it into the hashmap
            // Look up corresponding edges in the node hashmap
            MapEdge e = new MapEdge(row.get(0), nodeMap.get(row.get(1)), nodeMap.get(row.get(2)), 0);
            edgeMap.put(row.get(0), e);
        }
    }

    /**
     * Returns an array of CSV data lines
     * @param filename
     * @return
     */
    private ArrayList<List<String>> parseCSVFile(String filename) {
        BufferedReader nodeReader;
        try {
            // FileReader reads text files in the default encoding.
            FileReader fileReader = new FileReader(filename);

            // Always wrap FileReader in BufferedReader.
            nodeReader =  new BufferedReader(fileReader);

        }
        catch(FileNotFoundException ex) {
            System.out.println(
                    "Unable to open file '" +
                            filename + "'");
            return null;
        }

        ArrayList<List<String>> data = new ArrayList<>();

        try {
            while(nodeReader.ready()) {
                // Add a new parsed CSV line to the list
                String[] sep = nodeReader.readLine().split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)");
                // separated CSV string may still have trailing and starting quotes, remove these
                // TODO: Be able to parse multi-layer quotes, only remove the outer layer.
                for(int i = 0; i < sep.length; i++) {
                    sep[i] = sep[i].replace("\"", "");
                }
                data.add(Arrays.asList(sep));
            }
            nodeReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }

    @Override
    public MapNode getNode(String id) {
        return nodeMap.get(id);
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
        nodeMap.put(node.getId(), node);
        // Open node file, and put the updated CSV into it
        writeNodes();
    }

    @Override
    public void removeNode(String id) {
        nodeMap.remove(id);
        // Open the node file, and put the updated CSV into it
        writeNodes();
    }

    @Override
    public void addEdge(MapEdge edge) {
        edgeMap.put(edge.getId(), edge);
        // Open the edge file, and put the updated CSV into it
        writeEdges();
    }

    private void writeNodes() {
        String header = "nodeID,xcoord,ycoord,floor,building,nodeType,longName,shortName,teamAssigned";
        for(MapNode n : nodeMap.values()) {
            System.out.println(n.toCSV());
        }
    }

    private void writeEdges() {
        String header = "edgeID,startNode,endNode";
        for(MapEdge e : edgeMap.values()) {
            System.out.println(e.toCSV());
        }
    }
}
