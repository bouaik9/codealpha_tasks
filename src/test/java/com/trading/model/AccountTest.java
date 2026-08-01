package com.trading.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class AccountTest {

    private Account account;

    @BeforeEach
    void setup() {
        account = new Account("Ayoub", 100.00);
    }

    @Test
    void shouldInitializeAccountWithBalance() {
        assertEquals("Ayoub", account.getOwner());
        assertEquals(new BigDecimal("100.00"), account.getCashBalance());
    }

    @Test
    void shouldDebitFunds() {
        account.debit(new BigDecimal("20.00"));
        assertEquals(new BigDecimal("80.00"), account.getCashBalance());
    }

    @Test
    void shouldCreateFunds() {
        account.credit(new BigDecimal("100.00"));
        assertThat(account.getCashBalance()).isEqualByComparingTo(new BigDecimal("200.00"));
    }

    @Test
    void shouldThrowOnInsufficientFunds() {
        assertThatThrownBy(() -> account.debit(new BigDecimal("15000.00")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Insufficient funds");
    }

    @Test
    void shouldThrowOnNegativeAmount() {
        assertThatThrownBy(() -> account.debit(new BigDecimal("-100.00")))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
