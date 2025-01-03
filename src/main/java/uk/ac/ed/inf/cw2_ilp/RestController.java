package uk.ac.ed.inf.cw2_ilp;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import uk.ac.ed.inf.cw2_ilp.dataTypes.*;
import uk.ac.ed.inf.cw2_ilp.Validation.*;
import uk.ac.ed.inf.cw2_ilp.GeometryFunctions;
import uk.ac.ed.inf.cw2_ilp.Constants;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.*;


@org.springframework.web.bind.annotation.RestController
public class RestController {

    ObjectMapper mapper = new ObjectMapper().setDefaultPrettyPrinter(new DefaultPrettyPrinter());

    // returns my student ID as a string
    @GetMapping("/uuid")
    public ResponseEntity<String> getUuid() {
        return ResponseEntity.ok("s2282430");
    }

    //returns the Euclidean distance between two points as a Double
    @PostMapping("/distanceTo")
    public ResponseEntity<Double> getDistanceTo(@RequestBody String lngLatPair) throws JsonProcessingException {

        lngLatPair LngLats;
        Double distance;

        //checks input isn't empty
        if (Validation.isntValidString(lngLatPair)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }

        try {
            //maps input to LngLat class
            LngLats = mapper.readValue(lngLatPair, lngLatPair.class);
            LngLat pos_1 = LngLats.getPos1();
            LngLat pos_2 = LngLats.getPos2();

            //checks if either position is null if so returns 400
            if (pos_1 == null || pos_2 == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
            }

            //checks if they are valid positions, if not returns 400
            if (!Validation.isValidPosition(pos_1) || !Validation.isValidPosition(pos_2)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
            }

            //calculates difference between the latitudes and longitudes
            double latDiff = pos_1.getLat() - pos_2.getLat();
            double lngDiff = pos_1.getLng() - pos_2.getLng();

            //calculates and returns the Euclidean distance
            distance = Math.sqrt(Math.pow(latDiff, 2) + Math.pow(lngDiff, 2));
            return ResponseEntity.ok(distance);
        } catch (JsonProcessingException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }

    }
    //calculates if two given points are within 0.00015  of one another and returns a Boolean
    @PostMapping("/isCloseTo")
    public ResponseEntity<Boolean> isCloseTo(@RequestBody String lngLatPair) throws JsonProcessingException {
        boolean Close;

        //calls getDistanceTo on the input and returns only the Double distance
        Double distance = getDistanceTo(lngLatPair).getBody();

        if (distance == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        } else {

            //checks if the distance calculated is within the accepted distance of 0.00015
            Close = distance < 0.00015;
            return ResponseEntity.ok(Close);
        }
    }

