package uk.ac.ed.inf;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ApacheDatabase {
    private static final String jdbcString = "jdbc:derby://localhost:"+ App.databasePort+"/derbyDB";


    public static void createDeliveriesDatabase(){
        try{
            Connection conn = DriverManager.getConnection(jdbcString);
            Statement statement = conn.createStatement();
            DatabaseMetaData databaseMetadata = conn.getMetaData();
            ResultSet resultSet = databaseMetadata.getTables(null, null, "DELIVERIES", null);
            if (resultSet.next()) {
                statement.execute("drop table deliveries");
            }
            statement.execute(
                    "create table deliveries(" +
                            "orderNo char(8), " +
                            "deliveredTo varchar(19), " +
                            "costInPence int)");
            PreparedStatement psDeliveries = conn.prepareStatement("insert into deliveries values (?, ?, ?)");
            ArrayList<Deliveries> deliveries = getDeliveryList();
            for (Deliveries delivery : deliveries) {
                psDeliveries.setString(1, delivery.orderNo);
                psDeliveries.setString(2, delivery.deliveredTo);
                psDeliveries.setInt(3, delivery.costInPence);
                psDeliveries.execute();
            }
        }catch(java.sql.SQLException e){
            e.printStackTrace();
        }
    }
    private static ArrayList<Deliveries> getDeliveryList(){
        ArrayList<String> orders = HexGraph.OrdersCompleted;
        ArrayList<Deliveries> deliveries = new ArrayList<>();
        for(String order: orders){
            Deliveries d = new Deliveries();
            d.orderNo = order;
            d.deliveredTo = Orders.deliveryAddress.get(order);
            d.costInPence = Menus.getChargeForOneOrder(order);
            deliveries.add(d);
        }
        return deliveries;
    }
    public static void createFlightPathDatabase(List<LongLat> moves){
        try{
            Connection conn = DriverManager.getConnection(jdbcString);
            Statement statement = conn.createStatement();
            DatabaseMetaData databaseMetadata = conn.getMetaData();
            ResultSet resultSet = databaseMetadata.getTables(null, null, "FLIGHTPATH", null);
            if (resultSet.next()) {
                statement.execute("drop table flightpath");
            }
            statement.execute(
                    "create table flightpath(" +
                            "orderNo char(8), " +
                            "fromLongitude double, " +
                            "fromLatitude double, "+
                            "angle integer, "+
                            "toLongitude double, "+
                            "toLatitude double)");
            PreparedStatement psFlightPaths = conn.prepareStatement("insert into flightpath values (?, ?, ?, ?, ?, ?)");
            ArrayList<FlightPath> flightPaths = getFlightPaths(moves);
            for (FlightPath flightPath : flightPaths) {
                psFlightPaths.setString(1, flightPath.orderNo);
                psFlightPaths.setDouble(2, flightPath.fromLongitude);
                psFlightPaths.setDouble(3, flightPath.fromLatitude);
                psFlightPaths.setInt(4, flightPath.angle);
                psFlightPaths.setDouble(5, flightPath.toLongitude);
                psFlightPaths.setDouble(6, flightPath.toLatitude);
                psFlightPaths.execute();
            }
        }catch(java.sql.SQLException e){
            e.printStackTrace();
        }
    }
    private static ArrayList<FlightPath> getFlightPaths (List<LongLat> moves){
        ArrayList<FlightPath> flightPaths = new ArrayList<>();
        for(int i=1;i<moves.size();i++){
            FlightPath f = new FlightPath();
            f.fromLatitude = moves.get(i-1).latitude;
            f.fromLongitude = moves.get(i-1).longitude;
            f.angle=moves.get(i-1).angleDirectionTo(moves.get(i));
            f.toLatitude = moves.get(i).latitude;
            f.toLongitude = moves.get(i).longitude;
            flightPaths.add(f);
        }
        return flightPaths;
    }

}
