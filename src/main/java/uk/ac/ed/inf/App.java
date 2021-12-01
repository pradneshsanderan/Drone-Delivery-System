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
        int count =0;
        boolean lessThan = true;
        for(int i=0;i<Drone.distances.size();i++){
            if (Drone.distances.get(i) > 0.00015) {
                lessThan = false;
                count++;

            }
        }
        System.out.println(count);
        System.out.println(lessThan);
        System.out.println("done");
    }
}
