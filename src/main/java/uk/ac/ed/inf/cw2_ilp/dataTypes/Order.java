package uk.ac.ed.inf.cw2_ilp.dataTypes;

import com.fasterxml.jackson.annotation.JsonProperty;

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
    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }
    public void setPriceTotalInPence(int priceTotalInPence) {
        this.priceTotalInPence = priceTotalInPence;
    }
    public void setPizzasInOrder(Pizza[] pizzasInOrder) {
        this.pizzasInOrder = pizzasInOrder;
    }

    public void setCreditCardInformation(CreditCardInformation creditCardInformation) {
        this.creditCardInformation = creditCardInformation;
    }
}
