import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Arrays;

public class RegistrationUtilityTest {
    @Test
    void testFileReading() {
        //Manually make array and compare with reading of file.
        FacultyMember[] expected = new FacultyMember[4];
        expected[0] = new FacultyMember("K.Starmer@hind.ac.uk", "HF*2705HsGfIHgf");
        expected[1] = new FacultyMember("R.Sunak@hind.ac.uk", "FfqugtHQ94Gr1r*");
        expected[2] = new FacultyMember("L.Truss@hind.ac.uk", "*4681HFwifgH");
        expected[3] = new FacultyMember("email", "password");

        RegistrationUtility utility = new RegistrationUtility("src/mockList");
        FacultyMember[] actual = utility.registerFacultyMembers();

        assert Arrays.equals(expected, actual) : "Registration Utility builds wrong array of members.";
    }

    @Test
    void testIncorrectFile() {
        //Attempts to read missing file.
        final String filePath = "src/non-existant_file";
        RegistrationUtility utility = new RegistrationUtility(filePath);

        FacultyMember[] facultyMembers = utility.registerFacultyMembers();

        assert facultyMembers==null : "Missing file is read.";
    }
}
