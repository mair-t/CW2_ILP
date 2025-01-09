package uk.ac.ed.inf.cw2_ilp;


import org.junit.jupiter.api.Test;

import uk.ac.ed.inf.cw2_ilp.dataTypes.*;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;

import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;


public class ValidationTests {

    Random random = new Random();


    //ensures a valid string returns false as it is invalid
    @Test
    public void isntValidStringTest_String(){
        String input = "Valid String";
        boolean result = Validation.isntValidString(input);
        assertFalse(result);

    }
    //ensures a null string returns true
    @Test
    public void isntValidStringTest_NullString(){
        String input = null;
        boolean result = Validation.isntValidString(input);
        assertTrue(result);
    }

    //ensures an empty string returns true
    @Test
    public void isntValidStringTest_EmptyString(){
        String input = "";
        boolean result = Validation.isntValidString(input);
        assertTrue(result);
    }

    //ensures a whitespace string returns false
    @Test
    public void isntValidStringTest_WhitespaceString(){
        String input = " ";
        boolean result = Validation.isntValidString(input);
        assertFalse(result);
    }

    //ensures a valid position returns true
    @Test
    public void isValidPositionTest_ValidPosition(){
        LngLat test = generateRandomLngLat();
        boolean result = Validation.isValidPosition(test);
        assertTrue(result);
    }
    //ensures if the latitude is invalid it returns false
    @Test
    public void isValidPositionTest_InvalidLat(){
        LngLat test = generateRandomLngLat();
        test.setLat(100.0);
        boolean result = Validation.isValidPosition(test);
        assertFalse(result);

    }
    //ensures if the longitude is invalid it returns false
    @Test
    public void isValidPositionTest_InvalidLng(){
        LngLat test = generateRandomLngLat();
        test.setLng(-200.0);
        boolean result = Validation.isValidPosition(test);
        assertFalse(result);

    }
    //ensures if lat is missing it is false
    @Test
    public void isValidPositionTest_MissingLat(){
        Double lng = random.nextDouble(180-(-180))-180;
        LngLat test = new LngLat();
        test.setLng(lng);
        boolean result = Validation.isValidPosition(test);
        assertFalse(result);

    }
    //ensures if lng is missing it is false
    @Test
    public void isValidPositionTest_MissingLng(){
        Double lat = random.nextDouble(90-(-90))-90;
        LngLat test = new LngLat();
        test.setLat(lat);
        boolean result = Validation.isValidPosition(test);
        assertFalse(result);

    }

    //if the position is null it should return false
    @Test
    public void isValidPositionTest_NullPosition(){
        LngLat test = null;
        boolean result = Validation.isValidPosition(test);
        assertFalse(result);
    }

    //if the position is not defined it returns false
    @Test
    public void isValidPositionTest_EmptyPosition(){
        LngLat test = new LngLat();
        boolean result = Validation.isValidPosition(test);
        assertFalse(result);
    }

    //a valid angle should return true
    @Test
    public void isValidAngleTest_ValidAngle(){
        double angle = generateRandomAngle();
        boolean result = Validation.isValidAngle(angle);
        assertTrue(result);
    }

    //if the angle is null it should return false
    @Test
    public void isValidAngleTest_NullAngle(){
        Double angle = null;
        boolean result = Validation.isValidAngle(angle);
        assertFalse(result);
    }
    //if the angle is invalid (too high or low) it should return false
    @Test
    public void isValidAngleTest_InvalidAngle(){
        Double angle = 570.0;
        boolean result = Validation.isValidAngle(angle);
        assertFalse(result);
        Double angle2 = -10.0;
        boolean result2 = Validation.isValidAngle(angle2);
        assertFalse(result2);
    }
    //A valid region should return true
    @Test
    public void isValidRegionTest_ValidRegion(){
        NamedRegion region = generateRandomValidRegion();
        boolean result = Validation.isValidRegion(region);
        assertTrue(result);

    }
    //if the region does not have the same first and last it returns false
    @Test
    public void isValidRegionTest_OpenRegion(){
        NamedRegion region = generateRandomOpenRegion();
        boolean result = Validation.isValidRegion(region);
        assertFalse(result);

    }
    //if the region has too few points it returns false
    @Test
    public void isValidRegionTest_SmallRegion(){
        NamedRegion region = generateRandomSmallRegion();
        boolean result = Validation.isValidRegion(region);
        assertFalse(result);
    }
    //an empty region should return false
    @Test
    public void isValidRegionTest_EmptyRegion(){
        NamedRegion region = null;
        boolean result = Validation.isValidRegion(region);
        assertFalse(result);
    }

