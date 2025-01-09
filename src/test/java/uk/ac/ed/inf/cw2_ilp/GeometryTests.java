package uk.ac.ed.inf.cw2_ilp;

import org.junit.jupiter.api.Test;

import uk.ac.ed.inf.cw2_ilp.dataTypes.*;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

public class GeometryTests {

    Random random = new Random();

    @Test
    public void getDistanceBetweenTest()  {
        LngLat pos1 = new LngLat();
        pos1.setLat(0.0);
        pos1.setLng(0.0);

        LngLat pos2 = new LngLat();
        pos2.setLat(3.0);
        pos2.setLng(4.0);

        double expected = 5.0;

        double result = GeometryFunctions.getDistanceBetween(pos1, pos2);
        assertEquals(expected, result, 1e-6);
    }
    @Test
    public void getDistanceBetweenTest_SamePoint(){
        LngLat pos1 = new LngLat();
        pos1.setLat(3.0);
        pos1.setLng(4.0);

        LngLat pos2 = new LngLat();
        pos2.setLat(3.0);
        pos2.setLng(4.0);

        double expected = 0.0;

        double result = GeometryFunctions.getDistanceBetween(pos1, pos2);
        assertEquals(expected, result, 1e-6);
    }
    @Test
    public void getDistanceBetweenTest_Negatives()  {
        LngLat pos1 = new LngLat();
        pos1.setLat(0.0);
        pos1.setLng(0.0);

        LngLat pos2 = new LngLat();
        pos2.setLat(-3.0);
        pos2.setLng(-4.0);

        double expected = 5.0;

        double result = GeometryFunctions.getDistanceBetween(pos1, pos2);
        assertEquals(expected, result, 1e-6);
    }
    @Test
    public void getDistanceBetweenTest_Random()  {
        LngLat pos1 = generateRandomLngLat();
        LngLat pos2 = generateRandomLngLat();
        LngLatPair test = new LngLatPair();
        test.setPos1(pos1);
        test.setPos2(pos2);

        double lng1 = pos1.getLng();
        double lat1 = pos1.getLat();
        double lng2 = pos2.getLng();
        double lat2 = pos2.getLat();

        double result = GeometryFunctions.getDistanceBetween(pos1, pos2);

        double expected = Math.sqrt(Math.pow(lat1-lat2, 2) + Math.pow(lng1-lng2, 2));

        assertEquals(expected, result, 1e-6);
    }
    @Test
    public void calculateNewPosTest_ZeroAngle() {
        LngLat start = generateRandomLngLat();

        LngLat result = GeometryFunctions.calculateNewPos(start, 0.0);

        LngLat expected = new LngLat();
        expected.setLat(start.getLat() + Constants.MOVEMENT);
        expected.setLng(start.getLng());

        assertEquals(expected.getLat(), result.getLat());
        assertEquals(expected.getLng(), result.getLng());
    }

    @Test
    public void calculateNewPosTest_90Angle() {
        LngLat start = generateRandomLngLat();

        LngLat result = GeometryFunctions.calculateNewPos(start, 90.0);

        LngLat expected = new LngLat();
        expected.setLat(start.getLat() );
        expected.setLng(start.getLng()+ Constants.MOVEMENT);

        assertEquals(expected.getLat(), result.getLat());
        assertEquals(expected.getLng(), result.getLng());
    }

    @Test
    public void calculateNewPosTest_180Angle() {
        LngLat start = generateRandomLngLat();

        LngLat result = GeometryFunctions.calculateNewPos(start, 180.0);

        LngLat expected = new LngLat();
        expected.setLat(start.getLat() - Constants.MOVEMENT);
        expected.setLng(start.getLng());

        assertEquals(expected.getLat(), result.getLat());
        assertEquals(expected.getLng(), result.getLng());
    }
    @Test
    public void calculateNewPosTest_RandomAngle() {
        LngLat start = generateRandomLngLat();
        double angle = generateRandomAngle();

        LngLat result = GeometryFunctions.calculateNewPos(start, angle);

        LngLat expected = new LngLat();
        double latChange = Constants.MOVEMENT * Math.cos(Math.toRadians(angle));
        double lngChange = Constants.MOVEMENT * Math.sin(Math.toRadians(angle));
        expected.setLat(start.getLat() + latChange);
        expected.setLng(start.getLng() + lngChange);

        assertEquals(expected.getLat(), result.getLat());
        assertEquals(expected.getLng(), result.getLng());
    }

