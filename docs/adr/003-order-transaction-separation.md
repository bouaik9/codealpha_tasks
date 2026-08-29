# ADR 003: Separate Order and Transaction

- Status: Accepted

## Context

A trade has two important concepts: the request to trade and the actual executed event. Treating them as one concept would blur business intent and financial outcome.

## Decision

We will keep `Order` and `Transaction` distinct.

- `Order` stores trade intent, quantity, price, and status.
- `Transaction` stores the completed execution details and financial impact.

## Why this design?

This separation supports auditing, history, and future extensions such as partial fills, cancellations, and pending orders.

## Alternatives considered

### Merge them into one class
This would be simpler but would make it difficult to represent non-executed or placed orders separately from executed trades.

## Consequences

### Positive
- Clear historical record.
- Better support for lifecycle states.
- More realistic domain modeling.

### Negative
- Additional persistence and model complexity.

## Result

The platform will preserve a clear distinction between trade intent and trade execution.
