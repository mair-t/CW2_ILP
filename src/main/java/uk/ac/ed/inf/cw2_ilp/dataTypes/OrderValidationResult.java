package uk.ac.ed.inf.cw2_ilp.dataTypes;

public class OrderValidationResult {
    private OrderStatus orderStatus;
    private OrderValidationCode orderValidationCode;

    public OrderValidationCode getOrderValidationCode() {
        return orderValidationCode;
    }
    public void setOrderValidationCode(OrderValidationCode orderValidationCode) {
        this.orderValidationCode = orderValidationCode;
    }
    public OrderStatus getOrderStatus() {
        return orderStatus;
    }
    public void setOrderStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }
}
