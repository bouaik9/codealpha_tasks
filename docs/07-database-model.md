# 07. Database Model

## 1. Design goals

The relational model matches the domain model and keeps the system simple and consistent:

- `users` store user identity information.
- `accounts` store cash balance and owner linkage.
- `stocks` store asset metadata and current price.
- `holdings` store ownership positions for each account and stock.
- `orders` store requests to buy or sell.
- `transactions` store completed financial events.

The schema is designed for PostgreSQL and uses `NUMERIC(19,4)` or `NUMERIC(19,2)` for money where appropriate. This avoids floating-point errors and ensures financial correctness.

## 2. ER model

```mermaid
erDiagram
    USERS ||--o{ ACCOUNTS : owns
    ACCOUNTS ||--|| PORTFOLIOS : has
    PORTFOLIOS ||--o{ HOLDINGS : contains
    STOCKS ||--o{ HOLDINGS : underlies
    ACCOUNTS ||--o{ ORDERS : places
    ACCOUNTS ||--o{ TRANSACTIONS : records
    ORDERS ||--o| TRANSACTIONS : executes

    USERS {
        bigint id PK
        varchar username UK
        varchar email UK
        timestamp created_at
    }

    ACCOUNTS {
        bigint id PK
        bigint user_id FK
        decimal balance NUMERIC(19,4)
        timestamp created_at
    }

    PORTFOLIOS {
        bigint id PK
        bigint account_id FK UK
        timestamp created_at
    }

    STOCKS {
        varchar symbol PK
        varchar name
        decimal current_price NUMERIC(19,4)
        timestamp updated_at
    }

    HOLDINGS {
        bigint id PK
        bigint portfolio_id FK
        varchar stock_symbol FK
        int quantity
        decimal average_purchase_price NUMERIC(19,4)
        timestamp created_at
        timestamp updated_at
    }

    ORDERS {
        bigint id PK
        bigint account_id FK
        varchar stock_symbol FK
        varchar order_type
        int quantity
        decimal unit_price NUMERIC(19,4)
        decimal total_amount NUMERIC(19,4)
        varchar status
        timestamp created_at
        timestamp executed_at
    }

    TRANSACTIONS {
        bigint id PK
        bigint account_id FK
        bigint order_id FK
        varchar transaction_type
        varchar stock_symbol FK
        int quantity
        decimal amount NUMERIC(19,4)
        decimal unit_price NUMERIC(19,4)
        timestamp created_at
    }
```

## 3. Table definitions

### users
| Column | Type | Constraints | Notes |
|---|---|---|---|
| id | BIGSERIAL | PRIMARY KEY | Unique user identifier |
| username | VARCHAR(50) | NOT NULL, UNIQUE | Unique user login name |
| email | VARCHAR(255) | NOT NULL, UNIQUE | Unique email value |
| created_at | TIMESTAMP | NOT NULL DEFAULT now() | Creation time |

Indexes:
- Unique index on `username`
- Unique index on `email`

### accounts
| Column | Type | Constraints | Notes |
|---|---|---|---|
| id | BIGSERIAL | PRIMARY KEY | Unique account identifier |
| user_id | BIGINT | NOT NULL, FK -> users.id | Owns the account |
| balance | NUMERIC(19,4) | NOT NULL DEFAULT 0 | Cash balance |
| created_at | TIMESTAMP | NOT NULL DEFAULT now() | Creation time |

Constraints:
- `CHECK (balance >= 0)`
- `FOREIGN KEY (user_id) REFERENCES users(id)`

Indexes:
- Index on `user_id`

### portfolios
| Column | Type | Constraints | Notes |
|---|---|---|---|
| id | BIGSERIAL | PRIMARY KEY | Portfolio identifier |
| account_id | BIGINT | NOT NULL, FK -> accounts.id, UNIQUE | One portfolio per account |
| created_at | TIMESTAMP | NOT NULL DEFAULT now() | Creation time |

### stocks
| Column | Type | Constraints | Notes |
|---|---|---|---|
| symbol | VARCHAR(10) | PRIMARY KEY | Unique stock symbol |
| name | VARCHAR(255) | NOT NULL | Company name |
| current_price | NUMERIC(19,4) | NOT NULL | Current market price |
| updated_at | TIMESTAMP | NOT NULL DEFAULT now() | Last price update |

Constraints:
- `CHECK (current_price > 0)`

