package uk.ac.ed.inf.cw2_ilp;

import uk.ac.ed.inf.cw2_ilp.dataTypes.*;

import java.time.DateTimeException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Validation {


    //check if a given string is invalid, returns true for invalid strings
    public static boolean isntValidString(String input) {
        return input == null || input.isEmpty();
    }

    //checks if a given position is valid, returns true for valid positions
    public static boolean isValidPosition(LngLat position) {
        if (position == null) {
            return false;
        }

        Double lat = position.getLat();
        Double lng = position.getLng();

        //if any of the points is outside the valid range return false
        return lat != null && !lat.isNaN() && lat >= -90 && lat <= 90 &&
                lng != null && !lng.isNaN() && lng >= -180 && lng <= 180;

    }

    //check the angle is valid, return true if angle is valid
    public static boolean isValidAngle(Double checkAngle) {
        return (checkAngle != null && checkAngle >= 0 && checkAngle <= 360);
    }


    //checks to make sue the region provided is valid, returns true if so
    public static boolean isValidRegion(NamedRegion region) {

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
    //check that all characters in a string are digits
    private static boolean isDigitString(String input) {
        return input.chars().allMatch(Character::isDigit);
    }

    //checks the credit card information to see if its valid
    public static OrderValidationCode creditCardCheck(CreditCardInformation creditCardInformation, Order order) {
        //get the 3 aspects of the credit card information as strings
        String creditCardNumber = creditCardInformation.getCreditCardNumber();
        String CVV = creditCardInformation.getCvv();
        String expiryDate = creditCardInformation.getCreditCardExpiry();

        //Check if the CVV is valid and return CVV_INVALID if not
        if(CVV.length() != 3 || isntValidString(CVV) || !isDigitString(CVV)){
            return OrderValidationCode.CVV_INVALID;
        }
        //Check if the creditCardNumber is valid and return CARD_NUMBER_INVALID if not
        if(!isDigitString(creditCardNumber)|| isntValidString(creditCardNumber)|| creditCardNumber.length() != 16){
            return OrderValidationCode.CARD_NUMBER_INVALID;
        }
        try {
            //format the given date as MM/yy
            DateTimeFormatter expiryFormatter = DateTimeFormatter.ofPattern("MM/yy");
            YearMonth expiryYearMonth = YearMonth.parse(expiryDate, expiryFormatter);


            LocalDate expiry = expiryYearMonth.atEndOfMonth();

            LocalDate orderDate = LocalDate.parse(order.getDate());

            //if the expiry date is not after the order date return EXPIRY_DATE_INVALID
            if (!expiry.isAfter(orderDate)) {
                return OrderValidationCode.EXPIRY_DATE_INVALID;
            }
            //if there are no issues return NO_ERROR
            return OrderValidationCode.NO_ERROR;
        }
        catch  (DateTimeException e) {
            // Return invalid result if date parsing fails
            return OrderValidationCode.EXPIRY_DATE_INVALID;
        }
    }

    //checks that elements associated with pizza and restaurants are valid or returns and updates the result
    public static OrderValidationCode pizzaCheck(Pizza[] pizzas, Order currentOrder){
        int total = 0;
        //if there are more than 4 pizzas it is invalid
        if( pizzas.length>4){
            return OrderValidationCode.MAX_PIZZA_COUNT_EXCEEDED;
        }
        if(pizzas.length == 0 ){
            return OrderValidationCode.EMPTY_ORDER;
        }
        //add up the order total
        for (Pizza pizza : pizzas){
            total = total + pizza.priceInPence;
        }
        //add the delivery fee
        total += 100;
        // if the totals are not equal then return TOTAL_INCORRECT
        if(total != currentOrder.getPriceTotalInPence()){
            return OrderValidationCode.TOTAL_INCORRECT;
        }
        //create a set of valid pizzas
        Set<String> validPizzas = new HashSet<>();
        //fetch the list of restaurants from the rest service
        List<Restaurant> restaurants = FetchFunctions.fetchRestaurants();

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
                return OrderValidationCode.PIZZA_NOT_DEFINED;
            }

            //using the name figure out which restaurant the pizza came from
            String name = pizza.getName();
            Restaurant restaurant = FetchFunctions.getRestaurantForPizza(name,restaurants);

            //get the menu for this restaurant
            Pizza[] menu = restaurant.getMenu();
            List <String> menuPizzas = Arrays.stream(menu).map(Pizza::getName).toList();
            //if this restaurant isnt open return RESTAURANT_CLOSED
            if(!isRestaurantOpen(restaurant, currentOrder.getDate())){
                return OrderValidationCode.RESTAURANT_CLOSED;
            }
            //if the price on the order doesnt match the menu then return PRICE_FOR_PIZZA_INVALID
            int index = menuPizzas.indexOf(pizza.getName());

            if(pizza.getPriceInPence() != menu[index].getPriceInPence()){
                return OrderValidationCode.PRICE_FOR_PIZZA_INVALID;
            }

            //if the restaurant doesn't match the pizza before return PIZZA_FROM_MULTIPLE_RESTAURANTS
            if (firstRestaurant == null) {
                firstRestaurant = restaurant;
            } else if (!restaurant.equals(firstRestaurant)) {
                return OrderValidationCode.PIZZA_FROM_MULTIPLE_RESTAURANTS;
            }


        }
        //else return NO_ERROR
        return OrderValidationCode.NO_ERROR;
    }

    //given a date calculate if the restaurant given is open
    private static boolean isRestaurantOpen(Restaurant restaurant, String orderDate) {
        //calculate what day it is using the date
        LocalDate date = LocalDate.parse(orderDate);
        DayOfWeek day = date.getDayOfWeek();
        //return true if the day is in the list of opening days
        return restaurant.openingDays.contains(day);
    }







}
