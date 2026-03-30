import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Scanner;

public class FacultyMemberTest {

    //Test equality function.
    @Test
    void positiveEqualityCheckTest() {
        FacultyMember member1 = new FacultyMember("a", "b");
        FacultyMember member2 = new FacultyMember("a", "b");

        assert member1.equals(member2) : "Equality checks find false negatives";
    }

    @Test
    void negativeEqualityCheckTest1() {
        //Same email; different password
        FacultyMember member1 = new FacultyMember("a", "b");
        FacultyMember member2 = new FacultyMember("a", "c");

        assert !member1.equals(member2) : "Equality checks find false positives";
    }

    @Test
    void negativeEqualityCheckTest2() {
        // different email; same password
        FacultyMember member1 = new FacultyMember("a", "b");
        FacultyMember member2 = new FacultyMember("c", "b");

        assert !member1.equals(member2) : "Equality checks find false positives";
    }

    @Test
    void negativeEqualityCheckTest3() {
        // different email; different password
        FacultyMember member1 = new FacultyMember("a", "b");
        FacultyMember member2 = new FacultyMember("c", "d");

        assert !member1.equals(member2) : "Equality checks find false positives";
    }

    //Test output of changing passwords.
    //Uses redirected I/O to test against output to System.
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
    void testLoginWithPasswordChange() {
        FacultyMember member = new FacultyMember("email", "password");

        //Set up test input/output.
        String simulatedInput = String.join("\n", "wrongpassword", "password", "Y", "newpassword") + "\n";

        ByteArrayInputStream testIn = new ByteArrayInputStream(simulatedInput.getBytes());
        System.setIn(testIn);

        ByteArrayOutputStream testOut = new ByteArrayOutputStream();
        System.setOut(new PrintStream(testOut));

        //Call functionality.
        Scanner scanner = new Scanner(System.in);
        member.login(scanner);

        //Test effects and outputs.
        assert member.getPassword().equals("newpassword") : "Password not updated.";

        String output = testOut.toString();

        assert output.contains("Enter password:") : "Does not prompt for password";
        assert output.contains("Password incorrect") : "Does not flag wrong password";
        assert output.contains("Logged in.") : "Does not confirm login";
        assert output.contains("Would you like to change your password?") : "Does not prompt for password change";
    }

    @Test
    void testLoginWithoutPasswordChange() {
        FacultyMember member = new FacultyMember("email", "password");

        String simulatedInput = String.join("\n", "wrongpassword", "password", "N") + "\n";

        ByteArrayInputStream testIn = new ByteArrayInputStream(simulatedInput.getBytes());
        System.setIn(testIn);

        ByteArrayOutputStream testOut = new ByteArrayOutputStream();
        System.setOut(new PrintStream(testOut));

        Scanner scanner = new Scanner(System.in);
        member.login(scanner);

        assert member.getPassword().equals("password") : "Password updated.";

        String output = testOut.toString();

        assert output.contains("Enter password:") : "Does not prompt for password";
        assert output.contains("Password incorrect") : "Does not flag wrong password";
        assert output.contains("Logged in.") : "Does not confirm login";
        assert output.contains("Would you like to change your password?") : "Does not prompt for password change";
    }

    @Test
    void testUnsuccessfulLogin() {
        FacultyMember member = new FacultyMember("email", "password");

        String simulatedInput = String.join("\n", "wrongpassword", "anotherwrongpassword", "X") + "\n";

        ByteArrayInputStream testIn = new ByteArrayInputStream(simulatedInput.getBytes());
        System.setIn(testIn);

        ByteArrayOutputStream testOut = new ByteArrayOutputStream();
        System.setOut(new PrintStream(testOut));

        Scanner scanner = new Scanner(System.in);
        member.login(scanner);

        String output = testOut.toString();

        assert output.contains("Enter password:") : "Does not prompt for password";
        assert output.contains("Password incorrect") : "Does not flag wrong password";
        assert !output.contains("Logged in.") : "Does not confirm login";
    }

}
