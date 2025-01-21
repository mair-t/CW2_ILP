package uk.ac.ed.inf.cw2_ilp;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

import org.springframework.http.ResponseEntity;

import static org.mockito.Mockito.*;


import uk.ac.ed.inf.cw2_ilp.dataTypes.*;

import java.time.DayOfWeek;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


import static org.junit.jupiter.api.Assertions.*;


@ExtendWith(MockitoExtension.class)
public class MockedTests {

    @InjectMocks
    private RestController validationController;


    @Mock
    private ObjectMapper objectMapper;
    Random random = new Random();

    @Test
    void testInvalidOrderString() throws JsonProcessingException {
        String invalidOrderRequest = null;

        HttpStatusCode response = validationController.validateOrder(invalidOrderRequest).getStatusCode();


        assertEquals(HttpStatus.BAD_REQUEST, response);
    }

    @Test
    void testInvalidCVV() throws JsonProcessingException {
        String validOrderRequest = "{\"creditCardInformation\": {\"creditCardNumber\": \"1234567812345678\", \"cvv\": \"12\", \"creditCardExpiry\": \"12/23\"}, \"pizzasInOrder\": [], \"priceTotalInPence\": 0}";
        Order mockOrder = new Order();
        CreditCardInformation mockCreditCardInfo = new CreditCardInformation();
        mockCreditCardInfo.setCreditCardNumber("1234567812345678");
        mockCreditCardInfo.setCvv("12");
        mockCreditCardInfo.setCreditCardExpiry("12/23");

        when(objectMapper.readValue(validOrderRequest, Order.class)).thenReturn(mockOrder);
        mockOrder.setCreditCardInformation(mockCreditCardInfo);

        ResponseEntity<OrderValidationResult> response = validationController.validateOrder(validOrderRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(OrderValidationCode.CVV_INVALID, response.getBody().getOrderValidationCode());
        assertEquals(OrderStatus.INVALID, response.getBody().getOrderStatus());
    }

    @Test
    void testInvalidCardNumber() throws JsonProcessingException {
        String validOrderRequest = "{\"creditCardInformation\": {\"creditCardNumber\": \"1234567812345678\", \"cvv\": \"12\", \"creditCardExpiry\": \"12/23\"}, \"pizzasInOrder\": [], \"priceTotalInPence\": 0}";
        Order mockOrder = new Order();
        CreditCardInformation mockCreditCardInfo = new CreditCardInformation();
        mockCreditCardInfo.setCreditCardNumber("12345678123456");
        mockCreditCardInfo.setCvv("123");
        mockCreditCardInfo.setCreditCardExpiry("12/23");

        when(objectMapper.readValue(validOrderRequest, Order.class)).thenReturn(mockOrder);
        mockOrder.setCreditCardInformation(mockCreditCardInfo);

        ResponseEntity<OrderValidationResult> response = validationController.validateOrder(validOrderRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(OrderValidationCode.CARD_NUMBER_INVALID, response.getBody().getOrderValidationCode());
        assertEquals(OrderStatus.INVALID, response.getBody().getOrderStatus());
    }

    @Test
    void testInvalidExpiryDate() throws JsonProcessingException {
        String validOrderRequest = "{\"creditCardInformation\": {\"creditCardNumber\": \"1234567812345678\", \"cvv\": \"12\", \"creditCardExpiry\": \"12/23\"}, \"pizzasInOrder\": [], \"priceTotalInPence\": 0}";
        Order mockOrder = new Order();
        CreditCardInformation mockCreditCardInfo = new CreditCardInformation();
        mockCreditCardInfo.setCreditCardNumber("1234567812345678");
        mockCreditCardInfo.setCvv("123");
        mockCreditCardInfo.setCreditCardExpiry("12/23");

        mockOrder.setOrderDate("2025-01-06");

        when(objectMapper.readValue(validOrderRequest, Order.class)).thenReturn(mockOrder);
        mockOrder.setCreditCardInformation(mockCreditCardInfo);

        ResponseEntity<OrderValidationResult> response = validationController.validateOrder(validOrderRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(OrderValidationCode.EXPIRY_DATE_INVALID, response.getBody().getOrderValidationCode());
        assertEquals(OrderStatus.INVALID, response.getBody().getOrderStatus());
    }

    @Test
    void testPizzaCheckInvalidPizza() throws JsonProcessingException {
        String validOrderRequest = "{\"creditCardInformation\": {\"creditCardNumber\": \"1234567812345678\", \"cvv\": \"123\", \"creditCardExpiry\": \"12/23\"}, \"pizzasInOrder\": [{\"name\": \"InvalidPizza\", \"priceInPence\": 1500}], \"priceTotalInPence\": 1600}";

        Order mockOrder = new Order();
        CreditCardInformation mockCreditCardInfo = new CreditCardInformation();
        mockCreditCardInfo.setCreditCardNumber("1234567812345678");
        mockCreditCardInfo.setCvv("123");
        mockCreditCardInfo.setCreditCardExpiry("12/29");

        Pizza mockPizza = new Pizza();
        mockPizza.setName("InvalidPizza");
        mockPizza.setPriceInPence(1500);
        Pizza[] pizzas = {mockPizza};

        when(objectMapper.readValue(validOrderRequest, Order.class)).thenReturn(mockOrder);
        mockOrder.setCreditCardInformation(mockCreditCardInfo);
        mockOrder.setPizzasInOrder(pizzas);
        mockOrder.setOrderDate("2025-01-06");
        mockOrder.setPriceTotalInPence(1600);

        Restaurant mockRestaurant = new Restaurant();
        mockRestaurant.setMenu(new Pizza[]{}); // Empty menu for clarity
        mockRestaurant.setName("MockRestaurant");
        mockRestaurant.setOpeningDays(List.of(DayOfWeek.MONDAY));

        try (MockedStatic<FetchFunctions> mockedStatic = mockStatic(FetchFunctions.class)) {

            mockedStatic.when(FetchFunctions::fetchRestaurants).thenReturn(List.of(mockRestaurant));


            mockedStatic.when(() -> FetchFunctions.getRestaurantForPizza("ValidPizza", List.of(mockRestaurant)))
                    .thenReturn(mockRestaurant);
            mockedStatic.when(() -> FetchFunctions.getRestaurantForPizza("InvalidPizza", List.of(mockRestaurant)))
                    .thenReturn(null);


            ResponseEntity<OrderValidationResult> response = validationController.validateOrder(validOrderRequest);


            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());
            assertEquals(OrderValidationCode.PIZZA_NOT_DEFINED, response.getBody().getOrderValidationCode());
            assertEquals(OrderStatus.INVALID, response.getBody().getOrderStatus());
        }
    }

    @Test
    void testPizzaCheck_TotalInvalid() throws JsonProcessingException {
        String validOrderRequest = "{\"creditCardInformation\": {\"creditCardNumber\": \"1234567812345678\", \"cvv\": \"123\", \"creditCardExpiry\": \"12/23\"}, \"pizzasInOrder\": [{\"name\": \"InvalidPizza\", \"priceInPence\": 1500}], \"priceTotalInPence\": 1600}";

        Order mockOrder = new Order();
        CreditCardInformation mockCreditCardInfo = new CreditCardInformation();
        mockCreditCardInfo.setCreditCardNumber("1234567812345678");
        mockCreditCardInfo.setCvv("123");
        mockCreditCardInfo.setCreditCardExpiry("12/29");

        Pizza mockPizza = new Pizza();
        mockPizza.setName("ValidPizza");
        mockPizza.setPriceInPence(1000);
        Pizza[] pizzas = {mockPizza};


        when(objectMapper.readValue(validOrderRequest, Order.class)).thenReturn(mockOrder);
        mockOrder.setCreditCardInformation(mockCreditCardInfo);
        mockOrder.setPizzasInOrder(pizzas);
        mockOrder.setOrderDate("2025-01-06");
        mockOrder.setPriceTotalInPence(1600);

        Restaurant mockRestaurant = new Restaurant();
        mockRestaurant.setMenu(new Pizza[]{}); // Empty menu for clarity
        mockRestaurant.setName("MockRestaurant");
        mockRestaurant.setOpeningDays(List.of(DayOfWeek.MONDAY));

        try (MockedStatic<FetchFunctions> mockedStatic = mockStatic(FetchFunctions.class)) {

            mockedStatic.when(FetchFunctions::fetchRestaurants).thenReturn(List.of(mockRestaurant));


            mockedStatic.when(() -> FetchFunctions.getRestaurantForPizza("ValidPizza", List.of(mockRestaurant)))
                    .thenReturn(mockRestaurant);


            ResponseEntity<OrderValidationResult> response = validationController.validateOrder(validOrderRequest);


            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());
            assertEquals(OrderValidationCode.TOTAL_INCORRECT, response.getBody().getOrderValidationCode());
            assertEquals(OrderStatus.INVALID, response.getBody().getOrderStatus());
        }
    }

    @Test
    void testPizzaCheck_ClosedRestaurant() throws JsonProcessingException {
        String validOrderRequest = "{\"creditCardInformation\": {\"creditCardNumber\": \"1234567812345678\", \"cvv\": \"123\", \"creditCardExpiry\": \"12/23\"}, \"pizzasInOrder\": [{\"name\": \"InvalidPizza\", \"priceInPence\": 1500}], \"priceTotalInPence\": 1600}";

        Order mockOrder = new Order();
        CreditCardInformation mockCreditCardInfo = new CreditCardInformation();
        mockCreditCardInfo.setCreditCardNumber("1234567812345678");
        mockCreditCardInfo.setCvv("123");
        mockCreditCardInfo.setCreditCardExpiry("12/29");

        Pizza mockPizza = new Pizza();
        mockPizza.setName("ValidPizza");
        mockPizza.setPriceInPence(1000);
        Pizza[] pizzas = {mockPizza};


        when(objectMapper.readValue(validOrderRequest, Order.class)).thenReturn(mockOrder);
        mockOrder.setCreditCardInformation(mockCreditCardInfo);
        mockOrder.setPizzasInOrder(pizzas);
        mockOrder.setOrderDate("2025-01-01");
        mockOrder.setPriceTotalInPence(1100);

        Restaurant mockRestaurant = new Restaurant();
        mockRestaurant.setMenu(pizzas);
        mockRestaurant.setName("MockRestaurant");
        mockRestaurant.setOpeningDays(List.of(DayOfWeek.MONDAY));

        try (MockedStatic<FetchFunctions> mockedStatic = mockStatic(FetchFunctions.class)) {

            mockedStatic.when(FetchFunctions::fetchRestaurants).thenReturn(List.of(mockRestaurant));


            mockedStatic.when(() -> FetchFunctions.getRestaurantForPizza("ValidPizza", List.of(mockRestaurant)))
                    .thenReturn(mockRestaurant);


            ResponseEntity<OrderValidationResult> response = validationController.validateOrder(validOrderRequest);


            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());
            assertEquals(OrderValidationCode.RESTAURANT_CLOSED, response.getBody().getOrderValidationCode());
            assertEquals(OrderStatus.INVALID, response.getBody().getOrderStatus());
        }
    }

