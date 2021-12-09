package uk.ac.ed.inf;
import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.alg.shortestpath.AStarShortestPath;
import org.jgrapht.alg.interfaces.AStarAdmissibleHeuristic;

import org.jgrapht.graph.DefaultUndirectedWeightedGraph;
import org.w3c.dom.Node;

import java.util.*;

public class HexGraph {
    public static ArrayList<String> OrdersCompleted = new ArrayList<>();
    private static final double heightOfTriangle = Math.sqrt(Math.pow(LongLat.oneMove,2) - Math.pow(LongLat.oneMove/2,2));
    public static ArrayList<Integer> HoverLocation= new ArrayList<>();
    public static HashMap<Integer,String> OrderNumberChanges = new HashMap<>();
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
            boolean found = false;
            for (LongLat nodes : hexGraph.vertexSet()) {
                if (currentOrder.closeTo(nodes)) {
                    deliveryNodes.put(order, nodes);
                    found = true;
                    break;
                }
            }
            if(!found){
                System.out.println("cant find del"+ order);
            }

        }
        return deliveryNodes;
    }



    public static HashMap<String,ArrayList<LongLat>> getPickUpNodes(Graph<LongLat,NodeEdges> hexGraph){
        ArrayList<String> orderNos = Orders.orderNos;
        HashMap<String, ArrayList<double[]>> pickUpCoordinates= Orders.pickUpCoordinates;
        HashMap<String,ArrayList<LongLat>> pickUpNodes = new HashMap<>();
        for(String order:orderNos){
           ArrayList<double[]> pickUpShops = pickUpCoordinates.get(order);
           ArrayList<LongLat> nodes = new ArrayList<>();
           for (double[] shops:pickUpShops){
               boolean found = false;
               LongLat curr = new LongLat(shops[0],shops[1]);
               for (LongLat node : hexGraph.vertexSet()) {
                   if (curr.closeTo(node)) {
                       nodes.add(node);
                       found = true;
                       break;
                   }
               }
               if(!found){
                   System.out.println("not found "+ order);
               }
           }
           pickUpNodes.put(order,nodes);
        }
        return pickUpNodes;
    }
    private static LongLat getAppleton(Graph<LongLat,NodeEdges> g){
        for (LongLat nodes : g.vertexSet()) {
            if (LongLat.appleton.closeTo(nodes)) {
                return nodes;

            }
        }
        return null;
    }
    public static List<LongLat> getRoute(){
        Graph<LongLat,NodeEdges> g = HexGraph.genValidHexGraph();
        HashMap<String,LongLat> delivery =HexGraph.getDeliveryNodes(g);
        HashMap<String,ArrayList<LongLat>> pickup = getPickUpNodes(g);
        ArrayList<String> orders = greedyApproach(Orders.items);
        //System.out.println(orders);
        LongLat appleton = getAppleton(g);

        List<LongLat> movements = new ArrayList<>();
        DijkstraShortestPath<LongLat,NodeEdges> d= new DijkstraShortestPath<LongLat,NodeEdges>(g);
//        AStarAdmissibleHeuristic<LongLat> n= new AStarAdmissibleHeuristic<LongLat>() {
//            @Override
//            public double getCostEstimate(LongLat longLat, LongLat v1) {
//                return longLat.distanceTo(v1);
//            }
//        };
//
//        AStarShortestPath<LongLat,NodeEdges> aStarShortestPath = new AStarShortestPath<LongLat,NodeEdges>(g,n);
        int movesLeft = 1500;
        HoverLocation.add(0);
        OrderNumberChanges.put(0,orders.get(0));
        if(pickup.get(orders.get(0)).size()==1){
            movesLeft--;
            assert appleton != null;
            movements = d.getPath(appleton,pickup.get(orders.get(0)).get(0)).getVertexList();
            HoverLocation.add(movements.size());
            movesLeft--;
            movements.addAll(d.getPath(pickup.get(orders.get(0)).get(0),delivery.get(orders.get(0))).getVertexList());
            HoverLocation.add(movements.size());
            movesLeft--;
            int pickUpmoves =d.getPath(appleton,pickup.get(orders.get(0)).get(0)).getVertexList().size();
            int deliverMoves = d.getPath(pickup.get(orders.get(0)).get(0),delivery.get(orders.get(0))).getVertexList().size();
            movesLeft = movesLeft- pickUpmoves-deliverMoves;
        }
        else{
            movesLeft--;
            movements = d.getPath(appleton,pickup.get(orders.get(0)).get(0)).getVertexList();

            HoverLocation.add(movements.size());
            movesLeft--;
            movements.addAll(d.getPath(pickup.get(orders.get(0)).get(0),pickup.get(orders.get(0)).get(1)).getVertexList());

            HoverLocation.add(movements.size());
            movesLeft--;
            movements.addAll(d.getPath(pickup.get(orders.get(0)).get(1),delivery.get(orders.get(0))).getVertexList());

            HoverLocation.add(movements.size());
            movesLeft--;
            int pickUpMoves = d.getPath(appleton,pickup.get(orders.get(0)).get(0)).getVertexList().size() + d.getPath(pickup.get(orders.get(0)).get(0),pickup.get(orders.get(0)).get(1)).getVertexList().size();
            int deliverMoves = d.getPath(pickup.get(orders.get(0)).get(1),delivery.get(orders.get(0))).getVertexList().size();
            movesLeft = movesLeft-pickUpMoves-deliverMoves;
        }
        OrdersCompleted.add(orders.get(0));
        int pointer =0;
        for(int i=1;i<orders.size();i++){
            if(pickup.get(orders.get(i)).size()==1){
                int pickUpMoves = d.getPath(delivery.get(orders.get(i-1)),pickup.get(orders.get(i)).get(0)).getVertexList().size();
                int deliveryMove = d.getPath(pickup.get(orders.get(i)).get(0),delivery.get(orders.get(i))).getVertexList().size();
                int returnHomeMoves = d.getPath(delivery.get(orders.get(i)),appleton).getVertexList().size();
                int hoverMoves = 2;
                if(movesLeft-hoverMoves-pickUpMoves-deliveryMove-returnHomeMoves>=0){
                    OrderNumberChanges.put(movements.size(),orders.get(i));
                    movements.addAll(d.getPath(delivery.get(orders.get(i-1)),pickup.get(orders.get(i)).get(0)).getVertexList());

                    HoverLocation.add(movements.size());
                    movements.addAll(d.getPath(pickup.get(orders.get(i)).get(0),delivery.get(orders.get(i))).getVertexList());

                    HoverLocation.add(movements.size());
                    movesLeft = movesLeft-pickUpMoves-deliveryMove-hoverMoves;
                    pointer =i;
                    OrdersCompleted.add(orders.get(i));
                }

            }
            else{
                int pickUpMoves = d.getPath(delivery.get(orders.get(i-1)),pickup.get(orders.get(i)).get(0)).getVertexList().size()+d.getPath(pickup.get(orders.get(i)).get(0),pickup.get(orders.get(i)).get(1)).getVertexList().size();
                int deliveryMove = d.getPath(pickup.get(orders.get(i)).get(1),delivery.get(orders.get(i))).getVertexList().size();
                int returnHomeMoves = d.getPath(delivery.get(orders.get(i)),appleton).getVertexList().size();
                int hoverMoves = 3;
                if(movesLeft-hoverMoves-pickUpMoves-deliveryMove-returnHomeMoves>=0){
                    OrderNumberChanges.put(movements.size(),orders.get(i));
                    movements.addAll(d.getPath(delivery.get(orders.get(i-1)),pickup.get(orders.get(i)).get(0)).getVertexList());

                    HoverLocation.add(movements.size());
                    movements.addAll(d.getPath(pickup.get(orders.get(i)).get(0),pickup.get(orders.get(i)).get(1)).getVertexList());

                    HoverLocation.add(movements.size());
                    movements.addAll(d.getPath(pickup.get(orders.get(i)).get(1),delivery.get(orders.get(i))).getVertexList());

                    HoverLocation.add(movements.size());
                    movesLeft = movesLeft-pickUpMoves-deliveryMove-hoverMoves;
                    pointer=i;
                    OrdersCompleted.add(orders.get(i));
                }

            }
        }
        movements.addAll(d.getPath(delivery.get(orders.get(pointer)),appleton).getVertexList());


        return movements;
    }
    public static ArrayList<String> nearestNeighbourApproach(Graph<LongLat,NodeEdges> g,HashMap<String,ArrayList<LongLat>> pickUpNodes,HashMap<String,LongLat> deliveryNodes){
        ArrayList<String> orders = Orders.orderNos;
        //System.out.println(orders);
        LongLat appleton = getAppleton(g);
        if(appleton == null){
            System.out.println("Could not associate start location with a node on the map");
            appleton = LongLat.appleton;
        }
        ArrayList<String> sortedOrders = new ArrayList<>();
        String closest = null;
        int ind = 0;
        double dist = Double.MAX_VALUE;
        for(String order:orders){
            ArrayList<LongLat> currentNodes = pickUpNodes.get(order);
            for(int i=0;i<currentNodes.size();i++){
                if(appleton.distanceTo(currentNodes.get(i))<dist){
                    closest = order;
                    dist = appleton.distanceTo(currentNodes.get(i));
                    ind=i;
                }
            }
        }
        sortedOrders.add(closest);
        while (sortedOrders.size()!=orders.size()){
            closest = null;
            ind = 0;
            dist = Double.MAX_VALUE;
            for(String order:orders){
                if(!sortedOrders.contains(order)){
                    ArrayList<LongLat> currentNodes = pickUpNodes.get(order);
                    for(int i=0;i<currentNodes.size();i++){
                        if(deliveryNodes.get(sortedOrders.get(sortedOrders.size()-1)).distanceTo(currentNodes.get(i))<dist){
                            closest = order;
                            dist = deliveryNodes.get(sortedOrders.get(sortedOrders.size()-1)).distanceTo(currentNodes.get(i));
                            ind=i;
                        }
                    }
                }

            }
            sortedOrders.add(closest);
        }
        //System.out.println(Orders.orderNos);
        return sortedOrders;
    }
    public static ArrayList<String> randomAppraoch(){
        ArrayList<String> random = new ArrayList<>(Orders.orderNos);
        Collections.shuffle(random);
        return random;
    }
    public static ArrayList<String> greedyApproach(HashMap<String,ArrayList<String>> items){
        ArrayList<String> sortedOrders = new ArrayList<>();
        ArrayList<String> ord = Orders.orderNos;
        String[] orderArray= new String[ord.size()];
        int[][] deliveryCosts = new int[ord.size()][2];
        Menus.getPrices();
        HashMap<String,Integer> itemPrices = Menus.prices;
        int i=0;
        for(String order :ord){
            int total =50;
            for(String item : items.get(order)){
                total = total + itemPrices.get(item);
            }
            deliveryCosts[i][0] = total;
            deliveryCosts[i][1] = i;
            orderArray[i] = order;
            i++;
        }
        Arrays.sort(deliveryCosts, Comparator.comparingDouble(o -> o[0]));


        for(int k=deliveryCosts.length-1;k>=0;k--){
            int ind = deliveryCosts[k][1];
            sortedOrders.add(orderArray[ind]);
        }
        System.out.println(sortedOrders);
        System.out.println(Orders.orderNos);
        return sortedOrders;
    }
}
