package com.trading.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

import com.trading.exception.InvalidAmountException;

public class Account {
    private Long id;
    private User user;
    private BigDecimal cashBalance;
    private Portfolio portfolio;

    public Account(User user, BigDecimal cashBalance) {
        this.user = Objects.requireNonNull(user, "User is required");
        this.cashBalance = normalizeMoney(cashBalance == null ? BigDecimal.ZERO : cashBalance);
        if (this.cashBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new InvalidAmountException("Account balance cannot be negative");
        }
        this.portfolio = new Portfolio(this);
    }

    public Account(String owner, double balance) {
        this(new User(owner, owner + "@example.com"), BigDecimal.valueOf(balance));
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getOwner() {
        return user != null ? user.getUsername() : null;
    }

    public BigDecimal getCashBalance() {
        return cashBalance;
    }

    public void setCashBalance(BigDecimal cashBalance) {
        if (cashBalance == null) {
            throw new InvalidAmountException("Cash balance is required");
        }
        if (cashBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new InvalidAmountException("Account balance cannot be negative");
        }
        this.cashBalance = normalizeMoney(cashBalance);
    }

    public double getBalance() {
        return cashBalance.doubleValue();
    }

    public Portfolio getPortfolio() {
        return portfolio;
    }

    public void setPortfolio(Portfolio portfolio) {
        this.portfolio = portfolio;
    }

    public void deposit(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidAmountException("Deposit amount must be positive");
        }
        this.cashBalance = normalizeMoney(this.cashBalance.add(normalizeMoney(amount)));
    }

    public void withdraw(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidAmountException("Withdrawal amount must be positive");
        }
        if (amount.compareTo(this.cashBalance) > 0) {
            throw new InvalidAmountException("Insufficient funds");
        }
        this.cashBalance = normalizeMoney(this.cashBalance.subtract(normalizeMoney(amount)));
    }

    public void debit(BigDecimal amount) {
        withdraw(amount);
    }

    public void credit(BigDecimal amount) {
        deposit(amount);
    }

    public void debit(double amount) {
        debit(BigDecimal.valueOf(amount));
    }

    public void credit(double amount) {
        credit(BigDecimal.valueOf(amount));
    }

    private BigDecimal normalizeMoney(BigDecimal amount) {
        return amount.setScale(2, RoundingMode.HALF_UP);
    }
}

