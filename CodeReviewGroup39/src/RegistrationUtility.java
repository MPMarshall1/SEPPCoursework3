import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class RegistrationUtility {
    private final String filePath;

    RegistrationUtility(String FILEPATH){
        filePath = FILEPATH;
    }

    public FacultyMember[] registerFacultyMembers() {
        ArrayList<FacultyMember> facultyMembers = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;

            while ((line = br.readLine()) != null) {
                String[] splits = line.split(" ");
                assert (splits.length == 2) : "Too many inputs in line.";
                facultyMembers.add(new FacultyMember(splits[0], splits[1]));

            }
        } catch (IOException e) {
            System.out.println("File does not exist.");
        }

        return facultyMembers.toArray(new FacultyMember[0]);
    }
}
