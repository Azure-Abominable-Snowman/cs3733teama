package entities.db;

import entities.*;

import java.io.*;
import java.util.*;

public class CSVDatabaseSource implements MapDataSource {

    // Hashmaps that store all of the node and edge id keys and object values in memory
    private HashMap<String, MapNode> nodeMap = new HashMap<>();
    private HashMap<String, MapEdge> edgeMap = new HashMap<>();
    private String nodeFilename, edgeFilename;

    public CSVDatabaseSource(String nodeFilename, String edgeFilename) {
        this.nodeFilename = nodeFilename;
        this.edgeFilename = edgeFilename;
        ArrayList<List<String>> nodeData = parseCSVFile(nodeFilename);
        for (List<String> row : nodeData.subList(1, nodeData.size())) {
            // Iterate through each row and make a node object
            // for each one and put it into the hashmap
            MapNode n = nodeListToObj(row);
            nodeMap.put(row.get(0), n);
        }

        ArrayList<List<String>> edgeData = parseCSVFile(edgeFilename);
        for (List<String> row : edgeData.subList(1, edgeData.size())) {
            // Iterate through each row and make an edge object
            // for each one and put it into the hashmap
            // Look up corresponding edges in the node hashmap
            MapEdge e = edgeListToObj(row);
            // Add this edge to the node objects that are associated with it
            nodeMap.get(e.getStart().getId()).addEdge(e);
            nodeMap.get(e.getEnd().getId()).addEdge(e);
            edgeMap.put(row.get(0), e);
        }
    }

    /**
     * Returns an array of CSV data lines parsed from a given filename
     * @param filename
     * @return
     */
    private ArrayList<List<String>> parseCSVFile(String filename) {
        BufferedReader nodeReader;
        try {
            FileReader fileReader = new FileReader(filename);
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
                data.add(splitCSVLine(nodeReader.readLine()));
            }
            nodeReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }

    /**
     * Splits a given CSV line into an array of its components
     * Doesn't detect types, assumes all CSV values are strings.
     * @param line
     * @return
     */
    private List<String> splitCSVLine(String line) {
        // Add a new parsed CSV line to the list
        String[] sep = line.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)");
        // separated CSV string may still have trailing and starting quotes, remove these
        // TODO: Be able to parse multi-layer quotes, only remove the outer layer.
        for(int i = 0; i < sep.length; i++) {
            sep[i] = sep[i].replace("\"", "");
            // Check to see if it is bordered by single quotes, if it is remove them.
            if(sep[i].charAt(0) == '\'' && sep[i].charAt(sep[i].length()-1) == '\'') {
                sep[i] = sep[i].substring(1, sep[i].length()-1);
            }
        }
        return Arrays.asList(sep);
    }

    /**
     * Converts a node CSV row into an object
     * Edges are null because they are not specified in node CSV lines
     * @param row
     * @return
     */
    private MapNode nodeListToObj(List<String> row) {
        return new MapNode(row.get(0),
                new Location(Integer.parseInt(row.get(1)), Integer.parseInt(row.get(2)),
                        row.get(3), row.get(4)), NodeType.valueOf(row.get(5)), row.get(6), row.get(7), row.get(8));
    }

    /**
     * Converts an edge CSV row into an object
     * @param row
     * @return
     */
    private MapEdge edgeListToObj(List<String> row) {
        return new MapEdge(row.get(0), row.get(1), row.get(2));
    }

    @Override
    public MapNode getNode(String id) {
        return nodeMap.get(id);
    }

    @Override
    public void addNode(MapNode node) {
        nodeMap.put(node.getId(), node);
        // Open node file, and put the updated CSV into it
        try {
            writeNode(node, false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void removeNode(String id) {
        // Open the node file, and put the updated CSV into it
        try {
            writeNode(nodeMap.get(id), true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        nodeMap.remove(id);
    }

    @Override
    public void addEdge(MapEdge edge) {
        edgeMap.put(edge.getId(), edge);
        // Open the edge file, and put the updated CSV into it
        writeEdge(edge, false);
    }

    @Override
    public ArrayList<String> getNodeIds() {
        return new ArrayList<String>(nodeMap.keySet());
    }

    @Override
    public ArrayList<String> getEdgeIds() {
        return new ArrayList<String>(edgeMap.keySet());
    }

    private void writeNode(MapNode node, boolean delete) throws IOException {
        // Create whole new CSV file with every edit
        try {
            File file = new File(nodeFilename);
            if(!file.exists()) {
                if (!(file.createNewFile())) {
                    // If the file doesn't exist something must have gone wrong
                    throw new IOException();
                }
            }
            FileWriter fw = new FileWriter(nodeFilename);
            BufferedWriter writer = new BufferedWriter(fw);
            writer.write("nodeID,xcoord,ycoord,floor,building,nodeType,longName,shortName,teamAssigned\n");
            for(MapNode n : nodeMap.values()) {
                writer.write(n.toCSV()+"\n");
            }
            writer.close();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    private void writeEdge(MapEdge edge, boolean delete) {
        // Create whole new CSV file with every edit
        try {
            File file = new File(edgeFilename);

            if(!file.exists()) {
                if (!(file.createNewFile())) {
                    // If the file doesn't exist something must have gone wrong
                    throw new IOException();
                }
            }
            FileWriter fw = new FileWriter(edgeFilename);
            BufferedWriter writer = new BufferedWriter(fw);
            writer.write("edgeID,startNode,endNode\n");
            for(MapEdge n : edgeMap.values()) {
                writer.write(n.toCSV()+"\n");
            }
            writer.close();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public MapEdge getEdge(String id) {
        return edgeMap.get(id);
    }
}
