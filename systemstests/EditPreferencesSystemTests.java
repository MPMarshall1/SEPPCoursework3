package systemstests;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EditPreferencesSystemTests extends BaseSystemTest {

    @Test
    void studentShouldBeAbleToEditPreferences() {
        // student updates preferences and are stored
        userController.login("student@gmail.com", "pw123");

        StudentPreferences preferences = new StudentPreferences();
        preferences.setFavouriteEventType(EventType.Music);
        preferences.setMaxBudget(25.0);

        student.setPreferences(preferences);

        assertEquals(EventType.Music, student.getPreferences().getFavouriteEventType(),
                "Student preference should store selected event type");
        assertEquals(25.0, student.getPreferences().getMaxBudget(),
                "Student preference should store max budget");
    }

    @Test
    void editingPreferencesShouldReplacePreviousValues() {
        // updating preferences should overwrite previous values
        StudentPreferences original = new StudentPreferences();
        original.setFavouriteEventType(EventType.Theatre);
        original.setMaxBudget(50.0);
        student.setPreferences(original);

        StudentPreferences updated = new StudentPreferences();
        updated.setFavouriteEventType(EventType.Dance);
        updated.setMaxBudget(15.0);
        student.setPreferences(updated);

        assertEquals(EventType.Dance, student.getPreferences().getFavouriteEventType(),
                "Updated preferences should replace the previous preferred type");
        assertEquals(15.0, student.getPreferences().getMaxBudget(),
                "Updated preferences should replace the previous budget");
    }
}