    @Test
    public void isInPolygonTest_PointInside(){
        double[] vertLng = {-5.0, 5.0, 5.0, -5.0};
        double[] vertLat = {-5.0, -5.0, 5.0, 5.0};
        int numVert = 4;

        double pointLng = 0.0;
        double pointLat = 0.0;

        boolean result = GeometryFunctions.isInPolygon(numVert, vertLng, vertLat, pointLng, pointLat);
        assertTrue(result);
    }

    @Test
    public void isInPolygonTest_PointOutside(){
        double[] vertLng = {-5.0, 5.0, 5.0, -5.0};
        double[] vertLat = {-5.0, -5.0, 5.0, 5.0};
        int numVert = 4;

        double pointLng = 10.0;
        double pointLat = -10.0;

        boolean result = GeometryFunctions.isInPolygon(numVert, vertLng, vertLat, pointLng, pointLat);
        assertFalse(result);
    }

    @Test
    public void isInPolygonTest_PointOnLine(){
        double[] vertLng = {-5.0, 5.0, 5.0, -5.0};
        double[] vertLat = {-5.0, -5.0, 5.0, 5.0};
        int numVert = 4;

        double pointLng = 2;
        double pointLat = -2;

        boolean result = GeometryFunctions.isInPolygon(numVert, vertLng, vertLat, pointLng, pointLat);
        assertTrue(result);
    }

    @Test
    public void isInPolygonTest_PointOnVertex() {

        double[] vertLng = {-5.0, 5.0, 5.0, -5.0};
        double[] vertLat = {-5.0, -5.0, 5.0, 5.0};
        int numVert = 4;

        double pointLng = -5.0;
        double pointLat = -5.0;

        boolean result = GeometryFunctions.isInPolygon(numVert, vertLng, vertLat, pointLng, pointLat);
        assertTrue(result);
    }

    @Test
    public void isInPolygonTest_CollinearPoint() {
        double[] vertLng = {-5.0, 5.0, 5.0, -5.0};
        double[] vertLat = {-5.0, -5.0, 5.0, 5.0};
        int numVert = 4;

        double pointLng = 0.0;
        double pointLat = -10.0;

        boolean result = GeometryFunctions.isInPolygon(numVert, vertLng, vertLat, pointLng, pointLat);
        assertFalse(result);
    }
    @Test
    public void isPointOnLineTest_OnLine(){
        double Lng1 = 0.0;
        double Lat1 = 0.0;
        double Lng2 = 10.0;
        double Lat2 = 10.0;


        double pointLng = 5.0;
        double pointLat = 5.0;

        boolean result = GeometryFunctions.isPointOnLine(pointLng, pointLat, Lng1, Lat1, Lng2, Lat2);
        assertTrue(result);

    }

    @Test
    public void isPointOnLineTest_OffLine(){
        double Lng1 = 0.0;
        double Lat1 = 0.0;
        double Lng2 = 10.0;
        double Lat2 = 10.0;


        double pointLng = 0.0;
        double pointLat = 5.0;

        boolean result = GeometryFunctions.isPointOnLine(pointLng, pointLat, Lng1, Lat1, Lng2, Lat2);
        assertFalse(result);

    }

    @Test
    public void isPointOnLineTest_ColinearPoint(){
        double Lng1 = 0.0;
        double Lat1 = 0.0;
        double Lng2 = 10.0;
        double Lat2 = 10.0;


        double pointLng = 11.0;
        double pointLat = 11.0;

        boolean result = GeometryFunctions.isPointOnLine(pointLng, pointLat, Lng1, Lat1, Lng2, Lat2);
        assertFalse(result);

    }
    @Test
    public void isPointOnLineTest_EndPoint(){
        double Lng1 = 0.0;
        double Lat1 = 0.0;
        double Lng2 = 10.0;
        double Lat2 = 10.0;


        double pointLng = 10.0;
        double pointLat = 10.0;

        boolean result = GeometryFunctions.isPointOnLine(pointLng, pointLat, Lng1, Lat1, Lng2, Lat2);
        assertTrue(result);

    }

    @Test
    public void isPointOnLineTest_ClosePoint(){
        double Lng1 = 0.0;
        double Lat1 = 0.0;
        double Lng2 = 10.0;
        double Lat2 = 10.0;


        double pointLng = 5.01;
        double pointLat = 5.0;

        boolean result = GeometryFunctions.isPointOnLine(pointLng, pointLat, Lng1, Lat1, Lng2, Lat2);
        assertFalse(result);

    }



    private LngLat generateRandomLngLat(){
        Double lng = random.nextDouble(180-(-180))-180;
        Double lat = random.nextDouble(90-(-90))-90;
        LngLat test = new LngLat();
        test.setLng(lng);
        test.setLat(lat);
        return test;
    }
    private double generateRandomAngle(){
        return random.nextDouble()*360;
    }
}
