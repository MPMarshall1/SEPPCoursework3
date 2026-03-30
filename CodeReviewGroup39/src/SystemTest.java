import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public class SystemTest {
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
    void testMSS() {
        //Main success scenario.
        String simulatedInput = String.join("\n", "email", "wrongpassword", "password", "Y", "newpassword") + "\n";

        ByteArrayInputStream testIn = new ByteArrayInputStream(simulatedInput.getBytes());
        System.setIn(testIn);

        ByteArrayOutputStream testOut = new ByteArrayOutputStream();
        System.setOut(new PrintStream(testOut));

        Main.main(new String[0]);

        String output = testOut.toString();

        //Test against outputs.
        assert output.contains("Enter X at any time to exit.") : "Does not provide exit";
        assert output.contains("Enter email:") : "Does not prompt for email";
        assert output.contains("Enter password:") : "Does not prompt for password";
        assert output.contains("Password incorrect") : "Does not flag wrong password";
        assert output.contains("Logged in.") : "Does not confirm login";
        assert output.contains("Would you like to change your password?") : "Does not prompt for password change";
    }

    @Test
    void testTermination() {
        //User terminates early.
        String simulatedInput = String.join("\n", "email", "X") + "\n";

        ByteArrayInputStream testIn = new ByteArrayInputStream(simulatedInput.getBytes());
        System.setIn(testIn);

        ByteArrayOutputStream testOut = new ByteArrayOutputStream();
        System.setOut(new PrintStream(testOut));

        Main.main(new String[0]);

        String output = testOut.toString();

        //Check inputs are there.
        assert output.contains("Enter X at any time to exit.") : "Does not provide exit";
        assert output.contains("Enter email:") : "Does not prompt for email";
        assert output.contains("Enter password:") : "Does not prompt for password";
        //Check inputs are not there.
        assert !output.contains("Password incorrect") : "Does not flag wrong password";
        assert !output.contains("Logged in.") : "Does not confirm login";
        assert !output.contains("Would you like to change your password?") : "Does not prompt for password change";
    }

    @Test
    void testIncorrectEmail() {
        //User mis-inputs email (twice).
        String simulatedInput = String.join("\n", "wrongemail", "anotherwrongemail", "X") + "\n";

        ByteArrayInputStream testIn = new ByteArrayInputStream(simulatedInput.getBytes());
        System.setIn(testIn);

        ByteArrayOutputStream testOut = new ByteArrayOutputStream();
        System.setOut(new PrintStream(testOut));

        Main.main(new String[0]);

        String output = testOut.toString();

        //Check inputs are there.
        assert output.contains("Enter X at any time to exit.") : "Does not provide exit";
        assert output.contains("Enter email:") : "Does not prompt for email";
        assert output.contains("That email is not associated with any faculty member. Enter email:") : "Does not prompt for email again";
        //Check inputs are not there.
        assert !output.contains("Enter password:") : "Does prompt for password";
        assert !output.contains("Password incorrect") : "Does flag wrong password";
        assert !output.contains("Logged in.") : "Does confirm login";
        assert !output.contains("Would you like to change your password?") : "Does prompt for password change";
    }
}
