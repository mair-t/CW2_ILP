package uk.ac.ed.inf.cw2_ilp.dataTypes;

public class Order {

    public String orderNo;

    public String orderDate;
    public OrderStatus orderStatus;
    public OrderValidationCode orderValidationCode;

    public int priceTotalInPence;

    public Pizza[] pizzasInOrder;

    public CreditCardInformation creditCardInformation;


    public String getDate() {
        return orderDate;
    }

    public OrderStatus getOrderStatus() {
        return orderStatus;
    }
    public OrderValidationCode getOrderValidationCode() {
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

    public void setOrderValidationCode(OrderValidationCode orderValidationCode) {
        this.orderValidationCode = orderValidationCode;
    }

    public void setOrderStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }
}
