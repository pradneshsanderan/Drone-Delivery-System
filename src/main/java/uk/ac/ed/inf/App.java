package uk.ac.ed.inf;

import com.mapbox.geojson.*;
import org.jgrapht.Graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;

/**
 * Hello world!
 *
 */
public class App 
{
    public static String day;
    public static String month;
    public static String year;
    public static String webServerPort;
    public static String databasePort;


    public static void main(String[] args) {
        day = args[0];
        month = args[1];
        year = args[2];
        webServerPort = args[3];
        databasePort = args[4];
        Orders.getCoordinatesAndOrders();
        GeoJsonParser.getNoFlyZones();
        GeoJsonParser.getLandmarks();
        List<LongLat> moves = HexGraph.getRoute();

        System.out.println(moves.size());
//        List<Point> pointList = new ArrayList<>();
//
//        for (LongLat move : moves) {
//            pointList.add(move.point);
//        }
//        System.out.println(pointList.size());
//        Geometry geometry = (Geometry) LineString.fromLngLats(pointList);
//        FeatureCollection fc = FeatureCollection.fromFeature(Feature.fromGeometry(geometry));
//        System.out.println(fc.toJson());
//        System.out.println("done");
    }
//sort drone locations by distance from each start and end
}


























//    public static void main( String[] args ) {
//        day = args[0];
//        month= args[1];
//        year = args[2];
//        webServerPort = args[3];
//        databasePort = args[4];
//        Orders.getCoordinatesAndOrders();
//        GeoJsonParser.getNoFlyZones();
//        GeoJsonParser.getLandmarks();
//        Drone drone = new Drone();
//        ArrayList<LongLat> n =new ArrayList<>();
//        //n.add(Drone.appleton);
//        drone.setCurrPosition(Drone.appleton);
//        System.out.println(Orders.orderNos);
//        Orders.orderNos.remove(0);
//        while(!Orders.orderNos.isEmpty()){
//            String order = drone.closestOrder()[0];
//            int shop = Integer.parseInt(drone.closestOrder()[1]);
//            //goes to each shop to pick up orders
//            LongLat nextPos = new LongLat(Orders.pickUpCoordinates.get(order).get(shop)[0],Orders.pickUpCoordinates.get(order).get(shop)[1]);
//            System.out.println("pick up");
//            ArrayList<LongLat> k = AStarPathFinder.setup(drone.currentPosition,nextPos);
//            n.addAll(k);
//
//            drone.setCurrPosition(n.get(n.size()-1));
//            //drone.move(nextPos);
//            if(Orders.pickUpCoordinates.get(order).size()==2){
//                int nextShop =0;
//                if(shop==0){
//                    nextShop = 1;
//                }
//                LongLat nextPos1 = new LongLat(Orders.pickUpCoordinates.get(order).get(nextShop)[0],Orders.pickUpCoordinates.get(order).get(nextShop)[1]);
//                System.out.println("pick up 2");
//                drone.setCurrPosition(n.get(n.size()-1));
//                ArrayList<LongLat> m = AStarPathFinder.setup(drone.currentPosition,nextPos1);
//                n.addAll(m);
//                //drone.move(nextPos1);
//
//            }
//            LongLat newPos = n.get(n.size()-1);
//            //moves to delivery
//            System.out.println("Delivery");
//            LongLat deliveryPos = new LongLat(Orders.deliveryCoordinates.get(order)[0],Orders.deliveryCoordinates.get(order)[1]);
//            ArrayList<LongLat> d = AStarPathFinder.setup(newPos,deliveryPos);
//            n.addAll(d);
//            drone.setCurrPosition(n.get(n.size()-1));
//            Orders.orderNos.remove(order);
//        }
//
//        List<Point> pointList = new ArrayList<>();
//        for(int i=0;i<n.size();i++){
//            pointList.add(Point.fromLngLat(n.get(i).longitude,n.get(i).latitude));
//        }
//        System.out.println(pointList.size());
//        Geometry geometry = (Geometry) LineString.fromLngLats(pointList);
//        FeatureCollection fc = FeatureCollection.fromFeature(Feature.fromGeometry(geometry));
//        System.out.println(Drone.movements.size());
//        System.out.println(fc.toJson());
//        System.out.println("done");
//    }