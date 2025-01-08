package uk.ac.ed.inf.cw2_ilp;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import uk.ac.ed.inf.cw2_ilp.dataTypes.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

public class ControllerTests {

    RestController restController = new RestController();
    ObjectMapper mapper = new ObjectMapper();
    Random random = new Random();

    @Test
    public void getUUIDTest(){
        String result = restController.getUuid().getBody();
        String myUUID = "s2282430";

        assertEquals(result, myUUID);
    }

    @Test
    public void distanceToTest_ValidInput() throws JsonProcessingException {
        LngLatPair test = generateRandomLngLatPair();

        double lng1 = test.getPos1().getLng();
        double lat1 = test.getPos1().getLat();
        double lng2 = test.getPos2().getLng();
        double lat2 = test.getPos2().getLat();

        Double result = restController.getDistanceTo(mapper.writeValueAsString(test)).getBody();
        Double expected = Math.sqrt(Math.pow(lat1-lat2, 2) + Math.pow(lng1-lng2, 2));
        assertEquals(expected, result);


    }

    @Test
    public void distanceToTest_MissingPosition() throws JsonProcessingException {
        LngLat test1 = generateRandomLngLat();
        LngLatPair testPair = new LngLatPair();
        testPair.setPos1(test1);

        HttpStatusCode result = restController.getDistanceTo(mapper.writeValueAsString(testPair)).getStatusCode();

        assertEquals(HttpStatus.BAD_REQUEST, result);


    }

    @Test
    public void distanceToTest_InvalidPosition() throws JsonProcessingException {
        LngLat test1 = generateRandomLngLat();
        LngLat test2 = new LngLat();
        test2.setLng(200.0);
        test2.setLat(100.0);
        LngLatPair testPair = new LngLatPair();
        testPair.setPos1(test1);
        testPair.setPos2(test2);
        HttpStatusCode result = restController.getDistanceTo(mapper.writeValueAsString(testPair)).getStatusCode();

        assertEquals(HttpStatus.BAD_REQUEST, result);
    }

    @Test
    public void distanceToTest_EmptyInput() throws JsonProcessingException {
        HttpStatusCode result = restController.getDistanceTo(mapper.writeValueAsString("")).getStatusCode();

        assertEquals(HttpStatus.BAD_REQUEST, result);
    }

    @Test
    public void isCloseToTest_ValidInput() throws JsonProcessingException {
        LngLatPair test = generateRandomLngLatPair();

        double lng1 = test.getPos1().getLng();
        double lat1 = test.getPos1().getLat();
        double lng2 = test.getPos2().getLng();
        double lat2 = test.getPos2().getLat();

        Boolean result = restController.isCloseTo(mapper.writeValueAsString(test)).getBody();
        Double distance = Math.sqrt(Math.pow(lat1-lat2, 2) + Math.pow(lng1-lng2, 2));
        Boolean expected = distance < Constants.MOVEMENT;
        assertEquals(expected, result);

    }

    @Test
    public void isCloseToTest_MissingPosition() throws JsonProcessingException {
        LngLat test1 = generateRandomLngLat();
        LngLatPair testPair = new LngLatPair();
        testPair.setPos1(test1);

        HttpStatusCode result = restController.isCloseTo(mapper.writeValueAsString(testPair)).getStatusCode();

        assertEquals(HttpStatus.BAD_REQUEST, result);


    }

    @Test
    public void isCloseToTest_InvalidPosition() throws JsonProcessingException {
        LngLat test1 = generateRandomLngLat();
        LngLat test2 = generateInvalidLngLat();
        LngLatPair testPair = new LngLatPair();
        testPair.setPos1(test1);
        testPair.setPos2(test2);

        HttpStatusCode result = restController.isCloseTo(mapper.writeValueAsString(testPair)).getStatusCode();

        assertEquals(HttpStatus.BAD_REQUEST, result);

    }

    @Test
    public void isCloseToTest_EmptyInput() throws JsonProcessingException {
        HttpStatusCode result = restController.isCloseTo(mapper.writeValueAsString("")).getStatusCode();
        assertEquals(HttpStatus.BAD_REQUEST, result);
    }

