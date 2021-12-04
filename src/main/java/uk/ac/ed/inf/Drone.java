package uk.ac.ed.inf;

import com.mapbox.geojson.*;
import com.mapbox.geojson.constants.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.awt.geom.Line2D;
import java.util.HashSet;
import java.util.List;

public class Drone {
    public static LongLat appleton = new LongLat(-3.186874,55.944494);
    public LongLat currentPosition = appleton;
    public LongLat prevPosition ;
    public int currAngle =0;
    public static ArrayList<LongLat> movements = new ArrayList<>();
    public static ArrayList<Double> distances = new ArrayList<>();

//    public void move(LongLat targetPosition){
////        System.out.println( currAngle);
//        LongLat nextPostWithCurrAngle = currentPosition.nextPosition(currAngle);
//        double distanceToTargetPos = currentPosition.distanceTo(targetPosition);
//        if(!inNoFlyZone(nextPostWithCurrAngle) && nextPostWithCurrAngle.isConfined()&& (distanceToTargetPos>=nextPostWithCurrAngle.distanceTo(targetPosition))){
////            System.out.println("no change");
//            prevPosition = currentPosition;
//            currentPosition = nextPostWithCurrAngle;
//
//        }
//        //current angle must be changed
//        else{
//            //System.out.println("change angle");
//            double lowestDistance = Double.MAX_VALUE;
//            int lowestDistanceAngle = currAngle;
//            for(int i=1;i<36;i++){
//                int newAngle = (currAngle + (i*10)) %360;
//                LongLat newPos = currentPosition.nextPosition(newAngle);
//                if(!inNoFlyZone(newPos) && newPos.isConfined() && (newPos.distanceTo(targetPosition)<=lowestDistance) && newPos.latitude != prevPosition.latitude && newPos.longitude!= prevPosition.longitude){
//                   //System.out.println(i);
////                    System.out.println("prevPos: "+prevPosition.longitude+","+prevPosition.latitude);
////                    System.out.println("newPos: "+newPos.longitude+","+newPos.latitude);
//                    lowestDistance = newPos.distanceTo(targetPosition);
//                    lowestDistanceAngle = newAngle;
//                }
//            }
//            //System.out.println("lowest distance: "+lowestDistance);
//            //System.out.println("best angle: "+lowestDistanceAngle);
//            currAngle = lowestDistanceAngle;
//            prevPosition = currentPosition;
//            currentPosition = currentPosition.nextPosition(lowestDistanceAngle);
//        }
//        movements.add(currentPosition);
//    }


    public void move(LongLat targetPosition){
        HashMap<Integer,LongLat> coordinatesVisited = new HashMap<>();
        int coordinatesVisitedInd = 1;
        coordinatesVisited.put(0,currentPosition);
        HashMap<Integer,Integer> anglesTaken = new HashMap<>();
        int anglesTakenInd =0;
        while(!currentPosition.closeTo(targetPosition)){
            LongLat nextPostWithCurrAngle = currentPosition.nextPosition(currAngle);
            double distanceToTargetPos = currentPosition.distanceTo(targetPosition);
            if(!inNoFlyZone(nextPostWithCurrAngle) && nextPostWithCurrAngle.isConfined()&& (distanceToTargetPos>nextPostWithCurrAngle.distanceTo(targetPosition))){
                if(!coordinatesVisited.containsValue(nextPostWithCurrAngle)){
                    prevPosition = currentPosition;
                    currentPosition = nextPostWithCurrAngle;
                    anglesTaken.put(anglesTakenInd,currAngle);
                    coordinatesVisited.put(coordinatesVisitedInd,currentPosition);
                    coordinatesVisitedInd++;
                    anglesTakenInd++;
                }
                else{
                    //backtrack
                }

            }
            else{
                double lowestDistance = Double.MAX_VALUE;
                int lowestDistanceAngle = currAngle;
                for(int i=1;i<36;i++){
                    int newAngle = (currAngle + (i*10)) %360;
                    LongLat newPos = currentPosition.nextPosition(newAngle);
                    if(!inNoFlyZone(newPos) && newPos.isConfined() && (newPos.distanceTo(targetPosition)<lowestDistance) && newPos.latitude != prevPosition.latitude
                            && newPos.longitude!= prevPosition.longitude & !coordinatesVisited.containsValue(newPos)){
                       //System.out.println(i);
    //                    System.out.println("prevPos: "+prevPosition.longitude+","+prevPosition.latitude);
    //                    System.out.println("newPos: "+newPos.longitude+","+newPos.latitude);
                        lowestDistance = newPos.distanceTo(targetPosition);
                        lowestDistanceAngle = newAngle;
                    }
                }
                currAngle = lowestDistanceAngle;
                prevPosition = currentPosition;
                currentPosition = currentPosition.nextPosition(lowestDistanceAngle);
                anglesTaken.put(anglesTakenInd,currAngle);
                coordinatesVisited.put(coordinatesVisitedInd,currentPosition);
                anglesTakenInd++;
                coordinatesVisitedInd++;
            }
            movements.add(currentPosition);
//            List<Point> pointList = new ArrayList<>();
//            pointList.add(Point.fromLngLat(-3.186874,55.944494));
//            for(int i=0;i<Drone.movements.size();i++){
//                pointList.add(Point.fromLngLat(Drone.movements.get(i).longitude,Drone.movements.get(i).latitude));
//            }
//
//            Geometry geometry = (Geometry) LineString.fromLngLats(pointList);
//            FeatureCollection fc = FeatureCollection.fromFeature(Feature.fromGeometry(geometry));
//            System.out.println(fc.toJson());
        }

    }



