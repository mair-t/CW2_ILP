package uk.ac.ed.inf.cw2_ilp.dataTypes;
import java.time.LocalDate;

public class Order {

    public String orderNumber;

    public LocalDate date;

    public OrderStatus status;

    public OrderValidationResult validationResult;

    public int priceTotal;

    public Pizza[] pizzas;

    public CreditCardInformation creditCardInformation;

    public LocalDate getDate() {
        return date;
    }

    public OrderStatus getStatus() {
        return status;
    }
    public OrderValidationResult getValidationResult() {
        return validationResult;
    }
    public int getPriceTotal() {
        return priceTotal;
    }
    public Pizza[] getPizzas() {
        return pizzas;
    }
    public CreditCardInformation getCreditCardInformation() {
        return creditCardInformation;
    }
}
