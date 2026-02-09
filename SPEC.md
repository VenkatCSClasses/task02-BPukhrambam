# BankAccount Specification v1

## Overview

BankAccount is a library for managing bank account operations including deposits, withdrawals, and transfers. It enforces validation on email addresses and monetary amounts to ensure data integrity.

All functions validate inputs before modifying state. The library uses standard exception handling to report errors.

## Design Principles

1. **Input validation first.** All inputs are validated before any state changes occur.

2. **Precise decimal handling.** Amounts must not exceed two decimal places to match currency precision.

3. **Clear error messages.** Exceptions include descriptive messages indicating what went wrong.

4. **Deterministic.** Given the same inputs and account state, operations always produce the same result.

---

## Output Structure

Generate the minimal files needed to use and test the library. Do not create package distribution scaffolding.

**Do generate:**

- Library source file(s)
- Test file(s)
- usage.md

**Do not generate:**

- setup.py, pyproject.toml with build/publish metadata (Python)
- Publishable Cargo.toml fields like description, license, repository, keywords (Rust—keep only [package] name, version, edition)
- package.json with publish config (Node)
- gemspec files (Ruby)
- go.mod with module paths pointing to repositories (Go—use a simple local module name)
- Any CI/CD configuration, GitHub Actions, etc.

The goal is a working implementation that can be copied into a project, not a publishable package.

---

## Type Conventions

Since this spec targets multiple languages, types are described abstractly:

| Spec type | Meaning                             | Examples                                 |
| --------- | ----------------------------------- | ---------------------------------------- |
| `string`  | UTF-8 text                          | `"user@example.com"`                     |
| `number`  | Decimal number for currency amounts | `100.50`, `0.00`, `1234.56`              |
| `account` | BankAccount instance                | Language-specific object/class instance  |
| `error`   | Language-idiomatic error/exception  | `IllegalArgumentException`, `ValueError` |

## Error Handling

Errors should be reported idiomatically for the target language:

| Language   | Error style                                                      |
| ---------- | ---------------------------------------------------------------- |
| Java       | Throw `IllegalArgumentException` or `InsufficientFundsException` |
| Python     | Raise `ValueError` or custom `InsufficientFundsError`            |
| TypeScript | Throw `Error` subclasses                                         |
| C#         | Throw `ArgumentException` or custom exceptions                   |
| Go         | Return `(value, error)` tuple                                    |

**Error conditions by function:**

| Function      | Error when                                             |
| ------------- | ------------------------------------------------------ |
| `constructor` | Invalid email format OR invalid starting balance       |
| `withdraw`    | Invalid amount OR amount is zero OR insufficient funds |
| `deposit`     | Invalid amount                                         |
| `transfer`    | Invalid amount OR insufficient funds                   |

**Exception types:**

- **IllegalArgumentException** (or equivalent): Used for invalid inputs (bad email, invalid amounts, zero withdrawal)
- **InsufficientFundsException** (or equivalent): Used when attempting to withdraw/transfer more than available balance

---

## Validation Rules

### Email Validation

A valid email address must meet ALL of the following criteria:

**Structure requirements:**

- Must not be empty or contain only whitespace
- Must contain exactly one `@` symbol
- Must contain at least one `.` (period) after the `@` symbol
- The last `.` must come after the `@` symbol

**Prefix (before `@`) requirements:**

- Must not be empty
- First character must be alphanumeric
- Can contain alphanumeric characters and special characters: `. ! $ % & ' * + - / = ? ^ _ \` { | } ~`
- Special characters cannot appear consecutively
- Special characters cannot appear as the first character

**Domain (after `@`) requirements:**

- Can contain alphanumeric characters, hyphens (`-`), and periods (`.`)
- Must have at least 2 characters after the final `.` (top-level domain)
- Cannot have other special characters

**Invalid examples:**

