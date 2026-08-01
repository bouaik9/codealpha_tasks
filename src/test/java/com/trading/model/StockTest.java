package com.trading.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class StockTest {

    @Test
    @DisplayName("Should create stock with symbol and price")
    void shouldCreateStock() {
        Stock apple = new Stock("AAPL", 150.00);

        assertThat(apple.getSymbol()).isEqualTo("AAPL");
        assertThat(apple.getCurrentPrice()).isEqualByComparingTo(new BigDecimal("150.00"));
    }

    @Test
    @DisplayName("Should update stock price")
    void shouldUpdatePrice() {
        Stock apple = new Stock("AAPL", 150.00);

        apple.updatePrice(155.50);

        assertThat(apple.getCurrentPrice()).isEqualByComparingTo(new BigDecimal("155.50"));
    }

    @Test
    @DisplayName("Should track price history")
    void shouldTrackPriceHistory() {
        Stock apple = new Stock("AAPL", 150.00);

        apple.updatePrice(155.00);
        apple.updatePrice(160.00);

        assertThat(apple.getPriceHistory()).hasSize(3)
                .contains(new BigDecimal("150.00"), new BigDecimal("155.00"), new BigDecimal("160.00"));
    }

    @Test
    @DisplayName("Should reject negative price")
    void shouldRejectNegativePrice() {
        assertThatThrownBy(() -> new Stock("AAPL", -10.00))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Price");
    }
}