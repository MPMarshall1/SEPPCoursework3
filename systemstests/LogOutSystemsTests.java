package systemstests;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LogOutSystemTests extends BaseSystemTest {

    @Test
    void logoutAfterSuccessfulLoginShouldClearCurrentUser() {

        userController.login("student@gmail.com", "pw123");
        userController.logout();
        assertNull(userController.getCurrentUser(), "Current user should be cleared after logout");
    }

    @Test
    void logoutWhenNoUserLoggedInShouldLeaveStateSafe() {
        // attemp to log out when no one is logged in
        assertDoesNotThrow(() -> userController.logout(),
                "Logout with no active session should not crash");
        assertNull(userController.getCurrentUser(),
                "Current user should remain null when logging out with no session");
    }
}