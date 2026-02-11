import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class BankAccountYamlTests {

    private static final double EPS = 1e-9;

    @Test
    void getBalanceTests() {
        BankAccount a = new BankAccount("a@b.com", 200);
        assertEquals(200.0, a.getBalance(), EPS);

        BankAccount b = new BankAccount("b@c.com", 0);
        assertEquals(0.0, b.getBalance(), EPS);
    }

    @Test
    void withdrawTests() {
        BankAccount a = new BankAccount("a@b.com", 200);
        assertDoesNotThrow(() -> a.withdraw(100));
        assertEquals(100.0, a.getBalance(), EPS);

        BankAccount a2 = new BankAccount("a@b.com", 100);
        assertDoesNotThrow(() -> a2.withdraw(100));
        assertEquals(0.0, a2.getBalance(), EPS);

        BankAccount b = new BankAccount("b@c.com", 100);
        assertThrows(IllegalArgumentException.class, () -> b.withdraw(-50));
        assertThrows(IllegalArgumentException.class, () -> b.withdraw(0));
        assertThrows(IllegalArgumentException.class, () -> b.withdraw(10.001));

        BankAccount b2 = new BankAccount("b@c.com", 100);
        assertThrows(InsufficientFundsException.class, () -> b2.withdraw(100.01));
        assertThrows(InsufficientFundsException.class, () -> b2.withdraw(150));
    }

    @Test
    void isEmailValidTests() {
        assertTrue(BankAccount.isEmailValid("abc_def@mail.com"));
        assertTrue(BankAccount.isEmailValid("abc.def@mail.com"));
        assertTrue(BankAccount.isEmailValid("abc-d@mail.com"));
        assertTrue(BankAccount.isEmailValid("abc.def@mail-archive.com"));
        assertTrue(BankAccount.isEmailValid("b@google.com"));
        assertTrue(BankAccount.isEmailValid("abc@hotmail.com"));
        assertFalse(BankAccount.isEmailValid(" baneet@"));
        assertTrue(BankAccount.isEmailValid("b@g.co"));
        assertTrue(BankAccount.isEmailValid("abc@yahoo.com"));
        assertFalse(BankAccount.isEmailValid("abc@def.g"));
    }

    @Test
    void constructorTests() {
        BankAccount a = new BankAccount("a@b.com", 200);
        assertEquals("a@b.com", a.getEmail());
        assertEquals(200.0, a.getBalance(), EPS);

        BankAccount b = new BankAccount("b@g.com", 20.34);
        assertEquals("b@g.com", b.getEmail());
        assertEquals(20.34, b.getBalance(), EPS);

        assertThrows(IllegalArgumentException.class, () -> new BankAccount("", 100));
        assertThrows(IllegalArgumentException.class, () -> new BankAccount("b@c.com", -100.02));
        assertThrows(IllegalArgumentException.class, () -> new BankAccount("b@c.com", 100.001));
    }

    @Test
    void isAmountValidTests() {
        assertFalse(BankAccount.isAmountValid(0.001));
        assertFalse(BankAccount.isAmountValid(54.021));
        assertTrue(BankAccount.isAmountValid(0.99));
        assertTrue(BankAccount.isAmountValid(2.1));
        assertTrue(BankAccount.isAmountValid(200.01));
        assertTrue(BankAccount.isAmountValid(0.0));
        assertTrue(BankAccount.isAmountValid(20.0));
        assertFalse(BankAccount.isAmountValid(-0.01));
        assertFalse(BankAccount.isAmountValid(-100.00));
    }

    @Test
    void depositTests() {
        BankAccount a = new BankAccount("a@b.com", 100);
        a.deposit(50);
        assertEquals(150.0, a.getBalance(), EPS);

        BankAccount a2 = new BankAccount("a@b.com", 100);
        assertThrows(IllegalArgumentException.class, () -> a2.deposit(-20));
        assertThrows(IllegalArgumentException.class, () -> a2.deposit(-0.01));

        BankAccount a3 = new BankAccount("a@b.com", 150);
        a3.deposit(0);
        assertEquals(150.0, a3.getBalance(), EPS);

        BankAccount a4 = new BankAccount("a@b.com", 150);
        assertThrows(IllegalArgumentException.class, () -> a4.deposit(10.001));
        assertThrows(IllegalArgumentException.class, () -> a4.deposit(5.6789));

        BankAccount a5 = new BankAccount("a@b.com", 150);
        a5.deposit(20.99);
        assertEquals(170.99, a5.getBalance(), EPS);
    }

    @Test
    void transferTests() {
        BankAccount from = new BankAccount("a@b.com", 200);
        BankAccount to = new BankAccount("b@c.com", 100);
        assertDoesNotThrow(() -> from.transfer(to, 50));
        assertEquals(150.0, from.getBalance(), EPS);
        assertEquals(150.0, to.getBalance(), EPS);

        BankAccount from2 = new BankAccount("a@b.com", 150);
        BankAccount to2 = new BankAccount("b@c.com", 150);
        assertDoesNotThrow(() -> from2.transfer(to2, 150));
        assertEquals(0.0, from2.getBalance(), EPS);
        assertEquals(300.0, to2.getBalance(), EPS);

        BankAccount ff = new BankAccount("b@c.com", 300);
        BankAccount tt = new BankAccount("a@b.com", 0);
        assertThrows(IllegalArgumentException.class, () -> ff.transfer(tt, -20));
        assertThrows(IllegalArgumentException.class, () -> ff.transfer(tt, -0.01));
        assertThrows(IllegalArgumentException.class, () -> ff.transfer(tt, 10.001));
        assertThrows(IllegalArgumentException.class, () -> ff.transfer(tt, 5.6789));

        BankAccount from3 = new BankAccount("b@c.com", 300);
        BankAccount to3 = new BankAccount("a@b.com", 0);
        assertDoesNotThrow(() -> from3.transfer(to3, 50.99));
        assertEquals(249.01, from3.getBalance(), EPS);
        assertEquals(50.99, to3.getBalance(), EPS);

        BankAccount f4 = new BankAccount("a@b.com", 50.99);
        BankAccount t4 = new BankAccount("b@c.com", 249.01);
        assertThrows(InsufficientFundsException.class, () -> f4.transfer(t4, 51));
        assertThrows(InsufficientFundsException.class, () -> f4.transfer(t4, 100));

        BankAccount f5 = new BankAccount("a@b.com", 50.99);
        BankAccount t5 = new BankAccount("b@c.com", 249.01);
        assertDoesNotThrow(() -> f5.transfer(t5, 50.98));
        assertEquals(0.01, f5.getBalance(), EPS);
        assertEquals(299.99, t5.getBalance(), EPS);
    }
}
