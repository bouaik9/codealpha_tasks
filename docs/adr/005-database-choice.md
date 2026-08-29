# ADR 005: Use PostgreSQL with relational schema

- Status: Accepted

## Context

The project needs a durable, queryable persistence mechanism for accounts, stocks, holdings, orders, and transactions. The requirements mention PostgreSQL explicitly.

## Decision

We will use PostgreSQL as the relational database for version 1.

## Why this design?

- It fits the financial and transactional nature of the domain.
- It supports relational integrity for account balances, holdings, and order history.
- It aligns with the requirement and is easy to reason about in a Java application.

## Alternatives considered

### NoSQL
This would be less natural for a transactional portfolio and order schema.

### In-memory-only persistence
This would reduce setup cost but would not match the intended production-ready path.

## Consequences

### Positive
- Strong data integrity.
- Good fit for reporting and transaction history.

### Negative
- Requires schema design and migration planning later.

## Result

PostgreSQL is the correct persistence choice for the project’s current scope and future growth.
