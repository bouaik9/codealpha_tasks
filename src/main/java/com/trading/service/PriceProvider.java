package com.trading.service;

import java.math.BigDecimal;

public interface PriceProvider {
    BigDecimal getCurrentPrice(String symbol);
}
