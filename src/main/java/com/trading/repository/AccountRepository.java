package com.trading.repository;

import java.util.Optional;

import com.trading.model.Account;

public interface AccountRepository {
    void save(Account account);
    Optional<Account> findById(Long id);
    void deleteById(Long id);
}
