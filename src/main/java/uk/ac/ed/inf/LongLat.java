package uk.ac.ed.inf;

public class LongLat {
    public double longitude;
    public double latitude;

    /**
     * The Constructor for the LongLat class which accepts 2 double precision numbers which are the
     * longitude and latitude
     * @param longitude a double precision number that represents the longitude of a specific point
     * @param latitude a double precision number that represents the latitude of a specific point
     */
    LongLat(double longitude,double latitude){
        this.longitude = longitude;
        this.latitude = latitude;
    }

    /**
     * A Method that states if the drone is within the drone confinement area.
     * @return true if the drone is within the confinement area and false otherwise
     */
    public boolean isConfined(){
        double confAreaRight = -3.184319;
        double confAreaLeft = -3.192473;
        double confAreaTop = 55.946233;
        double confAreaBottom = 55.942617;
        return ((longitude< confAreaRight && longitude> confAreaLeft) && (latitude< confAreaTop && latitude> confAreaBottom));
    }

    /**
     * A Method that calculates the Pythagorean distance between the point l1 and the current point of the instance
     * @param l1 a LongLat object
     * @return the Pythagorean distance between the LongLat object l1 and the current point of the instance
     */
    public double distanceTo(LongLat l1){
        LongLat l2 = new LongLat(longitude,latitude);
        return Math.sqrt(Math.pow((l1.latitude-l2.latitude),2)+Math.pow((l1.longitude-l2.longitude),2));

    }

    /**
     * A method which checks if a LongLat object is close to (distance is less than 0.00015) the current point of the instance.
     * @param l1 a LongLat object
     * @return true if the Longlat object is close to the current point of the instance and false otherwise.
     */
    public boolean closeTo(LongLat l1){
        return distanceTo(l1)<0.00015;
    }

    /**
     *  A method that shows the next position that the drone would be in if it makes one move (moves a distance of 0.00015)
     *  in the current angle that it is facing.
     * @param angle the current angle that the drone is facing
     * @return a LongLat object that represents the next position the drone would be in if it makes one move in the angle
     * given as a parameter
     */
    public LongLat nextPosition(int angle){
        final double dist = 0.00015;
        if(angle == -999){
            return new LongLat(longitude,latitude);
        }
        if(angle==0){
            return new LongLat(longitude+0.00015,latitude);
        }
        else if(angle<90){
            double radians = Math.toRadians(angle);
            double xMoved = dist * Math.cos(radians);
            double yMoved = dist * Math.sin(radians);
            return new LongLat(longitude+xMoved,latitude+yMoved);
        }
        else if(angle ==90){
            return new LongLat(longitude,latitude+0.00015);
        }
        else if(angle<180){
            angle = angle - 90;
            double radians = Math.toRadians(angle);
            double xMoved = dist * Math.sin(radians);
            double yMoved = dist * Math.cos(radians);
            return new LongLat(longitude-xMoved,latitude+yMoved);
        }
        else if(angle ==180){
            return new LongLat(longitude-0.00015,latitude);
        }
        else if(angle<270){
            angle = angle - 180;
            double radians = Math.toRadians(angle);
            double xMoved = dist * Math.cos(radians);
            double yMoved = dist * Math.sin(radians);
            return new LongLat(longitude-xMoved,latitude-yMoved);
        }
        else if(angle==270){
            return new LongLat(longitude,latitude-0.00015);
        }
        else{
            angle = angle -270;
            double radians = Math.toRadians(angle);
            double xMoved = dist * Math.sin(radians);
            double yMoved = dist * Math.cos(radians);
            return new LongLat(longitude+xMoved,latitude-yMoved);
        }
    }


}
