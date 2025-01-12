package uk.ac.ed.inf.cw2_ilp;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.mockito.MockedStatic;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import uk.ac.ed.inf.cw2_ilp.dataTypes.*;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class MockedTests {

    @InjectMocks
    private RestController validationController;



    @Mock
    private ObjectMapper objectMapper;

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


        Pizza[] pizzas = {mockPizza,mockPizza2};


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
        mockRestaurant2.setOpeningDays(List.of(DayOfWeek.MONDAY,DayOfWeek.WEDNESDAY,DayOfWeek.FRIDAY));

        try (MockedStatic<FetchFunctions> mockedStatic = mockStatic(FetchFunctions.class)) {

            mockedStatic.when(FetchFunctions::fetchRestaurants).thenReturn(List.of(mockRestaurant,mockRestaurant2));

            mockedStatic.when(() -> FetchFunctions.getRestaurantForPizza("ValidPizza", List.of(mockRestaurant,mockRestaurant2)))
                    .thenReturn(mockRestaurant);
            mockedStatic.when(() -> FetchFunctions.getRestaurantForPizza("ValidPizza2", List.of(mockRestaurant,mockRestaurant2)))
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
        Pizza[] menu= {restPizza};

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
    void testCalcDeliveryPath_ValidOrder() throws JsonProcessingException {
    }


}
