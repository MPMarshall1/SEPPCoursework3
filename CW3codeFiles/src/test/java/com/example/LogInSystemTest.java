package com.example;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;

public class LogInSystemTest {
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
    void systemStarts() {
        //System starts normally and displays initial message.
        String simulatedInput = String.join("\n", "X") + "\n";

        ByteArrayInputStream testIn = new ByteArrayInputStream(simulatedInput.getBytes());
        System.setIn(testIn);

        ByteArrayOutputStream testOut = new ByteArrayOutputStream();
        System.setOut(new PrintStream(testOut));

        Main.main(new String[0]);

        String output = testOut.toString();

        //Test against outputs.
        assert output.contains("[SUCCESS] System started. Enter 'X' to exit.") : "Does not start.";
        assert output.contains("Enter option (LOGIN, REGISTER_EP): ") : "Does not start in guest mode.";
    }

    @Test
    void logInStudentCorrectly() {
        //Can log in as student
        String simulatedInput = String.join("\n", "LOGIN", "student1@ed.ac.uk", "student1pass", "X") + "\n";

        ByteArrayInputStream testIn = new ByteArrayInputStream(simulatedInput.getBytes());
        System.setIn(testIn);

        ByteArrayOutputStream testOut = new ByteArrayOutputStream();
        System.setOut(new PrintStream(testOut));

        Main.main(new String[0]);

        String output = testOut.toString();

        //Test against outputs.
        assert output.contains("[SUCCESS] Successfully logged in as student1@ed.ac.uk.") : "Does not log in.";
    }

    @Test
    void logInStudentIncorrectEmail() {
        //Can't log in as student with wrong email
        String simulatedInput = String.join("\n", "LOGIN", "wrongstudent1@ed.ac.uk", "student1pass", "X") + "\n";

        ByteArrayInputStream testIn = new ByteArrayInputStream(simulatedInput.getBytes());
        System.setIn(testIn);

        ByteArrayOutputStream testOut = new ByteArrayOutputStream();
        System.setOut(new PrintStream(testOut));

        Main.main(new String[0]);

        String output = testOut.toString();

        //Test against outputs.
        assert output.contains("[ERROR] Incorrect email or password. Please try again.") : "Does not flag incorrect log in.";
    }

    @Test
    void logInStudentIncorrectPassword() {
        //Can't log in as student with wrong email
        String simulatedInput = String.join("\n", "LOGIN", "student1@ed.ac.uk", "student1wrongpass", "X") + "\n";

        ByteArrayInputStream testIn = new ByteArrayInputStream(simulatedInput.getBytes());
        System.setIn(testIn);

        ByteArrayOutputStream testOut = new ByteArrayOutputStream();
        System.setOut(new PrintStream(testOut));

        Main.main(new String[0]);

        String output = testOut.toString();

        //Test against outputs.
        assert output.contains("[ERROR] Incorrect email or password. Please try again.") : "Does not flag incorrect log in.";
    }

    @Test
    void logInAdminCorrectly() {
        //Can log in as student
        String simulatedInput = String.join("\n", "LOGIN", "admin@ed.ac.uk", "adminpass", "X") + "\n";

        ByteArrayInputStream testIn = new ByteArrayInputStream(simulatedInput.getBytes());
        System.setIn(testIn);

        ByteArrayOutputStream testOut = new ByteArrayOutputStream();
        System.setOut(new PrintStream(testOut));

        Main.main(new String[0]);

        String output = testOut.toString();

        //Test against outputs.
        assert output.contains("[SUCCESS] Successfully logged in as admin@ed.ac.uk.") : "Does not log in.";
    }

