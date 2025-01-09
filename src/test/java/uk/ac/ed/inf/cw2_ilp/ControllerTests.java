package uk.ac.ed.inf.cw2_ilp;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import uk.ac.ed.inf.cw2_ilp.dataTypes.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


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
    public void distanceToTest_SameInput() throws JsonProcessingException {
        LngLat test = generateRandomLngLat();

        LngLatPair testPair = new LngLatPair();
        testPair.setPos1(test);
        testPair.setPos2(test);

        Double result = restController.getDistanceTo(mapper.writeValueAsString(testPair)).getBody();
        Double expected = 0.0;
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
    public void distanceToTest_EmptyCall() throws JsonProcessingException {
        LngLat test1 = new LngLat();
        LngLat test2 = new LngLat();
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
    public void isCloseToTest_EmptyCall() throws JsonProcessingException {
        LngLat test1 = new LngLat();
        LngLat test2 = new LngLat();
        LngLatPair testPair = new LngLatPair();
        testPair.setPos1(test1);
        testPair.setPos2(test2);

        HttpStatusCode result = restController.getDistanceTo(mapper.writeValueAsString(testPair)).getStatusCode();

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
    public void nextPositionTest_EdgeAngle() throws JsonProcessingException {
        LngLat position = generateRandomLngLat();
        double angle = 360;
        NextPositionRequest test = new NextPositionRequest();
        test.setStart(position);
        test.setAngle(angle);
        String result = restController.nextPosition(mapper.writeValueAsString(test)).getBody();
        HttpStatusCode code = restController.nextPosition(mapper.writeValueAsString(test)).getStatusCode();

        LngLat expected = new LngLat();

        expected.setLat(position.getLat() + Constants.MOVEMENT);
        expected.setLng(position.getLng());
        String expectedValue = mapper.writeValueAsString(expected);
        assertEquals(expectedValue, result);
        assertEquals(HttpStatus.OK, code);

    }
    @Test
    public void nextPositionTest_EdgePosition() throws JsonProcessingException {
        LngLat position = new LngLat();
        position.setLat(-90.0);
        position.setLng(180.0);
        double angle = generateRandomAngle();
        NextPositionRequest test = new NextPositionRequest();
        test.setStart(position);
        test.setAngle(angle);
        String result = restController.nextPosition(mapper.writeValueAsString(test)).getBody();
        HttpStatusCode code = restController.nextPosition(mapper.writeValueAsString(test)).getStatusCode();

        LngLat expected = new LngLat();
        double latChange = Constants.MOVEMENT * Math.cos(Math.toRadians(angle));
        double lngChange = Constants.MOVEMENT * Math.sin(Math.toRadians(angle));

        expected.setLat(position.getLat() + latChange);
        expected.setLng(position.getLng() + lngChange);
        String expectedValue = mapper.writeValueAsString(expected);
        assertEquals(expectedValue, result);
        assertEquals(HttpStatus.OK, code);

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
    public void nextPositionTest_EmptyCall() throws JsonProcessingException {
        LngLat position = new LngLat();
        NextPositionRequest test = new NextPositionRequest();
        test.setStart(position);
        HttpStatusCode result = restController.nextPosition(mapper.writeValueAsString(test)).getStatusCode();
        assertEquals(HttpStatus.BAD_REQUEST, result);
    }
    @Test
    public void nextPositionTest_EmptyInput() throws JsonProcessingException {
        HttpStatusCode result = restController.nextPosition("").getStatusCode();
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
    public void isInRegionTest_ValidInside() throws JsonProcessingException {
        NamedRegion region = new NamedRegion();
        LngLat vertex1 = new LngLat();
        vertex1.setLat(-5.0);
        vertex1.setLng(-5.0);
        LngLat vertex2 = new LngLat();
        vertex2.setLat(5.0);
        vertex2.setLng(-5.0);
        LngLat vertex3 = new LngLat();
        vertex3.setLat(5.0);
        vertex3.setLng(5.0);
        LngLat vertex4 = new LngLat();
        vertex4.setLat(-5.0);
        vertex4.setLng(5.0);
        region.setVertices(Arrays.asList(vertex1, vertex2, vertex3, vertex4, vertex1));

        LngLat position = new LngLat();
        position.setLat(0.0);
        position.setLng(0.0);

        IsInRegionRequest test = new IsInRegionRequest();
        test.setRegion(region);
        test.setPosition(position);

        boolean result = Boolean.TRUE.equals(restController.isInRegion(mapper.writeValueAsString(test)).getBody());

        assertEquals(result, Boolean.TRUE);

    }

    @Test
    public void isInRegionTest_ValidOutside() throws JsonProcessingException {
        NamedRegion region = new NamedRegion();
        LngLat vertex1 = new LngLat();
        vertex1.setLat(-5.0);
        vertex1.setLng(-5.0);
        LngLat vertex2 = new LngLat();
        vertex2.setLat(5.0);
        vertex2.setLng(-5.0);
        LngLat vertex3 = new LngLat();
        vertex3.setLat(5.0);
        vertex3.setLng(5.0);
        LngLat vertex4 = new LngLat();
        vertex4.setLat(-5.0);
        vertex4.setLng(5.0);
        region.setVertices(Arrays.asList(vertex1, vertex2, vertex3, vertex4, vertex1));

        LngLat position = new LngLat();
        position.setLat(10.0);
        position.setLng(10.0);

        IsInRegionRequest test = new IsInRegionRequest();
        test.setRegion(region);
        test.setPosition(position);

        boolean result = Boolean.TRUE.equals(restController.isInRegion(mapper.writeValueAsString(test)).getBody());

        assertEquals(result, Boolean.FALSE);

    }

    @Test
    public void isInRegionTest_ValidEdge() throws JsonProcessingException {
        NamedRegion region = new NamedRegion();
        LngLat vertex1 = new LngLat();
        vertex1.setLat(-5.0);
        vertex1.setLng(-5.0);
        LngLat vertex2 = new LngLat();
        vertex2.setLat(5.0);
        vertex2.setLng(-5.0);
        LngLat vertex3 = new LngLat();
        vertex3.setLat(5.0);
        vertex3.setLng(5.0);
        LngLat vertex4 = new LngLat();
        vertex4.setLat(-5.0);
        vertex4.setLng(5.0);
        region.setVertices(Arrays.asList(vertex1, vertex2, vertex3, vertex4, vertex1));

        LngLat position = new LngLat();
        position.setLat(2.0);
        position.setLng(-2.0);

        IsInRegionRequest test = new IsInRegionRequest();
        test.setRegion(region);
        test.setPosition(position);

        boolean result = Boolean.TRUE.equals(restController.isInRegion(mapper.writeValueAsString(test)).getBody());

        assertEquals(result, Boolean.TRUE);

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

    @Test
    public void isInRegionTest_MissingPosition() throws JsonProcessingException {
        NamedRegion region = generateRandomValidRegion();
        LngLat position = new LngLat();
        IsInRegionRequest test = new IsInRegionRequest();
        test.setRegion(region);
        test.setPosition(position);

        HttpStatusCode result = restController.isInRegion(mapper.writeValueAsString(test)).getStatusCode();
        assertEquals(HttpStatus.BAD_REQUEST, result);
    }

    @Test
    public void isInRegionTest_MissingRegion() throws JsonProcessingException {
        NamedRegion region = new NamedRegion();
        LngLat position = generateRandomLngLat();
        IsInRegionRequest test = new IsInRegionRequest();
        test.setRegion(region);
        test.setPosition(position);

        HttpStatusCode result = restController.isInRegion(mapper.writeValueAsString(test)).getStatusCode();
        assertEquals(HttpStatus.BAD_REQUEST, result);
    }

    @Test
    public void isInRegionTest_EmptyCall() throws JsonProcessingException {
        NamedRegion region = new NamedRegion();
        LngLat position = new LngLat();
        IsInRegionRequest test = new IsInRegionRequest();
        test.setRegion(region);
        test.setPosition(position);

        HttpStatusCode result = restController.isInRegion(mapper.writeValueAsString(test)).getStatusCode();
        assertEquals(HttpStatus.BAD_REQUEST, result);
    }

    @Test
    public void isInRegionTest_EmptyString() throws JsonProcessingException {

        HttpStatusCode result = restController.isInRegion("").getStatusCode();
        assertEquals(HttpStatus.BAD_REQUEST, result);
    }

    @Test
    public void calculatePath() throws JsonProcessingException {
        List <NamedRegion> noFlyZones = FetchFunctions.fetchNoFlyZones();
        LngLat start = generateEdiLngLat();
        LngLat end = generateEdiLngLat();
        boolean valid = true;

        List<LngLat> path = restController.calculatePath(start, end);

        for(LngLat point: path){
            if(restController.isInNoFlyZone(noFlyZones, point)){
                valid = false;
            }
        }
        assertTrue(valid);
    }

    @Test
    public void generateNeighboursTest() throws JsonProcessingException {
        List <NamedRegion> noFlyZones = FetchFunctions.fetchNoFlyZones();
        Node test  = new Node();
        test.setPosition(generateEdiLngLat());
        boolean result = true;

        List<Node> neighbours = restController.getNeighbours(test,noFlyZones);
        for(Node neighbour: neighbours){
            if(restController.isInNoFlyZone(noFlyZones, neighbour.getPosition())){
                result = false;
            }
            if(!Validation.isValidPosition(neighbour.getPosition())){
                result = false;
            }
        }
        assertTrue(result);
    }

    @Test
    public void isNodeSkippedTest_IsSkipped() throws JsonProcessingException {
        Node neighbour = new Node();
        LngLat point = new LngLat();
        point.setLng(-3.186874);
        point.setLat(55.944494);
        neighbour.setPosition(point); // Example coordinates
        neighbour.setF(10, 10);

        Node existingNode = new Node();
        existingNode.setPosition(point);
        existingNode.setF(5, 5);

        List<Node> nodeList = new ArrayList<>();
        nodeList.add(existingNode);

        assertTrue(restController.isNodeSkipped(neighbour, nodeList));
    }

    @Test
    public void isNodeSkippedTest_HigherF() throws JsonProcessingException {
        Node neighbour = new Node();
        LngLat point = new LngLat();
        point.setLng(-3.186874);
        point.setLat(55.944494);
        neighbour.setPosition(point); // Example coordinates
        neighbour.setF(10, 10);

        Node existingNode = new Node();
        existingNode.setPosition(point);
        existingNode.setF(20, 20);

        List<Node> nodeList = new ArrayList<>();
        nodeList.add(existingNode);

        assertFalse(restController.isNodeSkipped(neighbour, nodeList));
    }

    @Test
    public void isNodeSkippedTest_DifferentPos() throws JsonProcessingException {
        Node neighbour = new Node();
        LngLat point = new LngLat();
        point.setLng(-3.186874);
        point.setLat(55.944494);
        neighbour.setPosition(point); // Example coordinates
        neighbour.setF(10, 10);

        Node existingNode = new Node();
        LngLat pos = new LngLat();
        pos.setLng(-3.186734);
        pos.setLat(55.944454);
        existingNode.setPosition(pos);
        existingNode.setF(10, 10);

        List<Node> nodeList = new ArrayList<>();
        nodeList.add(existingNode);

        assertFalse(restController.isNodeSkipped(neighbour, nodeList));
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

    private LngLat generateEdiLngLat(){
        double centerLng = -3.186874;
        double centerLat = 55.944494;

        double lng = centerLng + (random.nextDouble() * 0.10 - 0.05);
        double lat = centerLat + (random.nextDouble() * 0.10 - 0.05);
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