- `""` (empty)
- `invalid` (no @ or .)
- `@example.com` (no prefix)
- `.user@example.com` (starts with special char)
- `user..name@example.com` (consecutive special chars)
- `user@example` (no period in domain)
- `user@example.c` (TLD too short)
- `user#name@example.com` (# not allowed in prefix)

**Valid examples:**

- `user@example.com`
- `user.name@example.com`
- `first+last@subdomain.example.co.uk`
- `user_123@test-domain.org`

### Amount Validation

A valid monetary amount must meet ALL of the following criteria:

- Must be non-negative (≥ 0)
- Must have at most 2 decimal places
- Cannot be `NaN` or `Infinity`

**Invalid examples:**

- `-10.00` (negative)
- `50.999` (more than 2 decimal places)
- `100.123` (more than 2 decimal places)

**Valid examples:**

- `0.00` (zero is valid)
- `100` (whole numbers)
- `50.5` (one decimal place)
- `99.99` (two decimal places)

---

## Class Structure

### Constructor

```
BankAccount(email: string, startingBalance: number)
```

Creates a new bank account with the specified email and starting balance.

**Parameters:**

- `email`: Email address for the account owner
- `startingBalance`: Initial account balance

**Throws:**

- `IllegalArgumentException` if email is invalid OR if starting balance is invalid

**Behavior:**

- Validates email format using `isEmailValid()`
- Validates starting balance using `isAmountValid()`
- If both valid, initializes account with provided values
- If either invalid, throws exception and does not create account

---

### Methods

#### getBalance() → number

Returns the current account balance.

**Returns:** Current balance as a decimal number

---

#### getEmail() → string

Returns the email address associated with the account.

**Returns:** Email address as a string

---

#### withdraw(amount: number) → void

Withdraws the specified amount from the account.

**Parameters:**

- `amount`: Amount to withdraw

**Throws:**

- `IllegalArgumentException` if amount is invalid (negative, more than 2 decimals)
- `IllegalArgumentException` if amount is exactly zero
- `InsufficientFundsException` if amount exceeds current balance

**Behavior:**

1. Validates amount using `isAmountValid()`
2. Checks if amount is zero (special case - must be non-zero)
3. Checks if amount ≤ balance
4. If all checks pass, reduces balance by amount
5. Otherwise throws appropriate exception

**Postcondition:** Balance is reduced by amount if successful; unchanged if error thrown

---

#### deposit(amount: number) → void

Deposits the specified amount into the account.

**Parameters:**

- `amount`: Amount to deposit

**Throws:**

- `IllegalArgumentException` if amount is invalid

**Behavior:**

1. Validates amount using `isAmountValid()`
2. If valid, increases balance by amount
3. Otherwise throws exception

**Postcondition:** Balance is increased by amount if successful; unchanged if error thrown

---

#### transfer(targetAccount: account, amount: number) → void

Transfers the specified amount from this account to another account.

**Parameters:**

- `targetAccount`: The account to receive the funds
- `amount`: Amount to transfer

**Throws:**

- `IllegalArgumentException` if amount is invalid
- `InsufficientFundsException` if amount exceeds current balance

**Behavior:**

1. Validates amount using `isAmountValid()`
2. Checks if amount ≤ balance
3. If valid, withdraws from this account and deposits to target account
4. Otherwise throws appropriate exception

**Postcondition:**

- This account's balance reduced by amount
- Target account's balance increased by amount
- If error thrown, both accounts unchanged

---

### Static Validation Methods

#### isEmailValid(email: string) → boolean

Validates email address format according to specification rules.

**Parameters:**

- `email`: Email address to validate

**Returns:** `true` if email is valid, `false` otherwise

---

#### isAmountValid(amount: number) → boolean

Validates monetary amount according to specification rules.

**Parameters:**

- `amount`: Monetary amount to validate

**Returns:** `true` if amount is valid, `false` otherwise

---

## Testing

### Test Categories

Tests should cover the following categories:

**Constructor tests:**

- Valid email and balance
- Invalid email formats (empty, no @, no domain, etc.)
- Invalid balance (negative, too many decimals)

**Email validation tests:**

- Empty/blank emails
- Missing @ symbol
- Missing domain period
- Invalid prefix characters
- Consecutive special characters in prefix
- Special character as first character
- Domain too short after final period
- Valid formats with various special characters

**Amount validation tests:**

- Negative amounts
- Amounts with more than 2 decimal places
- Zero amount (should be valid for validation, but not for withdrawal)
- Valid amounts (whole numbers, 1 decimal, 2 decimals)

**Withdraw tests:**

- Valid withdrawal
- Withdrawal of zero (should throw)
- Withdrawal exceeding balance
- Invalid amounts (negative, too many decimals)

**Deposit tests:**

- Valid deposits
- Deposit of zero (should succeed)
- Invalid amounts

**Transfer tests:**

- Valid transfer between accounts
- Transfer exceeding balance
- Invalid amounts
- Verify both accounts updated correctly

### Boundary Testing

Pay special attention to boundary cases:

**Balance boundaries:**

- Withdrawing exact balance
- Withdrawing balance + 0.01
- Balance of 0.00

**Decimal boundaries:**

- Amounts with 0, 1, and 2 decimal places
- Amounts with 3+ decimal places

**Email boundaries:**

- Minimum valid email length
- Special characters at boundaries

---

## Generated Documentation

Implementations MUST include a `usage.md` file documenting how to use the library in the target language.

### usage.md requirements

The file should be concise and practical. Include:

1. **Installation** — How to add the library to a project

2. **Quick start** — Minimal code example showing account creation and basic operations

3. **API reference** — For each method:
   - Signature in target language syntax
   - Parameter types and descriptions
   - Return type
   - Exceptions thrown
   - One or two examples

4. **Validation rules** — Summary of email and amount validation

5. **Error handling** — How to catch and handle exceptions

### usage.md template

```markdown
# BankAccount for [LANGUAGE]

Simple bank account management with built-in validation.

## Installation

[How to import/require/add the library]

## Quick start

[10-15 line example showing account creation, deposit, withdrawal, transfer]

## API Reference

### Constructor

[Signature, parameters, exceptions, example]

### Methods

#### getBalance()

[Signature, return type, example]

#### getEmail()

[Signature, return type, example]

#### withdraw(amount)

[Signature, parameters, exceptions, examples]

#### deposit(amount)

[Signature, parameters, exceptions, examples]

#### transfer(targetAccount, amount)

[Signature, parameters, exceptions, examples]

### Validation Methods

#### isEmailValid(email)

[Signature, parameters, return type, example]

#### isAmountValid(amount)

[Signature, parameters, return type, example]

## Validation Rules

### Email Format

[Summary of email requirements]

### Amount Format

[Summary of amount requirements]

## Error Handling

[Language-specific exception handling patterns with examples]
```

Keep it under 200 lines. Developers should understand the full API in under 2 minutes.

---

## Implementation Checklist

Before considering the implementation complete:

- [ ] Constructor validates email and starting balance
- [ ] All validation methods implemented (isEmailValid, isAmountValid)
- [ ] All account methods implemented (withdraw, deposit, transfer, getBalance, getEmail)
- [ ] Email validation follows all prefix and domain rules
- [ ] Amount validation checks for non-negative and max 2 decimals
- [ ] Withdrawal rejects zero amounts
- [ ] InsufficientFundsException thrown when appropriate
- [ ] IllegalArgumentException thrown for all invalid inputs
- [ ] All tests.yaml tests pass
- [ ] All test categories covered with passing tests
- [ ] Boundary cases tested
- [ ] usage.md generated

---

## Version History

- **v1.0.0** - Initial specification
