package uk.ac.ed.inf;

import java.util.ArrayList;
import java.util.HashMap;

public class Drone {

    public LongLat currentPosition;
    public int currAngle ;

    Drone(LongLat currentPosition,int currAngle){
        currAngle = this.currAngle;
        currentPosition = this.currentPosition;
    }


    public void move(LongLat targetPosition,int angle){
        double distanceToTarget = currentPosition.distanceTo(targetPosition);
        if(currentPosition.nextPosition(currAngle).distanceTo(targetPosition)<distanceToTarget){
            currentPosition = currentPosition.nextPosition(currAngle);
        }
        else{
            double newAngle = Math.toDegrees(Math.atan2(targetPosition.latitude-currentPosition.latitude,targetPosition.longitude-currentPosition.longitude));
            if(newAngle<0){
                newAngle += 360;
            }
            currAngle = (int) (10*(Math.round(newAngle/10)));
            currentPosition = currentPosition.nextPosition(currAngle);
        }
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