    //a credit card with valid attributes should return no error
    @Test
    public void creditCardCheckTest_ValidCreditCard(){
        Order order = new Order();
        order.setOrderDate(LocalDate.now().toString());
        CreditCardInformation test = new CreditCardInformation();
        test.setCvv("123");
        test.setCreditCardNumber("1234567890123456");
        test.setCreditCardExpiry("12/34");

        OrderValidationCode result = Validation.creditCardCheck(test, order);
        assertEquals(OrderValidationCode.NO_ERROR, result);

    }

    //if the CVV is more or less than 3 digits it should be cvv invalid
    @Test
    public void creditCardCheckTest_CVVNot3digits(){
        Order order = new Order();
        order.setOrderDate(LocalDate.now().toString());
        CreditCardInformation test = new CreditCardInformation();
        test.setCvv("1234");
        test.setCreditCardNumber("1234567890123456");
        test.setCreditCardExpiry("12/34");

        OrderValidationCode result = Validation.creditCardCheck(test, order);
        assertEquals(OrderValidationCode.CVV_INVALID, result);

        test.setCvv("12");
        OrderValidationCode result2 = Validation.creditCardCheck(test, order);
        assertEquals(OrderValidationCode.CVV_INVALID, result2);

    }
   // if the cvv is not a string it should return CVV Invalid
    @Test
    public void creditCardCheckTest_CVVNotString(){
        Order order = new Order();
        order.setOrderDate(LocalDate.now().toString());
        CreditCardInformation test = new CreditCardInformation();
        test.setCvv("");
        test.setCreditCardNumber("1234567890123456");
        test.setCreditCardExpiry("12/34");

        OrderValidationCode result = Validation.creditCardCheck(test, order);
        assertEquals(OrderValidationCode.CVV_INVALID, result);

    }

    //if the cvv is not numbers it should return CVV Invalid
    @Test
    public void creditCardCheckTest_CVVNotDigits(){
        Order order = new Order();
        order.setOrderDate(LocalDate.now().toString());
        CreditCardInformation test = new CreditCardInformation();
        test.setCvv("onetwothree");
        test.setCreditCardNumber("1234567890123456");
        test.setCreditCardExpiry("12/34");

        OrderValidationCode result = Validation.creditCardCheck(test, order);
        assertEquals(OrderValidationCode.CVV_INVALID, result);

    }

    //if the Card number is more or less than 16 digits it should be card number invalid
    @Test
    public void creditCardCheckTest_CardNumberLengthWrong(){
        Order order = new Order();
        order.setOrderDate(LocalDate.now().toString());
        CreditCardInformation test = new CreditCardInformation();
        test.setCvv("123");
        test.setCreditCardNumber("123456789012345678");
        test.setCreditCardExpiry("12/34");

        OrderValidationCode result = Validation.creditCardCheck(test, order);
        assertEquals(OrderValidationCode.CARD_NUMBER_INVALID, result);

        test.setCreditCardNumber("123456789012345");
        OrderValidationCode result2 = Validation.creditCardCheck(test, order);
        assertEquals(OrderValidationCode.CARD_NUMBER_INVALID, result2);


    }
    //if the card number is not a string it should return Card number Invalid
    @Test
    public void creditCardCheckTest_CardNumberNotString(){
        Order order = new Order();
        order.setOrderDate(LocalDate.now().toString());
        CreditCardInformation test = new CreditCardInformation();
        test.setCvv("123");
        test.setCreditCardNumber("");
        test.setCreditCardExpiry("12/34");

        OrderValidationCode result = Validation.creditCardCheck(test, order);
        assertEquals(OrderValidationCode.CARD_NUMBER_INVALID, result);

    }

    //if the card number is not digits it should return Card number Invalid
    @Test
    public void creditCardCheckTest_CardNumbernotNumber(){
        Order order = new Order();
        order.setOrderDate(LocalDate.now().toString());
        CreditCardInformation test = new CreditCardInformation();
        test.setCvv("123");
        test.setCreditCardNumber("onetwothree");
        test.setCreditCardExpiry("12/34");

        OrderValidationCode result = Validation.creditCardCheck(test, order);
        assertEquals(OrderValidationCode.CARD_NUMBER_INVALID, result);

    }

