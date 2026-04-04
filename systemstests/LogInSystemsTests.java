package systemstests;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LogInSystemsTests extends BaseSystemTest {

    @Test
    void loginStudentWithCorrectCredentialsShouldSetCurrentUser() {
        // attempt to log in using correct student credentials
        boolean loggedIn = userController.login("student@gmail.com", "pw123");

        assertTrue(loggedIn, "Student should be able to log in with correct credentials");
        assertEquals(student, userController.getCurrentUser(), "Current user should be the logged in student");
    }

    @Test
    void loginAdminWithCorrectCredentialsShouldSetCurrentUser() {
        // attempt to log in using correct admin credentials
        boolean loggedIn = userController.login("admin@gmail.com", "adminpw");

        assertTrue(loggedIn, "Admin should be able to log in with correct credentials");
        assertEquals(admin, userController.getCurrentUser(), "Current user should be the logged in admin");
    }

    @Test
    void loginWithWrongPasswordShouldFail() {
        // attempt login with incorrect password
        boolean loggedIn = userController.login("student@gmail.com", "wrong");

        assertFalse(loggedIn, "Login should fail for incorrect password");
        assertNull(userController.getCurrentUser(), "Current user should stay null after failed login");
    }

    @Test
    void loginWithUnknownEmailShouldFail() {
        // attemp to log in with an unkown email account
        boolean loggedIn = userController.login("unknown@gmail.com", "pw123");

        assertFalse(loggedIn, "Login should fail for unknown email");
        assertNull(userController.getCurrentUser(), "Current user should stay null after failed login");
    }
}