# 05. Buy Stock Sequence

```mermaid
sequenceDiagram
    actor User
    participant TradingService
    participant StockRepository
    participant PriceProvider
    participant AccountRepository
    participant Account
    participant Portfolio
    participant Holding
    participant Order
    participant Transaction
    participant OrderRepository
    participant TransactionRepository

    User->>TradingService: buyStock(accountId, symbol, quantity)

    TradingService->>StockRepository: findBySymbol(symbol)
    StockRepository-->>TradingService: Stock or null

    alt stock not found
        TradingService-->>User: StockNotFoundException
    else stock found
        TradingService->>PriceProvider: getCurrentPrice(symbol)
        PriceProvider-->>TradingService: price

        alt invalid price
            TradingService-->>User: InvalidPriceException
        else valid price
            alt invalid quantity
                TradingService-->>User: InvalidQuantityException
            else valid quantity
                TradingService->>AccountRepository: findById(accountId)
                AccountRepository-->>TradingService: Account

                TradingService->>Account: validateCashForBuy(quantity, price)
                alt insufficient funds
                    TradingService-->>User: InsufficientFundsException
                else sufficient funds
                    TradingService->>Order: create BUY order
                    Order-->>TradingService: order
                    TradingService->>Account: debit(totalAmount)
                    TradingService->>Portfolio: addHolding(stock, quantity, price)
                    Portfolio->>Holding: addShares(quantity, price)
                    TradingService->>Transaction: create BUY transaction
                    TradingService->>OrderRepository: save(order)
                    TradingService->>TransactionRepository: save(transaction)
                    TradingService->>Order: markExecuted()
                    TradingService-->>User: Buy confirmed
                end
            end
        end
    end
```

## Notes

- The price is retrieved from a `PriceProvider` abstraction.
- Validation happens before cash is deducted.
- The total order value is calculated as quantity × unit price.
- The holding is created or updated using the stock and purchase price.
- Execution is persisted as both an `Order` and a `Transaction`.
