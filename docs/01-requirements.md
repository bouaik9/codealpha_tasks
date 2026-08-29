# 01. Requirements

## 1. Project overview

This project is a simplified stock trading platform implemented as a Java modular monolith. It supports managing accounts, holding balances, trading stocks, and calculating portfolio-level value and profit/loss. The system is intentionally backend-focused and does not connect to a live exchange in version 1.

The platform is designed to be realistic enough to demonstrate professional Java engineering practices while remaining understandable for an internship-level project.

## 2. Functional requirements

### 2.1 User and account management
- A user can register with a username and email.
- A user can create one or more accounts.
- An account belongs to exactly one user.
- An account has a cash balance in a currency such as USD.
- The system can display account details and balance.

### 2.2 Cash operations
- A user can deposit funds into an account.
- A user can withdraw funds from an account.
- A deposit must be positive.
- A withdrawal must be positive.
- An account balance cannot become negative.
- Deposits and withdrawals are recorded as transactions.

### 2.3 Stocks
- The system can list available stocks.
- Each stock has a symbol, a company name, and a current price.
- Stock symbols are unique.
- A stock price must be positive.
- Initial market prices are supplied by a simulated price provider.

### 2.4 Buying and selling
- A user can buy a stock for an account.
- A user can sell a stock from an account.
- The system validates stock existence, quantity, and price.
- The system verifies sufficient funds before a buy.
- The system verifies sufficient shares before a sell.
- A buy or sell creates an order record and a transaction record.
- A buy or sell updates the account cash balance and portfolio holdings.

### 2.5 Portfolio and holdings
- A portfolio contains all holdings for a given account.
- A holding contains a stock, quantity, and average purchase price.
- Holdings cannot have a negative quantity.
- A portfolio can calculate market value.
- A portfolio can calculate cost basis.
- A portfolio can calculate unrealized profit/loss.
- A user can view all holdings and an individual holding.

### 2.6 Orders and transactions
- The system stores buy and sell orders.
- The system distinguishes between an order request and a completed transaction.
- Orders can have statuses such as PENDING, EXECUTED, or CANCELLED.
- Transactions record the financial result of executed orders.
- Users can view order history and transaction history.

### 2.7 Reporting
- Users can view an account balance.
- Users can view portfolio market value.
- Users can view unrealized profit/loss.
- Users can view order history.
- Users can view transaction history.

## 3. Non-functional requirements

### Correctness
- Business rules must be enforced inside the domain layer.
- Monetary values must use precise decimal arithmetic.
- Validation logic must prevent invalid quantities, negative balances, and invalid prices.

### Maintainability
- Domain logic should be separated from persistence and external APIs.
- Code should favor clear naming, small classes, and cohesive responsibilities.
- The project should remain easy to understand for a Java internship project.

### Testability
- The domain should be testable without external infrastructure.
- Unit tests should verify observable behavior rather than implementation details.
- Business workflows such as buy and sell should be covered with service-level tests.

### Security
- Input validation must reject invalid amounts and quantities.
- Data access must be restricted to the service and repository layers.
- No secret management is required for version 1.

### Performance
- The system is small and will be used in a local or single-instance environment.
- Basic indexing on common lookup fields is sufficient.
- The design must not over-engineer concurrency or distributed processing.

### Reliability
- The system should handle invalid requests gracefully using domain exceptions.
- State changes should be consistent when creating an order and transaction in the same workflow.

### Data consistency
- A cash debit and a corresponding transaction must not be stored independently without domain validation.
- A portfolio holding must always reflect the latest valid position after a trade.
- Stock symbols must remain unique.

### Extensibility
- A fake market-data provider can later be replaced by a real external provider.
- The architecture should allow future REST endpoints, database persistence, and advanced order types.

## 4. Business rules

### Account rules
- The account cash balance is always greater than or equal to zero.
- Deposits must be greater than zero.
- Withdrawals must be greater than zero.
- A withdrawal cannot exceed the current available cash.
- An account cannot hold a negative cash balance.

### Stock rules
- Stock symbol is unique.
- Symbol is not null and should be normalized to uppercase.
- Stock name is required.
- Stock price must be positive.

### Holding rules
- A holding represents shares of a stock owned by an account.
- Quantity must be positive when a position exists.
- A holding is associated with one account portfolio and one stock.
- A holding is updated when more shares are bought or sold.

### Buy rules
1. Fetch current stock price.
2. Validate quantity and price.
3. Compute total order value.
4. Ensure account has enough cash.
5. Deduct cash.
6. Update or create the holding.
7. Create the order.
8. Create the transaction.
9. Mark the order as executed.

### Sell rules
1. Fetch current stock price.
2. Validate quantity and price.
3. Verify the account owns enough shares.
4. Compute sale value.
5. Reduce or remove the holding.
6. Increase cash balance.
7. Create the order.
8. Create the transaction.
9. Mark the order as executed.

### Market value rules
- Portfolio market value is computed as quantity × current market price.
- Market value differs from original cost basis.
- Market value is based on current quoted prices, not historical purchase cost.

### Profit/loss rules
- Cost basis is the total amount paid for acquired shares.
- Market value is current value of the positions.
- Unrealized P/L is current market value minus cost basis.
- Realized P/L is profit or loss from completed sales.
- Realized P/L is a future enhancement for version 2 or later.

## 5. Scope

### Version 1 (implemented)
- User registration and account creation
- Deposit and withdrawal validation
- Cash balance tracking
- Stock browsing and stock lookup
- Buy and sell flows
- Portfolio snapshot and holdings
- Market value calculation
- Unrealized profit/loss calculation
- Order history
- Transaction history
- Simulated market data provider
- In-memory or database-backed persistence (depending on the stage of the project)

### Future extensions
- Real market data integration
- Limit orders and stop-loss orders
- Dividend processing
- Multiple currencies
- Watchlists
- Authentication and authorization
- Notification system
- Advanced analytics and charts
- REST API and web frontend
- Real exchange integration and market schedule logic
- Tax/loss harvesting features

## 6. Assumptions

- This project is a backend-first implementation.
- A modular monolith is preferred over a distributed architecture.
- The market provider is simulated and deterministic in version 1.
- Monetary values are stored using decimal precision rather than floating-point types.
- Fractions of shares are not supported in version 1; integer quantities are sufficient.
- A portfolio is a separate domain concept but is owned by a single account.