    //if the expiry date is not a valid date it should return expiry date invalid
    @Test
    public void creditCardCheckTest_ExpiryDateNotDate(){
        Order order = new Order();
        order.setOrderDate(LocalDate.now().toString());
        CreditCardInformation test = new CreditCardInformation();
        test.setCvv("123");
        test.setCreditCardNumber("1234567890123456");
        test.setCreditCardExpiry("14/34");

        OrderValidationCode result = Validation.creditCardCheck(test, order);
        assertEquals(OrderValidationCode.EXPIRY_DATE_INVALID, result);

    }

    //if the expiry date is before the order date it should return expiry date invalid
    @Test
    public void creditCardCheckTest_ExpiryDateBeforeDate(){
        Order order = new Order();
        order.setOrderDate(LocalDate.now().toString());
        CreditCardInformation test = new CreditCardInformation();
        test.setCvv("123");
        test.setCreditCardNumber("1234567890123456");
        test.setCreditCardExpiry("11/14");

        OrderValidationCode result = Validation.creditCardCheck(test, order);
        assertEquals(OrderValidationCode.EXPIRY_DATE_INVALID, result);

    }

    //if the order is valid it should return no_error
    @Test
    public void pizzaCheckTest_ValidOrder(){
        Pizza pizza1 = new Pizza();
        pizza1.setName("R1: Margarita");
        pizza1.setPriceInPence(1000);
        Pizza pizza2 = new Pizza();
        pizza2.setName("R1: Calzone");
        pizza2.setPriceInPence(1400);

        Pizza[] pizzas = {pizza1, pizza2};

        Order order = new Order();
        order.setPriceTotalInPence(1000+1400+100);
        order.setPizzasInOrder(pizzas);
        order.setOrderDate("2025-01-10");

        OrderValidationCode result = Validation.pizzaCheck(pizzas, order);

        assertEquals(OrderValidationCode.NO_ERROR, result);

    }
    //if there are more than 4 pizzas it should return max pizza count exceeded
    @Test
    public void pizzaCheckTest_TooManyPizzas(){
        Pizza pizza1 = new Pizza();
        pizza1.setName("R1: Margarita");
        pizza1.setPriceInPence(1000);
        Pizza pizza2 = new Pizza();
        pizza2.setName("R1: Calzone");
        pizza2.setPriceInPence(1400);
        Pizza pizza3 = new Pizza();
        pizza3.setName("R1: Margarita");
        pizza3.setPriceInPence(1000);
        Pizza pizza4 = new Pizza();
        pizza4.setName("R1: Calzone");
        pizza4.setPriceInPence(1400);
        Pizza pizza5 = new Pizza();
        pizza5.setName("R1: Calzone");
        pizza5.setPriceInPence(1400);

        Pizza[] pizzas = {pizza1, pizza2,pizza3,pizza4,pizza5};

        Order order = new Order();
        order.setPriceTotalInPence(6300);
        order.setPizzasInOrder(pizzas);
        order.setOrderDate("2025-01-10");

        OrderValidationCode result = Validation.pizzaCheck(pizzas, order);

        assertEquals(OrderValidationCode.MAX_PIZZA_COUNT_EXCEEDED, result);

    }
    //if there are no pizzas it should return empty order
    @Test
    public void pizzaCheckTest_EmptyOrder(){

        Pizza[] pizzas = {};

        Order order = new Order();
        order.setPriceTotalInPence(0);
        order.setPizzasInOrder(pizzas);
        order.setOrderDate("2025-01-10");

        OrderValidationCode result = Validation.pizzaCheck(pizzas, order);

        assertEquals(OrderValidationCode.EMPTY_ORDER, result);

    }
    //if the total of the order does not match the sum of the costs it should return total incorrect
    @Test
    public void pizzaCheckTest_TotalIncorrect(){
        Pizza pizza1 = new Pizza();
        pizza1.setName("R1: Margarita");
        pizza1.setPriceInPence(1000);
        Pizza pizza2 = new Pizza();
        pizza2.setName("R1: Calzone");
        pizza2.setPriceInPence(1400);

        Pizza[] pizzas = {pizza1, pizza2};

        Order order = new Order();
        order.setPriceTotalInPence(1000+1400);
        order.setPizzasInOrder(pizzas);
        order.setOrderDate("2025-01-10");

        OrderValidationCode result = Validation.pizzaCheck(pizzas, order);

        assertEquals(OrderValidationCode.TOTAL_INCORRECT, result);

    }

