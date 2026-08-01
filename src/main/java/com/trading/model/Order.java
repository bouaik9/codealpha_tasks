package com.trading.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.trading.exception.InvalidAmountException;
import com.trading.exception.InvalidQuantityException;

public class Order {
    private Long id;
    private Account account;
    private Stock stock;
    private OrderType type;
    private int quantity;
    private BigDecimal unitPrice;
    private BigDecimal totalAmount;
    private OrderStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime executedAt;

    public Order(Account account, Stock stock, OrderType type, int quantity, BigDecimal unitPrice) {
        if (account == null) {
            throw new IllegalArgumentException("Account is required");
        }
        if (stock == null) {
            throw new IllegalArgumentException("Stock is required");
        }
        if (type == null) {
            throw new IllegalArgumentException("Order type is required");
        }
        if (quantity <= 0) {
            throw new InvalidQuantityException("Order quantity must be positive");
        }
        if (unitPrice == null || unitPrice.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidAmountException("Order price must be positive");
        }

        this.account = account;
        this.stock = stock;
        this.type = type;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.totalAmount = unitPrice.multiply(BigDecimal.valueOf(quantity));
        this.status = OrderStatus.PENDING;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Account getAccount() {
        return account;
    }

    public Stock getStock() {
        return stock;
    }

    public OrderType getType() {
        return type;
    }

    public int getQuantity() {
        return quantity;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getExecutedAt() {
        return executedAt;
    }

    public void markExecuted() {
        this.status = OrderStatus.EXECUTED;
        this.executedAt = LocalDateTime.now();
    }
}
