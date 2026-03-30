import org.junit.jupiter.api.Test;
import java.util.Arrays;

public class RegistrationUtilityTest {
    @Test
    void testFileReading() {
        FacultyMember[] expected = new FacultyMember[4];
        expected[0] = new FacultyMember("K.Starmer@hind.ac.uk", "HF*2705HsGfIHgf");
        expected[1] = new FacultyMember("R.Sunak@hind.ac.uk", "FfqugtHQ94Gr1r*");
        expected[2] = new FacultyMember("L.Truss@hind.ac.uk", "*4681HFwifgH");
        expected[3] = new FacultyMember("email", "password");

        RegistrationUtility utility = new RegistrationUtility("src/mockList");
        FacultyMember[] actual = utility.registerFacultyMembers();

        assert Arrays.equals(expected, actual) : "Registration Utility builds wrong array of members.";
    }
}
