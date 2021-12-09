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
        ApacheDatabase.createDeliveriesDatabase();
        ApacheDatabase.createFlightPathDatabase(moves);
        System.out.println(moves);
        List<Point> pointList = new ArrayList<>();

        for (LongLat move : moves) {
            pointList.add(move.point);
        }
        System.out.println(pointList.size());
        Geometry geometry = (Geometry) LineString.fromLngLats(pointList);
        FeatureCollection fc = FeatureCollection.fromFeature(Feature.fromGeometry(geometry));
        System.out.println(fc.toJson());
        ArrayList<String> OrdersCOmp = HexGraph.OrdersCompleted;
        double total = Menus.getTotalChargeForOrders(OrdersCOmp);
        double supposedAmount = Menus.getTotalChargeForOrders(Orders.orderNos);
        double percent = (total/supposedAmount)*100;
        System.out.println("completion percentage:"+percent+"%");
        System.out.println("done");
    }

}
























