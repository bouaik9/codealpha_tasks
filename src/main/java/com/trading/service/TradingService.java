package com.trading.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import com.trading.exception.InsufficientFundsException;
import com.trading.exception.InvalidAmountException;
import com.trading.exception.InvalidPriceException;
import com.trading.exception.InvalidQuantityException;
import com.trading.model.Account;
import com.trading.model.Holding;
import com.trading.model.Order;
import com.trading.model.OrderType;
import com.trading.model.Portfolio;
import com.trading.model.Stock;
import com.trading.model.Transaction;
import com.trading.model.TransactionType;
import com.trading.repository.AccountRepository;
import com.trading.repository.OrderRepository;
import com.trading.repository.StockRepository;
import com.trading.repository.TransactionRepository;

public class TradingService {
    private final AccountRepository accountRepository;
    private final StockRepository stockRepository;
    private final OrderRepository orderRepository;
    private final TransactionRepository transactionRepository;
    private final PriceProvider priceProvider;

    public TradingService(
            AccountRepository accountRepository,
            StockRepository stockRepository,
            OrderRepository orderRepository,
            TransactionRepository transactionRepository,
            PriceProvider priceProvider) {
        this.accountRepository = accountRepository;
        this.stockRepository = stockRepository;
        this.orderRepository = orderRepository;
        this.transactionRepository = transactionRepository;
        this.priceProvider = priceProvider;
    }

    public Order buyStock(Long accountId, String symbol, int quantity) {
        if (quantity <= 0) {
            throw new InvalidQuantityException("Quantity must be positive");
        }

        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Account not found"));

        Stock stock = stockRepository.findBySymbol(symbol)
                .orElseThrow(() -> new IllegalArgumentException("Stock not found"));

        BigDecimal price = priceProvider.getCurrentPrice(symbol);
        if (price == null || price.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidPriceException("Price must be positive");
        }

        BigDecimal totalValue = price.multiply(BigDecimal.valueOf(quantity));
        if (account.getCashBalance().compareTo(totalValue) < 0) {
            throw new InsufficientFundsException("Insufficient funds");
        }

        Order order = new Order(account, stock, OrderType.BUY, quantity, price);
        account.debit(totalValue);
        Portfolio portfolio = account.getPortfolio();
        portfolio.addHolding(stock, quantity, price);
        Transaction transaction = new Transaction(account, order, TransactionType.BUY, stock.getSymbol(), quantity, totalValue, price);

        order.markExecuted();
        orderRepository.save(order);
        transactionRepository.save(transaction);
        accountRepository.save(account);

        return order;
    }

    public Order sellStock(Long accountId, String symbol, int quantity) {
        if (quantity <= 0) {
            throw new InvalidQuantityException("Quantity must be positive");
        }

        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Account not found"));

        Stock stock = stockRepository.findBySymbol(symbol)
                .orElseThrow(() -> new IllegalArgumentException("Stock not found"));

        BigDecimal price = priceProvider.getCurrentPrice(symbol);
        if (price == null || price.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidPriceException("Price must be positive");
        }

        Portfolio portfolio = account.getPortfolio();
        Holding holding = portfolio.getHolding(symbol)
                .orElseThrow(() -> new IllegalArgumentException("Holding not found"));

        if (holding.getQuantity() < quantity) {
            throw new IllegalArgumentException("Insufficient shares");
        }

        BigDecimal saleValue = price.multiply(BigDecimal.valueOf(quantity));
        Order order = new Order(account, stock, OrderType.SELL, quantity, price);
        holding.removeShares(quantity);
        account.credit(saleValue);
        Transaction transaction = new Transaction(account, order, TransactionType.SELL, stock.getSymbol(), quantity, saleValue, price);

        order.markExecuted();
        orderRepository.save(order);
        transactionRepository.save(transaction);
        accountRepository.save(account);

        return order;
    }

    public List<Order> getOrdersForAccount(Long accountId) {
        return orderRepository.findByAccountId(accountId);
    }

    public List<Transaction> getTransactionsForAccount(Long accountId) {
        return transactionRepository.findByAccountId(accountId);
    }
}
