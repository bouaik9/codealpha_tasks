# ADR 001: Use a modular monolith

- Status: Accepted

## Context

The project is a simplified stock trading platform intended for a Java/Maven internship-level implementation. The design should be simple, maintainable, and realistic without introducing unnecessary complexity.

## Decision

We will implement the application as a modular monolith, with clear separation between domain, application, infrastructure, and presentation concerns, rather than building a distributed system or microservices.

## Why this design?

- The project is small enough to be developed and tested as one deployable application.
- It reduces architectural overhead and keeps the system understandable.
- It still allows a clean internal layering model and strong separation of concerns.
- It fits the requirement to avoid overengineering.

## Alternatives considered

### Microservices
This would introduce network boundaries, deployment complexity, and distributed transaction concerns that are unnecessary for a first version.

### Single package application with no layering
This would be simpler at first, but it would collapse business rules into one place and make maintenance harder.

## Consequences

### Positive
- Simpler to run and test.
- Easier to explain in a learning project.
- Clear boundaries can still be maintained internally.

### Negative
- It will eventually need refactoring if the project grows significantly.
- Cross-module dependencies must still be managed carefully.

## Result

A modular monolith is the best balance between realism and simplicity for this project.
