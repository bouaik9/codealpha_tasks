package com.trading.repository;

import java.util.List;
import java.util.Optional;

import com.trading.model.Order;

public interface OrderRepository {
    void save(Order order);
    Optional<Order> findById(Long id);
    List<Order> findByAccountId(Long accountId);
}
