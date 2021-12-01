package uk.ac.ed.inf;

import com.mapbox.geojson.Polygon;
import com.mapbox.geojson.constants.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.awt.geom.Line2D;
import java.util.List;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.Geometry;

public class Drone {

    public LongLat currentPosition = new LongLat(-3.186874,55.944494);
    public int currAngle =0;
    public static ArrayList<LongLat> movements = new ArrayList<>();
    public static ArrayList<Double> distances = new ArrayList<>();




    public void move(LongLat targetPosition,int angle){
        double distanceToTarget = currentPosition.distanceTo(targetPosition);
        if(currentPosition.nextPosition(currAngle).distanceTo(targetPosition)<distanceToTarget && !inNoFlyZone(currentPosition.nextPosition(currAngle),GeoJsonParser.noFlyZoneFeatures)){
            distances.add(currentPosition.distanceTo(currentPosition.nextPosition(currAngle)));
            currentPosition = currentPosition.nextPosition(currAngle);

        }
        else{
            double newAngle = Math.toDegrees(Math.atan2(targetPosition.latitude-currentPosition.latitude,targetPosition.longitude-currentPosition.longitude));
            if(newAngle<0){
                newAngle += 360;
            }
            int newAngle1 = (int) (10*(Math.round(newAngle/10)));
            LongLat nextPosition = currentPosition.nextPosition(newAngle1);
            if(!inNoFlyZone(nextPosition,GeoJsonParser.noFlyZoneFeatures)){
                currAngle = newAngle1;
                distances.add(currentPosition.distanceTo(nextPosition));
                currentPosition = nextPosition;

            }
            else{
                for (int i=0;i<36;i++){
                    //check if add and sub in no fly zone
                    //if both in no flyzone
                    //if only one in no fly zone
                    //else loop
                    if(inNoFlyZone(currentPosition.nextPosition(newAngle1+(i*10)),GeoJsonParser.noFlyZoneFeatures) &&inNoFlyZone(currentPosition.nextPosition(newAngle1-(i*10)),GeoJsonParser.noFlyZoneFeatures) ){
                        if(currentPosition.distanceTo(currentPosition.nextPosition(newAngle1+(i*10)))> currentPosition.distanceTo(currentPosition.nextPosition(newAngle1-(i*10)))){
                            currAngle = newAngle1 - (i*10);
                            distances.add(currentPosition.distanceTo(currentPosition.nextPosition(newAngle1-(i*10))));
                            currentPosition = currentPosition.nextPosition(newAngle1-(i*10));
                        }
                        else{
                            currAngle = newAngle1 + (i*10);
                            distances.add(currentPosition.distanceTo(currentPosition.nextPosition(newAngle1+(i*10))));
                            currentPosition = currentPosition.nextPosition(newAngle1+(i*10));
                        }
                    }
                    else if(inNoFlyZone(currentPosition.nextPosition(currAngle+(i*10)),GeoJsonParser.noFlyZoneFeatures)){
                        currAngle = newAngle1 + (i*10);
                        distances.add(currentPosition.distanceTo(currentPosition.nextPosition(newAngle1+(i*10))));
                        currentPosition = currentPosition.nextPosition(newAngle1+(i*10));
                    }
                    else if(inNoFlyZone(currentPosition.nextPosition(currAngle-(i*10)),GeoJsonParser.noFlyZoneFeatures)){
                        currAngle = newAngle1 - (i*10);
                        distances.add(currentPosition.distanceTo(currentPosition.nextPosition(newAngle1-(i*10))));
                        currentPosition = currentPosition.nextPosition(newAngle1-(i*10));
                    }
                }
            }
        }
        movements.add(currentPosition);
    }
    private boolean inNoFlyZone(LongLat nextPosition, List<Feature> noFlyZones){
        Line2D movement = new Line2D.Double(currentPosition.longitude,currentPosition.latitude,nextPosition.longitude,nextPosition.latitude);
        for(int i =0;i<noFlyZones.size();i++){
            if(noFlyZones.get(i).geometry()!=null && noFlyZones.get(i).geometry().type().equals("Polygon")){
                Polygon polygon = (Polygon) noFlyZones.get(i).geometry();
                if(polygon!=null){
                    for(int j=0;j<polygon.coordinates().size()-1;j++){
                        int nextI = i+1;
                        List<Double> coordinates1 = polygon.coordinates().get(0).get(i).coordinates();
                        List<Double> coordinates2 = polygon.coordinates().get(0).get(nextI).coordinates();
                        Line2D edge = new Line2D.Double(coordinates1.get(0),coordinates1.get(1),coordinates2.get(0),coordinates2.get(1));
                        if(movement.intersectsLine(edge)){
                            return true;
                        }
                    }

                }

            }
        }
        return false;
    }

    public String closestOrder(HashMap<String, ArrayList<double[]>> pickUpCoordinates, ArrayList<String> orderNos){
        String closestOrderNo = orderNos.get(0);
        double lowest = Integer.MAX_VALUE;
        for(int i=0;i<orderNos.size();i++){
            String curr = orderNos.get(i);
            int listSize = pickUpCoordinates.get(curr).size();
            for(int j=0;j<listSize;j++) {
                double[] coordinates = pickUpCoordinates.get(curr).get(j);
                LongLat currCoordinates = new LongLat(coordinates[0],coordinates[1]);
                if(currentPosition.distanceTo(currCoordinates)<lowest){
                    closestOrderNo = curr;
                    lowest = currentPosition.distanceTo(currCoordinates);
                }
            }
        }
        return closestOrderNo;
    }
}
