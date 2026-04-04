package systemstests;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ViewPerformanceSystemTests extends BaseSystemTest {

    @Test
    void viewPerformanceWithExistingIdShouldReturnPerformance() {
        // viewing an existing performance should return the correct details
        createEventWithPerformanceOwnedByProvider();

        Performance performance = eventPerformanceController.getPerformanceByID(200L);

        assertNotNull(performance, "Existing performance ID should resolve to a performance");
        assertEquals("Spring Concert - Evening", performance.getTitle(),
                "Returned performance should match stored performance");
    }

    @Test
    void viewPerformanceWithUnknownIdShouldReturnNull() {
        // biewing a non existing performance should return nothing
        createEventWithPerformanceOwnedByProvider();

        Performance performance = eventPerformanceController.getPerformanceByID(999L);

        assertNull(performance, "Unknown performance ID should return null");
    }
}