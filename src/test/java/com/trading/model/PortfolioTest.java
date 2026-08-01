package com.trading.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PortfolioTest {

    private Portfolio portfolio;

    @BeforeEach
    void setUp() {
        portfolio = new Portfolio("trader1");
    }

    @Test
    void shouldStartEmpty() {
        assertThat(portfolio.getHoldings()).isEmpty();
        assertThat(portfolio.getTotalValue()).isZero();
    }

    @Test
    void shouldAddHolding() {
        portfolio.addHolding("AAPL", 10, 150.00);

        assertThat(portfolio.getHolding("AAPL"))
                .isPresent()
                .hasValueSatisfying(h -> {
                    assertThat(h.getQuantity()).isEqualTo(10);
                    assertThat(h.getAverageCost()).isEqualTo(150.00);
                });
    }

    @Test
    void shouldUpdateExistingHolding() {
        portfolio.addHolding("AAPL", 10, 150.00);
        portfolio.addHolding("AAPL", 5, 200.00);

        Holding holding = portfolio.getHolding("AAPL").orElseThrow();
        assertThat(holding.getQuantity()).isEqualTo(15);
    }

    @Test
    void shouldRemoveHoldingWhenAllSharesSold() {
        portfolio.addHolding("AAPL", 10, 150.00);
        portfolio.reduceHolding("AAPL", 10);

        assertThat(portfolio.getHolding("AAPL")).isEmpty();
    }

    @Test
    void shouldCalculateTotalValue() {
        portfolio.addHolding("AAPL", 10, 150.00);
        BigDecimal value = portfolio.calculateMarketValue(symbol -> new BigDecimal("200.00"));

        assertThat(value).isEqualByComparingTo(new BigDecimal("2000.00"));
    }

    @Test
    void shouldCalculateCostBasis() {
        portfolio.addHolding("AAPL", 10, 150.00);
        assertThat(portfolio.calculateCostBasis()).isEqualByComparingTo(new BigDecimal("1500.00"));
    }
}