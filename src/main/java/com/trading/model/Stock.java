package com.trading.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Stock {
    private String symbol;
    private String name;
    private BigDecimal currentPrice;
    private final List<BigDecimal> priceHistory = new ArrayList<>();

    public Stock(String symbol, BigDecimal price) {
        this(symbol, symbol, price);
    }

    public Stock(String symbol, String name, BigDecimal price) {
        if (symbol == null || symbol.trim().isEmpty()) {
            throw new IllegalArgumentException("Stock symbol is required");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Stock name is required");
        }
        if (price == null || price.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Price must be positive");
        }
        this.symbol = symbol.trim().toUpperCase();
        this.name = name.trim();
        this.currentPrice = normalizeMoney(price);
        this.priceHistory.add(this.currentPrice);
    }

    public Stock(String symbol, double price) {
        this(symbol, symbol, BigDecimal.valueOf(price));
    }

    public String getSymbol() {
        return symbol;
    }

    public String getName() {
        return name;
    }

    public BigDecimal getCurrentPrice() {
        return currentPrice;
    }

    public double getCurrentPriceValue() {
        return currentPrice.doubleValue();
    }

    public void updatePrice(BigDecimal price) {
        if (price == null || price.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Price must be positive");
        }
        this.currentPrice = normalizeMoney(price);
        this.priceHistory.add(this.currentPrice);
    }

    public void updatePrice(double price) {
        updatePrice(BigDecimal.valueOf(price));
    }

    public List<BigDecimal> getPriceHistory() {
        return Collections.unmodifiableList(priceHistory);
    }

    private BigDecimal normalizeMoney(BigDecimal amount) {
        return amount.setScale(2, RoundingMode.HALF_UP);
    }
}

