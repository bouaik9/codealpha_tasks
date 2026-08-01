package com.trading.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import com.trading.model.Stock;

public class InMemoryStockRepository implements StockRepository {
    private final Map<String, Stock> stocks = new ConcurrentHashMap<>();

    @Override
    public void save(Stock stock) {
        stocks.put(stock.getSymbol(), stock);
    }

    @Override
    public Optional<Stock> findBySymbol(String symbol) {
        if (symbol == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(stocks.get(symbol.trim().toUpperCase()));
    }

    @Override
    public List<Stock> findAll() {
        return new ArrayList<>(stocks.values());
    }
}
