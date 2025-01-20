package uk.ac.ed.inf.cw2_ilp;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;
import java.util.concurrent.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import uk.ac.ed.inf.cw2_ilp.dataTypes.*;

@SpringBootTest
public class PerformanceTests {

    private static final long TIMEOUT_MS = 60000; // 60 seconds


    @Autowired
    private RestController controller;

    @BeforeEach
    void setUp() {

    }
    @Test
    void testValidOrdersUnderLoad() throws InterruptedException, ExecutionException {
        int NUM_REQUESTS = 10;
        List<String> validOrders = generateValidOrders(NUM_REQUESTS);

        ExecutorService executor = Executors.newFixedThreadPool(100);
        List<Callable<Void>> tasks = new ArrayList<>();

        for (String orderRequest : validOrders) {
            tasks.add(() -> {
                long startTime = System.currentTimeMillis();
                try {
                    ResponseEntity<LngLat[]> response = controller.calcDeliveryPath(orderRequest);
                    long endTime = System.currentTimeMillis();


                    if ((endTime - startTime) >= TIMEOUT_MS) {
                        fail("Response time exceeded limit. Took " + (endTime - startTime) + " ms.");
                    }


                    assertNotNull(response.getBody(), "Response body is null");
                    assertEquals(HttpStatus.OK, response.getStatusCode(), "Expected OK status, but got " + response.getStatusCode());

                } catch (Exception e) {

                    e.printStackTrace();
                    fail("Exception during valid order processing: " + e.getMessage());
                }
                return null;
            });
        }

        List<Future<Void>> futures = executor.invokeAll(tasks);


        for (Future<Void> future : futures) {
            future.get();
        }

        executor.shutdown();
    }

    @Test
    void testInvalidOrdersUnderLoad() throws InterruptedException, ExecutionException {
        int NUM_REQUESTS = 10;
        List<String> invalidOrders = generateInvalidOrders(NUM_REQUESTS);

        ExecutorService executor = Executors.newFixedThreadPool(100);
        List<Callable<Void>> tasks = new ArrayList<>();

        for (String orderRequest : invalidOrders) {
            tasks.add(() -> {
                long startTime = System.currentTimeMillis();
                try {
                    ResponseEntity<LngLat[]> response = controller.calcDeliveryPath(orderRequest);
                    long endTime = System.currentTimeMillis();


                    if ((endTime - startTime) >= TIMEOUT_MS) {
                        fail("Response time exceeded limit. Took " + (endTime - startTime) + " ms.");
                    }

                    assertNull(response.getBody(), "Response body is null");
                    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode(), "Expected OK status, but got " + response.getStatusCode());

                } catch (Exception e) {

                    e.printStackTrace();
                    fail("Exception during valid order processing: " + e.getMessage());
                }
                return null;
            });
        }


        List<Future<Void>> futures = executor.invokeAll(tasks);


        for (Future<Void> future : futures) {
            future.get();
        }

