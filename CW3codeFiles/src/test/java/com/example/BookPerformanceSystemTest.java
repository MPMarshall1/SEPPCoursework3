package com.example;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;

public class BookPerformanceSystemTest {
    //Redirect I/O to fake input and capture output from System.

    private InputStream originalIn;
    private PrintStream originalOut;

    @BeforeEach
    void redirectIO() {
        originalIn = System.in;
        originalOut = System.out;
    }

    @AfterEach
    void restoreIO() {
        System.setIn(originalIn);
        System.setOut(originalOut);
    }

    @Test
    void bookTicketlessCorrectly() {
        //Can vacuously book for unticketed event.
        String simulatedInput = String.join("\n", "REGISTER_EP", "theCoffeeCompany", "0123456789", "Michael", "Makes Coffee", "Michael@CoffeeCo.com", "ILoveCoffee123", "LOGIN", "Michael@CoffeeCo.com", "ILoveCoffee123", "CREATE_EVENT", "CoffeeFest", "no", "Music", "CREATE_PERFORMANCE", "1", "07/04/2026 12:00", "07/04/2026 12:01", "TheCoffeeMan", "Appleton Tower", "400", "no", "no", "LOGOUT", "LOGIN", "student1@ed.ac.uk", "student1pass", "BOOK_EVENT", "1", "1", "X") + "\n";

        ByteArrayInputStream testIn = new ByteArrayInputStream(simulatedInput.getBytes());
        System.setIn(testIn);

        ByteArrayOutputStream testOut = new ByteArrayOutputStream();
        System.setOut(new PrintStream(testOut));

        Main.main(new String[0]);

        String output = testOut.toString();

        //Test against outputs.
        assert output.contains("Booking Number: 1 | Event: CoffeeFest | 0 | 0.0") : "Does not book performance.";
    }

    @Test
    void bookTicketedCorrectly() {
        //Can book for a ticketed event normally.
        String simulatedInput = String.join("\n", "REGISTER_EP", "theCoffeeCompany", "0123456789", "Michael", "Makes Coffee", "Michael@CoffeeCo.com", "ILoveCoffee123", "LOGIN", "Michael@CoffeeCo.com", "ILoveCoffee123", "CREATE_EVENT", "CoffeeFest", "yes", "Music", "CREATE_PERFORMANCE", "1", "07/04/2026 12:00", "07/04/2026 12:01", "TheCoffeeMan", "Appleton Tower", "400", "no", "no", "400", "10", "LOGOUT", "LOGIN", "student1@ed.ac.uk", "student1pass", "BOOK_EVENT", "1", "5", "X") + "\n";

        ByteArrayInputStream testIn = new ByteArrayInputStream(simulatedInput.getBytes());
        System.setIn(testIn);

        ByteArrayOutputStream testOut = new ByteArrayOutputStream();
        System.setOut(new PrintStream(testOut));

        Main.main(new String[0]);

        String output = testOut.toString();

        //Test against outputs.
        assert output.contains("Booking Number: 1 | Event: CoffeeFest | 5 | 50.0") : "Does not book performance.";
    }

    @Test
    void bookNegativeNumber() {
        //Can't book negative number of tickets
        String simulatedInput = String.join("\n", "REGISTER_EP", "theCoffeeCompany", "0123456789", "Michael", "Makes Coffee", "Michael@CoffeeCo.com", "ILoveCoffee123", "LOGIN", "Michael@CoffeeCo.com", "ILoveCoffee123", "CREATE_EVENT", "CoffeeFest", "yes", "Music", "CREATE_PERFORMANCE", "1", "07/04/2026 12:00", "07/04/2026 12:01", "TheCoffeeMan", "Appleton Tower", "400", "no", "no", "400", "10", "LOGOUT", "LOGIN", "student1@ed.ac.uk", "student1pass", "BOOK_EVENT", "1", "-5", "5", "X") + "\n";

        ByteArrayInputStream testIn = new ByteArrayInputStream(simulatedInput.getBytes());
        System.setIn(testIn);

        ByteArrayOutputStream testOut = new ByteArrayOutputStream();
        System.setOut(new PrintStream(testOut));

        Main.main(new String[0]);

        String output = testOut.toString();

        //Test against outputs.
        assert output.contains("Number of tickets must be positive.") : "allows negative number of tickets to be booked.";
    }

    @Test
    void overbookEvent() {
        //Can't book if not enough tickets left
        String simulatedInput = String.join("\n", "REGISTER_EP", "theCoffeeCompany", "0123456789", "Michael", "Makes Coffee", "Michael@CoffeeCo.com", "ILoveCoffee123", "LOGIN", "Michael@CoffeeCo.com", "ILoveCoffee123", "CREATE_EVENT", "CoffeeFest", "yes", "Music", "CREATE_PERFORMANCE", "1", "07/04/2026 12:00", "07/04/2026 12:01", "TheCoffeeMan", "Appleton Tower", "400", "no", "no", "400", "10", "LOGOUT", "LOGIN", "student1@ed.ac.uk", "student1pass", "BOOK_EVENT", "1", "401", "400", "X") + "\n";

        ByteArrayInputStream testIn = new ByteArrayInputStream(simulatedInput.getBytes());
        System.setIn(testIn);

        ByteArrayOutputStream testOut = new ByteArrayOutputStream();
        System.setOut(new PrintStream(testOut));

        Main.main(new String[0]);

        String output = testOut.toString();

        //Test against outputs.
        assert output.contains("Not enough tickets available.") : "allows overbooking.";
    }
}
