package uk.ac.ed.inf.cw2_ilp;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import uk.ac.ed.inf.cw2_ilp.dataTypes.*;
import com.fasterxml.jackson.databind.ObjectMapper;


import java.text.DecimalFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@org.springframework.web.bind.annotation.RestController
public class RestController {

    ObjectMapper mapper = new ObjectMapper().setDefaultPrettyPrinter(new DefaultPrettyPrinter());
    public Double MOVEMENT = 0.00015;
    public static final DecimalFormat DF = new DecimalFormat("0.000000");
    public String BASE_URL = "https://ilp-rest-2024.azurewebsites.net/";


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

    @PostMapping("/validateOrder")
    public ResponseEntity<OrderValidationResult> validateOrder(@RequestBody String orderRequest) throws JsonProcessingException {
        Order currentOrder;
        if (isntValidString(orderRequest)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        currentOrder = mapper.readValue(orderRequest, Order.class);
        currentOrder.setOrderValidationCode(OrderValidationResult.UNDEFINED);
        CreditCardInformation creditCardInformation = currentOrder.getCreditCardInformation();
        Pizza[] pizzas = currentOrder.getPizzasInOrder();

        if(creditCardCheck(creditCardInformation, currentOrder) != OrderValidationResult.NO_ERROR){
            return ResponseEntity.ok(creditCardCheck(creditCardInformation, currentOrder));
        }
        if(pizzaCheck(pizzas, currentOrder) != OrderValidationResult.NO_ERROR){
            return ResponseEntity.ok(pizzaCheck(pizzas, currentOrder));
        }

        return ResponseEntity.ok(OrderValidationResult.NO_ERROR);

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



    //calculates the next position given a start point and angle
    private LngLat calculateNewPos(LngLat start, Double angle) {


        //calculate hoe much the altitude and longitude have to change by using trig
        double latChange = MOVEMENT * Math.cos(Math.toRadians(angle));
        double lngChange = MOVEMENT * Math.sin(Math.toRadians(angle));

        //set the values to the values after the calculated change
        start.setLat(Double.parseDouble(DF.format(start.getLat() + latChange)));
        start.setLng(Double.parseDouble(DF.format(start.getLng() + lngChange)));
        return start;
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

    private OrderValidationResult creditCardCheck (CreditCardInformation creditCardInformation, Order order) {
        String creditCardNumber = creditCardInformation.getCreditCardNumber();
        String CVV = creditCardInformation.getCvv();
        String expiryDate = creditCardInformation.getCreditCardExpiry();

        if(CVV.length() != 3 || isntValidString(CVV) || !isDigitString(CVV)){
            return OrderValidationResult.CVV_INVALID;
        }
        if(!isDigitString(creditCardNumber)|| isntValidString(creditCardNumber)|| creditCardNumber.length() != 16){
            return OrderValidationResult.CARD_NUMBER_INVALID;
        }
        DateTimeFormatter expiryFormatter = DateTimeFormatter.ofPattern("MM/yy");
        YearMonth expiryYearMonth = YearMonth.parse(expiryDate, expiryFormatter);


        LocalDate expiry = expiryYearMonth.atEndOfMonth();

        LocalDate orderDate = LocalDate.parse(order.getDate());

        if(!expiry.isAfter(orderDate)){
            return OrderValidationResult.EXPIRY_DATE_INVALID;
        }

        return OrderValidationResult.NO_ERROR;
    }

    private boolean isDigitString (String input) {
        if (input.chars().allMatch(Character::isDigit)) {
            return true;
        } else {
            return false;
        }
    }
    private OrderValidationResult pizzaCheck (Pizza[] pizzas, Order currentOrder){
        int total = 0;
        if( pizzas.length>4){
            return OrderValidationResult.MAX_PIZZA_COUNT_EXCEEDED;
        }
        for (Pizza pizza : pizzas){
            total = total + pizza.priceInPence;
        }
        total += 100;
        if(total != currentOrder.getPriceTotalInPence()){
            return OrderValidationResult.TOTAL_INCORRECT;
        }
        Set<String> validPizzas = new HashSet<>();
        List<Restaurant> restaurants = fetchRestaurants();

        for (Restaurant restaurant : restaurants){
            for (Pizza menuItem : restaurant.menu) {
                validPizzas.add(menuItem.name);
            }
        }
        Restaurant firstRestaurant = null;
        for (Pizza pizza : pizzas) {
            if (!validPizzas.contains(pizza.name)) {
                return OrderValidationResult.PIZZA_NOT_DEFINED;
            }
            String name = pizza.getName();
            Restaurant restaurant = getRestaurantForPizza(name,restaurants);
            if(!isRestaurantOpen(restaurant, currentOrder.getDate())){
                return OrderValidationResult.RESTAURANT_CLOSED;
            }
            if (firstRestaurant == null) {
                firstRestaurant = restaurant; // Set the first restaurant
            } else if (!restaurant.equals(firstRestaurant)) {
                return OrderValidationResult.PIZZA_FROM_MULTIPLE_RESTAURANTS;
            }


        }
        return OrderValidationResult.NO_ERROR;
    }
    private List<Restaurant> fetchRestaurants() {
        RestTemplate restTemplate = new RestTemplate();
        Restaurant[] restaurants = restTemplate.getForObject(BASE_URL + "restaurants", Restaurant[].class);
        assert restaurants != null;
        return List.of(restaurants);
    }
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

    private boolean isRestaurantOpen(Restaurant restaurant, String orderDate) {
        LocalDate date = LocalDate.parse(orderDate);
        DayOfWeek day = date.getDayOfWeek();
        return restaurant.openingDays.contains(day);
    }


}
