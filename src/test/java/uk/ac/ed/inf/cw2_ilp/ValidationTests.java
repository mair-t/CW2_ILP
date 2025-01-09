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


    @Test
    public void isntValidStringTest_String(){
        String input = "Valid String";
        boolean result = Validation.isntValidString(input);
        assertFalse(result);

    }
    @Test
    public void isntValidStringTest_NullString(){
        String input = null;
        boolean result = Validation.isntValidString(input);
        assertTrue(result);
    }

    @Test
    public void isntValidStringTest_EmptyString(){
        String input = "";
        boolean result = Validation.isntValidString(input);
        assertTrue(result);
    }

    @Test
    public void isntValidStringTest_WhitespaceString(){
        String input = " ";
        boolean result = Validation.isntValidString(input);
        assertFalse(result);
    }

    @Test
    public void isValidPositionTest_ValidPosition(){
        LngLat test = generateRandomLngLat();
        boolean result = Validation.isValidPosition(test);
        assertTrue(result);
    }
    @Test
    public void isValidPositionTest_InvalidLat(){
        LngLat test = generateRandomLngLat();
        test.setLat(100.0);
        boolean result = Validation.isValidPosition(test);
        assertFalse(result);

    }
    @Test
    public void isValidPositionTest_InvalidLng(){
        LngLat test = generateRandomLngLat();
        test.setLng(-200.0);
        boolean result = Validation.isValidPosition(test);
        assertFalse(result);

    }
    @Test
    public void isValidPositionTest_MissingLat(){
        Double lng = random.nextDouble(180-(-180))-180;
        LngLat test = new LngLat();
        test.setLng(lng);
        boolean result = Validation.isValidPosition(test);
        assertFalse(result);

    }
    @Test
    public void isValidPositionTest_MissingLng(){
        Double lat = random.nextDouble(90-(-90))-90;
        LngLat test = new LngLat();
        test.setLat(lat);
        boolean result = Validation.isValidPosition(test);
        assertFalse(result);

    }

    @Test
    public void isValidPositionTest_NullPosition(){
        LngLat test = null;
        boolean result = Validation.isValidPosition(test);
        assertFalse(result);
    }

    @Test
    public void isValidPositionTest_EmptyPosition(){
        LngLat test = new LngLat();
        boolean result = Validation.isValidPosition(test);
        assertFalse(result);
    }

    @Test
    public void isValidAngleTest_ValidAngle(){
        double angle = generateRandomAngle();
        boolean result = Validation.isValidAngle(angle);
        assertTrue(result);
    }
    @Test
    public void isValidAngleTest_NullAngle(){
        Double angle = null;
        boolean result = Validation.isValidAngle(angle);
        assertFalse(result);
    }
    @Test
    public void isValidAngleTest_InvalidAngle(){
        Double angle = 570.0;
        boolean result = Validation.isValidAngle(angle);
        assertFalse(result);
        Double angle2 = -10.0;
        boolean result2 = Validation.isValidAngle(angle2);
        assertFalse(result2);
    }
    @Test
    public void isValidRegionTest_ValidRegion(){
        NamedRegion region = generateRandomValidRegion();
        boolean result = Validation.isValidRegion(region);
        assertTrue(result);

    }
    @Test
    public void isValidRegionTest_OpenRegion(){
        NamedRegion region = generateRandomOpenRegion();
        boolean result = Validation.isValidRegion(region);
        assertFalse(result);

    }
    @Test
    public void isValidRegionTest_SmallRegion(){
        NamedRegion region = generateRandomSmallRegion();
        boolean result = Validation.isValidRegion(region);
        assertFalse(result);
    }
    @Test
    public void isValidRegionTest_EmptyRegion(){
        NamedRegion region = null;
        boolean result = Validation.isValidRegion(region);
        assertFalse(result);
    }

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

    }
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

    }
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
        order.setOrderDate("2025-01-09");

        OrderValidationCode result = Validation.pizzaCheck(pizzas, order);

        assertEquals(OrderValidationCode.RESTAURANT_CLOSED, result);

    }

    @Test
    public void isRestaurantOpenTest_OpenRestaurant(){
        Restaurant restaurant = new Restaurant();
        restaurant.openingDays = List.of(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY, DayOfWeek.FRIDAY);

        String orderDate = "2025-01-10";

        boolean result = Validation.isRestaurantOpen(restaurant, orderDate);

        assertTrue(result);
    }

    @Test
    public void isRestaurantOpenTest_ClosedRestaurant(){
        Restaurant restaurant = new Restaurant();
        restaurant.openingDays = List.of(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY, DayOfWeek.FRIDAY);

        String orderDate = "2025-01-09";

        boolean result = Validation.isRestaurantOpen(restaurant, orderDate);

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
}
