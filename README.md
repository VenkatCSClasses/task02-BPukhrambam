# BankAccount Library

A Java implementation of a bank account management library with comprehensive input validation and error handling.

## Overview

BankAccount is a library for managing bank account operations including deposits, withdrawals, and transfers. It enforces strict validation on email addresses and monetary amounts to ensure data integrity. All operations validate inputs before modifying state and use standard exception handling to report errors.

## Features

- ✅ Account creation with email and balance validation
- ✅ Deposit and withdrawal operations
- ✅ Transfer funds between accounts
- ✅ Email format validation
- ✅ Currency precision validation (max 2 decimal places)
- ✅ Comprehensive error handling
- ✅ Thread-safe monetary operations using BigDecimal

## Project Structure

```
task02-BPukhrambam/
├── src/
│   ├── main/java/
│   │   ├── BankAccount.java
│   │   └── InsufficientFundsException.java
│   └── test/java/
│       └── BankAccountYamlTests.java
├── pom.xml
├── SPEC.md
├── tests.yaml
├── usage.md
└── README.md
```

## Requirements

- Java 17 or higher
- Apache Maven 3.8+

## Building the Project

```bash
mvn clean compile
```

## Running Tests

Run all tests:

```bash
mvn test
```

Run tests with detailed output:

```bash
mvn test -X
```

## Usage

See [usage.md](usage.md) for detailed API documentation and examples.

### Quick Example

```java
// Create accounts
BankAccount alice = new BankAccount("alice@example.com", 1000.00);
BankAccount bob = new BankAccount("bob@example.com", 500.00);

// Deposit money
alice.deposit(250.50);

// Withdraw money
try {
    alice.withdraw(100.00);
} catch (InsufficientFundsException e) {
    System.out.println("Not enough funds");
}

// Transfer between accounts
try {
    alice.transfer(bob, 200.00);
} catch (InsufficientFundsException e) {
    System.out.println("Transfer failed");
}

// Check balances
System.out.println(alice.getBalance()); // 950.5
System.out.println(bob.getBalance());   // 700.0
```

## API Overview

### Core Methods

- `BankAccount(String email, double startingBalance)` — Constructor
- `double getBalance()` — Get current balance
- `String getEmail()` — Get account email
- `void withdraw(double amount)` — Withdraw funds
- `void deposit(double amount)` — Deposit funds
- `void transfer(BankAccount target, double amount)` — Transfer funds

### Validation Methods

- `static boolean isEmailValid(String email)` — Validate email format
- `static boolean isAmountValid(double amount)` — Validate monetary amount

## Validation Rules

### Email Validation

- Must contain exactly one `@` symbol
- Must have at least one `.` after the `@`
- Prefix must start with alphanumeric character
- Prefix can contain: alphanumeric and `. ! $ % & ' * + - / = ? ^ _ \` { | } ~`
- No consecutive special characters in prefix
- Domain must have at least 2 characters after final `.`

### Amount Validation

- Must be non-negative (≥ 0)
- Must have at most 2 decimal places
- Cannot be `NaN` or `Infinity`
- Withdrawals cannot be zero (special case)

## Exception Handling

The library throws two types of exceptions:

- `IllegalArgumentException` — For invalid inputs (bad email, invalid amounts, zero withdrawal)
- `InsufficientFundsException` — When attempting to withdraw/transfer more than available balance

## Test Coverage

The project includes comprehensive test coverage based on `tests.yaml`:

- ✅ Constructor validation tests
- ✅ Balance retrieval tests
- ✅ Withdrawal tests (normal, boundary, and error cases)
- ✅ Deposit tests (normal, boundary, and error cases)
- ✅ Transfer tests (normal, boundary, and error cases)
- ✅ Email validation tests
- ✅ Amount validation tests

All 7 test suites pass successfully.

## Dependencies

```xml
<dependency>
    <groupId>org.junit.jupiter</groupId>
    <artifactId>junit-jupiter-api</artifactId>
    <version>5.11.0</version>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>org.junit.jupiter</groupId>
    <artifactId>junit-jupiter-engine</artifactId>
    <version>5.11.0</version>
    <scope>test</scope>
</dependency>
```

## License

Educational project for COMP 345.

## Authors

Implementation based on specification in `SPEC.md`.
