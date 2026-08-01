package com.trading.repository;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import com.trading.model.Account;

public class InMemoryAccountRepository implements AccountRepository {
    private final Map<Long, Account> accounts = new ConcurrentHashMap<>();
    private final AtomicLong sequence = new AtomicLong(1);

    @Override
    public void save(Account account) {
        if (account.getId() == null) {
            account.setId(sequence.getAndIncrement());
        }
        accounts.put(account.getId(), account);
    }

    @Override
    public Optional<Account> findById(Long id) {
        return Optional.ofNullable(accounts.get(id));
    }

    @Override
    public void deleteById(Long id) {
        accounts.remove(id);
    }
}
