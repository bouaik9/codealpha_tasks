# 06. Sell Stock Sequence

```mermaid
sequenceDiagram
    actor User
    participant TradingService
    participant StockRepository
    participant PriceProvider
    participant AccountRepository
    participant Portfolio
    participant Holding
    participant Account
    participant Order
    participant Transaction
    participant OrderRepository
    participant TransactionRepository

    User->>TradingService: sellStock(accountId, symbol, quantity)

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
                TradingService->>Portfolio: getHolding(symbol)
                Portfolio-->>TradingService: Holding or null

                alt holding missing or insufficient shares
                    TradingService-->>User: InsufficientSharesException
                else valid holding
                    TradingService->>AccountRepository: findById(accountId)
                    AccountRepository-->>TradingService: Account
                    TradingService->>Order: create SELL order
                    Order-->>TradingService: order
                    TradingService->>Holding: removeShares(quantity)
                    TradingService->>Account: credit(saleValue)
                    TradingService->>Transaction: create SELL transaction
                    TradingService->>OrderRepository: save(order)
                    TradingService->>TransactionRepository: save(transaction)
                    TradingService->>Order: markExecuted()
                    TradingService-->>User: Sell confirmed
                end
            end
        end
    end
```

## Notes

- The system verifies that the holding exists and has enough shares before execution.
- The holding quantity is reduced after validation passes.
- The account receives the sale proceeds at the current market price.
- The sell order and the sell transaction are both recorded for audit and reporting.