    private boolean inNoFlyZone(LongLat nextPosition){
        List<Feature> noFlyZones = GeoJsonParser.noFlyZoneFeatures;
        Line2D movement = new Line2D.Double(currentPosition.longitude,currentPosition.latitude,nextPosition.longitude,nextPosition.latitude);
        for(int i =0;i<noFlyZones.size();i++){
            //System.out.println("poly: "+i);
            // check if it crosses a polygon edge
            //System.out.println(noFlyZones.get(i).geometry().type());
            if(noFlyZones.get(i).geometry()!=null){
                Polygon polygon = (Polygon) noFlyZones.get(i).geometry();
                if(polygon!=null){
                    for(int j=0;j<polygon.coordinates().get(0).size()-1;j++){
                        int nextI = j+1;
                        List<Double> coordinates1 = polygon.coordinates().get(0).get(j).coordinates();
                        List<Double> coordinates2 = polygon.coordinates().get(0).get(nextI).coordinates();
                        //System.out.println("coord1: " + coordinates1);
                        //System.out.println("coord2:"+coordinates2);
                        Line2D edge = new Line2D.Double(coordinates1.get(0),coordinates1.get(1),coordinates2.get(0),coordinates2.get(1));
                        if(movement.intersectsLine(edge)){
                            //System.out.println("intersects");
                            return true;
                        }
                    }

                }

            }
        }
        return false;
    }

    public String[] closestOrder(){
        HashMap<String, ArrayList<double[]>> pickUpCoordinates = Orders.pickUpCoordinates;
        ArrayList<String> orderNos = Orders.orderNos;
        String closestOrderNo = orderNos.get(0);
        int shop = 0;
        double lowest = Integer.MAX_VALUE;
        for(int i=0;i<orderNos.size();i++){
            String curr = orderNos.get(i);
            int listSize = pickUpCoordinates.get(curr).size();
            for(int j=0;j<listSize;j++) {
                double[] coordinates = pickUpCoordinates.get(curr).get(j);
                LongLat currCoordinates = new LongLat(coordinates[0],coordinates[1]);
                if(currentPosition.distanceTo(currCoordinates)<=lowest){
                    closestOrderNo = curr;
                    lowest = currentPosition.distanceTo(currCoordinates);
                    shop = j;
                }
            }
        }

        return new String[] {closestOrderNo,shop+""};
    }
}









//    public void move(LongLat targetPosition){
//        System.out.println(currAngle);
//        double distanceToTarget = currentPosition.distanceTo(targetPosition);
//        if(currentPosition.nextPosition(currAngle).distanceTo(targetPosition)<distanceToTarget && !inNoFlyZone(currentPosition.nextPosition(currAngle),GeoJsonParser.noFlyZoneFeatures)){
//            distances.add(currentPosition.distanceTo(currentPosition.nextPosition(currAngle)));
//            currentPosition = currentPosition.nextPosition(currAngle);
//            System.out.println("iffed");
//
//        }
//        else{
//
//            double newAngle = Math.toDegrees(Math.atan2(targetPosition.latitude-currentPosition.latitude,targetPosition.longitude-currentPosition.longitude));
//            if(newAngle<0){
//                newAngle += 360;
//            }
//            int newAngle1 = (int) (10*(Math.round(newAngle/10)));
//            LongLat nextPos = currentPosition.nextPosition(newAngle1);
//            if(!inNoFlyZone(nextPos,GeoJsonParser.noFlyZoneFeatures)){
//                System.out.println("elsed 1");
//                currAngle = newAngle1;
//                distances.add(currentPosition.distanceTo(nextPos));
//                currentPosition = nextPos;
//
//            }
//            else{
//                for (int i=0;i<36;i++){
//                    System.out.println(i);
//                    if(!inNoFlyZone(currentPosition.nextPosition(newAngle1+(i*10)),GeoJsonParser.noFlyZoneFeatures) &&!inNoFlyZone(currentPosition.nextPosition(newAngle1-(i*10)),GeoJsonParser.noFlyZoneFeatures) ){
//                        if(currentPosition.distanceTo(currentPosition.nextPosition(newAngle1+(i*10)))> currentPosition.distanceTo(currentPosition.nextPosition(newAngle1-(i*10)))){
//                            currAngle = newAngle1 - (i*10);
//                            distances.add(currentPosition.distanceTo(currentPosition.nextPosition(newAngle1-(i*10))));
//                            currentPosition = currentPosition.nextPosition(newAngle1-(i*10));
//                        }
//                        else{
//                            currAngle = newAngle1 + (i*10);
//                            distances.add(currentPosition.distanceTo(currentPosition.nextPosition(newAngle1+(i*10))));
//                            currentPosition = currentPosition.nextPosition(newAngle1+(i*10));
//                        }
//                        System.out.println("elsed2");
//                    }
//                    else if(!inNoFlyZone(currentPosition.nextPosition(currAngle+(i*10)),GeoJsonParser.noFlyZoneFeatures)){
//                        currAngle = newAngle1 + (i*10);
//                        distances.add(currentPosition.distanceTo(currentPosition.nextPosition(newAngle1+(i*10))));
//                        currentPosition = currentPosition.nextPosition(newAngle1+(i*10));
//                        System.out.println("elsed 3");
//                    }
//                    else if(!inNoFlyZone(currentPosition.nextPosition(currAngle-(i*10)),GeoJsonParser.noFlyZoneFeatures)){
//                        currAngle = newAngle1 - (i*10);
//                        distances.add(currentPosition.distanceTo(currentPosition.nextPosition(newAngle1-(i*10))));
//                        currentPosition = currentPosition.nextPosition(newAngle1-(i*10));
//                        System.out.println("elsed4");
//                    }
//                    System.out.println("rotation: " +i);
//                }
//            }
//        }
//
//    }
