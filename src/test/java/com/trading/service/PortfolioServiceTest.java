package com.trading.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

import com.trading.model.Account;
import com.trading.model.Stock;
import com.trading.model.User;

class PortfolioServiceTest {

    @Test
    void shouldCalculateMarketValueAndUnrealizedPnL() {
        User user = new User("user", "user@example.com");
        Account account = new Account(user, new BigDecimal("5000.00"));
        Stock stock = new Stock("AAPL", "Apple Inc.", new BigDecimal("200.00"));
		account.getPortfolio().addHolding(stock, 10, new BigDecimal("150.00"));

        PortfolioService portfolioService = new PortfolioService();
        BigDecimal marketValue = portfolioService.calculateMarketValue(account.getPortfolio(), new FakePriceProvider());
        BigDecimal costBasis = portfolioService.calculateCostBasis(account.getPortfolio());
        BigDecimal unrealizedPnL = portfolioService.calculateUnrealizedPnL(account.getPortfolio(), new FakePriceProvider());

        assertThat(marketValue).isEqualByComparingTo(new BigDecimal("2000.00"));
        assertThat(costBasis).isEqualByComparingTo(new BigDecimal("1500.00"));
        assertThat(unrealizedPnL).isEqualByComparingTo(new BigDecimal("500.00"));
    }
}
