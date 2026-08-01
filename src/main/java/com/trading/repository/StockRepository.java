package com.trading.repository;

import java.util.Optional;

import com.trading.model.Stock;

public interface StockRepository {
    void save(Stock stock);
    Optional<Stock> findBySymbol(String symbol);
    java.util.List<Stock> findAll();
}