    //if a pizza does not exist it should return pizza not defined
    @Test
    public void pizzaCheckTest_InvalidPizza(){
        Pizza pizza1 = new Pizza();
        pizza1.setName("Margarita");
        pizza1.setPriceInPence(1000);
        Pizza pizza2 = new Pizza();
        pizza2.setName("R1: Calzone");
        pizza2.setPriceInPence(1400);

        Pizza[] pizzas = {pizza1, pizza2};

        Order order = new Order();
        order.setPriceTotalInPence(2500);
        order.setPizzasInOrder(pizzas);
        order.setOrderDate("2025-01-10");

        OrderValidationCode result = Validation.pizzaCheck(pizzas, order);

        assertEquals(OrderValidationCode.PIZZA_NOT_DEFINED, result);

    }
    //if the restaurant is not open on the order day it should return restaurant closed
    @Test
    public void pizzaCheckTest_RestaurantClosed(){
        Pizza pizza1 = new Pizza();
        pizza1.setName("R1: Margarita");
        pizza1.setPriceInPence(1000);
        Pizza pizza2 = new Pizza();
        pizza2.setName("R1: Calzone");
        pizza2.setPriceInPence(1400);

        Pizza[] pizzas = {pizza1, pizza2};

        Order order = new Order();
        order.setPriceTotalInPence(2500);
        order.setPizzasInOrder(pizzas);
        order.setOrderDate("2025-01-09");

        OrderValidationCode result = Validation.pizzaCheck(pizzas, order);

        assertEquals(OrderValidationCode.RESTAURANT_CLOSED, result);

    }

    //if the pizzas are from multiple restaurants it should return pizza from multiple restaurants
    @Test
    public void pizzaCheckTest_MultipleRestaurants(){
        Pizza pizza1 = new Pizza();
        pizza1.setName("R1: Margarita");
        pizza1.setPriceInPence(1000);
        Pizza pizza2 = new Pizza();
        pizza2.setName("R2: Meat Lover");
        pizza2.setPriceInPence(1400);

        Pizza[] pizzas = {pizza1, pizza2};

        Order order = new Order();
        order.setPriceTotalInPence(2500);
        order.setPizzasInOrder(pizzas);
        order.setOrderDate("2025-01-10");

        OrderValidationCode result = Validation.pizzaCheck(pizzas, order);

        assertEquals(OrderValidationCode.PIZZA_FROM_MULTIPLE_RESTAURANTS, result);

    }

    //if the price does not match it should return price invalid
    @Test
    public void pizzaCheckTest_PriceInvalid(){
        Pizza pizza1 = new Pizza();
        pizza1.setName("R1: Margarita");
        pizza1.setPriceInPence(1000);
        Pizza pizza2 = new Pizza();
        pizza2.setName("R1: Calzone");
        pizza2.setPriceInPence(1300);

        Pizza[] pizzas = {pizza1, pizza2};

        Order order = new Order();
        order.setPriceTotalInPence(2400);
        order.setPizzasInOrder(pizzas);
        order.setOrderDate("2025-01-10");

        OrderValidationCode result = Validation.pizzaCheck(pizzas, order);

        assertEquals(OrderValidationCode.PRICE_FOR_PIZZA_INVALID, result);

    }

    //if the day of the orderDate is listed in the restaurants opening days true should be returned
    @Test
    public void isRestaurantOpenTest_OpenRestaurant(){
        Restaurant restaurant = new Restaurant();
        restaurant.openingDays = List.of(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY, DayOfWeek.FRIDAY);

        String orderDate = "2025-01-10";

        boolean result = Validation.isRestaurantOpen(restaurant, orderDate);

        assertTrue(result);
    }

    //if the day of the orderDate is not listed in the restaurants opening days false should be returned
    @Test
    public void isRestaurantOpenTest_ClosedRestaurant(){
        Restaurant restaurant = new Restaurant();
        restaurant.openingDays = List.of(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY, DayOfWeek.FRIDAY);

        String orderDate = "2025-01-09";

        boolean result = Validation.isRestaurantOpen(restaurant, orderDate);

        assertFalse(result);
    }





    //generate a valid LngLat value
    private LngLat generateRandomLngLat(){
        Double lng = random.nextDouble(180-(-180))-180;
        Double lat = random.nextDouble(90-(-90))-90;
        LngLat test = new LngLat();
        test.setLng(lng);
        test.setLat(lat);
        return test;
    }

    //generate a valid random angle
    private double generateRandomAngle(){
        return random.nextDouble()*360;
    }

    //generate a valid region of random LngLats
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
    //generate a region where the first and last vertices dont match
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

    //generate a region with too few points
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
}
