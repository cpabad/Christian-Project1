# Database Normalization

The ERS schema (PostgreSQL, schema `ExpenseReimbursementManagementSystem`, 15 tables) is in
**Fifth Normal Form (5NF)**, and therefore also in 1NF through 4NF and BCNF. This document records
the candidate keys and dependencies that justify the claim, and how it was verified.

## Summary
Every table is in BCNF, 4NF, and 5NF given the functional, multivalued, and join dependencies
implied by its declared keys (PRIMARY KEY / UNIQUE) and the business rules: there are no
non-key-determines-non-key dependencies (3NF / BCNF), no independent multivalued facts sharing a
table (4NF), and no non-trivial join dependency that is not implied by a candidate key (5NF).

## How this was verified
1. The functional dependencies for each table were derived from its DDL (primary keys, unique
   constraints, foreign keys) together with the intended business rules.
2. The running database was confirmed to match the committed DDL exactly - same 15 tables, same
   columns and types, same primary keys, unique constraints, and foreign keys - i.e. zero
   structural drift between `ReimbursementManagement/ers_script.sql` and the live schema.

## Per-table analysis

### Lookup tables - `roles`, `request_status`, `supervisor_approval_status`, `reimbursement_status`, `city_state_postal`
A single-attribute primary key determines the rest; the status tables also carry a UNIQUE natural
key (a second candidate key). No non-key attribute determines another non-key attribute, so each is
in BCNF. Every attribute is single-valued, so there are no multivalued or join dependencies -> 5NF.
- `city_state_postal` (`postalCode` PK -> `city`, `state`): city/state are factored into their own
  table rather than duplicated on `event_location`, which is precisely what avoids the classic 3NF
  transitive-dependency violation.

### Entity tables - `users`, `request`, `request_image`, `request_confirmation`, `supervisor_approval`, `approval_confirmation`, `reimbursement`, `reimbursement_confirmation`
A surrogate primary key, with every non-key attribute depending on the whole key and nothing else.
Status names live in their own lookup tables (no inline status strings), so there are no transitive
dependencies. `users` additionally has two UNIQUE candidate keys (`loginUsername`, `email`).
`supervisor_approval` carries decision-specific attributes (date, status, approval flag) that depend
on the full key, so it cannot be losslessly decomposed - a genuine 5NF relation, not a spurious
join. All attributes are single-valued -> 5NF.

### Junction / identifying relations - `employee_supervisor_jt`, `event_location`
Both have a composite natural key plus a surrogate, so every attribute is *prime* (part of some
candidate key) -> trivially BCNF. `employee_supervisor_jt` is an all-key binary relationship
(supervisor <-> employee), which is automatically in 4NF/5NF. `event_location` represents one
indivisible address fact -> 5NF.

## Assumptions
The analysis rests on the business rules implied by the declared constraints: a postal code maps to
one city/state, a username and an email each identify one user, and a (request,
supervisor-relationship) pair has one approval. Each is backed by a PRIMARY KEY or UNIQUE
declaration.

## Stability across the 2026 refresh
The refresh changed application code, test data, and some stored values (notably, passwords moved
from plaintext to BCrypt hashes) - but it **never changed the schema**. No `CREATE TABLE`, column,
key, or constraint was altered. Because normal form is a property of the schema's structure and
dependencies, not of the row values, none of those changes affect the 5NF design. (The
`loginPassword` column was already an unbounded `varchar`, so the longer hash values required no
type change.)
