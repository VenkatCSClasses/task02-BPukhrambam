# BankAccount for Java 17

Simple bank account management with built-in validation.

## Installation

Copy `BankAccount.java` and `InsufficientFundsException.java` into your project's source directory.

## Quick start

```java
// Create accounts
BankAccount alice = new BankAccount("alice@example.com", 1000.00);
BankAccount bob = new BankAccount("bob@example.com", 500.00);

// Check balance
System.out.println(alice.getBalance()); // 1000.0

// Deposit money
alice.deposit(250.50);
System.out.println(alice.getBalance()); // 1250.5

// Withdraw money
alice.withdraw(100.00);
System.out.println(alice.getBalance()); // 1150.5

// Transfer between accounts
alice.transfer(bob, 200.00);
System.out.println(alice.getBalance()); // 950.5
System.out.println(bob.getBalance());   // 700.0
```

## API Reference

### Constructor

```java
BankAccount(String email, double startingBalance)
```

Creates a new bank account with the specified email and starting balance.

**Parameters:**

- `email` — Email address for the account owner
- `startingBalance` — Initial account balance

**Throws:**

- `IllegalArgumentException` if email is invalid or starting balance is invalid

**Example:**

```java
BankAccount account = new BankAccount("user@example.com", 1000.00);
BankAccount empty = new BankAccount("new@example.com", 0.00);
```

### Methods

#### getBalance()

```java
public double getBalance()
```

Returns the current account balance.

**Returns:** Current balance as a double

**Example:**

```java
BankAccount account = new BankAccount("user@example.com", 500.00);
double balance = account.getBalance(); // 500.0
```

#### getEmail()

```java
public String getEmail()
```

Returns the email address associated with the account.

**Returns:** Email address as a String

**Example:**

```java
BankAccount account = new BankAccount("user@example.com", 500.00);
String email = account.getEmail(); // "user@example.com"
```

#### withdraw(double amount)

```java
public void withdraw(double amount) throws InsufficientFundsException
```

Withdraws the specified amount from the account.

**Parameters:**

- `amount` — Amount to withdraw (must be positive, non-zero, with at most 2 decimal places)

**Throws:**

- `IllegalArgumentException` if amount is invalid (negative, zero, or more than 2 decimals)
- `InsufficientFundsException` if amount exceeds current balance

**Examples:**

```java
BankAccount account = new BankAccount("user@example.com", 500.00);
account.withdraw(100.00);  // Balance: 400.0
account.withdraw(400.00);  // Balance: 0.0
account.withdraw(0.01);    // Throws InsufficientFundsException
```

#### deposit(double amount)

```java
public void deposit(double amount)
```

Deposits the specified amount into the account.

**Parameters:**

- `amount` — Amount to deposit (must be non-negative with at most 2 decimal places)

**Throws:**

- `IllegalArgumentException` if amount is invalid

**Examples:**

```java
BankAccount account = new BankAccount("user@example.com", 100.00);
account.deposit(50.50);   // Balance: 150.5
account.deposit(0.00);    // Balance: 150.5 (zero deposit allowed)
```

#### transfer(BankAccount targetAccount, double amount)

```java
public void transfer(BankAccount targetAccount, double amount) throws InsufficientFundsException
```

Transfers the specified amount from this account to another account.

**Parameters:**

- `targetAccount` — The account to receive the funds
- `amount` — Amount to transfer (must be positive with at most 2 decimal places)

**Throws:**

- `IllegalArgumentException` if amount is invalid
- `InsufficientFundsException` if amount exceeds current balance

**Example:**

```java
BankAccount from = new BankAccount("sender@example.com", 500.00);
BankAccount to = new BankAccount("receiver@example.com", 100.00);
from.transfer(to, 200.00);
// from balance: 300.0
// to balance: 300.0
```

### Validation Methods

#### isEmailValid(String email)

```java
public static boolean isEmailValid(String email)
```

Validates email address format according to specification rules.

**Parameters:**

- `email` — Email address to validate

**Returns:** `true` if email is valid, `false` otherwise

**Example:**

```java
BankAccount.isEmailValid("user@example.com");      // true
BankAccount.isEmailValid("user.name@example.com"); // true
BankAccount.isEmailValid("invalid@");              // false
BankAccount.isEmailValid("@example.com");          // false
```

#### isAmountValid(double amount)

```java
public static boolean isAmountValid(double amount)
```

Validates monetary amount according to specification rules.

**Parameters:**

- `amount` — Monetary amount to validate

**Returns:** `true` if amount is valid, `false` otherwise

**Example:**

```java
BankAccount.isAmountValid(100.50);   // true
BankAccount.isAmountValid(0.00);     // true
BankAccount.isAmountValid(-10.00);   // false
BankAccount.isAmountValid(50.999);   // false (more than 2 decimals)
```

## Validation Rules

### Email Format

A valid email must:

- Contain exactly one `@` symbol
- Have at least one `.` after the `@`
- Have a prefix (before `@`) that:
  - Starts with an alphanumeric character
  - Can contain alphanumeric and special characters: `. ! $ % & ' * + - / = ? ^ _ \` { | } ~`
  - Cannot have consecutive special characters
- Have a domain (after `@`) that:
  - Contains alphanumeric characters, hyphens, and periods
  - Has at least 2 characters after the final `.` (top-level domain)

### Amount Format

A valid monetary amount must:

- Be non-negative (≥ 0)
- Have at most 2 decimal places
- Not be `NaN` or `Infinity`

Special case: Withdrawals cannot be zero (even though zero is a valid amount).

## Error Handling

Use try-catch blocks to handle exceptions:

```java
BankAccount account = new BankAccount("user@example.com", 100.00);

// Handling withdrawal errors
try {
    account.withdraw(200.00);
} catch (InsufficientFundsException e) {
    System.out.println("Not enough funds");
} catch (IllegalArgumentException e) {
    System.out.println("Invalid amount");
}

// Handling constructor errors
try {
    BankAccount invalid = new BankAccount("bad-email", -100);
} catch (IllegalArgumentException e) {
    System.out.println("Invalid email or balance: " + e.getMessage());
}

// Handling transfer errors
try {
    BankAccount from = new BankAccount("sender@example.com", 50.00);
    BankAccount to = new BankAccount("receiver@example.com", 0.00);
    from.transfer(to, 100.00);
} catch (InsufficientFundsException e) {
    System.out.println("Cannot complete transfer");
}
```
