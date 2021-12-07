package uk.ac.ed.inf;
import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.graph.DefaultUndirectedWeightedGraph;
import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class HexGraph {
    private static final double heightOfTriangle = Math.sqrt(Math.pow(LongLat.oneMove,2) - Math.pow(LongLat.oneMove/2,2));
    private static boolean shiftedRow(){
        double appletonLat = LongLat.appleton.latitude;
        int rowsCounter =0;
        while(appletonLat<LongLat.confAreaTop){
            appletonLat = appletonLat + heightOfTriangle;
            rowsCounter++;
        }
        return rowsCounter%2==1;

    }
    private static ArrayList<ArrayList<LongLat>> genNodesOnHexGraph(){
        ArrayList<ArrayList<LongLat>> nodeLocations = new ArrayList<>();
        boolean shifted = shiftedRow();
        double  startLocLong = LongLat.appleton.longitude;
        double startLocLat = LongLat.appleton.latitude;
        //move the point to just out of the flyzone. we use the point from appletons coordinates so that appletons coordinates will always be a node
        while(startLocLong>LongLat.confAreaLeft){
            startLocLong = startLocLong - LongLat.oneMove;
        }
        while(startLocLat<LongLat.confAreaTop){
            startLocLat = startLocLat + heightOfTriangle;
        }
        for( double currentLat = startLocLat;currentLat>LongLat.confAreaBottom;currentLat = currentLat-heightOfTriangle){
            ArrayList<LongLat> currentRow = new ArrayList<>();
            for(double currentLong = startLocLong;currentLong<LongLat.confAreaRight;currentLong = currentLong+LongLat.oneMove){
                if(shifted){
                    currentRow.add(new LongLat(currentLong+LongLat.oneMove/2,currentLat));
                }
                else{
                    currentRow.add(new LongLat(currentLong,currentLat));
                }
            }
            shifted = !shifted;
            nodeLocations.add(currentRow);
        }
        return nodeLocations;
    }
    private static HashSet<NodeEdges> genPathOnHexGraoh(ArrayList<ArrayList<LongLat>> nodesOnHexGraph){
        HashSet<NodeEdges> pathOnGraph = new HashSet<>();
        boolean shifted= shiftedRow();
        int numberOfRows = nodesOnHexGraph.size();
        for(int currentRow = 0;currentRow<numberOfRows;currentRow++){
            int numberofNodesInColumn = nodesOnHexGraph.get(currentRow).size();
            for(int currentNode =0;currentNode<numberofNodesInColumn;currentNode++){
                if(currentNode>0){
                    pathOnGraph.add(new NodeEdges(nodesOnHexGraph.get(currentRow).get(currentNode),nodesOnHexGraph.get(currentRow).get(currentNode-1)));

                }
                if(currentRow>0){
                    pathOnGraph.add(new NodeEdges(nodesOnHexGraph.get(currentRow).get(currentNode),nodesOnHexGraph.get(currentRow-1).get(currentNode)));

                }
                if(shifted){
                   if(numberOfRows-1>currentRow && numberofNodesInColumn-1>currentNode){
                       pathOnGraph.add(new NodeEdges(nodesOnHexGraph.get(currentRow).get(currentNode),nodesOnHexGraph.get(currentRow+1).get(currentNode+1)));
                   }
                   if(currentRow>0 && numberofNodesInColumn-1>currentNode){
                       pathOnGraph.add(new NodeEdges(nodesOnHexGraph.get(currentRow).get(currentNode),nodesOnHexGraph.get(currentRow-1).get(currentNode+1)));
                   }

                }
            }
            shifted = !shifted;
        }
        return pathOnGraph;
    }


    private static Graph<LongLat,NodeEdges> genDroneHexGraph(){
        DefaultUndirectedWeightedGraph<LongLat,NodeEdges> g = new DefaultUndirectedWeightedGraph<LongLat,NodeEdges>(NodeEdges.class);
        ArrayList<ArrayList<LongLat>> nodes = genNodesOnHexGraph();
        HashSet<NodeEdges> edges = genPathOnHexGraoh(nodes);
        for(int i=0;i< nodes.size();i++){
            for(int j=0;j<nodes.get(i).size();j++){
                g.addVertex(nodes.get(i).get(j));
            }
        }
        for(NodeEdges edge:edges){
            g.addEdge(edge.node1,edge.node2,edge);
            g.setEdgeWeight(edge,LongLat.oneMove);
        }
        return g;
    }
    //can combine these two ^
    public static Graph<LongLat,NodeEdges> genValidHexGraph(){
        Graph<LongLat, NodeEdges> droneHexGraph = genDroneHexGraph();
        DefaultUndirectedWeightedGraph<LongLat,NodeEdges> g = new DefaultUndirectedWeightedGraph<LongLat,NodeEdges>(NodeEdges.class);
        Set<LongLat> nodes = droneHexGraph.vertexSet();
        Set<NodeEdges> edges = droneHexGraph.edgeSet();
        for(LongLat node:nodes){
            if(!node.inNoFlyPolygon() && node.isConfined()){
                g.addVertex(node);
            }
        }
        for(NodeEdges edge:edges){
            LongLat node1 = edge.node1;
            LongLat node2 = edge.node2;
            if(node1.isConfined()&& node2.isConfined()&&
                    !node1.inNoFlyPolygon()&&!node2.inNoFlyPolygon() &&
                    !node1.inNoFlyZone(node2) && !node2.inNoFlyZone(node1)){
                g.addEdge(edge.node1,edge.node2,edge);
                g.setEdgeWeight(edge,1);
            }
        }
        return g;
    }
    public static HashMap<String,LongLat> getDeliveryNodes(Graph<LongLat,NodeEdges> hexGraph){
        ArrayList<String> orderNos = Orders.orderNos;
        HashMap<String,double[]> deliveryCoordinates = Orders.deliveryCoordinates;
        HashMap<String,LongLat> deliveryNodes = new HashMap<>();
        for(String order : orderNos){
            LongLat currentOrder = new LongLat(deliveryCoordinates.get(order)[0],deliveryCoordinates.get(order)[1]);
            for (LongLat nodes : hexGraph.vertexSet()) {
                if (currentOrder.closeTo(nodes)) {
                    deliveryNodes.put(order, nodes);
                    break;
                }
            }
        }
        return deliveryNodes;
    }
    public static HashMap<String,ArrayList<LongLat>> getPickUpNodes(Graph<LongLat,NodeEdges> hexGraph){
        ArrayList<String> orderNos = Orders.orderNos;
        HashMap<String, ArrayList<double[]>> pickUpCoordinates= Orders.pickUpCoordinates;
        HashMap<String,ArrayList<LongLat>> pickUpNodes = new HashMap<>();
        for(String order:orderNos){
            if(pickUpCoordinates.get(order).size()==1){
                double[] currCoords = pickUpCoordinates.get(order).get(0);
                LongLat currentCoordinate = new LongLat(currCoords[0],currCoords[1]);
                for(LongLat nodes:hexGraph.vertexSet()){
                    if(currentCoordinate.closeTo(nodes)){
                        ArrayList<LongLat> n = new ArrayList<>();
                        n.add(currentCoordinate);
                        pickUpNodes.put(order,n);
                        break;
                    }
                }
            }
            else{
                ArrayList<LongLat> n = new ArrayList<>();
                for( double[] coord : pickUpCoordinates.get(order)){
                    LongLat currentCoordinate = new LongLat(coord[0],coord[1]);
                    innerLoop:
                    for(LongLat nodes:hexGraph.vertexSet()){
                        if(currentCoordinate.closeTo(nodes)){
                            n.add(currentCoordinate);
                            break innerLoop;
                        }
                    }
                }
                pickUpNodes.put(order,n);
            }
        }
        return pickUpNodes;
    }
}
