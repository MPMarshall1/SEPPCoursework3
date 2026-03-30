import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.List;
import java.util.Scanner;

import static org.junit.Assert.assertTrue;

public class MainTest {
    @Test
    void testSearchTrue() {
        FacultyMember[] array = new FacultyMember[2]; //dummy array.
        array[0] = new FacultyMember("email1", "");
        array[1] = new FacultyMember("email2", "");

        FacultyMember searchResult = Main.search(array, "email1");

        assert array[0].equals(searchResult) : "Search function finds false negative.";
    }

    @Test
    void testSearchFalse() {
        FacultyMember[] array = new FacultyMember[2];
        array[0] = new FacultyMember("email1", "");
        array[1] = new FacultyMember("email2", "");

        FacultyMember searchResult = Main.search(array, "email3");

        assert searchResult==null : "Search function finds false positive.";
    }
}
