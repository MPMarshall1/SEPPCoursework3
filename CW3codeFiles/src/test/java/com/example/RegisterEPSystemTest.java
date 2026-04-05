package com.example;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;

public class RegisterEPSystemTest {
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
    void registerCorrectly() {
        //Register normal EP
        String simulatedInput = String.join("\n", "REGISTER_EP", "theCoffeeCompany", "0123456789", "Michael", "Makes Coffee", "Michael@CoffeeCo.com", "ILoveCoffee123", "X") + "\n";

        ByteArrayInputStream testIn = new ByteArrayInputStream(simulatedInput.getBytes());
        System.setIn(testIn);

        ByteArrayOutputStream testOut = new ByteArrayOutputStream();
        System.setOut(new PrintStream(testOut));

        Main.main(new String[0]);

        String output = testOut.toString();

        //Test against outputs.
        assert output.contains("[SUCCESS] Entertainment provider registered successfully: theCoffeeCompany") : "Does not register business.";
    }

    @Test
    void registerIncorrectNumber() {
        //Cannot register invalid business numebr
        String simulatedInput = String.join("\n", "REGISTER_EP", "theCoffeeCompany", "0123456789TOOLONG", "X") + "\n";

        ByteArrayInputStream testIn = new ByteArrayInputStream(simulatedInput.getBytes());
        System.setIn(testIn);

        ByteArrayOutputStream testOut = new ByteArrayOutputStream();
        System.setOut(new PrintStream(testOut));

        Main.main(new String[0]);

        String output = testOut.toString();

        //Test against outputs.
        assert output.contains("Business Number is not of the correct length.") : "Does allow invalid business number.";
        assert !output.contains("Enter representative name:") : "Does prompt for further fields";
    }

    @Test
    void registerBlankPassword() {
        //Cannot register with blank password
        String simulatedInput = String.join("\n", "REGISTER_EP", "theCoffeeCompany", "0123456789", "Michael", "Makes Coffee", "Michael@CoffeeCo.com", "", "X") + "\n";

        ByteArrayInputStream testIn = new ByteArrayInputStream(simulatedInput.getBytes());
        System.setIn(testIn);

        ByteArrayOutputStream testOut = new ByteArrayOutputStream();
        System.setOut(new PrintStream(testOut));

        Main.main(new String[0]);

        String output = testOut.toString();

        //Test against outputs.
        assert output.contains("Password cannot be empty. Registration failed.") : "Does not flag missing password.";
        assert !output.contains("[ERROR] All fields are required. Registration failed.") : "Does  flag missing field.";
        assert !output.contains("[SUCCESS] Entertainment provider registered successfully: theCoffeeCompany") : "Does register business.";
    }

    @Test
    void registerBlankField() {
        //Cannot register with blank field
        String simulatedInput = String.join("\n", "REGISTER_EP", "theCoffeeCompany", "0123456789", "", "Makes Coffee", "Michael@CoffeeCo.com", "ILoveCoffee123", "X") + "\n";

        ByteArrayInputStream testIn = new ByteArrayInputStream(simulatedInput.getBytes());
        System.setIn(testIn);

        ByteArrayOutputStream testOut = new ByteArrayOutputStream();
        System.setOut(new PrintStream(testOut));

        Main.main(new String[0]);

        String output = testOut.toString();

        //Test against outputs.
        assert output.contains("[ERROR] All fields are required. Registration failed.") : "Does not flag missing field.";
        assert !output.contains("[SUCCESS] Entertainment provider registered successfully: theCoffeeCompany") : "Does register business.";
    }

    @Test
    void registerReusedEmail() {
        //Cannot register with email of another user
        String simulatedInput = String.join("\n", "REGISTER_EP", "theCoffeeCompany", "0123456789", "Michael", "Makes Coffee", "student1@ed.ac.uk", "X") + "\n";

        ByteArrayInputStream testIn = new ByteArrayInputStream(simulatedInput.getBytes());
        System.setIn(testIn);

        ByteArrayOutputStream testOut = new ByteArrayOutputStream();
        System.setOut(new PrintStream(testOut));

        Main.main(new String[0]);

        String output = testOut.toString();

        //Test against outputs.
        assert output.contains("That email address is already registered.") : "Does not flag repeated email.";
        assert !output.contains("[SUCCESS] Entertainment provider registered successfully: theCoffeeCompany") : "Does register business.";
    }
}
