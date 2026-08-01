package com.trading.repository;

import java.util.List;
import java.util.Optional;

import com.trading.model.Transaction;

public interface TransactionRepository {
    void save(Transaction transaction);
    Optional<Transaction> findById(Long id);
    List<Transaction> findByAccountId(Long accountId);
}