    @Test
    public void nextPositionTest_ValidInput() throws JsonProcessingException {
        LngLat position = generateRandomLngLat();
        double angle = generateRandomAngle();
        NextPositionRequest test = new NextPositionRequest();
        test.setStart(position);
        test.setAngle(angle);
        String result = restController.nextPosition(mapper.writeValueAsString(test)).getBody();

        LngLat expected = new LngLat();
        double latChange = Constants.MOVEMENT * Math.cos(Math.toRadians(angle));
        double lngChange = Constants.MOVEMENT * Math.sin(Math.toRadians(angle));

        expected.setLat(position.getLat() + latChange);
        expected.setLng(position.getLng() + lngChange);
        String expectedValue = mapper.writeValueAsString(expected);
        assertEquals(expectedValue, result);

    }

    @Test
    public void nextPositionTest_InvalidPosition() throws JsonProcessingException {
        LngLat position = generateInvalidLngLat();
        double angle = generateRandomAngle();
        NextPositionRequest test = new NextPositionRequest();
        test.setStart(position);
        test.setAngle(angle);
        HttpStatusCode result = restController.nextPosition(mapper.writeValueAsString(test)).getStatusCode();
        assertEquals(HttpStatus.BAD_REQUEST, result);

    }
    @Test
    public void nextPositionTest_InvalidAngle() throws JsonProcessingException {
        LngLat position = generateRandomLngLat();
        double angle = generateInvalidAngle();
        NextPositionRequest test = new NextPositionRequest();
        test.setStart(position);
        test.setAngle(angle);
        HttpStatusCode result = restController.nextPosition(mapper.writeValueAsString(test)).getStatusCode();
        assertEquals(HttpStatus.BAD_REQUEST, result);

    }
    @Test
    public void nextPositionTest_MissingPosition() throws JsonProcessingException {
        double angle = generateRandomAngle();
        NextPositionRequest test = new NextPositionRequest();
        test.setAngle(angle);
        HttpStatusCode result = restController.nextPosition(mapper.writeValueAsString(test)).getStatusCode();
        assertEquals(HttpStatus.BAD_REQUEST, result);
    }

    @Test
    public void nextPositionTest_MissingAngle() throws JsonProcessingException {
        LngLat position = generateRandomLngLat();
        NextPositionRequest test = new NextPositionRequest();
        test.setStart(position);
        HttpStatusCode result = restController.nextPosition(mapper.writeValueAsString(test)).getStatusCode();
        assertEquals(HttpStatus.BAD_REQUEST, result);
    }
    @Test
    public void nextPositionTest_EmptyInput() throws JsonProcessingException {
        NextPositionRequest test = new NextPositionRequest();
        HttpStatusCode result = restController.nextPosition(mapper.writeValueAsString(test)).getStatusCode();
        assertEquals(HttpStatus.BAD_REQUEST, result);
    }
    @Test
    public void isInRegionTest_ValidInput() throws JsonProcessingException {
        NamedRegion region = generateRandomValidRegion();
        LngLat position = generateRandomLngLat();
        IsInRegionRequest test = new IsInRegionRequest();
        test.setRegion(region);
        test.setPosition(position);

        boolean result = Boolean.TRUE.equals(restController.isInRegion(mapper.writeValueAsString(test)).getBody());
        int numVertices = region.getVertices().size();
        double[] vertLng = new double[numVertices];
        double[] vertLat = new double[numVertices];
        for (int i = 0; i < numVertices; i++) {
            LngLat vertex = region.getVertices().get(i);
            vertLng[i] = vertex.getLng();
            vertLat[i] = vertex.getLat();
        }

        // Check if the point is inside using the isInPolygon function
        boolean isInside = GeometryFunctions.isInPolygon(numVertices, vertLng, vertLat, position.getLng(), position.getLat());
        assertEquals(isInside, result);

    }

    @Test
    public void isInRegionTest_OpenRegion() throws JsonProcessingException {
        NamedRegion region = generateRandomOpenRegion();
        LngLat position = generateRandomLngLat();
        IsInRegionRequest test = new IsInRegionRequest();
        test.setRegion(region);
        test.setPosition(position);

        HttpStatusCode result = restController.isInRegion(mapper.writeValueAsString(test)).getStatusCode();
        assertEquals(HttpStatus.BAD_REQUEST, result);

    }
    @Test
    public void isInRegionTest_SmallRegion() throws JsonProcessingException {
        NamedRegion region = generateRandomSmallRegion();
        LngLat position = generateRandomLngLat();
        IsInRegionRequest test = new IsInRegionRequest();
        test.setRegion(region);
        test.setPosition(position);

        HttpStatusCode result = restController.isInRegion(mapper.writeValueAsString(test)).getStatusCode();
        assertEquals(HttpStatus.BAD_REQUEST, result);
    }