    @Test
    void testPizzaCheck_MultipleRestaurants() throws JsonProcessingException {
        String validOrderRequest = "{\"creditCardInformation\": {\"creditCardNumber\": \"1234567812345678\", \"cvv\": \"123\", \"creditCardExpiry\": \"12/23\"}, \"pizzasInOrder\": [{\"name\": \"InvalidPizza\", \"priceInPence\": 1500}], \"priceTotalInPence\": 1600}";

        Order mockOrder = new Order();
        CreditCardInformation mockCreditCardInfo = new CreditCardInformation();
        mockCreditCardInfo.setCreditCardNumber("1234567812345678");
        mockCreditCardInfo.setCvv("123");
        mockCreditCardInfo.setCreditCardExpiry("12/29");

        Pizza mockPizza = new Pizza();
        mockPizza.setName("ValidPizza");
        mockPizza.setPriceInPence(1000);
        Pizza mockPizza2 = new Pizza();
        mockPizza2.setName("ValidPizza2");
        mockPizza2.setPriceInPence(1500);


        Pizza[] pizzas = {mockPizza, mockPizza2};


        when(objectMapper.readValue(validOrderRequest, Order.class)).thenReturn(mockOrder);
        mockOrder.setCreditCardInformation(mockCreditCardInfo);
        mockOrder.setPizzasInOrder(pizzas);
        mockOrder.setOrderDate("2025-01-06");
        mockOrder.setPriceTotalInPence(2600);

        Restaurant mockRestaurant = new Restaurant();
        Pizza[] menu = {mockPizza};
        mockRestaurant.setMenu(menu);
        mockRestaurant.setName("MockRestaurant");
        mockRestaurant.setOpeningDays(List.of(DayOfWeek.MONDAY));

        Restaurant mockRestaurant2 = new Restaurant();
        Pizza[] menu2 = {mockPizza2};
        mockRestaurant2.setMenu(menu2);
        mockRestaurant2.setName("MockRestaurant2");
        mockRestaurant2.setOpeningDays(List.of(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY, DayOfWeek.FRIDAY));

        try (MockedStatic<FetchFunctions> mockedStatic = mockStatic(FetchFunctions.class)) {

            mockedStatic.when(FetchFunctions::fetchRestaurants).thenReturn(List.of(mockRestaurant, mockRestaurant2));

            mockedStatic.when(() -> FetchFunctions.getRestaurantForPizza("ValidPizza", List.of(mockRestaurant, mockRestaurant2)))
                    .thenReturn(mockRestaurant);
            mockedStatic.when(() -> FetchFunctions.getRestaurantForPizza("ValidPizza2", List.of(mockRestaurant, mockRestaurant2)))
                    .thenReturn(mockRestaurant2);


            ResponseEntity<OrderValidationResult> response = validationController.validateOrder(validOrderRequest);


            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());
            assertEquals(OrderValidationCode.PIZZA_FROM_MULTIPLE_RESTAURANTS, response.getBody().getOrderValidationCode());
            assertEquals(OrderStatus.INVALID, response.getBody().getOrderStatus());
        }
    }

    @Test
    void testPizzaCheck_PriceInvalid() throws JsonProcessingException {
        String validOrderRequest = "{\"creditCardInformation\": {\"creditCardNumber\": \"1234567812345678\", \"cvv\": \"123\", \"creditCardExpiry\": \"12/23\"}, \"pizzasInOrder\": [{\"name\": \"InvalidPizza\", \"priceInPence\": 1500}], \"priceTotalInPence\": 1600}";

        Order mockOrder = new Order();
        CreditCardInformation mockCreditCardInfo = new CreditCardInformation();
        mockCreditCardInfo.setCreditCardNumber("1234567812345678");
        mockCreditCardInfo.setCvv("123");
        mockCreditCardInfo.setCreditCardExpiry("12/29");

        Pizza mockPizza = new Pizza();
        mockPizza.setName("ValidPizza");
        mockPizza.setPriceInPence(1000);
        Pizza[] pizzas = {mockPizza};


        when(objectMapper.readValue(validOrderRequest, Order.class)).thenReturn(mockOrder);
        mockOrder.setCreditCardInformation(mockCreditCardInfo);
        mockOrder.setPizzasInOrder(pizzas);
        mockOrder.setOrderDate("2025-01-06");
        mockOrder.setPriceTotalInPence(1100);

        Pizza restPizza = new Pizza();
        restPizza.setName("ValidPizza");
        restPizza.setPriceInPence(1600);
        Pizza[] menu = {restPizza};

        Restaurant mockRestaurant = new Restaurant();
        mockRestaurant.setMenu(menu);
        mockRestaurant.setName("MockRestaurant");
        mockRestaurant.setOpeningDays(List.of(DayOfWeek.MONDAY));

        try (MockedStatic<FetchFunctions> mockedStatic = mockStatic(FetchFunctions.class)) {
            mockedStatic.when(FetchFunctions::fetchRestaurants).thenReturn(List.of(mockRestaurant));


            mockedStatic.when(() -> FetchFunctions.getRestaurantForPizza("ValidPizza", List.of(mockRestaurant)))
                    .thenReturn(mockRestaurant);


            ResponseEntity<OrderValidationResult> response = validationController.validateOrder(validOrderRequest);


            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());
            assertEquals(OrderValidationCode.PRICE_FOR_PIZZA_INVALID, response.getBody().getOrderValidationCode());
            assertEquals(OrderStatus.INVALID, response.getBody().getOrderStatus());
        }
    }

    @Test
    void testValidOrder() throws JsonProcessingException {
        String validOrderRequest = "{\"creditCardInformation\": {\"creditCardNumber\": \"1234567812345678\", \"cvv\": \"123\", \"creditCardExpiry\": \"12/23\"}, \"pizzasInOrder\": [{\"name\": \"ValidPizza\", \"priceInPence\": 1000}], \"priceTotalInPence\": 1100}";
        Order mockOrder = new Order();
        CreditCardInformation mockCreditCardInfo = new CreditCardInformation();
        mockCreditCardInfo.setCreditCardNumber("1234567812345678");
        mockCreditCardInfo.setCvv("123");
        mockCreditCardInfo.setCreditCardExpiry("12/29");

        Pizza mockPizza = new Pizza();
        mockPizza.setName("ValidPizza");
        mockPizza.setPriceInPence(1000);
        Pizza[] pizzas = {mockPizza};


        when(objectMapper.readValue(validOrderRequest, Order.class)).thenReturn(mockOrder);
        mockOrder.setCreditCardInformation(mockCreditCardInfo);
        mockOrder.setPizzasInOrder(pizzas);
        mockOrder.setOrderDate("2025-01-06");
        mockOrder.setPriceTotalInPence(1100);

        Restaurant mockRestaurant = new Restaurant();
        mockRestaurant.setMenu(pizzas);
        mockRestaurant.setName("MockRestaurant");
        mockRestaurant.setOpeningDays(List.of(DayOfWeek.MONDAY));
        try (MockedStatic<FetchFunctions> mockedStatic = mockStatic(FetchFunctions.class)) {
            mockedStatic.when(FetchFunctions::fetchRestaurants).thenReturn(List.of(mockRestaurant));
            mockedStatic.when(() -> FetchFunctions.getRestaurantForPizza("ValidPizza", List.of(mockRestaurant)))
                    .thenReturn(mockRestaurant);


            ResponseEntity<OrderValidationResult> response = validationController.validateOrder(validOrderRequest);


            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());
            assertEquals(OrderValidationCode.NO_ERROR, response.getBody().getOrderValidationCode());
            assertEquals(OrderStatus.VALID, response.getBody().getOrderStatus());
        }
    }

    @Test
    void testInvalidOrderCalcPath() throws JsonProcessingException {
        String validOrderRequest = "{\"creditCardInformation\": {\"creditCardNumber\": \"1234567812345678\", \"cvv\": \"12\", \"creditCardExpiry\": \"12/23\"}, \"pizzasInOrder\": [], \"priceTotalInPence\": 0}";
        Order mockOrder = new Order();
        CreditCardInformation mockCreditCardInfo = new CreditCardInformation();
        mockCreditCardInfo.setCreditCardNumber("1234567812345678");
        mockCreditCardInfo.setCvv("12");
        mockCreditCardInfo.setCreditCardExpiry("12/23");

        when(objectMapper.readValue(validOrderRequest, Order.class)).thenReturn(mockOrder);
        mockOrder.setCreditCardInformation(mockCreditCardInfo);

        ResponseEntity<LngLat[]> response = validationController.calcDeliveryPath(validOrderRequest);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

    }


    @Test
    public void testCalculatePathWithOneNoFlyZone() throws Exception {
        LngLat startPos = generateEdiLngLat();
        LngLat endPos = generateEdiLngLat();
        boolean valid = true;


        List<NamedRegion> customNoFlyZones = List.of(generateEdiRegion());

        try (MockedStatic<FetchFunctions> mockedStatic = mockStatic(FetchFunctions.class)) {
            mockedStatic.when(FetchFunctions::fetchNoFlyZones).thenReturn(customNoFlyZones);


            List<LngLat> path = validationController.calculatePath(startPos, endPos);
            LngLat lastPoint = path.get(path.size() - 1);
            boolean close = GeometryFunctions.getDistanceBetween(endPos,lastPoint)<0.00015;

            assertNotNull(path);
            assertFalse(path.isEmpty());


            for (LngLat point: path){
                if(validationController.isInNoFlyZone(customNoFlyZones, point)){
                    valid = false;
                }
            }
            assertTrue(valid);
            assertTrue(close);
        }
    }

    @Test
    public void testCalculatePathWithNoNoFlyZones() throws Exception {
        LngLat startPos = generateEdiLngLat();
        LngLat endPos = generateEdiLngLat();
        boolean valid = true;


        List<NamedRegion> customNoFlyZones = List.of();

        try (MockedStatic<FetchFunctions> mockedStatic = mockStatic(FetchFunctions.class)) {
            mockedStatic.when(FetchFunctions::fetchNoFlyZones).thenReturn(customNoFlyZones);


            List<LngLat> path = validationController.calculatePath(startPos, endPos);
            LngLat lastPoint = path.get(path.size() - 1);
            boolean close = GeometryFunctions.getDistanceBetween(endPos,lastPoint)<0.00015;

            assertNotNull(path);
            assertFalse(path.isEmpty());


            for (LngLat point: path){
                if(validationController.isInNoFlyZone(customNoFlyZones, point)){
                    valid = false;
                }
            }
            assertTrue(valid);
            assertTrue(close);
        }
    }

    @Test
    public void testCalculatePathWithTwoNoFlyZones() throws Exception {
        LngLat startPos = generateEdiLngLat();
        LngLat endPos = generateEdiLngLat();
        boolean valid = true;

        NamedRegion zone1 = generateEdiRegion();
        NamedRegion zone2 = generateEdiRegion();
        List<NamedRegion> customNoFlyZones = List.of(zone1, zone2);

        try (MockedStatic<FetchFunctions> mockedStatic = mockStatic(FetchFunctions.class)) {
            mockedStatic.when(FetchFunctions::fetchNoFlyZones).thenReturn(customNoFlyZones);


            List<LngLat> path = validationController.calculatePath(startPos, endPos);
            LngLat lastPoint = path.get(path.size() - 1);
            boolean close = GeometryFunctions.getDistanceBetween(endPos,lastPoint)<0.00015;

            assertNotNull(path);
            assertFalse(path.isEmpty());


            for (LngLat point: path){
                if(validationController.isInNoFlyZone(customNoFlyZones, point)){
                    valid = false;
                }
            }
            assertTrue(valid);
            assertTrue(close);
        }
    }
    @Test
    public void testCalculatePathWithManyNoFlyZones() throws Exception {
        LngLat startPos = generateEdiLngLat();
        LngLat endPos = generateEdiLngLat();
        boolean valid = true;

        List<NamedRegion> customNoFlyZones = generateNoFlyZones();

        try (MockedStatic<FetchFunctions> mockedStatic = mockStatic(FetchFunctions.class)) {
            mockedStatic.when(FetchFunctions::fetchNoFlyZones).thenReturn(customNoFlyZones);


            List<LngLat> path = validationController.calculatePath(startPos, endPos);
            LngLat lastPoint = path.get(path.size() - 1);
            boolean close = GeometryFunctions.getDistanceBetween(endPos,lastPoint)<0.00015;

            assertNotNull(path);
            assertFalse(path.isEmpty());


            for (LngLat point: path){
                if(validationController.isInNoFlyZone(customNoFlyZones, point)){
                    valid = false;
                }
            }
            assertTrue(valid);
            assertTrue(close);
        }
    }
    @Test
    public void testPathStartingInNoFlyZone() throws Exception {

        LngLat startPos = new LngLat();
        startPos.setLat(55.944494);
        startPos.setLng(-3.186874);
        LngLat endPos = new LngLat();
        endPos.setLat(55.942617);
        endPos.setLng(-3.190234);

        LngLat one = new LngLat();
        one.setLat(55.944);
        one.setLng(-3.187);

        LngLat two = new LngLat();
        two.setLat(55.944);
        two.setLng(-3.185);

        LngLat three = new LngLat();
        three.setLat(55.945);
        three.setLng(-3.185);

        LngLat four = new LngLat();
        four.setLat(55.945);
        four.setLng(-3.187);


        NamedRegion noFlyZone = new NamedRegion();

        noFlyZone.setVertices(List.of(one, two, three, four,one));


        try (MockedStatic<FetchFunctions> mockedStatic = mockStatic(FetchFunctions.class)) {
            mockedStatic.when(FetchFunctions::fetchNoFlyZones).thenReturn(List.of(noFlyZone));


               List<LngLat> path = validationController.calculatePath(startPos, endPos);

                assertNull(path);



        }
    }

    @Test
    public void testCalculatePathPerformanceWithOneNoFlyZone() throws Exception {
        LngLat startPos = generateEdiLngLat();
        LngLat endPos = generateEdiLngLat();


        List<NamedRegion> customNoFlyZones = List.of(generateEdiRegion());

        try (MockedStatic<FetchFunctions> mockedStatic = mockStatic(FetchFunctions.class)) {
            mockedStatic.when(FetchFunctions::fetchNoFlyZones).thenReturn(customNoFlyZones);

            long startTime = System.nanoTime();

            List<LngLat> path = validationController.calculatePath(startPos, endPos);


            long endTime = System.nanoTime();

            long duration = endTime - startTime;

            assertNotNull(path);
            assertFalse(path.isEmpty());


            long acceptableTimeThresholdInMilliseconds = 60000;
            assertTrue(duration < acceptableTimeThresholdInMilliseconds * 1_000_000);
        }
    }

    @Test
    public void testCalculatePathPerformanceRepeatWithOneNoFlyZone() throws Exception {



        int iterations = 100;
        long totalDuration = 0;

        try (MockedStatic<FetchFunctions> mockedStatic = mockStatic(FetchFunctions.class)) {
            for (int i = 0; i < iterations; i++) {
                LngLat startPos = generateEdiLngLat();
                LngLat endPos = generateEdiLngLat();


                List<NamedRegion> customNoFlyZones = List.of(generateEdiRegion());


                mockedStatic.when(FetchFunctions::fetchNoFlyZones).thenReturn(customNoFlyZones);

                long startTime = System.nanoTime();

                List<LngLat> path = validationController.calculatePath(startPos, endPos);

                long endTime = System.nanoTime();
                long duration = endTime - startTime;

                totalDuration += duration;

                assertNotNull(path);
                assertFalse(path.isEmpty());
                System.out.println(i);

                long acceptableTimeThresholdInMilliseconds = 60000;
                assertTrue(duration < acceptableTimeThresholdInMilliseconds * 1_000_000,
                        "Iteration " + (i + 1) + ": Duration exceeded acceptable threshold (" + duration / 1_000_000 + " ms)");
            }
        }


        double averageDurationInMilliseconds = (double) totalDuration / iterations / 1_000_000;

        System.out.println("Average duration: " + averageDurationInMilliseconds + " ms");

        long acceptableAverageThresholdInMilliseconds = 60000;
        assertTrue(averageDurationInMilliseconds < acceptableAverageThresholdInMilliseconds,
                "Average duration exceeded acceptable threshold (" + averageDurationInMilliseconds + " ms)");
    }
    @Test
    public void testCalculatePathPerformanceWithNoNoFlyZones() throws Exception {
        LngLat startPos = generateEdiLngLat();
        LngLat endPos = generateEdiLngLat();

        List<NamedRegion> customNoFlyZones = List.of();

        try (MockedStatic<FetchFunctions> mockedStatic = mockStatic(FetchFunctions.class)) {
            mockedStatic.when(FetchFunctions::fetchNoFlyZones).thenReturn(customNoFlyZones);


            long startTime = System.nanoTime();


            List<LngLat> path = validationController.calculatePath(startPos, endPos);


            long endTime = System.nanoTime();

            long duration = endTime - startTime;


            assertNotNull(path);
            assertFalse(path.isEmpty());


            long acceptableTimeThresholdInMilliseconds = 60000;
            assertTrue(duration < acceptableTimeThresholdInMilliseconds * 1_000_000);
        }
    }

    @Test
    public void testCalculatePathPerformanceRepeatWithNoNoFlyZone() throws Exception {



        int iterations = 100;
        long totalDuration = 0;

        try (MockedStatic<FetchFunctions> mockedStatic = mockStatic(FetchFunctions.class)) {
            for (int i = 0; i < iterations; i++) {


                LngLat startPos = generateEdiLngLat();
                LngLat endPos = generateEdiLngLat();
                List<NamedRegion> customNoFlyZones = List.of();


                mockedStatic.when(FetchFunctions::fetchNoFlyZones).thenReturn(customNoFlyZones);

                long startTime = System.nanoTime();

                List<LngLat> path = validationController.calculatePath(startPos, endPos);

                long endTime = System.nanoTime();
                long duration = endTime - startTime;

                totalDuration += duration;

                assertNotNull(path);
                assertFalse(path.isEmpty());
                System.out.println(i);

                long acceptableTimeThresholdInMilliseconds = 60000;
                assertTrue(duration < acceptableTimeThresholdInMilliseconds * 1_000_000,
                        "Iteration " + (i + 1) + ": Duration exceeded acceptable threshold (" + duration / 1_000_000 + " ms)");
            }
        }

        double averageDurationInMilliseconds = (double) totalDuration / iterations / 1_000_000;

        System.out.println("Average duration: " + averageDurationInMilliseconds + " ms");


        long acceptableAverageThresholdInMilliseconds = 60000;
        assertTrue(averageDurationInMilliseconds < acceptableAverageThresholdInMilliseconds,
                "Average duration exceeded acceptable threshold (" + averageDurationInMilliseconds + " ms)");
    }
    @Test
    public void testCalculatePathPerformanceWithTwoNoFlyZones() throws Exception {
        LngLat startPos = generateEdiLngLat();
        LngLat endPos = generateEdiLngLat();


        NamedRegion zone1 = generateEdiRegion();
        NamedRegion zone2 = generateEdiRegion();
        List<NamedRegion> customNoFlyZones = List.of(zone1, zone2);


        try (MockedStatic<FetchFunctions> mockedStatic = mockStatic(FetchFunctions.class)) {
            mockedStatic.when(FetchFunctions::fetchNoFlyZones).thenReturn(customNoFlyZones);


            long startTime = System.nanoTime();


            List<LngLat> path = validationController.calculatePath(startPos, endPos);


            long endTime = System.nanoTime();

            long duration = endTime - startTime;


            assertNotNull(path);
            assertFalse(path.isEmpty());



            long acceptableTimeThresholdInMilliseconds = 60000;
            assertTrue(duration < acceptableTimeThresholdInMilliseconds * 1_000_000);
        }
    }

    @Test
    public void testCalculatePathPerformanceRepeatWithTwoNoFlyZone() throws Exception {



        int iterations = 100;
        long totalDuration = 0;

        try (MockedStatic<FetchFunctions> mockedStatic = mockStatic(FetchFunctions.class)) {
            for (int i = 0; i < iterations; i++) {
                LngLat startPos = generateEdiLngLat();
                LngLat endPos = generateEdiLngLat();

                NamedRegion zone1 = generateEdiRegion();
                NamedRegion zone2 = generateEdiRegion();
                List<NamedRegion> customNoFlyZones = List.of(zone1, zone2);


                mockedStatic.when(FetchFunctions::fetchNoFlyZones).thenReturn(customNoFlyZones);

                long startTime = System.nanoTime();

                List<LngLat> path = validationController.calculatePath(startPos, endPos);

                long endTime = System.nanoTime();
                long duration = endTime - startTime;

                totalDuration += duration;

                assertNotNull(path);
                assertFalse(path.isEmpty());

                System.out.println(i);

                long acceptableTimeThresholdInMilliseconds = 60000;
                assertTrue(duration < acceptableTimeThresholdInMilliseconds * 1_000_000,
                        "Iteration " + (i + 1) + ": Duration exceeded acceptable threshold (" + duration / 1_000_000 + " ms)");
            }
        }

        double averageDurationInMilliseconds = (double) totalDuration / iterations / 1_000_000;


        System.out.println("Average duration: " + averageDurationInMilliseconds + " ms");

        long acceptableAverageThresholdInMilliseconds = 60000;
        assertTrue(averageDurationInMilliseconds < acceptableAverageThresholdInMilliseconds,
                "Average duration exceeded acceptable threshold (" + averageDurationInMilliseconds + " ms)");
    }
    @Test
    public void testCalculatePathPerformanceWithManyNoFlyZones() throws Exception {
        LngLat startPos = generateEdiLngLat();
        LngLat endPos = generateEdiLngLat();



        List<NamedRegion> customNoFlyZones = generateNoFlyZones();

        try (MockedStatic<FetchFunctions> mockedStatic = mockStatic(FetchFunctions.class)) {
            mockedStatic.when(FetchFunctions::fetchNoFlyZones).thenReturn(customNoFlyZones);


            long startTime = System.nanoTime();


            List<LngLat> path = validationController.calculatePath(startPos, endPos);


            long endTime = System.nanoTime();

            long duration = endTime - startTime;


            assertNotNull(path);
            assertFalse(path.isEmpty());


            long acceptableTimeThresholdInMilliseconds = 60000;
            assertTrue(duration < acceptableTimeThresholdInMilliseconds * 1_000_000);
        }
    }

    @Test
    public void testCalculatePathPerformanceRepeatWithManyNoFlyZone() throws Exception {



        int iterations = 100;
        long totalDuration = 0;

        try (MockedStatic<FetchFunctions> mockedStatic = mockStatic(FetchFunctions.class)) {
            for (int i = 0; i < iterations; i++) {
                LngLat startPos = generateEdiLngLat();
                LngLat endPos = generateEdiLngLat();


                List<NamedRegion> customNoFlyZones = generateNoFlyZones();


                mockedStatic.when(FetchFunctions::fetchNoFlyZones).thenReturn(customNoFlyZones);

                long startTime = System.nanoTime();

                List<LngLat> path = validationController.calculatePath(startPos, endPos);

                long endTime = System.nanoTime();
                long duration = endTime - startTime;

                totalDuration += duration;

                assertNotNull(path);
                assertFalse(path.isEmpty());

                System.out.println(i);

                long acceptableTimeThresholdInMilliseconds = 60000;
                assertTrue(duration < acceptableTimeThresholdInMilliseconds * 1_000_000,
                        "Iteration " + (i + 1) + ": Duration exceeded acceptable threshold (" + duration / 1_000_000 + " ms)");
            }
        }


        double averageDurationInMilliseconds = (double) totalDuration / iterations / 1_000_000;

        System.out.println("Average duration: " + averageDurationInMilliseconds + " ms");


        long acceptableAverageThresholdInMilliseconds = 60000;
        assertTrue(averageDurationInMilliseconds < acceptableAverageThresholdInMilliseconds,
                "Average duration exceeded acceptable threshold (" + averageDurationInMilliseconds + " ms)");
    }


    //generate a LngLat close to Appleton
    private LngLat generateCloseEdiLngLat(){
        double centerLng = -3.186874;
        double centerLat = 55.944494;

        double lng = centerLng + (random.nextDouble() * 0.10 - 0.05);
        double lat = centerLat + (random.nextDouble() * 0.10 - 0.05);
        LngLat test = new LngLat();
        test.setLng(lng);
        test.setLat(lat);
        return test;
    }
    private LngLat generateEdiLngLat(){
         double MIN_LNG = -3.202541; // Minimum longitude
         double MAX_LNG = -3.179799; // Maximum longitude
         double MIN_LAT = 55.938911; // Minimum latitude
         double MAX_LAT = 55.945846;

        double lng = MIN_LNG + (random.nextDouble() * (MAX_LNG - MIN_LNG));

        double lat = MIN_LAT + (random.nextDouble() * (MAX_LAT - MIN_LAT));

        LngLat test = new LngLat();
        test.setLng(lng);
        test.setLat(lat);
        return test;
    }

    private NamedRegion generateEdiRegion(){
        NamedRegion region = new NamedRegion();
        List<LngLat> vertices = new ArrayList<>();

        int numVertices = random.nextInt(4) + 3;
        for (int i = 0; i < numVertices - 1; i++) {
            LngLat vertex = generateEdiLngLat();
            vertices.add(vertex);
        }

        vertices.add(vertices.get(0));

        region.setVertices(vertices);
        return region;
    }

    private List <NamedRegion> generateNoFlyZones(){
        int numNoFlyZones = random.nextInt(4) + 3;
        List<NamedRegion> customNoFlyZones = new ArrayList<>();
        for (int i = 0; i < numNoFlyZones; i++) {
            customNoFlyZones.add(generateEdiRegion());
        }
        return customNoFlyZones;

    }



}
