package uk.ac.ed.inf;

/**
 * Hello world!
 *
 */
public class App 
{
    public static String day;
    public static String month;
    public static String year;
    public static int webServerPort;
    public static int databasePort;
    public static void main( String[] args ) {
        day = args[0];
        month= args[1];
        year = args[2];
        webServerPort = Integer.parseInt( args[3]);
        databasePort = Integer.parseInt(args[4]);
        Orders.getCoordinatesAndOrders();
        GeoJsonParser.getNoFlyZones();
        GeoJsonParser.getLandmarks();
        Drone drone = new Drone(new LongLat(-3.186874,55.944494),0);
        while(!Orders.orderNos.isEmpty()){
            String order = drone.closestOrder(Orders.pickUpCoordinates,Orders.orderNos);
            //goes to each shop to pick up orders
            for(int i=0;i<Orders.pickUpCoordinates.get(order).size();i++){
                LongLat nextPos = new LongLat(Orders.pickUpCoordinates.get(order).get(i)[0],Orders.pickUpCoordinates.get(order).get(i)[1]);
                //while its not close to the shop, it moves
                while(!drone.currentPosition.closeTo(nextPos)){
                    drone.move(nextPos,drone.currAngle);
                }
            }
            //moves to delivery
            LongLat deliveryPos = new LongLat(Orders.deliveryCoordinates.get(order)[0],Orders.deliveryCoordinates.get(order)[1]);
            while(!drone.currentPosition.closeTo(deliveryPos)){
                drone.move(deliveryPos,drone.currAngle);
            }
            Orders.orderNos.remove(order);
        }
    }
}
