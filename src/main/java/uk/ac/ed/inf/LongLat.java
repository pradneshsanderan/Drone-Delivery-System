package uk.ac.ed.inf;

public class LongLat {
    public double longitude;
    public double latitude;

    LongLat(double longitude,double latitude){
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public boolean isConfined(){
        double confAreaRight = -3.184319;
        double confAreaLeft = -3.192473;
        double confAreaTop = 55.946233;
        double confAreaBottom = 55.942617;
        return ((longitude< confAreaRight && longitude> confAreaLeft) && (latitude< confAreaTop && latitude> confAreaBottom));
    }

    public static double distanceTo(LongLat l1,LongLat l2){
        return Math.sqrt(Math.pow((l1.latitude-l2.latitude),2)+Math.pow((l1.longitude-l1.longitude),2));

    }

    public boolean closeTo(LongLat l1){
        LongLat l2 = new LongLat(longitude,latitude);
        return distanceTo(l1,l2)<0.00015;
    }

    LongLat nextPosition(int angle){
        if(angle==0){
            return new LongLat(longitude+0.00015,latitude);
        }
        else if(angle<90){
            double xPosition = 
        }
        else if(angle ==90){
            return new LongLat(longitude,latitude+0.00015);
        }
        else if(angle<180){

        }
        else if(angle ==180){
            return new LongLat(longitude-0.00015,latitude);
        }
        else if(angle<270){

        }
        else if(angle==270){
            return new LongLat(longitude,latitude-0.00015);
        }
        else{
            //less than 360
        }
    }


}
