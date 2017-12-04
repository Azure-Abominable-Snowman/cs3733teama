package com.teama.mapsubsystem;

import com.teama.mapsubsystem.data.Floor;
import com.teama.mapsubsystem.data.MapDataSource;
import com.teama.mapsubsystem.data.MapEdge;
import com.teama.mapsubsystem.data.MapNode;

import java.util.ArrayList;
import java.util.HashMap;



public class TotalMapCache extends MapCache {

    ArrayList<String> nodeIds;
    HashMap<String,MapNode> nodeCash ;
    HashMap<String,MapNode> describeToNode;
    HashMap<String,MapNode> longDescribeToNode;

    ArrayList<String> edgeIds;
    HashMap<String,MapEdge> edgeCash ;

    HashMap<String,ArrayList<MapNode>> floorNode;
    HashMap<String,ArrayList<MapEdge>> floorEdge;

    public TotalMapCache(MapDataSource dataSource) {
        super(dataSource);
        // pointer stored into MapDataSource dataSource.

        nodeIds = new ArrayList<>();
        nodeCash = new HashMap<>();
        describeToNode = new HashMap<>();
        longDescribeToNode = new HashMap<>();
        edgeIds = new ArrayList<>();
        edgeCash = new HashMap<>();

        floorNode = new HashMap<>();
        floorEdge = new HashMap<>();
        initialCash ();
    }



    private void initialCash ()
    {
        // node related.
        nodeIds = dataSource.getNodeIds();

        for (String id : nodeIds) {
            nodeCash.put(id,dataSource.getNode(id));
        }
        for (MapNode mapNode : nodeCash.values()) {
            describeToNode.put(mapNode.getShortDescription(),mapNode);
        }
        for (MapNode mapNode : nodeCash.values()) {
            longDescribeToNode.put(mapNode.getLongDescription(),mapNode);
        }
        for (Floor floor : Floor.values()) {
            ArrayList<MapNode> list = new ArrayList<>();
            for (MapNode mapNode : nodeCash.values())
                if(mapNode.getCoordinate().getLevel().equals(floor)) list.add(mapNode);
            floorNode.put(floor.toString(),list);
        } // get the nodes from nodeCash to the pointers whould be the same.

        // edge related.
        edgeIds=dataSource.getEdgeIds();

        for (String edgeId : edgeIds) {
            edgeCash.put(edgeId,dataSource.getEdge(edgeId));
        }

        for (Floor floor : Floor.values()) {
            floorEdge.put(floor.toString(),dataSource.getEdgesOnFloor(floor.toString()));
        }

    }


    @Override
    public MapNode getNode(String id) {
        MapNode result = nodeCash.get(id);
        if( result == null ) {
            result = dataSource.getNode(id);
            addmissedNode(result);
        }
        return nodeCash.get(id);
    }

    @Override
    public MapNode getNode(String description, boolean longDescription) {
        HashMap<String, MapNode> localDescribToNode;
        if (longDescription) localDescribToNode = longDescribeToNode; // todo check the meaning for the boolean pram.
        else localDescribToNode = describeToNode;
        MapNode temp = localDescribToNode.get(description);
        if (temp == null) {
            temp = dataSource.getNode(description, longDescription);
            addmissedNode(temp);
        }
        return temp;
    }

    @Override
    public ArrayList<String> getNodeIds() {
        return nodeIds;
    }

    @Override
    public ArrayList<MapNode> getNodesOnFloor(String floor) {
        return floorNode.get(floor); //TODO no check for missed?
    }

    @Override
    public ArrayList<String> getEdgeIds() {
        return edgeIds;
    }

    @Override
    public MapEdge getEdge(String id) {
        MapEdge temp = edgeCash.get(id);
        if(temp == null) {
            temp= dataSource.getEdge(id);
            addmissedEdge(temp);
        }
        return temp;
    }

    @Override
    public ArrayList<MapEdge> getEdgesOnFloor(String floor) {
        return floorEdge.get(floor); //TODO no check for missed ? also now to renew the list?
        // TODO slow at add and remove solution now.
    }



    @Override
    public void addNode(MapNode node) {
        dataSource.addNode(node); // first add into dataSource.
        addmissedNode(node);
    }

    @Override
    public void removeNode(String id) {
        dataSource.removeNode(id);
        MapNode oldNode = nodeCash.get(id);
        ArrayList<MapNode> nodes =  floorNode.get(oldNode.getCoordinate().getLevel().toString());
        for(int i=0;i<nodes.size();++i)
        {
            if(nodes.get(i).getId().equals(oldNode.getId())){
                nodes.remove(i); break;
            }
        }
        nodeIds.remove(id);
        describeToNode.remove(oldNode.getShortDescription());
        longDescribeToNode.remove((oldNode.getLongDescription()));
        nodeCash.remove(id);

    }
    // why intellij always say no change deleted?

    @Override
    public void addEdge(MapEdge edge) {
        dataSource.addEdge(edge);
        addmissedEdge(edge);

    }

    @Override
    public void removeEdge(String id) {
        dataSource.removeEdge(id);
        edgeIds.remove(id);
       // MapEdge oldEdge = edgeCash.get(id);
        // TODO also how to remove it from the floor cache? super stupid way now.
        updateFloorEdges();
    }


    @Override
    public void close() {
        dataSource.close();
    }


    /**
     * this will reset the dataSource, then rerun the initialCash
     * @param newSource the new source that the cache will rely on.
     */
    public void reset(MapDataSource newSource)
    {
        dataSource  = newSource;
        initialCash();
    }

    /**
     * In the case of dataSource contains it but cache doesn't.
     * @param node the node that is missed.
     */
    private void addmissedNode(MapNode node)
    {
        if(node == null) return;
        MapNode oldNode = nodeCash.get(node.getId());
        if(oldNode != null ) {
            ArrayList<MapNode> nodes = floorNode.get(oldNode.getCoordinate().getLevel().toString());
            for (int i = 0; i < nodes.size(); ++i) {
                if (nodes.get(i).getId().equals(oldNode.getId())) {
                    nodes.remove(i);
                    break;
                }
            }
        }

        if (! nodeIds.contains(node.getId()))
            nodeIds.add(node.getId());  // add if not already in it.
        describeToNode.remove(node.getId()); // first remove the old one, then put in the new
        describeToNode.put(node.getShortDescription(), node);
        longDescribeToNode.remove(node.getId());
        longDescribeToNode.put(node.getLongDescription(),node );

        nodeCash.put(node.getId(), node); // over write or put new one.
        floorNode.get(node.getCoordinate().getLevel().toString()).add(nodeCash.get(node.getId()));
        // add from cache to ensure same pointer.

    }

    /**
     * In the case of dataSource contains it but cache doesn't.
     * @param edge the node that is missed.
     */
    private void addmissedEdge(MapEdge edge)
    {
        if(edge== null) return;
        edgeCash.put(edge.getId(),edge);
        if( ! edgeIds.contains(edge.getId())) edgeIds.add(edge.getId());
        // TODO put it into the floor edges. now is just doing it the stupied way.
        updateFloorEdges();
    }

    private void updateFloorEdges ()
    {
        floorEdge = new HashMap<>();
        for (Floor floor : Floor.values()) {
            floorEdge.put(floor.toString(),dataSource.getEdgesOnFloor(floor.toString()));
        }
    }

}
