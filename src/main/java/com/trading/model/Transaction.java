package com.trading.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.trading.exception.InvalidAmountException;
import com.trading.exception.InvalidQuantityException;

public class Transaction {
    private Long id;
    private Account account;
    private Order order;
    private TransactionType type;
    private String stockSymbol;
    private int quantity;
    private BigDecimal amount;
    private BigDecimal unitPrice;
    private LocalDateTime timestamp;

    public Transaction(Account account, Order order, TransactionType type, String stockSymbol, int quantity, BigDecimal amount, BigDecimal unitPrice) {
        if (account == null) {
            throw new IllegalArgumentException("Account is required");
        }
        if (type == null) {
            throw new IllegalArgumentException("Transaction type is required");
        }
        if (amount == null || amount.compareTo(BigDecimal.ZERO) == 0) {
            throw new InvalidAmountException("Transaction amount cannot be zero");
        }
        if (quantity < 0) {
            throw new InvalidQuantityException("Quantity cannot be negative");
        }
        this.account = account;
        this.order = order;
        this.type = type;
        this.stockSymbol = stockSymbol;
        this.quantity = quantity;
        this.amount = amount;
        this.unitPrice = unitPrice;
        this.timestamp = LocalDateTime.now();
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

    public Order getOrder() {
        return order;
    }

    public TransactionType getType() {
        return type;
    }

    public String getStockSymbol() {
        return stockSymbol;
    }

    public int getQuantity() {
        return quantity;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}
