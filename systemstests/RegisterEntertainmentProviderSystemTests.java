package systemstests;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RegisterEntertainmentProviderSystemTests extends BaseSystemTest {

    @Test
    void registerEntertainmentProviderWithValidDataShouldCreateAccount() {
        // creates a new entertainment provider with new details
        EntertainmentProvider newProvider = new EntertainmentProvider(
                "new.ep@org.com",
                "secret123",
                "New Org",
                "BN999"
        );

        boolean registered = userController.registerEntertainmentProvider(newProvider);

        assertTrue(registered, "Valid entertainment provider should be registered");
        assertNotNull(userController.findUserByEmail("new.ep@org.com"),
                "New entertainment provider account should exist after registration");
    }

    @Test
    // the systems fails because of a duplicated entertainment provider that already exists
    void registerEntertainmentProviderWithDuplicateEmailShouldFail() {
        EntertainmentProvider duplicate = new EntertainmentProvider(
                "ep@org.com",
                "anotherPw",
                "Other Org",
                "BN777"
        );

        boolean registered = userController.registerEntertainmentProvider(duplicate);

        assertFalse(registered, "Registration should fail when email already exists");
    }

}