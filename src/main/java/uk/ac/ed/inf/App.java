package uk.ac.ed.inf;

import com.mapbox.geojson.*;

import java.util.ArrayList;
import java.util.List;

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
    public static void main( String[] args ) {
        day = args[0];
        month= args[1];
        year = args[2];
        webServerPort = args[3];
        databasePort = args[4];
        Orders.getCoordinatesAndOrders();
        GeoJsonParser.getNoFlyZones();
        GeoJsonParser.getLandmarks();
        Drone drone = new Drone();
        System.out.println(Orders.orderNos);
        while(!Orders.orderNos.isEmpty()){
            String order = drone.closestOrder()[0];
            System.out.println(order);
            int shop = Integer.parseInt(drone.closestOrder()[1]);
            //goes to each shop to pick up orders
            LongLat nextPos = new LongLat(Orders.pickUpCoordinates.get(order).get(shop)[0],Orders.pickUpCoordinates.get(order).get(shop)[1]);
            System.out.println("pick up");
            while(!drone.currentPosition.closeTo(nextPos)){
                //System.out.println("moving");
                drone.move(nextPos);
//                List<Point> pointList = new ArrayList<>();
//                pointList.add(Point.fromLngLat(-3.186874,55.944494));
//                for(int i=0;i<Drone.movements.size();i++){
//                    pointList.add(Point.fromLngLat(Drone.movements.get(i).longitude,Drone.movements.get(i).latitude));
//                }
//
//                Geometry geometry = (Geometry) LineString.fromLngLats(pointList);
//                FeatureCollection fc = FeatureCollection.fromFeature(Feature.fromGeometry(geometry));
//                System.out.println(fc.toJson());
            }
            if(Orders.pickUpCoordinates.get(order).size()==2){
                int nextShop =0;
                if(shop==0){
                    nextShop = 1;
                }
                LongLat nextPos1 = new LongLat(Orders.pickUpCoordinates.get(order).get(nextShop)[0],Orders.pickUpCoordinates.get(order).get(nextShop)[1]);
                System.out.println("pick up");
                while(!drone.currentPosition.closeTo(nextPos1)){
                    //System.out.println("moving");
                    drone.move(nextPos1);
//                    List<Point> pointList = new ArrayList<>();
//                    pointList.add(Point.fromLngLat(-3.186874,55.944494));
//                    for(int i=0;i<Drone.movements.size();i++){
//                        pointList.add(Point.fromLngLat(Drone.movements.get(i).longitude,Drone.movements.get(i).latitude));
//                    }
//
//                    Geometry geometry = (Geometry) LineString.fromLngLats(pointList);
//                    FeatureCollection fc = FeatureCollection.fromFeature(Feature.fromGeometry(geometry));
//                    System.out.println(fc.toJson());
                }
            }

            //moves to delivery
            System.out.println("Delivery");
            LongLat deliveryPos = new LongLat(Orders.deliveryCoordinates.get(order)[0],Orders.deliveryCoordinates.get(order)[1]);
            while(!drone.currentPosition.closeTo(deliveryPos)){
                System.out.println("  ");
                drone.move(deliveryPos);
                List<Point> pointList = new ArrayList<>();
                pointList.add(Point.fromLngLat(-3.186874,55.944494));
                for(int i=0;i<Drone.movements.size();i++){
                    pointList.add(Point.fromLngLat(Drone.movements.get(i).longitude,Drone.movements.get(i).latitude));
                }

                Geometry geometry = (Geometry) LineString.fromLngLats(pointList);
                FeatureCollection fc = FeatureCollection.fromFeature(Feature.fromGeometry(geometry));
                System.out.println(fc.toJson());
            }
            Orders.orderNos.remove(order);
        }

        List<Point> pointList = new ArrayList<>();
        pointList.add(Point.fromLngLat(-3.186874,55.944494));
        for(int i=0;i<Drone.movements.size();i++){
            pointList.add(Point.fromLngLat(Drone.movements.get(i).longitude,Drone.movements.get(i).latitude));
        }
        pointList.add(Point.fromLngLat(-3.186874,55.944494));
        Geometry geometry = (Geometry) LineString.fromLngLats(pointList);
        FeatureCollection fc = FeatureCollection.fromFeature(Feature.fromGeometry(geometry));
        System.out.println(Drone.movements.size());
        System.out.println(fc.toJson());
        System.out.println("done");
    }
}
