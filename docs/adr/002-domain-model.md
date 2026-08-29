# ADR 002: Separate account cash from portfolio holdings

- Status: Accepted

## Context

The original idea of putting most logic into `Account` would create a very large, tightly coupled object. The business clearly distinguishes between money and positions.

## Decision

The platform will use separate domain concepts:

- `Account` owns cash and balance rules.
- `Portfolio` owns holdings and position calculations.
- `Holding` represents ownership of a stock by an account.
- `Stock` represents the asset itself.

## Why this design?

This separation matches how trading systems think: one object owns cash, another owns investments. It reduces coupling and improves clarity.

## Alternatives considered

### Put everything in `Account`
This would overload `Account` with trade workflows, market-value logic, and position management. It would violate the single responsibility principle.

### Remove `Portfolio`
This would make portfolio logic harder to isolate and would blur the distinction between wallet balance and stock positions.

## Consequences

### Positive
- Better responsibility boundaries.
- Easier testing and validation.
- Cleaner understanding of the domain.

### Negative
- Slightly more coordination between account and portfolio logic.

## Result

This separation is a clear and maintainable domain model for the project.
