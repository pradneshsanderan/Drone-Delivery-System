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
    public static void createFlightPathDatabase(){
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
            PreparedStatement psFlightPaths = conn.prepareStatement("insert into deliveries values (?, ?, ?, ?, ?, ?)");
            ArrayList<FlightPath> flightPaths = getDeliveryList();
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
    private static ArrayList<FlightPath> flightPaths (List<LongLat> moves){

    }

}
