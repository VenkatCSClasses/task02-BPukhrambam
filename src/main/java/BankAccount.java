import java.math.BigDecimal;
import java.math.RoundingMode;

public class BankAccount {
    private final String email;
    private BigDecimal balance;

    public BankAccount(String email, double startingBalance) {
        if (!isEmailValid(email))
            throw new IllegalArgumentException("Invalid email");
        if (!isAmountValid(startingBalance))
            throw new IllegalArgumentException("Invalid starting balance");

        this.email = email;
        this.balance = BigDecimal.valueOf(startingBalance).setScale(2, RoundingMode.UNNECESSARY);
    }

    public double getBalance() {
        return balance.doubleValue();
    }

    public String getEmail() {
        return email;
    }

    public void withdraw(double amount) throws InsufficientFundsException {
        if (!isAmountValid(amount))
            throw new IllegalArgumentException("Invalid amount");
        if (amount == 0.0)
            throw new IllegalArgumentException("Cannot withdraw zero");
        if (balance.compareTo(BigDecimal.valueOf(amount)) < 0)
            throw new InsufficientFundsException("Insufficient funds");

        balance = balance.subtract(BigDecimal.valueOf(amount));
    }

    public void deposit(double amount) {
        if (!isAmountValid(amount))
            throw new IllegalArgumentException("Invalid amount");

        balance = balance.add(BigDecimal.valueOf(amount));
    }

    public void transfer(BankAccount targetAccount, double amount) throws InsufficientFundsException {
        if (!isAmountValid(amount))
            throw new IllegalArgumentException("Invalid amount");
        if (balance.compareTo(BigDecimal.valueOf(amount)) < 0)
            throw new InsufficientFundsException("Insufficient funds");

        this.withdraw(amount);
        targetAccount.deposit(amount);
    }

    public static boolean isEmailValid(String email) {
        if (email == null || email.trim().isEmpty())
            return false;

        // Must contain exactly one @
        int atIndex = email.indexOf('@');
        if (atIndex == -1 || atIndex != email.lastIndexOf('@'))
            return false;

        // Must have at least one . after @
        int lastDotIndex = email.lastIndexOf('.');
        if (lastDotIndex == -1 || lastDotIndex <= atIndex)
            return false;

        // Validate prefix (before @)
        String prefix = email.substring(0, atIndex);
        if (!isValidPrefix(prefix))
            return false;

        // Validate domain (after @)
        String domain = email.substring(atIndex + 1);
        if (!isValidDomain(domain))
            return false;

        return true;
    }

    private static boolean isValidPrefix(String prefix) {
        if (prefix.isEmpty())
            return false;

        // First character must be alphanumeric
        char first = prefix.charAt(0);
        if (!Character.isLetterOrDigit(first))
            return false;

        // Check for valid characters and no consecutive special chars
        char prev = first;
        for (int i = 1; i < prefix.length(); i++) {
            char c = prefix.charAt(i);

            // Valid characters: alphanumeric and . ! $ % & ' * + - / = ? ^ _ ` { | } ~
            boolean isValid = Character.isLetterOrDigit(c) ||
                    ".!$%&'*+-/=?^_`{|}~".indexOf(c) != -1;

            if (!isValid)
                return false;

            // Check for consecutive special characters
            boolean prevIsSpecial = !Character.isLetterOrDigit(prev);
            boolean currIsSpecial = !Character.isLetterOrDigit(c);

            if (prevIsSpecial && currIsSpecial)
                return false;

            prev = c;
        }

        return true;
    }

    private static boolean isValidDomain(String domain) {
        if (domain.isEmpty())
            return false;

        // Must have at least one .
        int lastDotIndex = domain.lastIndexOf('.');
        if (lastDotIndex == -1)
            return false;

        // Must have at least 2 characters after final .
        String tld = domain.substring(lastDotIndex + 1);
        if (tld.length() < 2)
            return false;

        // Check for valid characters: alphanumeric, hyphen, period
        for (char c : domain.toCharArray()) {
            if (!Character.isLetterOrDigit(c) && c != '-' && c != '.')
                return false;
        }

        return true;
    }

    public static boolean isAmountValid(double amount) {
        if (amount < 0)
            return false;

        if (Double.isNaN(amount) || Double.isInfinite(amount))
            return false;

        // Check for at most 2 decimal places
        BigDecimal bd = BigDecimal.valueOf(amount);
        try {
            bd.setScale(2, RoundingMode.UNNECESSARY);
            return true;
        } catch (ArithmeticException e) {
            return false;
        }
    }
}
