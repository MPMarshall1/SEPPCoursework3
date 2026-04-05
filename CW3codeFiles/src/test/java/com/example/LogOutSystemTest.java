package com.example;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;

public class LogOutSystemTest {
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
    void logOutStudentCorrectly() {
        //Can log out as student
        String simulatedInput = String.join("\n", "LOGIN", "student1@ed.ac.uk", "student1pass", "LOGOUT", "X") + "\n";

        ByteArrayInputStream testIn = new ByteArrayInputStream(simulatedInput.getBytes());
        System.setIn(testIn);

        ByteArrayOutputStream testOut = new ByteArrayOutputStream();
        System.setOut(new PrintStream(testOut));

        Main.main(new String[0]);

        String output = testOut.toString();

        //Test against outputs.
        assert output.contains("[SUCCESS] Successfully logged in as student1@ed.ac.uk.") : "Does not log in.";
        assert output.contains("[SUCCESS] Successfully logged out (student1@ed.ac.uk).") : "Does not log out.";
    }

    @Test
    void logOutAdminCorrectly() {
        //Can log out as admin
        String simulatedInput = String.join("\n", "LOGIN", "admin@ed.ac.uk", "adminpass", "LOGOUT", "X") + "\n";

        ByteArrayInputStream testIn = new ByteArrayInputStream(simulatedInput.getBytes());
        System.setIn(testIn);

        ByteArrayOutputStream testOut = new ByteArrayOutputStream();
        System.setOut(new PrintStream(testOut));

        Main.main(new String[0]);

        String output = testOut.toString();

        //Test against outputs.
        assert output.contains("[SUCCESS] Successfully logged in as admin@ed.ac.uk.") : "Does not log in.";
        assert output.contains("[SUCCESS] Successfully logged out (admin@ed.ac.uk).") : "Does not log out.";
    }

    @Test
    void logOutIncorrectly() {
        //Can't log out without logging in.
        String simulatedInput = String.join("\n", "LOGOUT", "X") + "\n";

        ByteArrayInputStream testIn = new ByteArrayInputStream(simulatedInput.getBytes());
        System.setIn(testIn);

        ByteArrayOutputStream testOut = new ByteArrayOutputStream();
        System.setOut(new PrintStream(testOut));

        Main.main(new String[0]);

        String output = testOut.toString();

        //Test against outputs.
        assert output.contains("[ERROR] Invalid menu selection. Please try again.") : "Does not flag error.";
    }

    @Test
    void logOutAndIn() {
        //Can log back into different account after logging out. (Works for same account by extension.)
        String simulatedInput = String.join("\n", "LOGIN", "student1@ed.ac.uk", "student1pass", "LOGOUT", "LOGIN", "admin@ed.ac.uk", "adminpass", "X") + "\n";

        ByteArrayInputStream testIn = new ByteArrayInputStream(simulatedInput.getBytes());
        System.setIn(testIn);

        ByteArrayOutputStream testOut = new ByteArrayOutputStream();
        System.setOut(new PrintStream(testOut));

        Main.main(new String[0]);

        String output = testOut.toString();

        //Test against outputs.
        assert output.contains("[SUCCESS] Successfully logged in as student1@ed.ac.uk.") : "Does not complete first log in.";
        assert output.contains("[SUCCESS] Successfully logged out (student1@ed.ac.uk).") : "Does not log out.";
        assert output.contains("[SUCCESS] Successfully logged in as admin@ed.ac.uk.") : "Does not complete second log in.";
    }
}