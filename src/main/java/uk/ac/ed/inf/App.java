package uk.ac.ed.inf;

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


    public static void main(String[] args) {
        day = args[0];
        month = args[1];
        year = args[2];
        webServerPort = args[3];
        databasePort = args[4];
        Orders.getCoordinatesAndOrders();
        GeoJsonParser.getNoFlyZones();
        GeoJsonParser.getLandmarks();
        Drone drone = new Drone(1500);
        List<LongLat> moves = HexGraph.getRoute(drone);
        String fc =GeoJsonParser.movesToFCCollection(moves);
        GeoJsonParser.outputGeoJsonFile(fc);
        System.out.println(moves.size());
        ApacheDatabase.createDeliveriesDatabase();
        ApacheDatabase.createFlightPathDatabase(moves);
        System.out.println("done");
    }

}
























