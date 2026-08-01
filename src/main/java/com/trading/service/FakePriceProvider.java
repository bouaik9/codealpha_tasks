package com.trading.service;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class FakePriceProvider implements PriceProvider {
    private final Map<String, BigDecimal> prices = new ConcurrentHashMap<>();

    public FakePriceProvider() {
        prices.put("AAPL", new BigDecimal("200.00"));
        prices.put("MSFT", new BigDecimal("300.00"));
        prices.put("GOOG", new BigDecimal("150.00"));
    }

    @Override
    public BigDecimal getCurrentPrice(String symbol) {
        if (symbol == null || symbol.trim().isEmpty()) {
            throw new IllegalArgumentException("Symbol is required");
        }
        BigDecimal price = prices.get(symbol.trim().toUpperCase());
        if (price == null) {
            throw new IllegalArgumentException("Stock not found");
        }
        return price;
    }
}
