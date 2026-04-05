package com.example;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;

public class CancelBookingSystemTest {
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
    void CancelBlankBookings() {
        //Cannot cancel booking if there are none.
        String simulatedInput = String.join("\n", "LOGIN", "student1@ed.ac.uk", "student1pass", "CANCEL_BOOKING", "X") + "\n";

        ByteArrayInputStream testIn = new ByteArrayInputStream(simulatedInput.getBytes());
        System.setIn(testIn);

        ByteArrayOutputStream testOut = new ByteArrayOutputStream();
        System.setOut(new PrintStream(testOut));

        Main.main(new String[0]);

        String output = testOut.toString();

        //Test against outputs.
        assert output.contains("[ERROR] You have no active bookings to cancel.") : "Does not flag no bookings.";
    }

    @Test
    void CancelBookingCorrectly() {
        //Can cancel one's own booking normally
        String simulatedInput = String.join("\n", "REGISTER_EP", "theCoffeeCompany", "0123456789", "Michael", "Makes Coffee", "Michael@CoffeeCo.com", "ILoveCoffee123", "LOGIN", "Michael@CoffeeCo.com", "ILoveCoffee123", "CREATE_EVENT", "CoffeeFest", "yes", "Music", "CREATE_PERFORMANCE", "1", "07/04/2026 12:00", "07/04/2026 12:01", "TheCoffeeMan", "Appleton Tower", "400", "no", "no", "400", "10", "LOGOUT", "LOGIN", "student1@ed.ac.uk", "student1pass", "BOOK_EVENT", "1", "5", "CANCEL_BOOKING", "1", "X", "X") + "\n";

        ByteArrayInputStream testIn = new ByteArrayInputStream(simulatedInput.getBytes());
        System.setIn(testIn);

        ByteArrayOutputStream testOut = new ByteArrayOutputStream();
        System.setOut(new PrintStream(testOut));

        Main.main(new String[0]);

        String output = testOut.toString();

        //Test against outputs.
        assert output.contains("Booking 1 cancelled successfully.") : "Does not cancel booking.";
    }

    @Test
    void CancelAnothersBooking() {
        //Can cancel one's own booking normally
        String simulatedInput = String.join("\n", "REGISTER_EP", "theCoffeeCompany", "0123456789", "Michael", "Makes Coffee", "Michael@CoffeeCo.com", "ILoveCoffee123", "LOGIN", "Michael@CoffeeCo.com", "ILoveCoffee123", "CREATE_EVENT", "CoffeeFest", "yes", "Music", "CREATE_PERFORMANCE", "1", "07/04/2026 12:00", "07/04/2026 12:01", "TheCoffeeMan", "Appleton Tower", "400", "no", "no", "400", "10", "LOGOUT", "LOGIN", "student1@ed.ac.uk", "student1pass", "BOOK_EVENT", "1", "5", "LOGOUT", "student2@ed.ac.uk", "student2pass", "BOOK_EVENT", "1", "4", "CANCEL_BOOKING", "1", "X", "X") + "\n";

        ByteArrayInputStream testIn = new ByteArrayInputStream(simulatedInput.getBytes());
        System.setIn(testIn);

        ByteArrayOutputStream testOut = new ByteArrayOutputStream();
        System.setOut(new PrintStream(testOut));

        Main.main(new String[0]);

        String output = testOut.toString();

        //Test against outputs.
        assert !output.contains("Booking 1 cancelled successfully.") : "Does not cancel booking.";
    }
}