    @Test
    public void isInRegionTest_InvalidRegion() throws JsonProcessingException {
        NamedRegion region = generateRandomInvalidRegion();
        LngLat position = generateRandomLngLat();
        IsInRegionRequest test = new IsInRegionRequest();
        test.setRegion(region);
        test.setPosition(position);

        HttpStatusCode result = restController.isInRegion(mapper.writeValueAsString(test)).getStatusCode();
        assertEquals(HttpStatus.BAD_REQUEST, result);
    }

    @Test
    public void isInRegionTest_InvalidPosition() throws JsonProcessingException {
        NamedRegion region = generateRandomValidRegion();
        LngLat position = generateInvalidLngLat();
        IsInRegionRequest test = new IsInRegionRequest();
        test.setRegion(region);
        test.setPosition(position);

        HttpStatusCode result = restController.isInRegion(mapper.writeValueAsString(test)).getStatusCode();
        assertEquals(HttpStatus.BAD_REQUEST, result);
    }

    private LngLat generateRandomLngLat(){
        Double lng = random.nextDouble(180-(-180))-180;
        Double lat = random.nextDouble(90-(-90))-90;
        LngLat test = new LngLat();
        test.setLng(lng);
        test.setLat(lat);
        return test;
    }
    private LngLat generateInvalidLngLat(){
        Double lng = random.nextDouble() * (450 - 200) + 200;
        Double lat = random.nextDouble() * (-90 - (-180)) - 90;
        LngLat test = new LngLat();
        test.setLng(lng);
        test.setLat(lat);
        return test;

    }
    private LngLatPair generateRandomLngLatPair() {
        LngLat pos1;
        pos1 = generateRandomLngLat();
        LngLat pos2;
        pos2 = generateRandomLngLat();
        LngLatPair test = new LngLatPair();
        test.setPos1(pos1);
        test.setPos2(pos2);
        return test;
    }

    private double generateRandomAngle(){
        return random.nextDouble()*360;
    }

    private double generateInvalidAngle() {

        double angle = random.nextDouble() * 720 - 360;

        if (angle < 0 || angle > 360) {
            return angle;
        } else {
            return angle + 360;
        }
    }

    private NamedRegion generateRandomValidRegion() {
        NamedRegion region = new NamedRegion();
        List<LngLat> vertices = new ArrayList<>();

        int numVertices = random.nextInt(4) + 3;
        for (int i = 0; i < numVertices - 1; i++) {
            LngLat vertex = generateRandomLngLat();
            vertices.add(vertex);
        }

        vertices.add(vertices.get(0));

        region.setVertices(vertices);
        return region;
    }

    private NamedRegion generateRandomOpenRegion() {
        NamedRegion region = new NamedRegion();
        List<LngLat> vertices = new ArrayList<>();

        int numVertices = random.nextInt(4) + 3;
        for (int i = 0; i < numVertices - 1; i++) {
            LngLat vertex = generateRandomLngLat();
            vertices.add(vertex);
        }

        region.setVertices(vertices);
        return region;
    }
    private NamedRegion generateRandomSmallRegion() {
        NamedRegion region = new NamedRegion();
        List<LngLat> vertices = new ArrayList<>();

        for (int i = 0; i < 2; i++) {
            LngLat vertex = generateRandomLngLat();
            vertices.add(vertex);
        }

        vertices.add(vertices.get(0));

        region.setVertices(vertices);
        return region;
    }
    private NamedRegion generateRandomInvalidRegion() {
        NamedRegion region = new NamedRegion();
        List<LngLat> vertices = new ArrayList<>();

        int numVertices = random.nextInt(4) + 3;
        for (int i = 0; i < numVertices - 1; i++) {
            LngLat vertex = generateInvalidLngLat();
            vertices.add(vertex);
        }

        vertices.add(vertices.get(0));

        region.setVertices(vertices);
        return region;
    }



}
