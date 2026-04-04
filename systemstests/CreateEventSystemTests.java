package systemstests;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CreateEventSystemTests extends BaseSystemTest {

    @Test
    void providerShouldBeAbleToCreateEvent() {
        // provider successfully creates a new event
        userController.login("ep@org.com", "eppw");

        Event event = eventPerformanceController.createEvent(
                300L,
                "Comedy Night",
                EventType.Theatre,
                provider
        );

        assertNotNull(event, "Created event should not be null");
        assertEquals("Comedy Night", event.getTitle(), "Event title should match input");
        assertEquals(provider, event.getEntertainmentProvider(), "Event should belong to logged-in provider");
    }

    @Test
    void nonProviderShouldNotBeAbleToCreateEvent() {
        // a non provider (eg student) should not bet abke to create an event
        userController.login("student@gmail.com", "pw123");

        assertThrows(IllegalStateException.class,
                () -> eventPerformanceController.createEvent(301L, "Bad Event", EventType.Music, student),
                "Students should not be able to create events");
    }
}