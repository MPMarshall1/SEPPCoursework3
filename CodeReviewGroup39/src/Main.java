import java.util.ArrayList;
import java.util.Objects;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        final String filePath = "src/mockList";
        RegistrationUtility utility = new RegistrationUtility(filePath);

        FacultyMember[] facultyMembers = utility.registerFacultyMembers();

        if (facultyMembers == null) {return;} //Stop if registration goes wrong.

        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter X at any time to exit.");

        //Prompts user for email, before matching for associated member's password.
        System.out.println("Enter email:");
        String email = scanner.nextLine();
        if (email.equals("X")) {return;}

        FacultyMember member = search(facultyMembers, email);

        while(member == null) {
            System.out.println("That email is not associated with any faculty member. Enter email:");
            email = scanner.nextLine();
            if (email.equals("X")) {return;}

            member = search(facultyMembers, email);
        }

        //move to specific member object.
        member.login(scanner);
    }

    public static FacultyMember search(FacultyMember[] array, String target){
        //returns matching member from array or null if none match.
        for (FacultyMember facultyMember : array) {
            if (Objects.equals(facultyMember.getEmail(), target)) {
                return facultyMember;
            }
        }
        return null;
    }
}