### holdings
| Column | Type | Constraints | Notes |
|---|---|---|---|
| id | BIGSERIAL | PRIMARY KEY | Unique holding ID |
| portfolio_id | BIGINT | NOT NULL, FK -> portfolios.id | Owning portfolio |
| stock_symbol | VARCHAR(10) | NOT NULL, FK -> stocks.symbol | Underlying stock |
| quantity | INTEGER | NOT NULL | Number of shares |
| average_purchase_price | NUMERIC(19,4) | NOT NULL | Average cost basis per share |
| created_at | TIMESTAMP | NOT NULL DEFAULT now() | Position creation |
| updated_at | TIMESTAMP | NOT NULL DEFAULT now() | Position update |

Constraints:
- `CHECK (quantity > 0)`
- `FOREIGN KEY (portfolio_id) REFERENCES portfolios(id)`
- `FOREIGN KEY (stock_symbol) REFERENCES stocks(symbol)`
- Unique constraint on `(portfolio_id, stock_symbol)`

### orders
| Column | Type | Constraints | Notes |
|---|---|---|---|
| id | BIGSERIAL | PRIMARY KEY | Unique order ID |
| account_id | BIGINT | NOT NULL, FK -> accounts.id | Account that placed order |
| stock_symbol | VARCHAR(10) | NOT NULL, FK -> stocks.symbol | Traded symbol |
| order_type | VARCHAR(10) | NOT NULL | BUY or SELL |
| quantity | INTEGER | NOT NULL | Shares requested |
| unit_price | NUMERIC(19,4) | NOT NULL | Execution price |
| total_amount | NUMERIC(19,4) | NOT NULL | Total order value |
| status | VARCHAR(20) | NOT NULL DEFAULT 'PENDING' | Order state |
| created_at | TIMESTAMP | NOT NULL DEFAULT now() | Creation time |
| executed_at | TIMESTAMP | NULL | Execution time |

Constraints:
- `CHECK (quantity > 0)`
- `CHECK (unit_price > 0)`
- `CHECK (total_amount > 0)`
- `CHECK (status IN ('PENDING','EXECUTED','CANCELLED'))`

### transactions
| Column | Type | Constraints | Notes |
|---|---|---|---|
| id | BIGSERIAL | PRIMARY KEY | Unique transaction ID |
| account_id | BIGINT | NOT NULL, FK -> accounts.id | The account affected |
| order_id | BIGINT | NOT NULL, FK -> orders.id | Related order |
| transaction_type | VARCHAR(20) | NOT NULL | DEPOSIT, WITHDRAWAL, BUY, SELL |
| stock_symbol | VARCHAR(10) | NULL, FK -> stocks.symbol | Optional for cash events |
| quantity | INTEGER | NULL | Shares for buy/sell |
| amount | NUMERIC(19,4) | NOT NULL | Monetary impact |
| unit_price | NUMERIC(19,4) | NULL | Unit price of trade |
| created_at | TIMESTAMP | NOT NULL DEFAULT now() | Transaction time |

Constraints:
- `CHECK (amount != 0)`
- `FOREIGN KEY (account_id) REFERENCES accounts(id)`
- `FOREIGN KEY (order_id) REFERENCES orders(id)`
- `FOREIGN KEY (stock_symbol) REFERENCES stocks(symbol)`

## 4. Money precision decision

Money should never use floating-point types in the database or in the domain model.

The correct choice for this project is `NUMERIC`/`DECIMAL` for all financial values:
- account balances
- stock prices
- transaction amounts
- order totals
- average purchase price
- profit and loss values

`NUMERIC(19,4)` is a good default because it offers enough precision for financial values while remaining readable and practical for an internship-level project. Precision is intentionally explicit and prevents rounding surprises that happen with `double`.

## 5. Why this schema matches the domain

- `Order` and `Transaction` are separate tables to preserve intent vs execution.
- `Holding` is linked to a portfolio and a stock to represent ownership.
- `Portfolio` is a separate table to maintain clear separation between cash and positions.
- `stocks` is a reference entity that stores symbol and market price.
- `accounts` stores cash balance and uses a check constraint to ensure non-negative values.

## 6. Future extensions

Possible additions later:
- `market_prices` table with historical price history
- `dividends` table
- `watchlists` table
- `users` authentication and roles
- order status history or audit table

These can be added without redesigning the core schema.
