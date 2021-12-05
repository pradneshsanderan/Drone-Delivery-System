package uk.ac.ed.inf;

import java.nio.file.LinkOption;
import java.util.ArrayList;
import java.util.HashMap;

public class AStarPathFinder {
    public static HashMap<LongLat,Double> fValue = new HashMap<>();
    public static HashMap<LongLat,Double> gValue = new HashMap<>();
    public static HashMap<LongLat,Double> hvalue = new HashMap<>();
    public static ArrayList<LongLat> openSet = new ArrayList<>();
    public static HashMap<LongLat, LongLat> nextPrevious = new HashMap<>();
    public  static ArrayList<LongLat> closedSet = new ArrayList<>();
    public static ArrayList<LongLat> path = new ArrayList<>();



    public static ArrayList<LongLat> setup(LongLat start, LongLat end){

        openSet.add(start);
        gValue.put(start,0.0);
        hvalue.put(start,manhattanDistance(start,end));
        fValue.put(start,hvalue.get(start));
        while(!openSet.isEmpty()){
            //System.out.println("here");
           LongLat lowest = null ;
           double lowestF = Double.MAX_VALUE;
           for(int i =0;i<openSet.size();i++){
               if(fValue.get(openSet.get(i))<lowestF){
                   lowest = openSet.get(i);
                   lowestF = fValue.get(openSet.get(i));
               }
           }
           LongLat current = lowest;
           if(current==end || current.closeTo(end)){
               System.out.println("DONE MOTHERFUCKER");
               LongLat temp = current;
               path.add(temp);
               while (nextPrevious.containsKey(temp)){
                   path.add(nextPrevious.get(temp));
                   temp = nextPrevious.get(temp);
               }
               ArrayList<LongLat> reversedPath = new ArrayList<>();
               for(int i=path.size()-1;i>=0;i--){
                   reversedPath.add(path.get(i));
               }
               return reversedPath;
           }
           openSet.remove(current);
           closedSet.add(current);
           for( int  i=0;i<9;i++){
               int angle = i*45;
               LongLat nextPos = current.nextPosition(angle);
               if(!closedSet.contains(nextPos) && nextPos.isConfined() && !current.inNoFlyZone(nextPos)){
                   if(openSet.contains(nextPos)){
                       if(gValue.get(current)+1<gValue.get(nextPos)){
                           gValue.remove(nextPos);
                           gValue.put(nextPos,gValue.get(current)+1);
                       }
                   }
                   else{
                       gValue.put(nextPos,gValue.get(current)+1);
                       openSet.add(nextPos);
                   }
                   hvalue.put(nextPos,manhattanDistance(nextPos,end));
                   fValue.put(nextPos,hvalue.get(nextPos));
                   nextPrevious.put(nextPos,current);
               }
           }

        }
        return path;
    }

    public static double manhattanDistance(LongLat start,LongLat end){
        return (Math.abs(start.longitude-end.longitude)+ Math.abs(start.latitude-end.latitude));

    }
}





































