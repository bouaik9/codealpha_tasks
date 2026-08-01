package com.trading.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.trading.exception.InvalidQuantityException;
import com.trading.service.PriceProvider;

public class Portfolio {
    private Long id;
    private Account account;
    private List<Holding> holdings;

    public Portfolio(Account account) {
        this.account = account;
        this.holdings = new ArrayList<>();
    }

    public Portfolio(String owner) {
        this(new Account(owner, 0.0));
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

    public void setAccount(Account account) {
        this.account = account;
    }

    public List<Holding> getHoldings() {
        return holdings;
    }

    public double getTotalValue() {
        return calculateMarketValue((PriceProvider) symbol -> BigDecimal.ZERO).doubleValue();
    }

    public void addHolding(Stock stock, int quantity, BigDecimal price) {
        if (stock == null) {
            throw new IllegalArgumentException("Stock is required");
        }
        if (quantity <= 0) {
            throw new InvalidQuantityException("Quantity must be positive");
        }
        if (price == null || price.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Price must be positive");
        }

        Optional<Holding> existing = getHolding(stock.getSymbol());
        if (existing.isPresent()) {
            existing.get().addShares(quantity, price);
            return;
        }

        Holding holding = new Holding(stock, quantity, price);
        holding.setPortfolio(this);
        holdings.add(holding);
    }

    public void addHolding(String symbol, int quantity, double price) {
        addHolding(new Stock(symbol, BigDecimal.valueOf(price)), quantity, BigDecimal.valueOf(price));
    }

    public Optional<Holding> getHolding(String symbol) {
        return holdings.stream()
                .filter(h -> h.getSymbol().equalsIgnoreCase(symbol))
                .findFirst();
    }

    public BigDecimal calculateMarketValue(PriceProvider priceProvider) {
        BigDecimal total = BigDecimal.ZERO;
        for (Holding holding : holdings) {
            BigDecimal currentPrice = priceProvider.getCurrentPrice(holding.getSymbol());
            total = total.add(currentPrice.multiply(BigDecimal.valueOf(holding.getQuantity())));
        }
        return total;
    }

    public BigDecimal calculateCostBasis() {
        BigDecimal total = BigDecimal.ZERO;
        for (Holding holding : holdings) {
            total = total.add(holding.getAveragePurchasePrice().multiply(BigDecimal.valueOf(holding.getQuantity())));
        }
        return total;
    }

    public BigDecimal calculateUnrealizedPnL(PriceProvider priceProvider) {
        return calculateMarketValue(priceProvider).subtract(calculateCostBasis());
    }

    public void reduceHolding(String symbol, int quantity) {
        Optional<Holding> existing = getHolding(symbol);
        if (existing.isEmpty()) {
            throw new IllegalArgumentException("Holding not found");
        }
        existing.get().removeShares(quantity);
        if (existing.get().getQuantity() == 0) {
            holdings.remove(existing.get());
        }
    }

    public void removeHolding(String symbol) {
        holdings.removeIf(h -> h.getSymbol().equalsIgnoreCase(symbol));
    }
}

