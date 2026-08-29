# ADR 004: Introduce a market price abstraction

- Status: Accepted

## Context

The system requires current prices but should not depend on external exchange APIs in version 1. A hard-coded price lookup would make the domain less flexible and harder to test.

## Decision

We will define a `PriceProvider` interface in the domain/application boundary and provide a `FakePriceProvider` implementation in infrastructure or a simulation layer.

## Why this design?

This keeps the domain logic independent from external APIs while allowing future integration with real market data.

## Alternatives considered

### Hard-code prices into `Stock`
This would make testing and future exchange integration difficult.

### Depend directly on an external API inside the domain
This would violate clean architecture and create coupling to the external system.

## Consequences

### Positive
- Testable domain logic.
- Easier integration with real providers later.
- Clear separation of concerns.

### Negative
- Slightly more abstraction.

## Result

The price system is decoupled from the trading domain and can be swapped without changing business logic.
