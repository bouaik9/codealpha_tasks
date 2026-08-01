package com.trading.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.trading.model.Account;
import com.trading.model.Holding;
import com.trading.model.Order;
import com.trading.model.Portfolio;
import com.trading.model.Stock;
import com.trading.model.Transaction;
import com.trading.model.User;
import com.trading.repository.InMemoryAccountRepository;
import com.trading.repository.InMemoryOrderRepository;
import com.trading.repository.InMemoryStockRepository;
import com.trading.repository.InMemoryTransactionRepository;
import com.trading.repository.InMemoryUserRepository;

class TradingServiceTest {

    @Test
    void shouldBuyStockSuccessfully() {
        User user = new User("alice", "alice@example.com");
        Account account = new Account(user, new BigDecimal("5000.00"));
        Stock stock = new Stock("AAPL", "Apple Inc.", new BigDecimal("200.00"));

        InMemoryUserRepository userRepository = new InMemoryUserRepository();
        InMemoryAccountRepository accountRepository = new InMemoryAccountRepository();
        InMemoryStockRepository stockRepository = new InMemoryStockRepository();
        InMemoryOrderRepository orderRepository = new InMemoryOrderRepository();
        InMemoryTransactionRepository transactionRepository = new InMemoryTransactionRepository();

        userRepository.save(user);
        accountRepository.save(account);
        stockRepository.save(stock);

        TradingService tradingService = new TradingService(
                accountRepository,
                stockRepository,
                orderRepository,
                transactionRepository,
                new FakePriceProvider()
        );

        Order order = tradingService.buyStock(account.getId(), "AAPL", 10);

        assertThat(order.getStatus().name()).isEqualTo("EXECUTED");
        assertThat(account.getCashBalance()).isEqualByComparingTo(new BigDecimal("3000.00"));
        assertThat(account.getPortfolio().getHoldings()).hasSize(1);
        assertThat(account.getPortfolio().getHolding("AAPL")).isPresent();
    }

    @Test
    void shouldRejectBuyWithInsufficientFunds() {
        User user = new User("bob", "bob@example.com");
        Account account = new Account(user, new BigDecimal("100.00"));
        Stock stock = new Stock("AAPL", "Apple Inc.", new BigDecimal("200.00"));

        InMemoryUserRepository userRepository = new InMemoryUserRepository();
        InMemoryAccountRepository accountRepository = new InMemoryAccountRepository();
        InMemoryStockRepository stockRepository = new InMemoryStockRepository();
        InMemoryOrderRepository orderRepository = new InMemoryOrderRepository();
        InMemoryTransactionRepository transactionRepository = new InMemoryTransactionRepository();

        userRepository.save(user);
        accountRepository.save(account);
        stockRepository.save(stock);

        TradingService tradingService = new TradingService(
                accountRepository,
                stockRepository,
                orderRepository,
                transactionRepository,
                new FakePriceProvider()
        );

        assertThatThrownBy(() -> tradingService.buyStock(account.getId(), "AAPL", 1))
                .hasMessageContaining("Insufficient funds");
    }

    @Test
    void shouldRejectBuyWithInvalidQuantity() {
        User user = new User("charlie", "charlie@example.com");
        Account account = new Account(user, new BigDecimal("5000.00"));
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

        assertThatThrownBy(() -> tradingService.buyStock(account.getId(), "AAPL", 0))
                .hasMessageContaining("positive");
    }

    @Test
    void shouldRejectBuyWhenStockNotFound() {
        User user = new User("dana", "dana@example.com");
        Account account = new Account(user, new BigDecimal("5000.00"));

        InMemoryAccountRepository accountRepository = new InMemoryAccountRepository();
        InMemoryStockRepository stockRepository = new InMemoryStockRepository();
        InMemoryOrderRepository orderRepository = new InMemoryOrderRepository();
        InMemoryTransactionRepository transactionRepository = new InMemoryTransactionRepository();

        accountRepository.save(account);

        TradingService tradingService = new TradingService(
                accountRepository,
                stockRepository,
                orderRepository,
                transactionRepository,
                new FakePriceProvider()
        );

        assertThatThrownBy(() -> tradingService.buyStock(account.getId(), "MSFT", 5))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    void shouldSellStockSuccessfully() {
        User user = new User("erin", "erin@example.com");
        Account account = new Account(user, new BigDecimal("1000.00"));
        Stock stock = new Stock("AAPL", "Apple Inc.", new BigDecimal("200.00"));

        InMemoryAccountRepository accountRepository = new InMemoryAccountRepository();
        InMemoryStockRepository stockRepository = new InMemoryStockRepository();
        InMemoryOrderRepository orderRepository = new InMemoryOrderRepository();
        InMemoryTransactionRepository transactionRepository = new InMemoryTransactionRepository();

        accountRepository.save(account);
        stockRepository.save(stock);
        account.getPortfolio().addHolding(stock, 10, new BigDecimal("150.00"));

        TradingService tradingService = new TradingService(
                accountRepository,
                stockRepository,
                orderRepository,
                transactionRepository,
                new FakePriceProvider()
        );

        Order order = tradingService.sellStock(account.getId(), "AAPL", 4);

        assertThat(order.getStatus().name()).isEqualTo("EXECUTED");
        assertThat(account.getCashBalance()).isEqualByComparingTo(new BigDecimal("1800.00"));
        assertThat(account.getPortfolio().getHolding("AAPL")).isPresent();
    }

    @Test
    void shouldRejectSellWithInsufficientShares() {
        User user = new User("frank", "frank@example.com");
        Account account = new Account(user, new BigDecimal("1000.00"));
        Stock stock = new Stock("AAPL", "Apple Inc.", new BigDecimal("200.00"));

        InMemoryAccountRepository accountRepository = new InMemoryAccountRepository();
        InMemoryStockRepository stockRepository = new InMemoryStockRepository();
        InMemoryOrderRepository orderRepository = new InMemoryOrderRepository();
        InMemoryTransactionRepository transactionRepository = new InMemoryTransactionRepository();

        accountRepository.save(account);
        stockRepository.save(stock);
        account.getPortfolio().addHolding(stock, 2, new BigDecimal("150.00"));

        TradingService tradingService = new TradingService(
                accountRepository,
                stockRepository,
                orderRepository,
                transactionRepository,
                new FakePriceProvider()
        );

        assertThatThrownBy(() -> tradingService.sellStock(account.getId(), "AAPL", 4))
                .hasMessageContaining("shares");
    }
}
