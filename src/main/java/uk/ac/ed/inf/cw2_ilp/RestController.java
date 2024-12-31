package uk.ac.ed.inf.cw2_ilp;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import uk.ac.ed.inf.cw2_ilp.dataTypes.*;
import com.fasterxml.jackson.databind.ObjectMapper;


import javax.swing.plaf.synth.Region;
import java.text.DecimalFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;

@org.springframework.web.bind.annotation.RestController
public class RestController {

    ObjectMapper mapper = new ObjectMapper().setDefaultPrettyPrinter(new DefaultPrettyPrinter());
    public Double MOVEMENT = 0.00015;
    public static final DecimalFormat DF = new DecimalFormat("0.000000000000000000");
    public String BASE_URL = "https://ilp-rest-2024.azurewebsites.net/";
    public String APPLETON_COORDINATES = "{\n" +
            "    \"lng\": -3.186874,\n" +
            "    \"lat\": 55.944494\n" +
            "}";


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
        if (isntValidString(lngLatPair)) {
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
            if (!isValidPosition(pos_1) || !isValidPosition(pos_2)) {
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
        if (isntValidString(Request)) {
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
            if (!isValidPosition(start)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
            }
            //checks that the angle is valid and if not returns 400
            if (!isValidAngle(angle)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
            }

            //runs calculateNewPosition on these inputs and turns the result to a string
            LngLat Position = calculateNewPos(start, angle);
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
        if (isntValidString(isInRegionRequest)) {
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
            if (!isValidPosition(point)|| !isValidRegion(region)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
            }

            //define arrays the size of the number of vertices
            int numVertices = region.getVertices().size();
            double[] vertLng = new double[numVertices];
            double[] vertLat = new double[numVertices];

            for (int i = 0; i < numVertices; i++) {
                LngLat vertex = region.getVertices().get(i);
                //check each vertex is a valid point, if not return 400
                if(!isValidPosition(vertex)){
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
                }
                //fill the arrays with the lng and lat values of each vertex respectively
                vertLng[i] = vertex.getLng();
                vertLat[i] = vertex.getLat();

            }

            //call isInPolygon on the new arrays and the given point and return the result
            inRegion = isInPolygon(numVertices, vertLng, vertLat, point.getLng(), point.getLat());
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
        if (isntValidString(orderRequest)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }

        //map input to order class
        currentOrder = mapper.readValue(orderRequest, Order.class);

        //set the code as undefined as we have not defined it
        currentOrder.setOrderValidationCode(OrderValidationResult.UNDEFINED);
        CreditCardInformation creditCardInformation = currentOrder.getCreditCardInformation();
        Pizza[] pizzas = currentOrder.getPizzasInOrder();

        //call method to check credit card information, if there is an error return this
        if(creditCardCheck(creditCardInformation, currentOrder) != OrderValidationResult.NO_ERROR){
            currentOrder.setOrderValidationCode(creditCardCheck(creditCardInformation, currentOrder));
            currentOrder.setOrderStatus(OrderStatus.INVALID);
            return ResponseEntity.ok(creditCardCheck(creditCardInformation, currentOrder));
        }
        //call method to check pizza and restaurant information, return the error if found
        if(pizzaCheck(pizzas, currentOrder) != OrderValidationResult.NO_ERROR){
            currentOrder.setOrderValidationCode(pizzaCheck(pizzas, currentOrder));
            currentOrder.setOrderStatus(OrderStatus.INVALID);
            return ResponseEntity.ok(pizzaCheck(pizzas, currentOrder));
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
        List<Restaurant> restaurants = fetchRestaurants();
        Restaurant restaurant = getRestaurantForPizza(pizzaName, restaurants);
        //find location of restaurant
        LngLat restaurantLocation = restaurant.getLocation();
        //find the location of appleton in LngLat format
        LngLat appletonLocation;
        appletonLocation = mapper.readValue(APPLETON_COORDINATES, LngLat.class);
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




    //check if a given string is invalid, returns true for invalid strings
    public boolean isntValidString(String input) {
        return input == null || input.isEmpty();
    }



    //checks if a given position is valid, returns true for valid positions
    private boolean isValidPosition(LngLat position) {
        if (position == null) {
            return false;
        }

        Double lat = position.getLat();
        Double lng = position.getLng();

        //if any of the points is outside the valid range return false
        return lat != null && !lat.isNaN() && lat >= -90 && lat <= 90 &&
                lng != null && !lng.isNaN() && lng >= -180 && lng <= 180;

    }

    //find the difference between two points
    private double getDistanceBetween(LngLat pos_1, LngLat pos_2){

        double latDiff = pos_1.getLat() - pos_2.getLat();
        double lngDiff = pos_1.getLng() - pos_2.getLng();

        //calculates and returns the Euclidean distance
        return Math.sqrt(Math.pow(latDiff, 2) + Math.pow(lngDiff, 2));
    }



    //calculates the next position given a start point and angle
    private LngLat calculateNewPos(LngLat start, Double angle) {
        LngLat position = new LngLat();
        position.setLat(start.getLat());
        position.setLng(start.getLng());


        //calculate hoe much the altitude and longitude have to change by using trig
        double latChange = MOVEMENT * Math.cos(Math.toRadians(angle));
        double lngChange = MOVEMENT * Math.sin(Math.toRadians(angle));

        //set the values to the values after the calculated change
        position.setLat(position.getLat() + latChange);
        position.setLng(position.getLng() + lngChange);
        return position;
    }

    //check the angle is valid, return true if angle is valid
    private boolean isValidAngle(Double checkAngle) {
        return (checkAngle != null && checkAngle >= 0 && checkAngle <= 360);
    }

    private boolean isInPolygon(int numVert, double[] vertLng, double[] vertLat, double pointLng, double pointLat) {
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
    private boolean isPointOnLine(double pointLng, double pointLat, double Lng1, double Lat1, double Lng2, double Lat2) {

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

    //checks to make sue the region provided is valid, returns true if so
    private boolean isValidRegion(NamedRegion region) {

        //checks that the region has at least 4 vertices (3 with the first and last being the same)
        int numVertices = region.getVertices().size();
        List<LngLat> vertices = region.getVertices();
        if (numVertices < 4) {
            return false;
        }
        //checks that the first and last vertex are the same, if not returns false
        if (!vertices.get(0).getLat().equals(vertices.get(vertices.size() - 1).getLat())
                || !vertices.get(0).getLng().equals(vertices.get(vertices.size() - 1).getLng())){
            return false;
        }
        return true;
    }

    //checks the credit card information to see if its valid
    private OrderValidationResult creditCardCheck (CreditCardInformation creditCardInformation, Order order) {
        //get the 3 aspects of the credit card information as strings
        String creditCardNumber = creditCardInformation.getCreditCardNumber();
        String CVV = creditCardInformation.getCvv();
        String expiryDate = creditCardInformation.getCreditCardExpiry();

        //Check if the CVV is valid and return CVV_INVALID if not
        if(CVV.length() != 3 || isntValidString(CVV) || !isDigitString(CVV)){
            return OrderValidationResult.CVV_INVALID;
        }
        //Check if the creditCardNumber is valid and return CARD_NUMBER_INVALID if not
        if(!isDigitString(creditCardNumber)|| isntValidString(creditCardNumber)|| creditCardNumber.length() != 16){
            return OrderValidationResult.CARD_NUMBER_INVALID;
        }
        //format the given date as MM/yy
        DateTimeFormatter expiryFormatter = DateTimeFormatter.ofPattern("MM/yy");
        YearMonth expiryYearMonth = YearMonth.parse(expiryDate, expiryFormatter);


        LocalDate expiry = expiryYearMonth.atEndOfMonth();

        LocalDate orderDate = LocalDate.parse(order.getDate());

        //if the expiry date is not after the order date return EXPIRY_DATE_INVALID
        if(!expiry.isAfter(orderDate)){
            return OrderValidationResult.EXPIRY_DATE_INVALID;
        }
        //if there are no issues return NO_ERROR
        return OrderValidationResult.NO_ERROR;
    }

    //check that all characters in a string are digits
    private boolean isDigitString (String input) {
        if (input.chars().allMatch(Character::isDigit)) {
            return true;
        } else {
            return false;
        }
    }

    //checks that elements associated with pizza and restaurants are valid or returns and updates the result
    private OrderValidationResult pizzaCheck (Pizza[] pizzas, Order currentOrder){
        int total = 0;
        //if there are more than 4 pizzas it is invalid
        if( pizzas.length>4){
            return OrderValidationResult.MAX_PIZZA_COUNT_EXCEEDED;
        }
        if(pizzas.length == 0 ){
            return OrderValidationResult.EMPTY_ORDER;
        }
        //add up the order total
        for (Pizza pizza : pizzas){
            total = total + pizza.priceInPence;
        }
        //add the delivery fee
        total += 100;
        // if the totals are not equal then return TOTAL_INCORRECT
        if(total != currentOrder.getPriceTotalInPence()){
            return OrderValidationResult.TOTAL_INCORRECT;
        }
        //create a set of valid pizzas
        Set<String> validPizzas = new HashSet<>();
        //fetch the list of restaurants from the rest service
        List<Restaurant> restaurants = fetchRestaurants();

        //add all valid pizzas to a set
        for (Restaurant restaurant : restaurants){
            for (Pizza menuItem : restaurant.menu) {
                validPizzas.add(menuItem.name);
            }
        }
        Restaurant firstRestaurant = null;

        for (Pizza pizza : pizzas) {
            //if any pizza is not in the valid pizza set return PIZZA_NOT_DEFINED
            if (!validPizzas.contains(pizza.name)) {
                return OrderValidationResult.PIZZA_NOT_DEFINED;
            }

            //using the name figure out which restaurant the pizza came from
            String name = pizza.getName();
            Restaurant restaurant = getRestaurantForPizza(name,restaurants);

            //get the menu for this restaurant
            Pizza[] menu = restaurant.getMenu();
            List <String> menuPizzas = Arrays.stream(menu).map(Pizza::getName).toList();
            //if this restaurant isnt open return RESTAURANT_CLOSED
            if(!isRestaurantOpen(restaurant, currentOrder.getDate())){
                return OrderValidationResult.RESTAURANT_CLOSED;
            }
            //if the price on the order doesnt match the menu then return PRICE_FOR_PIZZA_INVALID
            int index = menuPizzas.indexOf(pizza.getName());

            if(pizza.getPriceInPence() != menu[index].getPriceInPence()){
                return OrderValidationResult.PRICE_FOR_PIZZA_INVALID;
            }

            //if the restaurant doesn't match the pizza before return PIZZA_FROM_MULTIPLE_RESTAURANTS
            if (firstRestaurant == null) {
                firstRestaurant = restaurant;
            } else if (!restaurant.equals(firstRestaurant)) {
                return OrderValidationResult.PIZZA_FROM_MULTIPLE_RESTAURANTS;
            }


        }
        //else return NO_ERROR
        return OrderValidationResult.NO_ERROR;
    }
    //fetch all restaurants from the rest service
    private List<Restaurant> fetchRestaurants() {
        RestTemplate restTemplate = new RestTemplate();
        Restaurant[] restaurants = restTemplate.getForObject(BASE_URL + "restaurants", Restaurant[].class);
        assert restaurants != null;
        return List.of(restaurants);
    }
    //for a given pizza calculate which restaurant it came from
    private Restaurant getRestaurantForPizza(String pizzaName, List<Restaurant> restaurants) {
        for (Restaurant restaurant : restaurants) {
            for (Pizza pizza : restaurant.menu) {
                if (pizza.name.equalsIgnoreCase(pizzaName)) {
                    return restaurant;
                }
            }
        }
        return null;
    }

    //given a date calculate if the restaurant given is open
    private boolean isRestaurantOpen(Restaurant restaurant, String orderDate) {
        //calculate what day it is using the date
        LocalDate date = LocalDate.parse(orderDate);
        DayOfWeek day = date.getDayOfWeek();
        //return true if the day is in the list of opening days
        return restaurant.openingDays.contains(day);
    }

    //calculates a path using an A* algorithm
    private List<LngLat> calculatePath (LngLat startPos, LngLat endPos) throws JsonProcessingException {
       //retrieves no fly zones
        List<NamedRegion> noFlyZones = fetchNoFlyZones();
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
        start.setH(getDistanceBetween(startPos, endPos));

        //add start to open set
        openSet.add(start);

        //while the open set isnt empty
        while(!openSet.isEmpty()){
            //current node is the one with the lowest F
            Node current = openSet.poll();

            //return a list of neighbours of the current node
            List<Node> neighbours = getNeighbours(current, noFlyZones);

            closeGap = current.getH() > (30 * MOVEMENT);

            Node gapCloser = null;

            for (Node neighbour : neighbours) {
                //if the neighbour is close to the goal reconstruct the path and return it
                if (getDistanceBetween(neighbour.getPosition(), endPos) <0.00015){
                    List<LngLat> path = reconstructPath(neighbour);
                    return path;
                }
                else{
                    //calculate tentative g and h values
                    g = neighbour.getParent().getG()+ MOVEMENT;
                    h = getDistanceBetween(neighbour.getPosition(), endPos);
                    //set the G and F for the neighbour
                    neighbour.setG(current.getG() + MOVEMENT);
                    neighbour.setF(g,h);
                    neighbour.setH(h);

                    //skip the node if there exists one with the same position and lower F
                    if (isNodeSkipped(neighbour, openSet) || isNodeSkipped(neighbour, closedSet) || current.getH()<h){
                        continue;

                    }
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

    //retrieve noFlyZones from the website
    private List<NamedRegion> fetchNoFlyZones() {
        RestTemplate restTemplate = new RestTemplate();
        NamedRegion[] noFlyZones = restTemplate.getForObject(BASE_URL + "noFlyZones", NamedRegion[].class);
        assert noFlyZones != null;
        return List.of(noFlyZones);
    }

    //find if a given point is in a noFlyZone
    private boolean isInNoFlyZone(List<NamedRegion> noFlyZones, LngLat point) throws JsonProcessingException {
       //for each no fly zone use isInRegion to find if the point is located inside it
        for (NamedRegion noFlyZone : noFlyZones) {
            IsInRegionRequest request = new IsInRegionRequest();
            request.setRegion(noFlyZone);
            request.setPosition(point);

            String requestJson = mapper.writeValueAsString(request);

            if (isInRegion(requestJson).getBody()) {
                return true;
            }
        }

        return false;
    }

    //retrieve the central area
    private NamedRegion fetchCentralArea(){
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.getForObject(BASE_URL + "centralArea", NamedRegion.class);
    }

    //retrieve all valid neighbours for a node
    private List<Node> getNeighbours(Node current,List<NamedRegion> noFlyZones) throws JsonProcessingException {
       //initialise neighbours list
        List<Node> neighbours = new ArrayList<>();
        //find whether the initial node is in the central area
        boolean isInCentral = isInCentralArea(current.getPosition());
        //for each of the 16 points on a compass use next position to calculate the possible neighbours
        for (double angle = 0; angle < 360; angle += 22.5) {
            LngLat nextPosition = calculateNewPos(current.getPosition(),angle);
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
    private List<LngLat> reconstructPath ( Node current) {
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
    private boolean isNodeSkipped (Node neighbour, Collection<Node> list){
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

    //calculates whether a given position is within the central area
    private boolean isInCentralArea(LngLat position) throws JsonProcessingException {
        //fetches the central area and creates an IsInRegionRequest
        NamedRegion centralArea = fetchCentralArea();
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