    //calculates the next position from an angle and a position and returns a String
    @PostMapping("/nextPosition")
    public ResponseEntity<String> nextPosition(@RequestBody String Request) throws JsonProcessingException {

        NextPositionRequest startPos;

        //checks if the string isn't valid and returns 400 if so
        if (Validation.isntValidString(Request)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        } else {
            try {
                //maps the input string to the class NextPositionRequest
                startPos = mapper.readValue(Request, NextPositionRequest.class);


            } catch (JsonProcessingException e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
            }
            //get the start position and angle ready for calculations
            LngLat start = startPos.getStart();
            Double angle = startPos.getAngle();

            //checks that start is a valid position and if not returns 400
            if (!Validation.isValidPosition(start)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
            }
            //checks that the angle is valid and if not returns 400
            if (!Validation.isValidAngle(angle)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
            }

            //runs calculateNewPosition on these inputs and turns the result to a string
            LngLat Position = GeometryFunctions.calculateNewPos(start, angle);
            String nextPosition = mapper.writeValueAsString(Position);


            return ResponseEntity.ok(nextPosition);
        }
    }
    //calculates whether a given point is in a region and returns as a Boolean
    @PostMapping("/isInRegion")
    public ResponseEntity<Boolean> isInRegion(@RequestBody String isInRegionRequest) throws JsonProcessingException {
        IsInRegionRequest regionRequest;

        boolean inRegion;


        //Checks if the input isn't valid and if so returns 400
        if (Validation.isntValidString(isInRegionRequest)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }


        try {
            //maps the input String to the class IsInRegionRequest and sets the region and point
            regionRequest = mapper.readValue(isInRegionRequest, IsInRegionRequest.class);
            NamedRegion region = regionRequest.getRegion();
            LngLat point = regionRequest.getPosition();

            //check that the region and points are valid and if not return 400
            if (region == null || point == null || region.getVertices() == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
            }
            if (!Validation.isValidPosition(point)|| !Validation.isValidRegion(region)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
            }

            //define arrays the size of the number of vertices
            int numVertices = region.getVertices().size();
            double[] vertLng = new double[numVertices];
            double[] vertLat = new double[numVertices];

            for (int i = 0; i < numVertices; i++) {
                LngLat vertex = region.getVertices().get(i);
                //check each vertex is a valid point, if not return 400
                if(!Validation.isValidPosition(vertex)){
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
                }
                //fill the arrays with the lng and lat values of each vertex respectively
                vertLng[i] = vertex.getLng();
                vertLat[i] = vertex.getLat();

            }

            //call isInPolygon on the new arrays and the given point and return the result
            inRegion = GeometryFunctions.isInPolygon(numVertices, vertLng, vertLat, point.getLng(), point.getLat());
            return ResponseEntity.ok(inRegion);

        } catch (JsonProcessingException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

   // calculates whether the order is valid, updates OrderValidationResult and returns it
    @PostMapping("/validateOrder")
    public ResponseEntity<OrderValidationResult> validateOrder(@RequestBody String orderRequest) throws JsonProcessingException {
        Order currentOrder;

        //If the order isn't a valid string returns an error
        if (Validation.isntValidString(orderRequest)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }

        //map input to order class
        currentOrder = mapper.readValue(orderRequest, Order.class);

        //set the code as undefined as we have not defined it
        currentOrder.setOrderValidationCode(OrderValidationResult.UNDEFINED);
        CreditCardInformation creditCardInformation = currentOrder.getCreditCardInformation();
        Pizza[] pizzas = currentOrder.getPizzasInOrder();

        //call method to check credit card information, if there is an error return this
        if(Validation.creditCardCheck(creditCardInformation, currentOrder) != OrderValidationResult.NO_ERROR){
            currentOrder.setOrderValidationCode(Validation.creditCardCheck(creditCardInformation, currentOrder));
            currentOrder.setOrderStatus(OrderStatus.INVALID);
            return ResponseEntity.ok(Validation.creditCardCheck(creditCardInformation, currentOrder));
        }
        //call method to check pizza and restaurant information, return the error if found
        if(Validation.pizzaCheck(pizzas, currentOrder) != OrderValidationResult.NO_ERROR){
            currentOrder.setOrderValidationCode(Validation.pizzaCheck(pizzas, currentOrder));
            currentOrder.setOrderStatus(OrderStatus.INVALID);
            return ResponseEntity.ok(Validation.pizzaCheck(pizzas, currentOrder));
        }
        // if no error has been found, return no error
        currentOrder.setOrderValidationCode(OrderValidationResult.NO_ERROR);
        currentOrder.setOrderStatus(OrderStatus.VALID);
        return ResponseEntity.ok(OrderValidationResult.NO_ERROR);

    }
    //
    @PostMapping("calcDeliveryPath")
    public ResponseEntity<LngLat[]> calcDeliveryPath(@RequestBody String orderRequest) throws JsonProcessingException {
        Order currentOrder;
        // validate order, if invalid then return an error
        OrderValidationResult validation = validateOrder(orderRequest).getBody();
        if(!(validation == OrderValidationResult.NO_ERROR)){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        //map order to order class
        currentOrder = mapper.readValue(orderRequest, Order.class);
        //use pizzas to find the restaurant
        Pizza[] pizzas = currentOrder.getPizzasInOrder();
        String pizzaName = pizzas[0].getName();
        List<Restaurant> restaurants = FetchFunctions.fetchRestaurants();
        Restaurant restaurant = FetchFunctions.getRestaurantForPizza(pizzaName, restaurants);
        //find location of restaurant
        LngLat restaurantLocation = restaurant.getLocation();
        //find the location of appleton in LngLat format
        LngLat appletonLocation;
        appletonLocation = mapper.readValue(Constants.APPLETON_COORDINATES, LngLat.class);
        //use calculate path to find the path
        List<LngLat> pathList = calculatePath(restaurantLocation,appletonLocation);
        if (pathList ==null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        //format this as an array and return
        LngLat[] path = pathList.toArray(new LngLat[0]);


        return ResponseEntity.ok(path);
    }

    @PostMapping("calcDeliveryPathAsGeoJson")
    public ResponseEntity<String> calcDeliveryPathAsGeoJson(@RequestBody String orderRequest) throws JsonProcessingException {
       //calculate the path using calcdeliverypath
        LngLat[] path = calcDeliveryPath(orderRequest).getBody();

        //if there isn't a path return error
        if (path ==null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        // initialise variables
        List<double[]> coordinates = new ArrayList<>();
        LngLat previous = null;

        //for each unique point on the path add it to the list of coordinates
        for (LngLat point : path) {
            if (!point.equals(previous)) {
                coordinates.add(new double[]{point.getLng(), point.getLat()});
                previous = point;
            }
        }

        //create and fill hash maps needed for geojson format
        Map<String, Object> geoJson = new LinkedHashMap<>();
        geoJson.put("type", "Feature");

        Map<String, Object> geometry = new LinkedHashMap<>();
        geometry.put("type", "LineString");
        //add the points from the path to geometry
        geometry.put("coordinates", coordinates);

        //add all of geometry into geoJson
        geoJson.put("geometry", geometry);

        //properties is empty for this style
        geoJson.put("properties", new LinkedHashMap<>());

        return ResponseEntity.ok(mapper.writeValueAsString(geoJson));
    }

    //calculates a path using an A* algorithm
    public  List<LngLat> calculatePath(LngLat startPos, LngLat endPos) throws JsonProcessingException {
        //retrieves no fly zones
        List<NamedRegion> noFlyZones = FetchFunctions.fetchNoFlyZones();
        //initialise variables
        Set<Node> closedSet = new HashSet<>() {};
        PriorityQueue<Node> openSet = new PriorityQueue<>(Comparator.comparingDouble(Node::getF));

        double h;
        double g;
        boolean closeGap;

        //initialise start node
        Node start = new Node();
        start.setPosition(startPos);
        start.setG(0);
        start.setF(0,0);
        start.setH(GeometryFunctions.getDistanceBetween(startPos, endPos));

        //add start to open set
        openSet.add(start);

        //while the open set isnt empty
        while(!openSet.isEmpty()){
            //current node is the one with the lowest F
            Node current = openSet.poll();

            //return a list of neighbours of the current node
            List<Node> neighbours = getNeighbours(current, noFlyZones);

            //if the gap is too large and will take too long set closeGap to true
            closeGap = current.getH() > (30 * Constants.MOVEMENT);

            Node gapCloser = null;

            for (Node neighbour : neighbours) {
                //if the neighbour is close to the goal reconstruct the path and return it
                if (GeometryFunctions.getDistanceBetween(neighbour.getPosition(), endPos) <0.00015){
                    List<LngLat> path = reconstructPath(neighbour);
                    return path;
                }
                else{
                    //calculate tentative g and h values
                    g = neighbour.getParent().getG()+ Constants.MOVEMENT;
                    h = GeometryFunctions.getDistanceBetween(neighbour.getPosition(), endPos);
                    //set the G and F for the neighbour
                    neighbour.setG(current.getG() + Constants.MOVEMENT);
                    neighbour.setF(g,h);
                    neighbour.setH(h);

                    //skip the node if there exists one with the same position and lower F
                    if (isNodeSkipped(neighbour, openSet) || isNodeSkipped(neighbour, closedSet) || current.getH()<h){
                        continue;

                    }
                    //if the gap was too large add a gapCloser node to speed things up
                    if (closeGap) {
                        if (gapCloser == null || h < gapCloser.getH()) {
                            gapCloser = neighbour;
                        }
                    } else {
                        // Regular node expansion
                        openSet.add(neighbour);
                    }
                }
            }
            if (closeGap && gapCloser != null) {
                openSet.add(gapCloser);
            }
            //the current node was dealt with and can be added to closedSet
            closedSet.add(current);

        }

        //if no path is found return null
        return null;
    }

    //retrieve all valid neighbours for a node
    private  List<Node> getNeighbours(Node current,List<NamedRegion> noFlyZones) throws JsonProcessingException {
        //initialise neighbours list
        List<Node> neighbours = new ArrayList<>();
        //find whether the initial node is in the central area
        boolean isInCentral = isInCentralArea(current.getPosition());
        //for each of the 16 points on a compass use next position to calculate the possible neighbours
        for (double angle = 0; angle < 360; angle += 22.5) {
            LngLat nextPosition = GeometryFunctions.calculateNewPos(current.getPosition(),angle);
            //if the old point was in the central area and the new one isnt then skip
            if (isInCentral && !isInCentralArea(nextPosition)) {
                continue;
            }
            //if the node isnt in a noFyZone then add it to the list with the parent as current
            if (!isInNoFlyZone(noFlyZones, nextPosition)) {
                Node neighbour = new Node();
                neighbour.setParent(current);
                neighbour.setPosition(nextPosition);
                neighbours.add(neighbour);
            };
        }

        return neighbours;
    }

    //reconstructs the path given a node
    public  List<LngLat> reconstructPath(Node current) {
        List<LngLat> path = new ArrayList<>();
        //for each element in the list add it to the list and then set current to its parent
        while (current != null) {
            path.add(current.getPosition());
            current = current.getParent();
        }
        //reverse the path and return
        Collections.reverse(path);
        return path;
    }

    //calculate whether a given node should be skipped
    public  boolean isNodeSkipped(Node neighbour, Collection<Node> list){
        for (Node node : list) {
            LngLat position = node.getPosition();
            //if this position exists with a lower F it should be skipped and true is returned
            if (position.getLat().equals(neighbour.getPosition().getLat()) && position.getLng().equals(neighbour.getPosition().getLng())
                    && node.getF() <= neighbour.getF()) {
                return true;
            }
        }
        return false;
    }
    //find if a given point is in a noFlyZone
    public boolean isInNoFlyZone(List<NamedRegion> noFlyZones, LngLat point) throws JsonProcessingException {
        //for each no fly zone use isInRegion to find if the point is located inside it
        for (NamedRegion noFlyZone : noFlyZones) {
            IsInRegionRequest request = new IsInRegionRequest();
            request.setRegion(noFlyZone);
            request.setPosition(point);

            String requestJson = mapper.writeValueAsString(request);

            ResponseEntity<Boolean> response = isInRegion(requestJson);
            if (Boolean.TRUE.equals(response.getBody())) {
                return true;
            }
        }

        return false;
    }

    //calculates whether a given position is within the central area
    public boolean isInCentralArea(LngLat position) throws JsonProcessingException {
        //fetches the central area and creates an IsInRegionRequest
        NamedRegion centralArea = FetchFunctions.fetchCentralArea();
        IsInRegionRequest request = new IsInRegionRequest();
        request.setRegion(centralArea);
        request.setPosition(position);

        String requestJson = mapper.writeValueAsString(request);

        //utilise isInRegion to find if the position is in the central area
        if (Boolean.TRUE.equals(isInRegion(requestJson).getBody())) {
            return true;
        }

        return false;
    }















}
