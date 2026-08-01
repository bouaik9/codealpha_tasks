package com.trading.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

public class HoldingTest {

    @Test
    void shouldCreateHolding() {
        Holding holding = new Holding("APPL", 10, 150.00);

        assertThat(holding.getSymbol()).isEqualTo("APPL");
        assertThat(holding.getQuantity()).isEqualTo(10);
        assertThat(holding.getAverageCost()).isEqualTo(150.00);
    }

    @Test
    void shouldAddSharesAndRecalculateAverageCost() {
        Holding holding = new Holding("AAPL", 10, 150.00);
        holding.addShares(5, 200.00);

        assertThat(holding.getQuantity()).isEqualTo(15);
        assertThat(holding.getAverageCost()).isEqualTo(166.67);
    }

    @Test
    void shouldRemoveShares() {
        Holding holding = new Holding("AAPL", 10, 150.00);

        holding.removeShares(5);

        assertThat(holding.getQuantity()).isEqualTo(5);
    }

    @Test
    void shouldRejectSellMoreThanHolding() {
        Holding holding = new Holding("AAPL", 10, 150.00);

        assertThrows(IllegalArgumentException.class, () -> holding.removeShares(15));
    }
}
