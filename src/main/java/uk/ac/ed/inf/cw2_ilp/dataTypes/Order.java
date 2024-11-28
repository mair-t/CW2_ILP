package uk.ac.ed.inf.cw2_ilp.dataTypes;
import java.time.LocalDate;
import java.util.List;

public class Order {

    public String orderNo;
    public String orderDate;
    public OrderStatus orderStatus;
    public OrderValidationResult orderValidationCode;

    public int priceTotalInPence;

    public Pizza[] pizzasInOrder;

    public CreditCardInformation creditCardInformation;

    public String getDate() {
        return orderDate;
    }

    public OrderStatus getOrderStatus() {
        return orderStatus;
    }
    public OrderValidationResult getOrderValidationCode() {
        return orderValidationCode;
    }
    public int getPriceTotalInPence() {
        return priceTotalInPence;
    }
    public Pizza[] getPizzasInOrder() {
        return pizzasInOrder;
    }
    public CreditCardInformation getCreditCardInformation() {
        return creditCardInformation;
    }

    public void setOrderValidationCode(OrderValidationResult orderValidationCode) {
        this.orderValidationCode = orderValidationCode;
    }
}
