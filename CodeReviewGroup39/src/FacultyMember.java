import java.util.Objects;
import java.util.Scanner;

public class FacultyMember extends User{
    private int loginAttempts = 0;

    FacultyMember(String EMAIL, String PASSWORD){
        super(EMAIL, PASSWORD);
    }

    public void login(Scanner scanner) {

        //Prompts for password until correct or user exits.
        System.out.println("Enter password:");
        String passwordInput = scanner.nextLine();
        if (passwordInput.equals("X")) {return;}

        while (!Objects.equals(passwordInput, this.getPassword())) {
            System.out.println("Password incorrect. Enter password:");
            if (passwordInput.equals("X")) {return;}
            passwordInput = scanner.nextLine();
        }

        System.out.println("Logged in.");
        loginAttempts++;

        //Upon the first login, the user should be prompted to change the password if they wish.
        if (loginAttempts==1) {
            changePassword(scanner);
        }

    }

    private void changePassword(Scanner scanner) {
        //Allows user to change password or forgo.
        System.out.println("Would you like to change your password? Y or N:");
        String choice = scanner.nextLine();
        if (choice.equals("X")) {return;}

        while(!Objects.equals(choice, "Y") && !Objects.equals(choice, "N")) {
            System.out.println("That is neither 'Y' nor 'N'. Would you like to change your password? Y or N:");
            choice = scanner.nextLine();
            if (choice.equals("X")) {return;}
        }

        if (Objects.equals(choice, "Y")) {
            System.out.println("Enter new password:");
            String passwordInput = scanner.nextLine();
            if (passwordInput.equals("X")) {return;}

            this.setPassword(passwordInput);
        }
    }

    @Override
    public boolean equals(Object o) {
        //Equality test for members based on same email and password.
        if (this == o) return true;
        if (!(o instanceof FacultyMember other)) return false;
        return Objects.equals(this.getEmail(), other.getEmail()) &&
                Objects.equals(this.getPassword(), other.getPassword());
    }

}
