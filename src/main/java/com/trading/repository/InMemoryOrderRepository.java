package com.trading.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import com.trading.model.Order;

public class InMemoryOrderRepository implements OrderRepository {
    private final Map<Long, Order> orders = new ConcurrentHashMap<>();
    private final AtomicLong sequence = new AtomicLong(1);

    @Override
    public void save(Order order) {
        if (order.getId() == null) {
            order.setId(sequence.getAndIncrement());
        }
        orders.put(order.getId(), order);
    }

    @Override
    public Optional<Order> findById(Long id) {
        return Optional.ofNullable(orders.get(id));
    }

    @Override
    public List<Order> findByAccountId(Long accountId) {
        List<Order> result = new ArrayList<>();
        for (Order order : orders.values()) {
            if (order.getAccount().getId().equals(accountId)) {
                result.add(order);
            }
        }
        return result;
    }
}
