# 02. Use Cases

## 1. Actors

- User: The individual operating the stock trading platform.
- System: The trading application and business logic layer.
- Market Data Provider: Simulated external system that supplies current stock prices.

## 2. Use case overview

| Use case | Actor | Purpose |
|---|---|---|
| Register user | User | Create a user profile |
| Create account | User | Open a trading account |
| Deposit funds | User | Add cash to an account |
| Withdraw funds | User | Remove cash from an account |
| View account | User | Read balance and account details |
| Browse stocks | User | View available securities |
| View stock | User | Inspect a stock’s price and metadata |
| Buy stock | User | Purchase shares |
| Sell stock | User | Sell owned shares |
| View portfolio | User | See holdings, market value, and P/L |
| View holdings | User | Inspect individual positions |
| View market value | User | See portfolio value |
| View profit/loss | User | See unrealized gain/loss |
| View orders | User | Read order history |
| View transactions | User | Read transaction history |

## 3. Detailed use cases

### UC-01: Register user
- Actor: User
- Preconditions: The username and email are not already used.
- Main flow:
  1. User submits registration details.
  2. System validates required fields.
  3. System creates a User entity.
  4. System confirms successful registration.
- Failure cases:
  - Duplicate username or email.
  - Empty required fields.
- Postconditions:
  - A new User exists.

### UC-02: Create account
- Actor: User
- Preconditions: User is registered.
- Main flow:
  1. User requests a new account.
  2. System validates the user and account details.
  3. System creates an Account with a starting cash balance of zero.
  4. System creates an associated Portfolio.
  5. System returns account confirmation.
- Failure cases:
  - User not found.
- Postconditions:
  - Account and Portfolio exist and are linked.

### UC-03: Deposit funds
- Actor: User
- Preconditions: User owns the account.
- Main flow:
  1. User submits a positive amount.
  2. System validates the amount is > 0.
  3. System increases the account cash balance.
  4. System creates a DEPOSIT transaction.
  5. System returns successful confirmation.
- Alternative flow:
  - User deposits zero or a negative amount; system rejects.
- Failure cases:
  - Invalid amount.
- Postconditions:
  - Cash balance increases and transaction is recorded.
- Business rules:
  - Deposits must be positive.

### UC-04: Withdraw funds
- Actor: User
- Preconditions: User owns the account.
- Main flow:
  1. User submits a positive amount.
  2. System validates amount > 0.
  3. System checks available cash.
  4. System decreases the account cash balance.
  5. System records a WITHDRAWAL transaction.
  6. System returns confirmation.
- Failure cases:
  - Withdrawal exceeds balance.
  - Withdrawal is zero or negative.
- Postconditions:
  - Balance is reduced or transaction fails without state change.
- Business rules:
  - Balance cannot become negative.

### UC-05: View account
- Actor: User
- Preconditions: User has an account.
- Main flow:
  1. User requests account details.
  2. System fetches the account and current balance.
  3. System returns account summary.
- Postconditions:
  - Account information is displayed.

### UC-06: Browse stocks
- Actor: User
- Preconditions: The stock catalog exists.
- Main flow:
  1. User requests all available stocks.
  2. System reads active stock records.
  3. System returns list of symbols, names, and prices.
- Postconditions:
  - User sees the current stock catalogue.

### UC-07: View stock
- Actor: User
- Preconditions: Stock exists.
- Main flow:
  1. User requests a stock by symbol.
  2. System fetches the stock.
  3. System returns symbol, name, and current price.
- Failure cases:
  - Stock not found.

### UC-08: Buy stock
- Actor: User
- Preconditions: User owns the account, stock exists, quantity is valid.
- Main flow:
  1. User submits buy request with account, stock symbol, and quantity.
  2. System validates quantity > 0.
  3. System retrieves current market price.
  4. System calculates total order value.
  5. System verifies account has enough cash.
  6. System creates a BUY order.
  7. System debits account cash.
  8. System updates or creates the holding.
  9. System creates a BUY transaction.
  10. System marks the order as executed.
  11. System returns order confirmation.
- Alternative flows:
  - If quantity is invalid, return validation error.
  - If stock does not exist, return not-found error.
  - If funds are insufficient, reject the trade.
  - If price is invalid, reject the trade.
- Failure cases:
  - Invalid quantity.
  - Stock not found.
  - Insufficient funds.
  - Invalid price.
- Postconditions:
  - Cash is reduced, holding is updated, and order/transaction history is stored.
- Business rules:
  - Account cash cannot become negative.
  - Holdings use average purchase price when additional shares are acquired.

### UC-09: Sell stock
- Actor: User
- Preconditions: User owns the account and has sufficient shares.
- Main flow:
  1. User submits sell request with account, symbol, and quantity.
  2. System validates quantity > 0.
  3. System fetches the current market price.
  4. System checks the holding exists and has enough shares.
  5. System creates a SELL order.
  6. System reduces the holding quantity.
  7. System credits the account cash.
  8. System creates a SELL transaction.
  9. System marks the order as executed.
  10. System returns confirmation.
- Alternative flow:
  - If holding is missing or insufficient, request is rejected.
- Failure cases:
  - Stock not found.
  - Invalid quantity.
  - Invalid price.
  - Insufficient shares.
- Postconditions:
  - Holding is reduced, cash is credited, and order/transaction records are created.
- Business rules:
  - A sell should never reduce a holding below zero.

### UC-10: View portfolio
- Actor: User
- Preconditions: User owns the account.
- Main flow:
  1. User requests portfolio summary.
  2. System loads account portfolio.
  3. System aggregates holdings.
  4. System computes current market value.
  5. System computes cost basis and unrealized P/L.
  6. System returns portfolio summary.
- Failure cases:
  - No holdings found; portfolio is empty.
- Postconditions:
  - Portfolio summary is displayed.
- Business rules:
  - Market value is quantity × current stock price.
  - Unrealized P/L = market value − cost basis.

### UC-11: View holdings
- Actor: User
- Preconditions: Account exists.
- Main flow:
  1. User requests holdings for an account.
  2. System reads all holdings in the portfolio.
  3. System returns stock symbol, quantity, and average purchase price.
- Postconditions:
  - Holdings are visible to the user.

### UC-12: View market value
- Actor: User
- Preconditions: Portfolio exists.
- Main flow:
  1. User requests market value.
  2. System multiplies each holding quantity by its current price.
  3. System sums all positions.
  4. System returns total market value.

### UC-13: View profit/loss
- Actor: User
- Preconditions: Portfolio exists.
- Main flow:
  1. User requests profit/loss for the account portfolio.
  2. System calculates cost basis and current market value.
  3. System computes unrealized P/L.
  4. System returns result.
- Postconditions:
  - Unrealized P/L is displayed.

### UC-14: View orders
- Actor: User
- Preconditions: User has an account.
- Main flow:
  1. User requests order history.
  2. System loads all orders for the account.
  3. System presents order type, quantity, price, and status.

### UC-15: View transactions
- Actor: User
- Preconditions: User has an account.
- Main flow:
  1. User requests transaction history.
  2. System loads all transactions for the account.
  3. System presents date, type, amount, and associated stock data.

## 4. Key design notes

- The portfolio and the account are different concepts: the account owns cash, while the portfolio owns positions.
- Order and transaction remain separate to represent intent versus execution.
- Price lookup is delegated to a Market Data abstraction instead of hard-coded logic inside the domain model.
