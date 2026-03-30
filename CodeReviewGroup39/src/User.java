import java.util.Objects;

public abstract class User {
    private final String email;
    private String password;

    User(String EMAIL, String PASSWORD) {
        email = EMAIL;
        password = PASSWORD;
    }


    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}