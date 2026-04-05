package com.example;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;

public class CreateEventSystemTest {
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
    void createUnticketedCorrectly() {
        //Can create normal event
        String simulatedInput = String.join("\n", "REGISTER_EP", "theCoffeeCompany", "0123456789", "Michael", "Makes Coffee", "Michael@CoffeeCo.com", "ILoveCoffee123", "LOGIN", "Michael@CoffeeCo.com", "ILoveCoffee123", "CREATE_EVENT", "CoffeeFest", "no", "Music", "X") + "\n";

        ByteArrayInputStream testIn = new ByteArrayInputStream(simulatedInput.getBytes());
        System.setIn(testIn);

        ByteArrayOutputStream testOut = new ByteArrayOutputStream();
        System.setOut(new PrintStream(testOut));

        Main.main(new String[0]);

        String output = testOut.toString();

        //Test against outputs.
        assert output.contains("[SUCCESS] Successfully created event with ID: 1") : "Does not create event.";
    }

    @Test
    void createTicketedCorrectly() {
        //Can create normal event
        String simulatedInput = String.join("\n", "REGISTER_EP", "theCoffeeCompany", "0123456789", "Michael", "Makes Coffee", "Michael@CoffeeCo.com", "ILoveCoffee123", "LOGIN", "Michael@CoffeeCo.com", "ILoveCoffee123", "CREATE_EVENT", "CoffeeFest", "yes", "Music", "X") + "\n";

        ByteArrayInputStream testIn = new ByteArrayInputStream(simulatedInput.getBytes());
        System.setIn(testIn);

        ByteArrayOutputStream testOut = new ByteArrayOutputStream();
        System.setOut(new PrintStream(testOut));

        Main.main(new String[0]);

        String output = testOut.toString();

        //Test against outputs.
        assert output.contains("[SUCCESS] Successfully created event with ID: 1") : "Does not create event.";
    }

    @Test
    void createMaybeTicketed() {
        //Can't create event which might be ticketed until valid option is inputted.
        String simulatedInput = String.join("\n", "REGISTER_EP", "theCoffeeCompany", "0123456789", "Michael", "Makes Coffee", "Michael@CoffeeCo.com", "ILoveCoffee123", "LOGIN", "Michael@CoffeeCo.com", "ILoveCoffee123", "CREATE_EVENT", "CoffeeFest", "maybe", "no", "Music", "X") + "\n";

        ByteArrayInputStream testIn = new ByteArrayInputStream(simulatedInput.getBytes());
        System.setIn(testIn);

        ByteArrayOutputStream testOut = new ByteArrayOutputStream();
        System.setOut(new PrintStream(testOut));

        Main.main(new String[0]);

        String output = testOut.toString();

        //Test against outputs.
        assert output.contains("Invalid input. Please enter 'yes' or 'no'.") : "Does not flag ticket ambiguity.";
        assert output.contains("[SUCCESS] Successfully created event with ID: 1") : "Does not create event.";
    }

    @Test
    void createWrongType() {
        //Can't create event which might be ticketed until valid option is inputted.
        String simulatedInput = String.join("\n", "REGISTER_EP", "theCoffeeCompany", "0123456789", "Michael", "Makes Coffee", "Michael@CoffeeCo.com", "ILoveCoffee123", "LOGIN", "Michael@CoffeeCo.com", "ILoveCoffee123", "CREATE_EVENT", "CoffeeFest", "no", "Coffee drinking extravaganza", "Music", "X") + "\n";

        ByteArrayInputStream testIn = new ByteArrayInputStream(simulatedInput.getBytes());
        System.setIn(testIn);

        ByteArrayOutputStream testOut = new ByteArrayOutputStream();
        System.setOut(new PrintStream(testOut));

        Main.main(new String[0]);

        String output = testOut.toString();

        //Test against outputs.
        assert output.contains("Invalid event type. Please try again.") : "Does not flag type ambiguity.";
        assert output.contains("[SUCCESS] Successfully created event with ID: 1") : "Does not create event.";
    }

    @Test
    void createMultipleCorrectly() {
        //Can create normal events with different IDs
        String simulatedInput = String.join("\n", "REGISTER_EP", "theCoffeeCompany", "0123456789", "Michael", "Makes Coffee", "Michael@CoffeeCo.com", "ILoveCoffee123", "LOGIN", "Michael@CoffeeCo.com", "ILoveCoffee123", "CREATE_EVENT", "CoffeeFest", "no", "Music", "CREATE_EVENT", "CoffeeFest: The Sequel", "no", "Music", "X") + "\n";

        ByteArrayInputStream testIn = new ByteArrayInputStream(simulatedInput.getBytes());
        System.setIn(testIn);

        ByteArrayOutputStream testOut = new ByteArrayOutputStream();
        System.setOut(new PrintStream(testOut));

        Main.main(new String[0]);

        String output = testOut.toString();

        //Test against outputs.
        assert output.contains("[SUCCESS] Successfully created event with ID: 1") : "Does not create first event.";
        assert output.contains("[SUCCESS] Successfully created event with ID: 2") : "Does not create second event.";
    }
}