        executor.shutdown();
    }

    @Test
    void testMixedOrdersUnderLoad() throws InterruptedException, ExecutionException {
        int NUM_REQUESTS = 10;
        List<String> invalidOrders = generateMixedOrders(NUM_REQUESTS);

        ExecutorService executor = Executors.newFixedThreadPool(100);
        List<Callable<Void>> tasks = new ArrayList<>();

        for (String orderRequest : invalidOrders) {
            tasks.add(() -> {
                long startTime = System.currentTimeMillis();
                try {
                    ResponseEntity<LngLat[]> response = controller.calcDeliveryPath(orderRequest);
                    long endTime = System.currentTimeMillis();


                    if ((endTime - startTime) >= TIMEOUT_MS) {
                        fail("Response time exceeded limit. Took " + (endTime - startTime) + " ms.");
                    }


                } catch (Exception e) {

                    e.printStackTrace();
                    fail("Exception during valid order processing: " + e.getMessage());
                }
                return null;
            });
        }


        List<Future<Void>> futures = executor.invokeAll(tasks);

        for (Future<Void> future : futures) {
            future.get();
        }

        executor.shutdown();
    }

    @Test
    void test50ValidOrdersUnderLoad() throws InterruptedException, ExecutionException {
        int NUM_REQUESTS = 50;
        List<String> validOrders = generateValidOrders(NUM_REQUESTS);

        ExecutorService executor = Executors.newFixedThreadPool(100);
        List<Callable<Void>> tasks = new ArrayList<>();

        for (String orderRequest : validOrders) {
            tasks.add(() -> {
                long startTime = System.currentTimeMillis();
                try {
                    ResponseEntity<LngLat[]> response = controller.calcDeliveryPath(orderRequest);
                    long endTime = System.currentTimeMillis();


                    if ((endTime - startTime) >= TIMEOUT_MS) {
                        fail("Response time exceeded limit. Took " + (endTime - startTime) + " ms.");
                    }


                    assertNotNull(response.getBody(), "Response body is null");
                    assertEquals(HttpStatus.OK, response.getStatusCode(), "Expected OK status, but got " + response.getStatusCode());

                } catch (Exception e) {

                    e.printStackTrace();
                    fail("Exception during valid order processing: " + e.getMessage());
                }
                return null;
            });
        }

        List<Future<Void>> futures = executor.invokeAll(tasks);

        for (Future<Void> future : futures) {
            future.get();
        }

        executor.shutdown();
    }

    @Test
    void test50InvalidOrdersUnderLoad() throws InterruptedException, ExecutionException {
        int NUM_REQUESTS = 50;
        List<String> invalidOrders = generateInvalidOrders(NUM_REQUESTS);

        ExecutorService executor = Executors.newFixedThreadPool(100);
        List<Callable<Void>> tasks = new ArrayList<>();

        for (String orderRequest : invalidOrders) {
            tasks.add(() -> {
                long startTime = System.currentTimeMillis();
                try {
                    ResponseEntity<LngLat[]> response = controller.calcDeliveryPath(orderRequest);
                    long endTime = System.currentTimeMillis();


                    if ((endTime - startTime) >= TIMEOUT_MS) {
                        fail("Response time exceeded limit. Took " + (endTime - startTime) + " ms.");
                    }

                    assertNull(response.getBody(), "Response body is null");
                    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode(), "Expected OK status, but got " + response.getStatusCode());

                } catch (Exception e) {

                    e.printStackTrace();
                    fail("Exception during valid order processing: " + e.getMessage());
                }
                return null;
            });
        }


        List<Future<Void>> futures = executor.invokeAll(tasks);

        for (Future<Void> future : futures) {
            future.get();
        }

        executor.shutdown();
    }

    @Test
    void test50MixedOrdersUnderLoad() throws InterruptedException, ExecutionException {
        int NUM_REQUESTS = 50;
        List<String> invalidOrders = generateMixedOrders(NUM_REQUESTS);

        ExecutorService executor = Executors.newFixedThreadPool(100);
        List<Callable<Void>> tasks = new ArrayList<>();

        for (String orderRequest : invalidOrders) {
            tasks.add(() -> {
                long startTime = System.currentTimeMillis();
                try {
                    ResponseEntity<LngLat[]> response = controller.calcDeliveryPath(orderRequest);
                    long endTime = System.currentTimeMillis();


                    if ((endTime - startTime) >= TIMEOUT_MS) {
                        fail("Response time exceeded limit. Took " + (endTime - startTime) + " ms.");
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                    fail("Exception during valid order processing: " + e.getMessage());
                }
                return null;
            });
        }

        List<Future<Void>> futures = executor.invokeAll(tasks);

        for (Future<Void> future : futures) {
            future.get();
        }

        executor.shutdown();
    }

    @Test
    void test100ValidOrdersUnderLoad() throws InterruptedException, ExecutionException {
        int NUM_REQUESTS = 100;
        List<String> validOrders = generateValidOrders(NUM_REQUESTS);

        ExecutorService executor = Executors.newFixedThreadPool(100);
        List<Callable<Void>> tasks = new ArrayList<>();

        for (String orderRequest : validOrders) {
            tasks.add(() -> {
                long startTime = System.currentTimeMillis();
                try {
                    ResponseEntity<LngLat[]> response = controller.calcDeliveryPath(orderRequest);
                    long endTime = System.currentTimeMillis();

                    if ((endTime - startTime) >= TIMEOUT_MS) {
                        fail("Response time exceeded limit. Took " + (endTime - startTime) + " ms.");
                    }

                    assertNotNull(response.getBody(), "Response body is null");
                    assertEquals(HttpStatus.OK, response.getStatusCode(), "Expected OK status, but got " + response.getStatusCode());

                } catch (Exception e) {

                    e.printStackTrace();
                    fail("Exception during valid order processing: " + e.getMessage());
                }
                return null;
            });
        }

        List<Future<Void>> futures = executor.invokeAll(tasks);

        for (Future<Void> future : futures) {
            future.get();
        }

        executor.shutdown();
    }

    @Test
    void test100InvalidOrdersUnderLoad() throws InterruptedException, ExecutionException {
        int NUM_REQUESTS = 100;
        List<String> invalidOrders = generateInvalidOrders(NUM_REQUESTS);

        ExecutorService executor = Executors.newFixedThreadPool(100);
        List<Callable<Void>> tasks = new ArrayList<>();

        for (String orderRequest : invalidOrders) {
            tasks.add(() -> {
                long startTime = System.currentTimeMillis();
                try {
                    ResponseEntity<LngLat[]> response = controller.calcDeliveryPath(orderRequest);
                    long endTime = System.currentTimeMillis();


                    if ((endTime - startTime) >= TIMEOUT_MS) {
                        fail("Response time exceeded limit. Took " + (endTime - startTime) + " ms.");
                    }


                    assertNull(response.getBody(), "Response body is null");
                    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode(), "Expected OK status, but got " + response.getStatusCode());

                } catch (Exception e) {

                    e.printStackTrace();
                    fail("Exception during valid order processing: " + e.getMessage());
                }
                return null;
            });
        }


        List<Future<Void>> futures = executor.invokeAll(tasks);

        for (Future<Void> future : futures) {
            future.get();
        }

        executor.shutdown();
    }

    @Test
    void test100MixedOrdersUnderLoad() throws InterruptedException, ExecutionException {
        int NUM_REQUESTS = 100;
        List<String> invalidOrders = generateMixedOrders(NUM_REQUESTS);

        ExecutorService executor = Executors.newFixedThreadPool(100);
        List<Callable<Void>> tasks = new ArrayList<>();

        for (String orderRequest : invalidOrders) {
            tasks.add(() -> {
                long startTime = System.currentTimeMillis();
                try {
                    ResponseEntity<LngLat[]> response = controller.calcDeliveryPath(orderRequest);
                    long endTime = System.currentTimeMillis();

                    if ((endTime - startTime) >= TIMEOUT_MS) {
                        fail("Response time exceeded limit. Took " + (endTime - startTime) + " ms.");
                    }


                } catch (Exception e) {

                    e.printStackTrace();
                    fail("Exception during valid order processing: " + e.getMessage());
                }
                return null;
            });
        }


        List<Future<Void>> futures = executor.invokeAll(tasks);

        for (Future<Void> future : futures) {
            future.get();
        }

        executor.shutdown();
    }


    private List<String> generateValidOrders(int numOrders) {
        List<String> orders = new ArrayList<>();
        for (int i = 0; i < numOrders; i++) {
            String validOrder = "{\n" +
                    "\"orderNo\": \"22CB618D\",\n" +
                    "\"orderDate\": \"2025-02-12\",\n" +
                    "\"orderStatus\": \"VALID\",\n" +
                    "\"orderValidationCode\": \"NO_ERROR\",\n" +
                    "\"priceTotalInPence\": 2400,\n" +
                    "\"pizzasInOrder\": [\n" +
                    "    {\n" +
                    "        \"name\": \"R4: Proper Pizza\",\n" +
                    "        \"priceInPence\": 1400\n" +
                    "    },\n" +
                    "    {\n" +
                    "        \"name\": \"R4: Pineapple & Ham & Cheese\",\n" +
                    "        \"priceInPence\": 900\n" +
                    "    }\n" +
                    "],\n" +
                    "\"creditCardInformation\": {\n" +
                    "    \"creditCardNumber\": \"5206922253630442\",\n" +
                    "    \"creditCardExpiry\": \"07/25\",\n" +
                    "    \"cvv\": \"198\"\n" +
                    "}\n" +
                    "}";

            orders.add(validOrder);
        }
        return orders;
    }

    private List<String> generateInvalidOrders(int numOrders) {
        List<String> orders = new ArrayList<>();
        for (int i = 0; i < numOrders; i++) {
            orders.add("{\"creditCardInformation\": {\"creditCardNumber\": \"1234\", \"cvv\": \"123\", \"creditCardExpiry\": \"12/23\"}, \"pizzasInOrder\": [{\"name\": \"InvalidPizza\", \"priceInPence\": -100}], \"priceTotalInPence\": 1100}");
        }
        return orders;
    }

    private List<String> generateMixedOrders(int numOrders) {
        List<String> orders = new ArrayList<>();
        for (int i = 0; i < numOrders; i++) {
            if (i % 2 == 0) {
                orders.add(generateValidOrders(1).get(0));
            } else {
                orders.add(generateInvalidOrders(1).get(0));
            }
        }
        return orders;
    }
}
