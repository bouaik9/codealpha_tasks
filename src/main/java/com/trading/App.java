package com.trading;

import java.math.BigDecimal;

import com.trading.model.Account;
import com.trading.model.Stock;
import com.trading.model.User;
import com.trading.repository.InMemoryAccountRepository;
import com.trading.repository.InMemoryOrderRepository;
import com.trading.repository.InMemoryStockRepository;
import com.trading.repository.InMemoryTransactionRepository;
import com.trading.service.FakePriceProvider;
import com.trading.service.TradingService;

public class App {
    public static void main(String[] args) {
        User user = new User("demo", "demo@example.com");
        Account account = new Account(user, new BigDecimal("10000.00"));
        Stock stock = new Stock("AAPL", "Apple Inc.", new BigDecimal("200.00"));

        InMemoryAccountRepository accountRepository = new InMemoryAccountRepository();
        InMemoryStockRepository stockRepository = new InMemoryStockRepository();
        InMemoryOrderRepository orderRepository = new InMemoryOrderRepository();
        InMemoryTransactionRepository transactionRepository = new InMemoryTransactionRepository();

        accountRepository.save(account);
        stockRepository.save(stock);

        TradingService tradingService = new TradingService(
                accountRepository,
                stockRepository,
                orderRepository,
                transactionRepository,
                new FakePriceProvider()
        );

        tradingService.buyStock(account.getId(), "AAPL", 10);
        System.out.println("Balance: " + account.getCashBalance());
        System.out.println("Portfolio value: " + account.getPortfolio().calculateMarketValue(new FakePriceProvider()));
    }
}
