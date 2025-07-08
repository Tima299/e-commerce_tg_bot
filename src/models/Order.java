package models;
import enums.OrderStatus;

public class Order {
    private final Long id;
    private final Long userId;
    private final String productName;
    private final String orderDate;
    private final OrderStatus status;

    public Order(Long id, Long userId, String productName, String orderDate, OrderStatus status) {
        this.id = id;
        this.userId = userId;
        this.productName = productName;
        this.orderDate = orderDate;
        this.status = status;
    }

    public Long getId() { return id; }
    public String getProductName() { return productName; }
    public String getOrderDate() { return orderDate; }
    public OrderStatus getStatus() { return status; }
}