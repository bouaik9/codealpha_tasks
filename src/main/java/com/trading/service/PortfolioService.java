package com.trading.service;

import java.math.BigDecimal;

import com.trading.model.Portfolio;

public class PortfolioService {

    public BigDecimal calculateMarketValue(Portfolio portfolio, PriceProvider priceProvider) {
        return portfolio.calculateMarketValue(priceProvider);
    }

    public BigDecimal calculateCostBasis(Portfolio portfolio) {
        return portfolio.calculateCostBasis();
    }

    public BigDecimal calculateUnrealizedPnL(Portfolio portfolio, PriceProvider priceProvider) {
        return portfolio.calculateUnrealizedPnL(priceProvider);
    }
}
