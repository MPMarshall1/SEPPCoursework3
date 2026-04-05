package com.example;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;

public class SearchForPerformancesSystemTest {
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
    void searchBlankPerformances() {
        //No performances show when there are none.
        String simulatedInput = String.join("\n", "LOGIN", "admin@ed.ac.uk", "adminpass", "SEARCH_FOR_PERFORMANCES", "01/01/2000", "X") + "\n";

        ByteArrayInputStream testIn = new ByteArrayInputStream(simulatedInput.getBytes());
        System.setIn(testIn);

        ByteArrayOutputStream testOut = new ByteArrayOutputStream();
        System.setOut(new PrintStream(testOut));

        Main.main(new String[0]);

        String output = testOut.toString();

        //Test against outputs.
        assert output.contains("There are no performances on the provided date") : "Does not show no performances.";
    }

    @Test
    void searchInvalidDate() {
        //No performances until date is valid
        String simulatedInput = String.join("\n", "LOGIN", "admin@ed.ac.uk", "adminpass", "SEARCH_FOR_PERFORMANCES", "01/01/00", "01/01/2000", "X") + "\n";

        ByteArrayInputStream testIn = new ByteArrayInputStream(simulatedInput.getBytes());
        System.setIn(testIn);

        ByteArrayOutputStream testOut = new ByteArrayOutputStream();
        System.setOut(new PrintStream(testOut));

        Main.main(new String[0]);

        String output = testOut.toString();

        //Test against outputs.
        assert output.contains("Invalid format. Please try again.") : "Does not flag date format.";
    }

    @Test
    void searchCorrect() {
        //Search for performances normally.
        String simulatedInput = String.join("\n", "REGISTER_EP", "theCoffeeCompany", "0123456789", "Michael", "Makes Coffee", "Michael@CoffeeCo.com", "ILoveCoffee123", "LOGIN", "Michael@CoffeeCo.com", "ILoveCoffee123", "CREATE_EVENT", "CoffeeFest", "no", "Music", "CREATE_PERFORMANCE", "1", "07/04/2026 12:00", "07/04/2026 12:01", "TheCoffeeMan", "Appleton Tower", "400", "no", "no", "SEARCH_FOR_PERFORMANCES", "07/04/2026", "X") + "\n";

        ByteArrayInputStream testIn = new ByteArrayInputStream(simulatedInput.getBytes());
        System.setIn(testIn);

        ByteArrayOutputStream testOut = new ByteArrayOutputStream();
        System.setOut(new PrintStream(testOut));

        Main.main(new String[0]);

        String output = testOut.toString();

        //Test against outputs.
        assert output.contains("[SUCCESS] Performance added successfully with ID: 1") : "Does not confirm performance.";
        assert output.contains("Performances on 07/04/2026:") : "Does not show list.";

        assert output.contains("Event Name: CoffeeFest") : "Does not show event";
        //It is assumed that all other rows will show similarly.
    }
}
