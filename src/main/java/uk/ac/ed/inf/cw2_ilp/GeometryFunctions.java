package uk.ac.ed.inf.cw2_ilp;


import uk.ac.ed.inf.cw2_ilp.dataTypes.*;


public class GeometryFunctions {

    //find the difference between two points
    public static double getDistanceBetween(LngLat pos_1, LngLat pos_2){

        double latDiff = pos_1.getLat() - pos_2.getLat();
        double lngDiff = pos_1.getLng() - pos_2.getLng();

        //calculates and returns the Euclidean distance
        return Math.sqrt(Math.pow(latDiff, 2) + Math.pow(lngDiff, 2));
    }

    //calculates the next position given a start point and angle
    public static LngLat calculateNewPos(LngLat start, Double angle) {
        LngLat position = new LngLat();
        position.setLat(start.getLat());
        position.setLng(start.getLng());


        //calculate hoe much the altitude and longitude have to change by using trig
        double latChange = Constants.MOVEMENT * Math.cos(Math.toRadians(angle));
        double lngChange = Constants.MOVEMENT * Math.sin(Math.toRadians(angle));

        //set the values to the values after the calculated change
        position.setLat(position.getLat() + latChange);
        position.setLng(position.getLng() + lngChange);
        return position;
    }

    public static boolean isInPolygon(int numVert, double[] vertLng, double[] vertLat, double pointLng, double pointLat) {
        //initialise variables
        int i, j;
        boolean inside = false;

        for (i = 0, j = numVert - 1; i < numVert; j = i++) {
            //first check if the point is on the line between two vertices
            if (isPointOnLine(pointLng, pointLat, vertLng[i], vertLat[i], vertLng[j], vertLat[j])) {
                return true;
            }

            //use a ray casting algorithm, changing the value of inside everytime it crosses a line to check
            //whether the point is inside the given region
            if (((vertLat[i] > pointLat) != (vertLat[j] > pointLat)) &&
                    (pointLng < (vertLng[j] - vertLng[i]) * (pointLat - vertLat[i]) / (vertLat[j] - vertLat[i]) + vertLng[i])) {
                inside = !inside;
            }
        }
        return inside;

    }

    //checks whether a point is on a line between two vertices, returns result as a boolean
    public static boolean isPointOnLine(double pointLng, double pointLat, double Lng1, double Lat1, double Lng2, double Lat2) {

        //calculate the area of the triangle formed by the points
        double area = 0.5 * Math.abs(Lng1 * (Lat2 - pointLat) + Lng2 * (pointLat - Lat1) + pointLng * (Lat1 - Lat2));

        //if this is larger than 0 (or very close to) then it is not on the line
        if (area > 1e-10) {
            return false;
        }
        //checks to make sure it is on the correct segment of the line not just collinear
        return (Math.min(Lng1, Lng2) <= pointLng && pointLng <= Math.max(Lng1, Lng2)) &&
                (Math.min(Lat1, Lat2) <= pointLat && pointLat <= Math.max(Lat1, Lat2));
    }






}
