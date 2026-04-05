package com.example;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;

public class EditPreferencesSystemTest {
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
    void initiallyNoPreferences() {
        //No preferences should be listed initially.
        String simulatedInput = String.join("\n", "LOGIN", "student1@ed.ac.uk", "student1pass", "EDIT_PREFERENCES", "", "X") + "\n";

        ByteArrayInputStream testIn = new ByteArrayInputStream(simulatedInput.getBytes());
        System.setIn(testIn);

        ByteArrayOutputStream testOut = new ByteArrayOutputStream();
        System.setOut(new PrintStream(testOut));

        Main.main(new String[0]);

        String output = testOut.toString();

        //Test against outputs.
        assert output.contains("Current preferences — Music: false, Theatre: false, Dance: false, Movie: false, Sports: false") : "Initial preferences not correct.";
    }

    @Test
    void UpdateThreeCorrectly() {
        //Can update three preferences normally.
        String simulatedInput = String.join("\n", "LOGIN", "student1@ed.ac.uk", "student1pass", "EDIT_PREFERENCES", "Music, Theatre, Dance", "EDIT_PREFERENCES", "", "X") + "\n";

        ByteArrayInputStream testIn = new ByteArrayInputStream(simulatedInput.getBytes());
        System.setIn(testIn);

        ByteArrayOutputStream testOut = new ByteArrayOutputStream();
        System.setOut(new PrintStream(testOut));

        Main.main(new String[0]);

        String output = testOut.toString();

        //Test against outputs.
        assert output.contains("[SUCCESS] Preferences updated successfully.") : "Confirms update.";
        assert output.contains("[SUCCESS] Current preferences — Music: true, Theatre: true, Dance: true, Movie: false, Sports: false") : "Update holds.";
    }

    @Test
    void UpdateLessThanThreeCorrectly() {
        //Can update less than three preferences normally.
        String simulatedInput = String.join("\n", "LOGIN", "student1@ed.ac.uk", "student1pass", "EDIT_PREFERENCES", "Music, Theatre", "EDIT_PREFERENCES", "", "X") + "\n";

        ByteArrayInputStream testIn = new ByteArrayInputStream(simulatedInput.getBytes());
        System.setIn(testIn);

        ByteArrayOutputStream testOut = new ByteArrayOutputStream();
        System.setOut(new PrintStream(testOut));

        Main.main(new String[0]);

        String output = testOut.toString();

        //Test against outputs.
        assert output.contains("[SUCCESS] Preferences updated successfully.") : "Confirms update.";
        assert output.contains("[SUCCESS] Current preferences — Music: true, Theatre: true, Dance: false, Movie: false, Sports: false") : "Update holds.";
    }

    @Test
    void ResetPreviouslySelected() {
        //Can reset preferences previously selected.
        String simulatedInput = String.join("\n", "LOGIN", "student1@ed.ac.uk", "student1pass", "EDIT_PREFERENCES", "Music, Theatre", "EDIT_PREFERENCES", "Dance", "EDIT_PREFERENCES", "", "X") + "\n";

        ByteArrayInputStream testIn = new ByteArrayInputStream(simulatedInput.getBytes());
        System.setIn(testIn);

        ByteArrayOutputStream testOut = new ByteArrayOutputStream();
        System.setOut(new PrintStream(testOut));

        Main.main(new String[0]);

        String output = testOut.toString();

        //Test against outputs.
        assert output.contains("[SUCCESS] Preferences updated successfully.") : "Confirms update.";
        assert output.contains("[SUCCESS] Current preferences — Music: true, Theatre: true, Dance: false, Movie: false, Sports: false") : "First update holds.";
        assert output.contains("[SUCCESS] Current preferences — Music: false, Theatre: false, Dance: true, Movie: false, Sports: false") : "Second update holds.";
    }
}
