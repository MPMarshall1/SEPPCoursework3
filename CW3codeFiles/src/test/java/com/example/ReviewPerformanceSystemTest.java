package com.example;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;

public class ReviewPerformanceSystemTest {
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
    void reviewCorrectly() {
        //Can review performance
        String simulatedInput = String.join("\n", "REGISTER_EP", "theCoffeeCompany", "0123456789", "Michael", "Makes Coffee", "Michael@CoffeeCo.com", "ILoveCoffee123", "LOGIN", "Michael@CoffeeCo.com", "ILoveCoffee123", "CREATE_EVENT", "CoffeeFest", "yes", "Music", "CREATE_PERFORMANCE", "1", "07/04/2026 12:00", "07/04/2026 12:01", "TheCoffeeMan", "Appleton Tower", "400", "no", "no", "400", "10", "LOGOUT", "LOGIN", "student1@ed.ac.uk", "student1pass", "BOOK_EVENT", "1", "5", "REVIEW_PERFORMANCE", "1", "3", "Sublime!", "X") + "\n";

        ByteArrayInputStream testIn = new ByteArrayInputStream(simulatedInput.getBytes());
        System.setIn(testIn);

        ByteArrayOutputStream testOut = new ByteArrayOutputStream();
        System.setOut(new PrintStream(testOut));

        Main.main(new String[0]);

        String output = testOut.toString();

        //Test against outputs.
        assert output.contains("Review submitted successfully. Thank you for your feedback!") : "Does not take review.";
    }

    @Test
    void reviewNonExistentPerformance() {
        //Cannot review performance which doesn't exist
        String simulatedInput = String.join("\n", "REGISTER_EP", "theCoffeeCompany", "0123456789", "Michael", "Makes Coffee", "Michael@CoffeeCo.com", "ILoveCoffee123", "LOGIN", "Michael@CoffeeCo.com", "ILoveCoffee123", "CREATE_EVENT", "CoffeeFest", "yes", "Music", "CREATE_PERFORMANCE", "1", "07/04/2026 12:00", "07/04/2026 12:01", "TheCoffeeMan", "Appleton Tower", "400", "no", "no", "400", "10", "LOGOUT", "LOGIN", "student1@ed.ac.uk", "student1pass", "BOOK_EVENT", "1", "5", "REVIEW_PERFORMANCE", "2", "X", "X") + "\n";

        ByteArrayInputStream testIn = new ByteArrayInputStream(simulatedInput.getBytes());
        System.setIn(testIn);

        ByteArrayOutputStream testOut = new ByteArrayOutputStream();
        System.setOut(new PrintStream(testOut));

        Main.main(new String[0]);

        String output = testOut.toString();

        //Test against outputs.
        assert output.contains("No performance found with that ID. Please try again.") : "Does not flag wrong performance.";
    }

    @Test
    void reviewUnbookedPerformance() {
        //Cannot review performance one didn't book
        String simulatedInput = String.join("\n", "REGISTER_EP", "theCoffeeCompany", "0123456789", "Michael", "Makes Coffee", "Michael@CoffeeCo.com", "ILoveCoffee123", "LOGIN", "Michael@CoffeeCo.com", "ILoveCoffee123", "CREATE_EVENT", "CoffeeFest", "yes", "Music", "CREATE_PERFORMANCE", "1", "07/04/2026 12:00", "07/04/2026 12:01", "TheCoffeeMan", "Appleton Tower", "400", "no", "no", "400", "10", "CREATE_PERFORMANCE", "1", "07/04/2026 12:02", "07/04/2026 12:03", "TheCoffeeWoman", "Appleton Tower", "400", "no", "no", "400", "10", "LOGOUT", "LOGIN", "student1@ed.ac.uk", "student1pass", "BOOK_EVENT", "1", "5", "REVIEW_PERFORMANCE", "2", "X", "X") + "\n";

        ByteArrayInputStream testIn = new ByteArrayInputStream(simulatedInput.getBytes());
        System.setIn(testIn);

        ByteArrayOutputStream testOut = new ByteArrayOutputStream();
        System.setOut(new PrintStream(testOut));

        Main.main(new String[0]);

        String output = testOut.toString();

        //Test against outputs.
        assert output.contains("You can only review performances you have booked.") : "Allows review for wrong performance.";
    }

    @Test
    void reviewPerformanceWithInvalidRating() {
        //Cannot review performance until rating is 1-5.
        String simulatedInput = String.join("\n", "REGISTER_EP", "theCoffeeCompany", "0123456789", "Michael", "Makes Coffee", "Michael@CoffeeCo.com", "ILoveCoffee123", "LOGIN", "Michael@CoffeeCo.com", "ILoveCoffee123", "CREATE_EVENT", "CoffeeFest", "yes", "Music", "CREATE_PERFORMANCE", "1", "07/04/2026 12:00", "07/04/2026 12:01", "TheCoffeeMan", "Appleton Tower", "400", "no", "no", "400", "10", "LOGOUT", "LOGIN", "student1@ed.ac.uk", "student1pass", "BOOK_EVENT", "1", "5", "REVIEW_PERFORMANCE", "1", "10", "5", "Sublime!", "X") + "\n";

        ByteArrayInputStream testIn = new ByteArrayInputStream(simulatedInput.getBytes());
        System.setIn(testIn);

        ByteArrayOutputStream testOut = new ByteArrayOutputStream();
        System.setOut(new PrintStream(testOut));

        Main.main(new String[0]);

        String output = testOut.toString();

        //Test against outputs.
        assert output.contains("Rating must be between 1 and 5.") : "Does not flag wrong rating.";
    }

    @Test
    void reviewPerformanceWithInvalidComment() {
        //Cannot review performance until comment is non-empty.
        String simulatedInput = String.join("\n", "REGISTER_EP", "theCoffeeCompany", "0123456789", "Michael", "Makes Coffee", "Michael@CoffeeCo.com", "ILoveCoffee123", "LOGIN", "Michael@CoffeeCo.com", "ILoveCoffee123", "CREATE_EVENT", "CoffeeFest", "yes", "Music", "CREATE_PERFORMANCE", "1", "07/04/2026 12:00", "07/04/2026 12:01", "TheCoffeeMan", "Appleton Tower", "400", "no", "no", "400", "10", "LOGOUT", "LOGIN", "student1@ed.ac.uk", "student1pass", "BOOK_EVENT", "1", "5", "REVIEW_PERFORMANCE", "1", "5", "", "Fine... Sublime!", "X") + "\n";

        ByteArrayInputStream testIn = new ByteArrayInputStream(simulatedInput.getBytes());
        System.setIn(testIn);

        ByteArrayOutputStream testOut = new ByteArrayOutputStream();
        System.setOut(new PrintStream(testOut));

        Main.main(new String[0]);

        String output = testOut.toString();

        //Test against outputs.
        assert output.contains("Review comment cannot be empty.") : "Does not flag wrong rating.";
    }
}
