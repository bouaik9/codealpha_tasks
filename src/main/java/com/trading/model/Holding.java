package com.trading.model;

import java.math.BigDecimal;
import java.math.RoundingMode;

import com.trading.exception.InvalidQuantityException;

public class Holding {
    private Long id;
    private Portfolio portfolio;
    private Stock stock;
    private int quantity;
    private BigDecimal averagePurchasePrice;

    public Holding(Stock stock, int quantity, BigDecimal averagePurchasePrice) {
        if (stock == null) {
            throw new IllegalArgumentException("Stock is required");
        }
        if (quantity <= 0) {
            throw new InvalidQuantityException("Holding quantity must be positive");
        }
        if (averagePurchasePrice == null || averagePurchasePrice.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Average purchase price must be positive");
        }
        this.stock = stock;
        this.quantity = quantity;
        this.averagePurchasePrice = averagePurchasePrice;
    }

    public Holding(String symbol, int quantity, double averageCost) {
        this(new Stock(symbol, BigDecimal.valueOf(averageCost)), quantity, BigDecimal.valueOf(averageCost));
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Portfolio getPortfolio() {
        return portfolio;
    }

    public void setPortfolio(Portfolio portfolio) {
        this.portfolio = portfolio;
    }

    public Stock getStock() {
        return stock;
    }

    public String getSymbol() {
        return stock != null ? stock.getSymbol() : null;
    }

    public int getQuantity() {
        return quantity;
    }

    public BigDecimal getAveragePurchasePrice() {
        return averagePurchasePrice;
    }

    public double getAverageCost() {
        return averagePurchasePrice.doubleValue();
    }

    public void addShares(int quantityToAdd, BigDecimal price) {
        if (quantityToAdd <= 0) {
            throw new InvalidQuantityException("Quantity to add must be positive");
        }
        if (price == null || price.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Price must be positive");
        }

        BigDecimal oldCostBasis = averagePurchasePrice.multiply(BigDecimal.valueOf(this.quantity));
        BigDecimal newCostBasis = price.multiply(BigDecimal.valueOf(quantityToAdd));
        int newTotalQuantity = this.quantity + quantityToAdd;
        this.averagePurchasePrice = oldCostBasis.add(newCostBasis)
                .divide(BigDecimal.valueOf(newTotalQuantity), 2, RoundingMode.HALF_UP);
        this.quantity = newTotalQuantity;
    }

    public void addShares(int quantityToAdd, double price) {
        addShares(quantityToAdd, BigDecimal.valueOf(price));
    }

    public void removeShares(int quantityToRemove) {
        if (quantityToRemove <= 0) {
            throw new InvalidQuantityException("Quantity to remove must be positive");
        }
        if (quantityToRemove > this.quantity) {
            throw new IllegalArgumentException("Insufficient shares");
        }
        this.quantity -= quantityToRemove;
        if (this.quantity == 0) {
            if (portfolio != null) {
                portfolio.removeHolding(this.stock.getSymbol());
            }
        }
    }
}

  