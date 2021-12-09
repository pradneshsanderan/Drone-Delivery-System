package uk.ac.ed.inf;

import com.mapbox.geojson.*;
import org.jgrapht.Graph;

import java.nio.file.Paths;
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
        System.out.println("day:" + day);
        System.out.println("month: "+ month);
        System.out.println("year: "+ year);
        System.out.println("webserverPort: "+webServerPort);
        System.out.println("databasePost: "+ databasePort);
        Orders.getCoordinatesAndOrders();
        GeoJsonParser.getNoFlyZones();
        GeoJsonParser.getLandmarks();
        List<LongLat> moves = HexGraph.getRoute();
        ApacheDatabase.createDeliveriesDatabase();
        ApacheDatabase.createFlightPathDatabase(moves);
        String fc= GeoJsonParser.movesToFCCollection(moves);
        GeoJsonParser.outputGeoJsonFile(fc);
        ArrayList<String> OrdersCOmp = HexGraph.OrdersCompleted;
        double total = Menus.getTotalChargeForOrders(OrdersCOmp);
        double supposedAmount = Menus.getTotalChargeForOrders(Orders.orderNos);
        double percent = (total/supposedAmount)*100;
        System.out.println("completion percentage:"+percent+"%");
        System.out.println("done");
    }

}
























