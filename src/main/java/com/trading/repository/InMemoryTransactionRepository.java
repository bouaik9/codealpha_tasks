package com.trading.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import com.trading.model.Transaction;

public class InMemoryTransactionRepository implements TransactionRepository {
    private final Map<Long, Transaction> transactions = new ConcurrentHashMap<>();
    private final AtomicLong sequence = new AtomicLong(1);

    @Override
    public void save(Transaction transaction) {
        if (transaction.getId() == null) {
            transaction.setId(sequence.getAndIncrement());
        }
        transactions.put(transaction.getId(), transaction);
    }

    @Override
    public Optional<Transaction> findById(Long id) {
        return Optional.ofNullable(transactions.get(id));
    }

    @Override
    public List<Transaction> findByAccountId(Long accountId) {
        List<Transaction> result = new ArrayList<>();
        for (Transaction transaction : transactions.values()) {
            if (transaction.getAccount().getId().equals(accountId)) {
                result.add(transaction);
            }
        }
        return result;
    }
}