    @Test
    void logInAdminIncorrectEmail() {
        //Can't log in as student with wrong email
        String simulatedInput = String.join("\n", "LOGIN", "wrongadmin@ed.ac.uk", "adminpass", "X") + "\n";

        ByteArrayInputStream testIn = new ByteArrayInputStream(simulatedInput.getBytes());
        System.setIn(testIn);

        ByteArrayOutputStream testOut = new ByteArrayOutputStream();
        System.setOut(new PrintStream(testOut));

        Main.main(new String[0]);

        String output = testOut.toString();

        //Test against outputs.
        assert output.contains("[ERROR] Incorrect email or password. Please try again.") : "Does not flag incorrect log in.";
    }

    @Test
    void logInAdminIncorrectPassword() {
        //Can't log in as student with wrong email
        String simulatedInput = String.join("\n", "LOGIN", "admin@ed.ac.uk", "adminwrongpass", "X") + "\n";

        ByteArrayInputStream testIn = new ByteArrayInputStream(simulatedInput.getBytes());
        System.setIn(testIn);

        ByteArrayOutputStream testOut = new ByteArrayOutputStream();
        System.setOut(new PrintStream(testOut));

        Main.main(new String[0]);

        String output = testOut.toString();

        //Test against outputs.
        assert output.contains("[ERROR] Incorrect email or password. Please try again.") : "Does not flag incorrect log in.";
    }

    @Test
    void logInEPCorrectly() {
        //Can log in as EP after registering
        String simulatedInput = String.join("\n", "REGISTER_EP", "theCoffeeCompany", "0123456789", "Michael", "Makes Coffee", "Michael@CoffeeCo.com", "ILoveCoffee123", "LOGIN", "Michael@CoffeeCo.com", "ILoveCoffee123", "X") + "\n";

        ByteArrayInputStream testIn = new ByteArrayInputStream(simulatedInput.getBytes());
        System.setIn(testIn);

        ByteArrayOutputStream testOut = new ByteArrayOutputStream();
        System.setOut(new PrintStream(testOut));

        Main.main(new String[0]);

        String output = testOut.toString();

        //Test against outputs.
        assert output.contains("[SUCCESS] Successfully logged in as Michael@CoffeeCo.com.") : "Does not log in.";
    }

    @Test
    void logInEPIncorrectEmail() {
        //not log in as EP after registering if the email does not match
        String simulatedInput = String.join("\n", "REGISTER_EP", "theCoffeeCompany", "0123456789", "Michael", "Makes Coffee", "Michael@CoffeeCo.com", "ILoveCoffee123", "LOGIN", "wrongMichael@CoffeeCo.com", "ILoveCoffee123", "X") + "\n";

        ByteArrayInputStream testIn = new ByteArrayInputStream(simulatedInput.getBytes());
        System.setIn(testIn);

        ByteArrayOutputStream testOut = new ByteArrayOutputStream();
        System.setOut(new PrintStream(testOut));

        Main.main(new String[0]);

        String output = testOut.toString();

        //Test against outputs.
        assert output.contains("[ERROR] Incorrect email or password. Please try again.") : "Does not flag incorrect log in.";
        assert !output.contains("[SUCCESS] Successfully logged in as Michael@CoffeeCo.com.") : "Does log in.";
    }

    @Test
    void logInEPIncorrectPassword() {
        //not log in as EP after registering if the password does not match
        String simulatedInput = String.join("\n", "REGISTER_EP", "theCoffeeCompany", "0123456789", "Michael", "Makes Coffee", "Michael@CoffeeCo.com", "ILoveCoffee123", "LOGIN", "Michael@CoffeeCo.com", "IHateCoffee123", "X") + "\n";

        ByteArrayInputStream testIn = new ByteArrayInputStream(simulatedInput.getBytes());
        System.setIn(testIn);

        ByteArrayOutputStream testOut = new ByteArrayOutputStream();
        System.setOut(new PrintStream(testOut));

        Main.main(new String[0]);

        String output = testOut.toString();

        //Test against outputs.
        assert output.contains("[ERROR] Incorrect email or password. Please try again.") : "Does not flag incorrect log in.";
        assert !output.contains("[SUCCESS] Successfully logged in as Michael@CoffeeCo.com.") : "Does log in.";
    }
}
